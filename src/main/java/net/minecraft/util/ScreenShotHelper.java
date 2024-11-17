package net.minecraft.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.src.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ScreenShotHelper
{
    private static final Logger logger = LogManager.getLogger();
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    private static IntBuffer pixelBuffer;
    private static int[] pixelValues;

    public static IChatComponent saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer)
    {
        return saveScreenshot(gameDirectory, null, width, height, buffer);
    }

    public static IChatComponent saveScreenshot(File gameDirectory, String screenshotName, int width, int height, Framebuffer buffer)
    {
        try
        {
            File file1 = new File(gameDirectory, "screenshots");
            file1.mkdir();
            Minecraft minecraft = Minecraft.getMinecraft();
            int i = Config.getGameSettings().guiScale;
            ScaledResolution scaledresolution = new ScaledResolution(minecraft);
            int j = scaledresolution.getScaleFactor();
            int k = Config.getScreenshotSize();
            boolean flag = OpenGlHelper.isFramebufferEnabled() && k > 1;

            if (flag)
            {
                Config.getGameSettings().guiScale = j * k;
                resize(width * k, height * k);
                GlStateManager.pushMatrix();
                GlStateManager.clear(16640);
                minecraft.getFramebuffer().bindFramebuffer(true);
                minecraft.entityRenderer.updateCameraAndRender(Config.renderPartialTicks, System.nanoTime());
            }

            if (OpenGlHelper.isFramebufferEnabled())
            {
                width = buffer.framebufferTextureWidth;
                height = buffer.framebufferTextureHeight;
            }

            int l = width * height;

            if (pixelBuffer == null || pixelBuffer.capacity() < l)
            {
                pixelBuffer = BufferUtils.createIntBuffer(l);
                pixelValues = new int[l];
            }

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            pixelBuffer.clear();

            if (OpenGlHelper.isFramebufferEnabled())
            {
                GlStateManager.bindTexture(buffer.framebufferTexture);
                GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
            }
            else
            {
                GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
            }

            pixelBuffer.get(pixelValues);
            TextureUtil.processPixelValues(pixelValues, width, height);
            BufferedImage bufferedimage = null;

            if (OpenGlHelper.isFramebufferEnabled())
            {
                bufferedimage = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
                int i1 = buffer.framebufferTextureHeight - buffer.framebufferHeight;

                for (int j1 = i1; j1 < buffer.framebufferTextureHeight; ++j1)
                {
                    for (int k1 = 0; k1 < buffer.framebufferWidth; ++k1)
                    {
                        bufferedimage.setRGB(k1, j1 - i1, pixelValues[j1 * buffer.framebufferTextureWidth + k1]);
                    }
                }
            }
            else
            {
                bufferedimage = new BufferedImage(width, height, 1);
                bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
            }

            if (flag)
            {
                minecraft.getFramebuffer().unbindFramebuffer();
                GlStateManager.popMatrix();
                Config.getGameSettings().guiScale = i;
                resize(width, height);
            }

            File file2;

            if (screenshotName == null)
            {
                file2 = getTimestampedPNGFileForDirectory(file1);
            }
            else
            {
                file2 = new File(file1, screenshotName);
            }

            file2 = file2.getCanonicalFile();
            ImageIO.write(bufferedimage, "png", file2);
            IChatComponent ichatcomponent = new ChatComponentText(file2.getName());
            ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
            ichatcomponent.getChatStyle().setUnderlined(Boolean.valueOf(true));
            return new ChatComponentTranslation("screenshot.success", ichatcomponent);
        }
        catch (Exception exception)
        {
            logger.warn("Couldn't save screenshot", exception);
            return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
        }
    }

    private static File getTimestampedPNGFileForDirectory(File gameDirectory)
    {
        String s = dateFormat.format(new Date());
        int i = 1;

        while (true)
        {
            File file1 = new File(gameDirectory, s + (i == 1 ? "" : "_" + i) + ".png");

            if (!file1.exists())
            {
                return file1;
            }

            ++i;
        }
    }

    private static void resize(int p_resize_0_, int p_resize_1_)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.displayWidth = Math.max(1, p_resize_0_);
        minecraft.displayHeight = Math.max(1, p_resize_1_);

        if (minecraft.currentScreen != null)
        {
            ScaledResolution scaledresolution = new ScaledResolution(minecraft);
            minecraft.currentScreen.onResize(minecraft, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        }

        updateFramebufferSize();
    }

    private static void updateFramebufferSize()
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);

        if (minecraft.entityRenderer != null)
        {
            minecraft.entityRenderer.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight);
        }
    }
}
