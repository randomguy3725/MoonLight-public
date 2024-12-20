package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.shader.ShaderUtils;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.renderer.OpenGlHelper.glUniform1;

@ModuleInfo(name = "GlowESP",category = ModuleCategory.Visual)
public class GlowESP extends Module {

    private final SliderValue exposure = new SliderValue("Exposure", 2.2f, .5f, 3.5f, .1f,this);
    public SliderValue radius = new SliderValue("Radius", 4, 2, 30, 1,this);
    private final ShaderUtils outlineShader = new ShaderUtils("outline");
    private final ShaderUtils glowShader = new ShaderUtils("glow");

    public Framebuffer framebuffer;
    public Framebuffer outlineFrameBuffer;
    public Framebuffer glowFrameBuffer;

    private List<EntityPlayer> livingEntities = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();
    }

    public void createFrameBuffers() {
        framebuffer = RenderUtils.createFrameBuffer(framebuffer, true);
        outlineFrameBuffer = RenderUtils.createFrameBuffer(outlineFrameBuffer, true);
        glowFrameBuffer = RenderUtils.createFrameBuffer(glowFrameBuffer, true);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        createFrameBuffers();
        collectEntities();
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        renderEntities(event.getPartialTicks());
        framebuffer.unbindFramebuffer();
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.disableLighting();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {

        ScaledResolution sr = new ScaledResolution(mc);
        if (framebuffer != null && outlineFrameBuffer != null && !livingEntities.isEmpty()) {
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.0f);
            GlStateManager.enableBlend();
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            outlineFrameBuffer.framebufferClear();
            outlineFrameBuffer.bindFramebuffer(true);
            outlineShader.init();
            setupOutlineUniforms(0, 1);
            RenderUtils.bindTexture(framebuffer.framebufferTexture);
            ShaderUtils.drawQuads();
            outlineShader.init();
            setupOutlineUniforms(1, 0);
            RenderUtils.bindTexture(framebuffer.framebufferTexture);
            ShaderUtils.drawQuads();
            outlineShader.unload();
            outlineFrameBuffer.unbindFramebuffer();

            GlStateManager.color(1, 1, 1, 1);
            glowFrameBuffer.framebufferClear();
            glowFrameBuffer.bindFramebuffer(true);
            glowShader.init();
            setupGlowUniforms(1, 0);
            RenderUtils.bindTexture(outlineFrameBuffer.framebufferTexture);
            ShaderUtils.drawQuads();
            glowShader.unload();
            glowFrameBuffer.unbindFramebuffer();

            mc.getFramebuffer().bindFramebuffer(true);
            glowShader.init();
            setupGlowUniforms(0, 1);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            RenderUtils.bindTexture(glowFrameBuffer.framebufferTexture);
            ShaderUtils.drawQuads();
            glowShader.unload();

        }
    }

    public void setupGlowUniforms(float dir1, float dir2) {
        Color color = getColor();
        glowShader.setUniformi("texture", 0);
        glowShader.setUniformf("radius", radius.get());
        glowShader.setUniformf("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        glowShader.setUniformf("direction", dir1, dir2);
        glowShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        glowShader.setUniformf("exposure", exposure.get());
        glowShader.setUniformi("avoidTexture", 0);

        final FloatBuffer buffer = BufferUtils.createFloatBuffer(256);
        for (int i = 1; i <= radius.get(); i++) {
            buffer.put(MathUtils.calculateGaussianValue(i, radius.get() / 2));
        }
        buffer.rewind();

        glUniform1(glowShader.getUniform("weights"), buffer);
    }


    public void setupOutlineUniforms(float dir1, float dir2) {
        Color color = getColor();
        outlineShader.setUniformi("texture", 0);
        outlineShader.setUniformf("radius", radius.get() / 1.5f);
        outlineShader.setUniformf("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        outlineShader.setUniformf("direction", dir1, dir2);
        outlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    public void renderEntities(float ticks) {
        livingEntities.forEach(entity -> {
            mc.getRenderManager().renderEntityStaticNoShadow(entity, ticks, false);
        });
    }

    public void collectEntities() {
        livingEntities.clear();
        livingEntities = PlayerUtils.getLivingEntities(entity -> RenderUtils.isBBInFrustum(entity) || entity == mc.thePlayer && mc.gameSettings.thirdPersonView != 0);
    }

    private Color getColor() {
        return new Color(getModule(Interface.class).color());
    }
}
