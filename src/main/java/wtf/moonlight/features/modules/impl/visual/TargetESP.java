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
package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Entity;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.AttackEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.events.impl.render.Shader2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.combat.KillAura;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.GLUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.glTranslated;

@ModuleInfo(name = "TargetESP", category = ModuleCategory.Visual)
public class TargetESP extends Module {

    private final ModeValue mode = new ModeValue("Mark Mode", new String[]{"Points","Ghost", "Rectangle", "Exhi","Circle"}, "Points", this);
    private final SliderValue circleSpeed = new SliderValue("Circle Speed",2.0F, 1.0F, 5.0F, 0.1F,this,() -> mode.is("Circle"));
    private final BoolValue onlyPlayer = new BoolValue("Only Player",true,this);
    private EntityLivingBase target;
    private final TimerUtils timerUtils = new TimerUtils();
    private final long lastTime = System.currentTimeMillis();
    private final Animation alphaAnim = new DecelerateAnimation(400, 1);
    private final ResourceLocation glowCircle = new ResourceLocation("moonlight/texture/targetesp/glow_circle.png");
    private final ResourceLocation rectangle = new ResourceLocation("moonlight/texture/targetesp/rectangle.png");
    public double prevCircleStep;
    public double circleStep;
    
    @EventTarget
    public void onAttack(AttackEvent event){
        if(event.getTargetEntity() != null && (onlyPlayer.get() && event.getTargetEntity() instanceof EntityPlayer || !onlyPlayer.get())) {
            target = (EntityLivingBase) event.getTargetEntity();
            alphaAnim.setDirection(Direction.FORWARDS);
            timerUtils.reset();
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event){
        if(timerUtils.hasTimeElapsed(100)){
            alphaAnim.setDirection(Direction.BACKWARDS);
            if(alphaAnim.isDone())
                target = null;
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (target != null) {
            if (mode.is("Points"))
                points();

            if (mode.is("Exhi")) {
                int color = this.target.hurtTime > 3 ? new Color(200, 255, 100, 75).getRGB() : this.target.hurtTime < 3 ? new Color(235, 40, 40, 75).getRGB() : new Color(255, 255, 255, 75).getRGB();
                GlStateManager.pushMatrix();
                GL11.glShadeModel(7425);
                GL11.glHint(3154, 4354);
                mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
                double x = target.prevPosX + (target.posX - target.prevPosX) * (double) event.getPartialTicks() - mc.getRenderManager().renderPosX;
                double y = target.prevPosY + (target.posY - target.prevPosY) * (double) event.getPartialTicks() - mc.getRenderManager().renderPosY;
                double z = target.prevPosZ + (target.posZ - target.prevPosZ) * (double) event.getPartialTicks() - mc.getRenderManager().renderPosZ;
                double xMoved = target.posX - target.prevPosX;
                double yMoved = target.posY - target.prevPosY;
                double zMoved = target.posZ - target.prevPosZ;
                double motionX = 0.0;
                double motionY = 0.0;
                double motionZ = 0.0;
                GlStateManager.translate(x + (xMoved + motionX + (mc.thePlayer.motionX + 0.005)), y + (yMoved + motionY + (mc.thePlayer.motionY - 0.002)), z + (zMoved + motionZ + (mc.thePlayer.motionZ + 0.005)));
                AxisAlignedBB axisAlignedBB = target.getEntityBoundingBox();
                RenderUtils.drawAxisAlignedBB(new AxisAlignedBB(axisAlignedBB.minX - 0.1 - target.posX, axisAlignedBB.minY - 0.1 - target.posY, axisAlignedBB.minZ - 0.1 - target.posZ, axisAlignedBB.maxX + 0.1 - target.posX, axisAlignedBB.maxY + 0.2 - target.posY, axisAlignedBB.maxZ + 0.1 - target.posZ), true, color);
                GlStateManager.popMatrix();
            }

            if (mode.is("Ghost")) {
                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.shadeModel(7425);
                GlStateManager.disableCull();
                GlStateManager.disableAlpha();
                GlStateManager.tryBlendFuncSeparate(770, 1, 0, 1);
                double radius = 0.67;
                float speed = 45;
                float size = 0.4f;
                double distance = 19;
                int lenght = 20;

                Vec3 interpolated = MathUtils.interpolate(new Vec3(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ), target.getPositionVector(), event.getPartialTicks());
                interpolated.yCoord += 0.75f;

                RenderUtils.setupOrientationMatrix(interpolated.xCoord, interpolated.yCoord + 0.5f, interpolated.zCoord);

                float[] idk = new float[]{mc.getRenderManager().playerViewY, mc.getRenderManager().playerViewX};

                GL11.glRotated(-idk[0], 0.0, 1.0, 0.0);
                GL11.glRotated(idk[1], 1.0, 0.0, 0.0);

                for (int i = 0; i < lenght; i++) {
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = Math.sin(angle) * radius;
                    double c = Math.cos(angle) * radius;
                    GlStateManager.translate(s, (c), -c);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    int color = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(0)), (float) alphaAnim.getOutput()).getRGB();
                    RenderUtils.drawImage(glowCircle, 0f, 0f, -size, -size, color);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    GlStateManager.translate(-(s), -(c), (c));
                }
                for (int i = 0; i < lenght; i++) {
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = Math.sin(angle) * radius;
                    double c = Math.cos(angle) * radius;
                    GlStateManager.translate(-s, s, -c);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    int color = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(0)), (float) alphaAnim.getOutput()).getRGB();
                    RenderUtils.drawImage(glowCircle, 0f, 0f, -size, -size, color);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    GlStateManager.translate((s), -(s), (c));
                }
                for (int i = 0; i < lenght; i++) {
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = Math.sin(angle) * radius;
                    double c = Math.cos(angle) * radius;
                    GlStateManager.translate(-(s), -(s), (c));
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    int color = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(0)), (float) alphaAnim.getOutput()).getRGB();
                    RenderUtils.drawImage(glowCircle, 0f, 0f, -size, -size, color);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    GlStateManager.translate((s), (s), -(c));
                }
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.disableBlend();
                GlStateManager.enableCull();
                GlStateManager.enableAlpha();
                GlStateManager.depthMask(true);
                GlStateManager.popMatrix();
            }

            if (mode.is("Circle")) {
                prevCircleStep = circleStep;
                circleStep += (double) this.circleSpeed.get() * RenderUtils.deltaTime();
                float eyeHeight = target.getEyeHeight();
                if (target.isSneaking()) {
                    eyeHeight -= 0.2F;
                }

                double cs = prevCircleStep + (circleStep - prevCircleStep) * (double) mc.timer.renderPartialTicks;
                double prevSinAnim = Math.abs(1.0D + Math.sin(cs - 0.5D)) / 2.0D;
                double sinAnim = Math.abs(1.0D + Math.sin(cs)) / 2.0D;
                double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
                double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY + prevSinAnim * (double) eyeHeight;
                double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
                double nextY = target.lastTickPosY + (target.posY - target.lastTickPosY) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY + sinAnim * (double) eyeHeight;
                GL11.glPushMatrix();
                GL11.glDisable(2884);
                GL11.glDisable(3553);
                GL11.glEnable(3042);
                GL11.glDisable(2929);
                GL11.glDisable(3008);
                GL11.glShadeModel(7425);
                GL11.glBegin(8);

                int i;
                Color color;
                for (i = 0; i <= 360; ++i) {
                    color = new Color(getModule(Interface.class).color(i));
                    GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, 0.6F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double) target.width * 0.8D, nextY, z + Math.sin(Math.toRadians(i)) * (double) target.width * 0.8D);
                    GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, 0.01F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double) target.width * 0.8D, y, z + Math.sin(Math.toRadians(i)) * (double) target.width * 0.8D);
                }

                GL11.glEnd();
                GL11.glEnable(2848);
                GL11.glBegin(2);

                for (i = 0; i <= 360; ++i) {
                    color = new Color(getModule(Interface.class).color(i));
                    GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, 0.8F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double) target.width * 0.8D, nextY, z + Math.sin(Math.toRadians(i)) * (double) target.width * 0.8D);
                }

                GL11.glEnd();
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glEnable(3008);
                GL11.glEnable(2929);
                GL11.glShadeModel(7424);
                GL11.glDisable(3042);
                GL11.glEnable(2884);
                GL11.glPopMatrix();
                GlStateManager.resetColor();
            }
        }
    }
    @EventTarget
    public void onRender2D(Render2DEvent event) {
        int index = 3;
        if (mode.is("Rectangle") && target != null) {
            float dst = mc.thePlayer.getSmoothDistanceToEntity(target);
            drawTargetESP2D(Objects.requireNonNull(targetESPSPos(target))[0], Objects.requireNonNull(targetESPSPos(target))[1],
                    (1.0f - MathHelper.clamp_float(Math.abs(dst - 6.0f) / 60.0f, 0.0f, 0.75f)) * 1, index);
        }
    }

    @EventTarget
    public void onShader2D(Shader2DEvent event) {
        if (event.getShaderType() == Shader2DEvent.ShaderType.GLOW) {
            int index = 3;
            if (mode.is("Rectangle") && target != null) {
                float dst = mc.thePlayer.getSmoothDistanceToEntity(target);
                drawTargetESP2D(Objects.requireNonNull(targetESPSPos(target))[0], Objects.requireNonNull(targetESPSPos(target))[1],
                        (1.0f - MathHelper.clamp_float(Math.abs(dst - 6.0f) / 60.0f, 0.0f, 0.75f)) * 1, index);
            }
        }
    }

    private void points() {
        if (target != null) {
            double markerX = MathUtils.interporate(mc.timer.renderPartialTicks, target.lastTickPosX, target.posX);
            double markerY = MathUtils.interporate(mc.timer.renderPartialTicks, target.lastTickPosY, target.posY) + target.height / 1.6f;
            double markerZ = MathUtils.interporate(mc.timer.renderPartialTicks, target.lastTickPosZ, target.posZ);
            float time = (float) ((((System.currentTimeMillis() - lastTime) / 1500F)) + (Math.sin((((System.currentTimeMillis() - lastTime) / 1500F))) / 10f));
            float alpha = ((Shaders.shaderPackLoaded ? 1 : 0.5f) * 1);
            float pl = 0;
            boolean fa = false;
            for (int iteration = 0; iteration < 3; iteration++) {
                for (float i = time * 360; i < time * 360 + 90; i += 2) {
                    float max = time * 360 + 90;
                    float dc = MathUtils.normalize(i, time * 360 - 45, max);
                    float rf = 0.6f;
                    double radians = Math.toRadians(i);
                    double plY = pl + Math.sin(radians * 1.2f) * 0.1f;
                    int firstColor = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(0)), (float) alphaAnim.getOutput()).getRGB();
                    int secondColor = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(90)), (float) alphaAnim.getOutput()).getRGB();
                    GlStateManager.pushMatrix();
                    RenderUtils.setupOrientationMatrix(markerX, markerY, markerZ);

                    float[] idk = new float[]{mc.getRenderManager().playerViewY, mc.getRenderManager().playerViewX};

                    GL11.glRotated(-idk[0], 0.0, 1.0, 0.0);
                    GL11.glRotated(idk[1], 1.0, 0.0, 0.0);

                    GlStateManager.depthMask(false);
                    float q = (!fa ? 0.25f : 0.15f) * (Math.max(fa ? 0.25f : 0.15f, fa ? dc : (1f + (0.4f - dc)) / 2f) + 0.45f);
                    float size = q * (2f + ((0.5f - alpha) * 2));
                    RenderUtils.drawImage(
                            glowCircle,
                            Math.cos(radians) * rf - size / 2f,
                            plY - 0.7,
                            Math.sin(radians) * rf - size / 2f, size, size,
                            firstColor,
                            secondColor,
                            secondColor,
                            firstColor);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GlStateManager.depthMask(true);
                    GlStateManager.popMatrix();
                }
                time *= -1.025f;
                fa = !fa;
                pl += 0.45f;
            }
        }
    }

    private void drawTargetESP2D(float x, float y, float scale, int index) {
        long millis = System.currentTimeMillis() + index * 400L;
        double angle = MathHelper.clamp_double((Math.sin(millis / 150.0) + 1.0) / 2.0 * 30.0, 0.0, 30.0);
        double scaled = MathHelper.clamp_double((Math.sin(millis / 500.0) + 1.0) / 2.0, 0.8, 1.0);
        double rotate = MathHelper.clamp_double((Math.sin(millis / 1000.0) + 1.0) / 2.0 * 360.0, 0.0, 360.0);
        int color = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(0)), (float) alphaAnim.getOutput()).getRGB();
        int color2 = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(90)), (float) alphaAnim.getOutput()).getRGB();
        int color3 = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(180)), (float) alphaAnim.getOutput()).getRGB();
        int color4 = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(270)), (float) alphaAnim.getOutput()).getRGB();

        rotate = 45 - (angle - 15.0) + rotate;
        float size = 128.0f * scale * (float) scaled;
        float x2 = (x -= size / 2.0f) + size;
        float y2 = (y -= size / 2.0f) + size;
        GlStateManager.pushMatrix();
        RenderUtils.customRotatedObject2D(x, y, size, size, (float) rotate);
        GL11.glDisable(3008);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425);
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        RenderUtils.drawImage(rectangle, x, y, x2, y2, color, color2, color3, color4);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.resetColor();
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GL11.glEnable(3008);
        GlStateManager.popMatrix();
    }

    private float[] targetESPSPos(EntityLivingBase entity) {
        EntityRenderer entityRenderer = mc.entityRenderer;
        float partialTicks = mc.timer.renderPartialTicks;
        double x = MathUtils.interpolate(entity.prevPosX, entity.posX, partialTicks);
        double y = MathUtils.interpolate(entity.prevPosY, entity.posY, partialTicks);
        double z = MathUtils.interpolate(entity.prevPosZ, entity.posZ, partialTicks);
        double height = entity.height / (entity.isChild() ? 1.75f : 1.0f) / 2.0f;
        AxisAlignedBB bb = new AxisAlignedBB(x - 0.0, y, z - 0.0, x + 0.0, y + height, z + 0.0);
        final double[][] vectors = {{bb.minX, bb.minY, bb.minZ},
                {bb.minX, bb.maxY, bb.minZ},
                {bb.minX, bb.maxY, bb.maxZ},
                {bb.minX, bb.minY, bb.maxZ},
                {bb.maxX, bb.minY, bb.minZ},
                {bb.maxX, bb.maxY, bb.minZ},
                {bb.maxX, bb.maxY, bb.maxZ},
                {bb.maxX, bb.minY, bb.maxZ}};
        entityRenderer.setupCameraTransform(partialTicks, 0);
        float[] projection;
        final float[] position = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F};
        for (final double[] vec : vectors) {
            projection = GLUtils.project2D((float) (vec[0] - mc.getRenderManager().viewerPosX), (float) (vec[1] - mc.getRenderManager().viewerPosY), (float) (vec[2] - mc.getRenderManager().viewerPosZ), new ScaledResolution(mc).getScaleFactor());
            if (projection != null && projection[2] >= 0.0F && projection[2] < 1.0F) {
                position[0] = Math.min(projection[0], position[0]);
                position[1] = Math.min(projection[1], position[1]);
                position[2] = Math.max(projection[0], position[2]);
                position[3] = Math.max(projection[1], position[3]);
            }
        }
        entityRenderer.setupOverlayRendering();
        return new float[]{position[0], position[1]};
    }
}
