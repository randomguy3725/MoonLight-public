package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.shader.ShaderUtils;

import java.awt.*;

@ModuleInfo(name = "GlowESP",category = ModuleCategory.Visual)
public class GlowESP extends Module {

    public SliderValue radius = new SliderValue("Radius", 1, 1, 50, 1,this);
    public SliderValue alpha = new SliderValue("Transparency", 1, 1, 255, 1,this);
    public ShaderUtils glow = new ShaderUtils("esp");
    public Framebuffer fbVertical, fbHorizontal;

    @EventTarget
    public void onRender3D(Render3DEvent event){
        mc.gameSettings.ofFastRender = false;

        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        fbVertical = setupFramebuffer(fbVertical);
        fbHorizontal = setupFramebuffer(fbHorizontal);

        fbVertical.bindFramebuffer(true);
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
        for (Entity entity : mc.theWorld.playerEntities) {
            if(entity != mc.thePlayer) {
                if(RenderUtils.isBBInFrustum(entity.getEntityBoundingBox())) {
                    mc.getRenderManager().renderEntitySimple(entity, event.getPartialTicks());
                }
            }
        }

        mc.entityRenderer.setupOverlayRendering();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        fbHorizontal.bindFramebuffer(true);
        glow.init();
        updateUniforms();
        glow.setUniformf("direction", 1, 0);
        drawFramebuffer(fbVertical, new ScaledResolution(mc));

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit + 7);
        drawFramebuffer(fbVertical, new ScaledResolution(mc));
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        mc.getFramebuffer().bindFramebuffer(true);
        glow.setUniformf("direction", 0, 1);
        drawFramebuffer(fbHorizontal, new ScaledResolution(mc));
        OpenGlHelper.glUseProgram(0);
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
    private Framebuffer setupFramebuffer(Framebuffer framebuffer) {
        if (framebuffer != null) {
            framebuffer.deleteFramebuffer();
        }

        framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        return framebuffer;
    }
    private void drawFramebuffer(Framebuffer framebuffer, ScaledResolution scaledResolution) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2d(0, 0);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2d(0, scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2d(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(scaledResolution.getScaledWidth(), 0);
        GL11.glEnd();
    }
    private void updateUniforms() {
        glow.setUniformi("texture", 0);
        glow.setUniformi("texture2", 8);
        glow.setUniformf("texelSize", 1.0F / mc.displayWidth, 1.0F / mc.displayHeight);
        glow.setUniformf("alpha", 3f / 255 * alpha.get());
        glow.setUniformi("radius", (int) radius.get());

        glow.setUniformf("color", new Color(getModule(Interface.class).color(0)).getRed(), new Color(getModule(Interface.class).color(0)).getGreen(), new Color(getModule(Interface.class).color(0)).getBlue());
    }
}
