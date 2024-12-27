/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.utils.render.shader.impl;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.shader.ShaderUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform1fv;

public class Shadow implements InstanceAccess {

    public static ShaderUtils bloomShader = new ShaderUtils("shadow");
    public static Framebuffer bloomFramebuffer = new Framebuffer(1, 1, false);
    public static float prevRadius;

    public static void renderBloom(int sourceTexture, int radius, int offset) {
        bloomFramebuffer = RenderUtils.createFrameBuffer(bloomFramebuffer, true);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, 0);

        bloomFramebuffer.framebufferClear();
        bloomFramebuffer.bindFramebuffer(true);
        bloomShader.init();
        setupUniforms(radius, offset, 0);
        glBindTexture(GL_TEXTURE_2D, sourceTexture);
        ShaderUtils.drawQuads();
        bloomShader.unload();
        bloomFramebuffer.unbindFramebuffer();


        mc.getFramebuffer().bindFramebuffer(true);

        bloomShader.init();
        setupUniforms(radius, 0, offset);
        GL13.glActiveTexture(GL13.GL_TEXTURE16);
        glBindTexture(GL_TEXTURE_2D, sourceTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, bloomFramebuffer.framebufferTexture);
        ShaderUtils.drawQuads();
        bloomShader.unload();

        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();

        GlStateManager.bindTexture(0);
    }

    public static void setupUniforms(int radius, int directionX, int directionY) {
        if (radius != prevRadius) {
            final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
            for (int i = 0; i <= radius; i++) {
                weightBuffer.put(MathUtils.calculateGaussianValue(i, radius));
            }
            weightBuffer.rewind();

            bloomShader.setUniformi("inTexture", 0);
            bloomShader.setUniformi("textureToCheck", 16);
            bloomShader.setUniformf("radius", radius);
            glUniform1fv(bloomShader.getUniform("weights"), weightBuffer);
            prevRadius = radius;
        }

        bloomShader.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        bloomShader.setUniformf("direction", directionX, directionY);
    }
}