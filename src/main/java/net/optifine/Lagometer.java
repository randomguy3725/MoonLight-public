package net.optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.profiler.Profiler;
import net.minecraft.src.Config;
import net.optifine.util.MemoryMonitor;
import org.lwjgl.opengl.GL11;

public class Lagometer
{
    private static Minecraft mc;
    private static GameSettings gameSettings;
    private static Profiler profiler;
    public static boolean active = false;
    public static Lagometer.TimerNano timerTick = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerScheduledExecutables = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerChunkUpload = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerChunkUpdate = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerVisibility = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerTerrain = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerServer = new Lagometer.TimerNano();
    private static final long[] timesFrame = new long[512];
    private static final long[] timesTick = new long[512];
    private static final long[] timesScheduledExecutables = new long[512];
    private static final long[] timesChunkUpload = new long[512];
    private static final long[] timesChunkUpdate = new long[512];
    private static final long[] timesVisibility = new long[512];
    private static final long[] timesTerrain = new long[512];
    private static final long[] timesServer = new long[512];
    private static final boolean[] gcs = new boolean[512];
    private static int numRecordedFrameTimes = 0;
    private static long prevFrameTimeNano = -1L;
    private static long renderTimeNano = 0L;

    public static void updateLagometer()
    {
        if (mc == null)
        {
            mc = Minecraft.getMinecraft();
            gameSettings = mc.gameSettings;
            profiler = mc.mcProfiler;
        }

        if (gameSettings.showDebugInfo && (gameSettings.ofLagometer || gameSettings.showLagometer))
        {
            active = true;
            long timeNowNano = System.nanoTime();

            if (prevFrameTimeNano == -1L)
            {
                prevFrameTimeNano = timeNowNano;
            }
            else
            {
                int j = numRecordedFrameTimes & timesFrame.length - 1;
                ++numRecordedFrameTimes;
                boolean flag = MemoryMonitor.isGcEvent();
                timesFrame[j] = timeNowNano - prevFrameTimeNano - renderTimeNano;
                timesTick[j] = timerTick.timeNano;
                timesScheduledExecutables[j] = timerScheduledExecutables.timeNano;
                timesChunkUpload[j] = timerChunkUpload.timeNano;
                timesChunkUpdate[j] = timerChunkUpdate.timeNano;
                timesVisibility[j] = timerVisibility.timeNano;
                timesTerrain[j] = timerTerrain.timeNano;
                timesServer[j] = timerServer.timeNano;
                gcs[j] = flag;
                timerTick.reset();
                timerScheduledExecutables.reset();
                timerVisibility.reset();
                timerChunkUpdate.reset();
                timerChunkUpload.reset();
                timerTerrain.reset();
                timerServer.reset();
                prevFrameTimeNano = System.nanoTime();
            }
        }
        else
        {
            active = false;
            prevFrameTimeNano = -1L;
        }
    }

    public static void showLagometer(ScaledResolution scaledResolution)
    {
        if (gameSettings != null)
        {
            if (gameSettings.ofLagometer || gameSettings.showLagometer)
            {
                long i = System.nanoTime();
                GlStateManager.clear(256);
                GlStateManager.matrixMode(5889);
                GlStateManager.pushMatrix();
                GlStateManager.enableColorMaterial();
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0.0D, mc.displayWidth, mc.displayHeight, 0.0D, 1000.0D, 3000.0D);
                GlStateManager.matrixMode(5888);
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();
                GlStateManager.translate(0.0F, 0.0F, -2000.0F);
                GL11.glLineWidth(1.0F);
                GlStateManager.disableTexture2D();
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                worldrenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);

                for (int j = 0; j < timesFrame.length; ++j)
                {
                    int k = (j - numRecordedFrameTimes & timesFrame.length - 1) * 100 / timesFrame.length;
                    k = k + 155;
                    float f = (float)mc.displayHeight;
                    long l = 0L;

                    if (gcs[j])
                    {
                        renderTime(j, timesFrame[j], k, k / 2, 0, f, worldrenderer);
                    }
                    else
                    {
                        renderTime(j, timesFrame[j], k, k, k, f, worldrenderer);
                        f = f - (float)renderTime(j, timesServer[j], k / 2, k / 2, k / 2, f, worldrenderer);
                        f = f - (float)renderTime(j, timesTerrain[j], 0, k, 0, f, worldrenderer);
                        f = f - (float)renderTime(j, timesVisibility[j], k, k, 0, f, worldrenderer);
                        f = f - (float)renderTime(j, timesChunkUpdate[j], k, 0, 0, f, worldrenderer);
                        f = f - (float)renderTime(j, timesChunkUpload[j], k, 0, k, f, worldrenderer);
                        f = f - (float)renderTime(j, timesScheduledExecutables[j], 0, 0, k, f, worldrenderer);
                        float f2 = f - (float)renderTime(j, timesTick[j], 0, k, k, f, worldrenderer);
                    }
                }

                renderTimeDivider(0, timesFrame.length, 33333333L, 196, 196, 196, (float)mc.displayHeight, worldrenderer);
                renderTimeDivider(0, timesFrame.length, 16666666L, 196, 196, 196, (float)mc.displayHeight, worldrenderer);
                tessellator.draw();
                GlStateManager.enableTexture2D();
                int j2 = mc.displayHeight - 80;
                int k2 = mc.displayHeight - 160;
                mc.fontRendererObj.drawString("30", 2, k2 + 1, -8947849);
                mc.fontRendererObj.drawString("30", 1, k2, -3881788);
                mc.fontRendererObj.drawString("60", 2, j2 + 1, -8947849);
                mc.fontRendererObj.drawString("60", 1, j2, -3881788);
                GlStateManager.matrixMode(5889);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
                GlStateManager.popMatrix();
                GlStateManager.enableTexture2D();
                float f1 = 1.0F - (float)((double)(System.currentTimeMillis() - MemoryMonitor.getStartTimeMs()) / 1000.0D);
                f1 = Config.limit(f1, 0.0F, 1.0F);
                int l2 = (int)(170.0F + f1 * 85.0F);
                int i1 = (int)(100.0F + f1 * 55.0F);
                int j1 = (int)(10.0F + f1 * 10.0F);
                int k1 = l2 << 16 | i1 << 8 | j1;
                int l1 = 512 / scaledResolution.getScaleFactor() + 2;
                int i2 = mc.displayHeight / scaledResolution.getScaleFactor() - 8;
                GuiIngame guiingame = mc.ingameGUI;
                GuiIngame.drawRect(l1 - 1, i2 - 1, l1 + 50, i2 + 10, -1605349296);
                mc.fontRendererObj.drawString(" " + MemoryMonitor.getAllocationRateMb() + " MB/s", l1, i2, k1);
                renderTimeNano = System.nanoTime() - i;
            }
        }
    }

    private static long renderTime(int frameNum, long time, int r, int g, int b, float baseHeight, WorldRenderer tessellator)
    {
        long i = time / 200000L;

        if (i < 3L)
        {
            return 0L;
        }
        else
        {
            tessellator.pos((float)frameNum + 0.5F, baseHeight - (float)i + 0.5F, 0.0D).color(r, g, b, 255).endVertex();
            tessellator.pos((float)frameNum + 0.5F, baseHeight + 0.5F, 0.0D).color(r, g, b, 255).endVertex();
            return i;
        }
    }

    private static long renderTimeDivider(int frameStart, int frameEnd, long time, int r, int g, int b, float baseHeight, WorldRenderer tessellator)
    {
        long i = time / 200000L;

        if (i < 3L)
        {
            return 0L;
        }
        else
        {
            tessellator.pos((float)frameStart + 0.5F, baseHeight - (float)i + 0.5F, 0.0D).color(r, g, b, 255).endVertex();
            tessellator.pos((float)frameEnd + 0.5F, baseHeight - (float)i + 0.5F, 0.0D).color(r, g, b, 255).endVertex();
            return i;
        }
    }

    public static boolean isActive()
    {
        return active;
    }

    public static class TimerNano
    {
        public long timeStartNano = 0L;
        public long timeNano = 0L;

        public void start()
        {
            if (Lagometer.active)
            {
                if (this.timeStartNano == 0L)
                {
                    this.timeStartNano = System.nanoTime();
                }
            }
        }

        public void end()
        {
            if (Lagometer.active)
            {
                if (this.timeStartNano != 0L)
                {
                    this.timeNano += System.nanoTime() - this.timeStartNano;
                    this.timeStartNano = 0L;
                }
            }
        }

        private void reset()
        {
            this.timeNano = 0L;
            this.timeStartNano = 0L;
        }
    }
}
