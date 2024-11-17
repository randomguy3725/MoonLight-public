package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.TickEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.MultiBoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.GLUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Camera", category = ModuleCategory.Visual)
public class Camera extends Module {

    public final MultiBoolValue setting = new MultiBoolValue("Option", Arrays.asList(
            new BoolValue("View Clip", true),
            new BoolValue("No Hurt Cam", false),
            new BoolValue("FPS Hurt Cam", false),
            new BoolValue("No Fire", false),
            new BoolValue("Shader Sky", false),
            new BoolValue("Bright Players", false))
            , this);
    public final BoolValue motionCamera = new BoolValue("Motion", false, this);
    public final SliderValue interpolation = new SliderValue("MotionInterpolation", 0.15f, 0.05f, 0.5f, 0.05f, this, motionCamera::get);
    public final BoolValue motionBlur = new BoolValue("Motion Blur", false, this);
    public final SliderValue amount = new SliderValue("Amount", 1, 1, 10, 1, this, motionBlur::get);

    @EventTarget
    public void onTick(TickEvent event) {
        if (mc.theWorld != null) {
            if (motionBlur.get()) {
                if ((mc.entityRenderer.getShaderGroup() == null))
                    mc.entityRenderer.loadShader(new ResourceLocation("minecraft", "shaders/post/motion_blur.json"));
                float uniform = 1F - Math.min(amount.get() / 10F, 0.9f);
                if (mc.entityRenderer.getShaderGroup() != null) {
                    mc.entityRenderer.getShaderGroup().listShaders.get(0).getShaderManager().getShaderUniform("Phosphor").set(uniform, 0F, 0F);
                }
            } else {
                if (mc.entityRenderer.isShaderActive())
                    mc.entityRenderer.stopUseShader();
            }
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (setting.isEnabled("FPS Hurt Cam")) {
            final float hurtTimePercentage = (this.mc.thePlayer.hurtTime - event.getPartialTicks()) / this.mc.thePlayer.maxHurtTime;

            if (hurtTimePercentage > 0.0) {
                glDisable(GL_TEXTURE_2D);
                GLUtils.startBlend();
                glShadeModel(GL_SMOOTH);
                glDisable(GL_ALPHA_TEST);

                final ScaledResolution scaledResolution = event.getScaledResolution();

                final float lineWidth = 20.f;

                glLineWidth(lineWidth);

                final int width = scaledResolution.getScaledWidth();
                final int height = scaledResolution.getScaledHeight();

                final int fadeOutColour = ColorUtils.fadeTo(0x00FF0000, 0xFFFF0000, hurtTimePercentage);

                glBegin(GL_QUADS);
                {
                    // Left
                    RenderUtils.color(fadeOutColour);
                    glVertex2f(0, 0);
                    glVertex2f(0, height);
                    RenderUtils.color(0x00FF0000);
                    glVertex2f(lineWidth, height - lineWidth);
                    glVertex2f(lineWidth, lineWidth);

                    // Right
                    RenderUtils.color(0x00FF0000);
                    glVertex2f(width - lineWidth, lineWidth);
                    glVertex2f(width - lineWidth, height - lineWidth);
                    RenderUtils.color(fadeOutColour);
                    glVertex2f(width, height);
                    glVertex2f(width, 0);

                    // Top
                    RenderUtils.color(fadeOutColour);
                    glVertex2f(0, 0);
                    RenderUtils.color(0x00FF0000);
                    glVertex2d(lineWidth, lineWidth);
                    glVertex2f(width - lineWidth, lineWidth);
                    RenderUtils.color(fadeOutColour);
                    glVertex2f(width, 0);

                    // Bottom
                    RenderUtils.color(0x00FF0000);
                    glVertex2f(lineWidth, height - lineWidth);
                    RenderUtils.color(fadeOutColour);
                    glVertex2d(0, height);
                    glVertex2f(width, height);
                    RenderUtils.color(0x00FF0000);
                    glVertex2f(width - lineWidth, height - lineWidth);
                }
                glEnd();

                glEnable(GL_ALPHA_TEST);
                glShadeModel(GL_FLAT);
                GLUtils.endBlend();
                glEnable(GL_TEXTURE_2D);
            }
        }
    }

}
