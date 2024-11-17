package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ChunkRenderDispatcher
{
    private static final Logger logger = LogManager.getLogger();
    private static final ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setNameFormat("Chunk Batcher %d").setDaemon(true).build();
    private final List<ChunkRenderWorker> listThreadedWorkers;
    private final BlockingQueue<ChunkCompileTaskGenerator> queueChunkUpdates;
    private final BlockingQueue<RegionRenderCacheBuilder> queueFreeRenderBuilders;
    private final WorldVertexBufferUploader worldVertexUploader;
    private final VertexBufferUploader vertexUploader;
    private final Queue < ListenableFutureTask<? >> queueChunkUploads;
    private final ChunkRenderWorker renderWorker;
    private final int countRenderBuilders;
    private final List<RegionRenderCacheBuilder> listPausedBuilders;

    public ChunkRenderDispatcher()
    {
        this(-1);
    }

    public ChunkRenderDispatcher(int p_i4_1_)
    {
        this.listThreadedWorkers = Lists.newArrayList();
        this.queueChunkUpdates = Queues.newArrayBlockingQueue(100);
        this.worldVertexUploader = new WorldVertexBufferUploader();
        this.vertexUploader = new VertexBufferUploader();
        this.queueChunkUploads = Queues.newArrayDeque();
        this.listPausedBuilders = new ArrayList();
        int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / 10485760);
        int j = Math.max(1, MathHelper.clamp_int(Runtime.getRuntime().availableProcessors() - 2, 1, i / 5));

        if (p_i4_1_ < 0)
        {
            this.countRenderBuilders = MathHelper.clamp_int(j * 8, 1, i);
        }
        else
        {
            this.countRenderBuilders = p_i4_1_;
        }

        for (int k = 0; k < j; ++k)
        {
            ChunkRenderWorker chunkrenderworker = new ChunkRenderWorker(this);
            Thread thread = threadFactory.newThread(chunkrenderworker);
            thread.start();
            this.listThreadedWorkers.add(chunkrenderworker);
        }

        this.queueFreeRenderBuilders = Queues.newArrayBlockingQueue(this.countRenderBuilders);

        for (int l = 0; l < this.countRenderBuilders; ++l)
        {
            this.queueFreeRenderBuilders.add(new RegionRenderCacheBuilder());
        }

        this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
    }

    public String getDebugInfo()
    {
        return String.format("pC: %03d, pU: %1d, aB: %1d", Integer.valueOf(this.queueChunkUpdates.size()), Integer.valueOf(this.queueChunkUploads.size()), Integer.valueOf(this.queueFreeRenderBuilders.size()));
    }

    public boolean runChunkUploads(long p_178516_1_)
    {
        boolean flag = false;

        while (true)
        {
            boolean flag1 = false;
            ListenableFutureTask listenablefuturetask = null;

            synchronized (this.queueChunkUploads)
            {
                listenablefuturetask = this.queueChunkUploads.poll();
            }

            if (listenablefuturetask != null)
            {
                listenablefuturetask.run();
                flag1 = true;
                flag = true;
            }

            if (p_178516_1_ == 0L || !flag1)
            {
                break;
            }

            long i = p_178516_1_ - System.nanoTime();

            if (i < 0L)
            {
                break;
            }
        }

        return flag;
    }

    public boolean updateChunkLater(RenderChunk chunkRenderer)
    {
        chunkRenderer.getLockCompileTask().lock();
        boolean flag;

        try
        {
            final ChunkCompileTaskGenerator chunkcompiletaskgenerator = chunkRenderer.makeCompileTaskChunk();
            chunkcompiletaskgenerator.addFinishRunnable(new Runnable()
            {
                public void run()
                {
                    ChunkRenderDispatcher.this.queueChunkUpdates.remove(chunkcompiletaskgenerator);
                }
            });
            boolean flag1 = this.queueChunkUpdates.offer(chunkcompiletaskgenerator);

            if (!flag1)
            {
                chunkcompiletaskgenerator.finish();
            }

            flag = flag1;
        }
        finally
        {
            chunkRenderer.getLockCompileTask().unlock();
        }

        return flag;
    }

    public boolean updateChunkNow(RenderChunk chunkRenderer)
    {
        chunkRenderer.getLockCompileTask().lock();
        boolean flag;

        try
        {
            ChunkCompileTaskGenerator chunkcompiletaskgenerator = chunkRenderer.makeCompileTaskChunk();

            try
            {
                this.renderWorker.processTask(chunkcompiletaskgenerator);
            }
            catch (InterruptedException var8)
            {
            }

            flag = true;
        }
        finally
        {
            chunkRenderer.getLockCompileTask().unlock();
        }

        return flag;
    }

    public void stopChunkUpdates()
    {
        this.clearChunkUpdates();

        while (this.runChunkUploads(0L))
        {
        }

        List<RegionRenderCacheBuilder> list = Lists.newArrayList();

        while (list.size() != this.countRenderBuilders)
        {
            try
            {
                list.add(this.allocateRenderBuilder());
            }
            catch (InterruptedException var3)
            {
            }
        }

        this.queueFreeRenderBuilders.addAll(list);
    }

    public void freeRenderBuilder(RegionRenderCacheBuilder p_178512_1_)
    {
        this.queueFreeRenderBuilders.add(p_178512_1_);
    }

    public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException
    {
        return this.queueFreeRenderBuilders.take();
    }

    public ChunkCompileTaskGenerator getNextChunkUpdate() throws InterruptedException
    {
        return this.queueChunkUpdates.take();
    }

    public boolean updateTransparencyLater(RenderChunk chunkRenderer)
    {
        chunkRenderer.getLockCompileTask().lock();
        boolean flag1;

        try
        {
            final ChunkCompileTaskGenerator chunkcompiletaskgenerator = chunkRenderer.makeCompileTaskTransparency();

            if (chunkcompiletaskgenerator != null)
            {
                chunkcompiletaskgenerator.addFinishRunnable(new Runnable()
                {
                    public void run()
                    {
                        ChunkRenderDispatcher.this.queueChunkUpdates.remove(chunkcompiletaskgenerator);
                    }
                });
                boolean flag2 = this.queueChunkUpdates.offer(chunkcompiletaskgenerator);
                return flag2;
            }

            boolean flag = true;
            flag1 = flag;
        }
        finally
        {
            chunkRenderer.getLockCompileTask().unlock();
        }

        return flag1;
    }

    public ListenableFuture<Object> uploadChunk(final EnumWorldBlockLayer player, final WorldRenderer p_178503_2_, final RenderChunk chunkRenderer, final CompiledChunk compiledChunkIn)
    {
        if (Minecraft.getMinecraft().isCallingFromMinecraftThread())
        {
            if (OpenGlHelper.useVbo())
            {
                this.uploadVertexBuffer(p_178503_2_, chunkRenderer.getVertexBufferByLayer(player.ordinal()));
            }
            else
            {
                this.uploadDisplayList(p_178503_2_, ((ListedRenderChunk)chunkRenderer).getDisplayList(player, compiledChunkIn), chunkRenderer);
            }

            p_178503_2_.setTranslation(0.0D, 0.0D, 0.0D);
            return Futures.immediateFuture(null);
        }
        else
        {
            ListenableFutureTask<Object> listenablefuturetask = ListenableFutureTask.create(new Runnable()
            {
                public void run()
                {
                    ChunkRenderDispatcher.this.uploadChunk(player, p_178503_2_, chunkRenderer, compiledChunkIn);
                }
            }, null);

            synchronized (this.queueChunkUploads)
            {
                this.queueChunkUploads.add(listenablefuturetask);
                return listenablefuturetask;
            }
        }
    }

    private void uploadDisplayList(WorldRenderer p_178510_1_, int p_178510_2_, RenderChunk chunkRenderer)
    {
        GL11.glNewList(p_178510_2_, GL11.GL_COMPILE);
        GlStateManager.pushMatrix();
        chunkRenderer.multModelviewMatrix();
        this.worldVertexUploader.draw(p_178510_1_);
        GlStateManager.popMatrix();
        GL11.glEndList();
    }

    private void uploadVertexBuffer(WorldRenderer p_178506_1_, VertexBuffer vertexBufferIn)
    {
        this.vertexUploader.setVertexBuffer(vertexBufferIn);
        this.vertexUploader.draw(p_178506_1_);
    }

    public void clearChunkUpdates()
    {
        while (!this.queueChunkUpdates.isEmpty())
        {
            ChunkCompileTaskGenerator chunkcompiletaskgenerator = this.queueChunkUpdates.poll();

            if (chunkcompiletaskgenerator != null)
            {
                chunkcompiletaskgenerator.finish();
            }
        }
    }

    public boolean hasChunkUpdates()
    {
        return this.queueChunkUpdates.isEmpty() && this.queueChunkUploads.isEmpty();
    }

    public void pauseChunkUpdates()
    {
        while (this.listPausedBuilders.size() != this.countRenderBuilders)
        {
            try
            {
                this.runChunkUploads(Long.MAX_VALUE);
                RegionRenderCacheBuilder regionrendercachebuilder = this.queueFreeRenderBuilders.poll(100L, TimeUnit.MILLISECONDS);

                if (regionrendercachebuilder != null)
                {
                    this.listPausedBuilders.add(regionrendercachebuilder);
                }
            }
            catch (InterruptedException var2)
            {
            }
        }
    }

    public void resumeChunkUpdates()
    {
        this.queueFreeRenderBuilders.addAll(this.listPausedBuilders);
        this.listPausedBuilders.clear();
    }
}
