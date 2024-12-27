/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.utils.render.shader.impl;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.render.GLUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.shader.ShaderUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE;

public class Bloom implements InstanceAccess {
    public static ShaderUtils kawaseDown = new ShaderUtils("kawaseDownBloom");
    public static ShaderUtils kawaseUp = new ShaderUtils("kawaseUpBloom");

    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);


    private static int currentIterations;

    private static final List<Framebuffer> framebufferList = new ArrayList<>();

    private static void initFramebuffers(float iterations) {
        for (Framebuffer framebuffer : framebufferList) {
            framebuffer.deleteFramebuffer();
        }
        framebufferList.clear();

        framebufferList.add(framebuffer = RenderUtils.createFrameBuffer(null, false));

        for (int i = 1; i <= iterations; i++) {
            Framebuffer currentBuffer = new Framebuffer((int) (mc.displayWidth / Math.pow(2, i)), (int) (mc.displayHeight / Math.pow(2, i)), false);
            currentBuffer.setFramebufferFilter(GL_LINEAR);

            GlStateManager.bindTexture(currentBuffer.framebufferTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
            GlStateManager.bindTexture(0);

            framebufferList.add(currentBuffer);
        }
    }


    public static void renderBlur(int framebufferTexture, int iterations, int offset) {
        if (currentIterations != iterations || (framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight)) {
            initFramebuffers(iterations);
            currentIterations = iterations;
        }
        RenderUtils.resetColor();

        RenderUtils.setAlphaLimit(0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_ONE, GL_ONE);

        GL11.glClearColor(0, 0, 0, 0);

        float currentOffset = offset;
        renderFBO(framebufferList.get(1), framebufferTexture, kawaseDown, currentOffset);

        for (int i = 1; i < iterations; i++) {
            currentOffset = offset / (float) Math.pow(1.5, i);
            renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, currentOffset);
        }

        for (int i = iterations; i > 1; i--) {
            currentOffset = offset / (float) Math.pow(1.5, i - 1);
            renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseUp, currentOffset);
        }

        Framebuffer lastBuffer = framebufferList.get(0);
        lastBuffer.framebufferClear();
        lastBuffer.bindFramebuffer(false);
        kawaseUp.init();
        kawaseUp.setUniformf("offset", offset, offset);
        kawaseUp.setUniformi("inTexture", 0);
        kawaseUp.setUniformi("check", 1);
        kawaseUp.setUniformi("textureToCheck", 16);
        kawaseUp.setUniformf("halfpixel", 1.0f / lastBuffer.framebufferWidth, 1.0f / lastBuffer.framebufferHeight);
        kawaseUp.setUniformf("iResolution", lastBuffer.framebufferWidth, lastBuffer.framebufferHeight);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE16);
        RenderUtils.bindTexture(framebufferTexture);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
        RenderUtils.bindTexture(framebufferList.get(1).framebufferTexture);
        ShaderUtils.drawQuads();
        kawaseUp.unload();

        GlStateManager.clearColor(0, 0, 0, 0);
        mc.getFramebuffer().bindFramebuffer(false);
        RenderUtils.bindTexture(framebufferList.get(0).framebufferTexture);
        RenderUtils.setAlphaLimit(0);
        GLUtils.startBlend();
        ShaderUtils.drawQuads();
        GlStateManager.bindTexture(0);
        RenderUtils.setAlphaLimit(0);
        GLUtils.startBlend();
    }

    private static void renderFBO(Framebuffer framebuffer, int framebufferTexture, ShaderUtils shader, float offset) {
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        shader.init();
        RenderUtils.bindTexture(framebufferTexture);
        shader.setUniformf("offset", offset, offset);
        shader.setUniformi("inTexture", 0);
        shader.setUniformi("check", 0);
        shader.setUniformf("halfpixel", 1.0f / framebuffer.framebufferWidth, 1.0f / framebuffer.framebufferHeight);
        shader.setUniformf("iResolution", framebuffer.framebufferWidth, framebuffer.framebufferHeight);
        ShaderUtils.drawQuads();
        shader.unload();
    }
}
