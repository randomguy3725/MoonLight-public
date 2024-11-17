package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.optifine.SmartAnimations;
import net.optifine.TextureAnimations;
import net.optifine.reflect.Reflector;
import net.optifine.util.MemoryMonitor;
import net.optifine.util.NativeMemory;
import org.lwjglx.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GuiOverlayDebug extends Gui
{
    private final Minecraft mc;
    private final FontRenderer fontRenderer;
    private String debugOF = null;
    private List<String> debugInfoLeft = null;
    private List<String> debugInfoRight = null;
    private long updateInfoLeftTimeMs = 0L;
    private long updateInfoRightTimeMs = 0L;

    public GuiOverlayDebug(Minecraft mc)
    {
        this.mc = mc;
        this.fontRenderer = mc.fontRendererObj;
    }

    public void renderDebugInfo(ScaledResolution scaledResolutionIn)
    {
        this.mc.mcProfiler.startSection("debug");
        GlStateManager.pushMatrix();
        this.renderDebugInfoLeft();
        this.renderDebugInfoRight(scaledResolutionIn);
        GlStateManager.popMatrix();

        if (this.mc.gameSettings.showLagometer)
        {
            this.renderLagometer();
        }

        this.mc.mcProfiler.endSection();
    }

    private boolean isReducedDebug()
    {
        return this.mc.thePlayer.hasReducedDebug() || this.mc.gameSettings.reducedDebugInfo;
    }

    protected void renderDebugInfoLeft()
    {
        List<String> list = this.debugInfoLeft;

        if (list == null || System.currentTimeMillis() > this.updateInfoLeftTimeMs)
        {
            list = this.call();
            this.debugInfoLeft = list;
            this.updateInfoLeftTimeMs = System.currentTimeMillis() + 100L;
        }

        for (int i = 0; i < list.size(); ++i)
        {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s))
            {
                int j = this.fontRenderer.FONT_HEIGHT;
                int k = this.fontRenderer.getStringWidth(s);
                int l = 2;
                int i1 = 2 + j * i;
                drawRect(1, i1 - 1, 2 + k + 1, i1 + j - 1, -1873784752);
                this.fontRenderer.drawString(s, 2, i1, 14737632);
            }
        }
    }

    protected void renderDebugInfoRight(ScaledResolution scaledRes)
    {
        List<String> list = this.debugInfoRight;

        if (list == null || System.currentTimeMillis() > this.updateInfoRightTimeMs)
        {
            list = this.getDebugInfoRight();
            this.debugInfoRight = list;
            this.updateInfoRightTimeMs = System.currentTimeMillis() + 100L;
        }

        for (int i = 0; i < list.size(); ++i)
        {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s))
            {
                int j = this.fontRenderer.FONT_HEIGHT;
                int k = this.fontRenderer.getStringWidth(s);
                int l = scaledRes.getScaledWidth() - 2 - k;
                int i1 = 2 + j * i;
                drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
                this.fontRenderer.drawString(s, l, i1, 14737632);
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected List<String> call()
    {
        BlockPos blockpos = new BlockPos(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);

        if (this.mc.debug != this.debugOF)
        {
            StringBuffer stringbuffer = new StringBuffer(this.mc.debug);
            int i = Config.getFpsMin();
            int j = this.mc.debug.indexOf(" fps ");

            if (j >= 0)
            {
                stringbuffer.insert(j, "/" + i);
            }

            if (Config.isSmoothFps())
            {
                stringbuffer.append(" sf");
            }

            if (Config.isFastRender())
            {
                stringbuffer.append(" fr");
            }

            if (Config.isAnisotropicFiltering())
            {
                stringbuffer.append(" af");
            }

            if (Config.isAntialiasing())
            {
                stringbuffer.append(" aa");
            }

            if (Config.isRenderRegions())
            {
                stringbuffer.append(" reg");
            }

            if (Config.isShaders())
            {
                stringbuffer.append(" sh");
            }

            this.mc.debug = stringbuffer.toString();
            this.debugOF = this.mc.debug;
        }

        StringBuilder stringbuilder = new StringBuilder();
        TextureMap texturemap = Config.getTextureMap();
        stringbuilder.append(", A: ");

        if (SmartAnimations.isActive())
        {
            stringbuilder.append(texturemap.getCountAnimationsActive() + TextureAnimations.getCountAnimationsActive());
            stringbuilder.append("/");
        }

        stringbuilder.append(texturemap.getCountAnimations() + TextureAnimations.getCountAnimations());
        String s1 = stringbuilder.toString();

        if (this.isReducedDebug())
        {
            return Lists.newArrayList("Minecraft 1.8.9 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities() + s1, this.mc.theWorld.getProviderName(), "", String.format("Chunk-relative: %d %d %d", Integer.valueOf(blockpos.getX() & 15), Integer.valueOf(blockpos.getY() & 15), Integer.valueOf(blockpos.getZ() & 15)));
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            EnumFacing enumfacing = entity.getHorizontalFacing();
            String s = "Invalid";

            switch (enumfacing)
            {
                case NORTH:
                    s = "Towards negative Z";
                    break;

                case SOUTH:
                    s = "Towards positive Z";
                    break;

                case WEST:
                    s = "Towards negative X";
                    break;

                case EAST:
                    s = "Towards positive X";
            }

            List<String> list = Lists.newArrayList("Minecraft 1.8.9 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities() + s1, this.mc.theWorld.getProviderName(), "", String.format("XYZ: %.3f / %.5f / %.3f", Double.valueOf(this.mc.getRenderViewEntity().posX), Double.valueOf(this.mc.getRenderViewEntity().getEntityBoundingBox().minY), Double.valueOf(this.mc.getRenderViewEntity().posZ)), String.format("Block: %d %d %d", Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()), Integer.valueOf(blockpos.getZ())), String.format("Chunk: %d %d %d in %d %d %d", Integer.valueOf(blockpos.getX() & 15), Integer.valueOf(blockpos.getY() & 15), Integer.valueOf(blockpos.getZ() & 15), Integer.valueOf(blockpos.getX() >> 4), Integer.valueOf(blockpos.getY() >> 4), Integer.valueOf(blockpos.getZ() >> 4)), String.format("Facing: %s (%s) (%.1f / %.1f)", enumfacing, s, Float.valueOf(MathHelper.wrapAngleTo180_float(entity.rotationYaw)), Float.valueOf(MathHelper.wrapAngleTo180_float(entity.rotationPitch))));

            if (this.mc.theWorld != null && this.mc.theWorld.isBlockLoaded(blockpos))
            {
                Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(blockpos);
                list.add("Biome: " + chunk.getBiome(blockpos, this.mc.theWorld.getWorldChunkManager()).biomeName);
                list.add("Light: " + chunk.getLightSubtracted(blockpos, 0) + " (" + chunk.getLightFor(EnumSkyBlock.SKY, blockpos) + " sky, " + chunk.getLightFor(EnumSkyBlock.BLOCK, blockpos) + " block)");
                DifficultyInstance difficultyinstance = this.mc.theWorld.getDifficultyForLocation(blockpos);

                if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null)
                {
                    EntityPlayerMP entityplayermp = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.thePlayer.getUniqueID());

                    if (entityplayermp != null)
                    {
                        DifficultyInstance difficultyinstance1 = this.mc.getIntegratedServer().getDifficultyAsync(entityplayermp.worldObj, new BlockPos(entityplayermp));

                        if (difficultyinstance1 != null)
                        {
                            difficultyinstance = difficultyinstance1;
                        }
                    }
                }

                list.add(String.format("Local Difficulty: %.2f (Day %d)", Float.valueOf(difficultyinstance.getAdditionalDifficulty()), Long.valueOf(this.mc.theWorld.getWorldTime() / 24000L)));
            }

            if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive())
            {
                list.add("Shader: " + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());
            }

            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null)
            {
                BlockPos blockpos1 = this.mc.objectMouseOver.getBlockPos();
                list.add(String.format("Looking at: %d %d %d", Integer.valueOf(blockpos1.getX()), Integer.valueOf(blockpos1.getY()), Integer.valueOf(blockpos1.getZ())));
            }

            return list;
        }
    }

    protected List<String> getDebugInfoRight()
    {
        long i = Runtime.getRuntime().maxMemory();
        long j = Runtime.getRuntime().totalMemory();
        long k = Runtime.getRuntime().freeMemory();
        long l = j - k;
        List<String> list = Lists.newArrayList(String.format("Java: %s %dbit", System.getProperty("java.version"), Integer.valueOf(this.mc.isJava64bit() ? 64 : 32)), String.format("Mem: % 2d%% %03d/%03dMB", Long.valueOf(l * 100L / i), Long.valueOf(bytesToMb(l)), Long.valueOf(bytesToMb(i))), String.format("Allocated: % 2d%% %03dMB", Long.valueOf(j * 100L / i), Long.valueOf(bytesToMb(j))), "", String.format("CPU: %s", OpenGlHelper.getCpu()), "", String.format("Display: %dx%d (%s)", Integer.valueOf(Display.getWidth()), Integer.valueOf(Display.getHeight()), GL11.glGetString(GL11.GL_VENDOR)), GL11.glGetString(GL11.GL_RENDERER), GL11.glGetString(GL11.GL_VERSION));
        long i1 = NativeMemory.getBufferAllocated();
        long j1 = NativeMemory.getBufferMaximum();
        String s = "Native: " + bytesToMb(i1) + "/" + bytesToMb(j1) + "MB";
        list.add(4, s);
        list.set(5, "GC: " + MemoryMonitor.getAllocationRateMb() + "MB/s");

        if (Reflector.FMLCommonHandler_getBrandings.exists())
        {
            Object object = Reflector.call(Reflector.FMLCommonHandler_instance);
            list.add("");
            list.addAll((Collection)Reflector.call(object, Reflector.FMLCommonHandler_getBrandings, new Object[] {Boolean.valueOf(false)}));
        }

        if (this.isReducedDebug())
        {
            return list;
        }
        else
        {
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null)
            {
                BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
                IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);

                if (this.mc.theWorld.getWorldType() != WorldType.DEBUG_WORLD)
                {
                    iblockstate = iblockstate.getBlock().getActualState(iblockstate, this.mc.theWorld, blockpos);
                }

                list.add("");
                list.add(String.valueOf(Block.blockRegistry.getNameForObject(iblockstate.getBlock())));

                for (Entry<IProperty, Comparable> entry : iblockstate.getProperties().entrySet())
                {
                    String s1 = entry.getValue().toString();

                    if (entry.getValue() == Boolean.TRUE)
                    {
                        s1 = EnumChatFormatting.GREEN + s1;
                    }
                    else if (entry.getValue() == Boolean.FALSE)
                    {
                        s1 = EnumChatFormatting.RED + s1;
                    }

                    list.add(entry.getKey().getName() + ": " + s1);
                }
            }

            return list;
        }
    }

    private void renderLagometer()
    {
    }

    private int getFrameColor(int p_181552_1_, int p_181552_2_, int p_181552_3_, int p_181552_4_)
    {
        return p_181552_1_ < p_181552_3_ ? this.blendColors(-16711936, -256, (float)p_181552_1_ / (float)p_181552_3_) : this.blendColors(-256, -65536, (float)(p_181552_1_ - p_181552_3_) / (float)(p_181552_4_ - p_181552_3_));
    }

    private int blendColors(int p_181553_1_, int p_181553_2_, float p_181553_3_)
    {
        int i = p_181553_1_ >> 24 & 255;
        int j = p_181553_1_ >> 16 & 255;
        int k = p_181553_1_ >> 8 & 255;
        int l = p_181553_1_ & 255;
        int i1 = p_181553_2_ >> 24 & 255;
        int j1 = p_181553_2_ >> 16 & 255;
        int k1 = p_181553_2_ >> 8 & 255;
        int l1 = p_181553_2_ & 255;
        int i2 = MathHelper.clamp_int((int)((float)i + (float)(i1 - i) * p_181553_3_), 0, 255);
        int j2 = MathHelper.clamp_int((int)((float)j + (float)(j1 - j) * p_181553_3_), 0, 255);
        int k2 = MathHelper.clamp_int((int)((float)k + (float)(k1 - k) * p_181553_3_), 0, 255);
        int l2 = MathHelper.clamp_int((int)((float)l + (float)(l1 - l) * p_181553_3_), 0, 255);
        return i2 << 24 | j2 << 16 | k2 << 8 | l2;
    }

    private static long bytesToMb(long bytes)
    {
        return bytes / 1024L / 1024L;
    }
}
