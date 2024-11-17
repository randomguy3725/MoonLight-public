package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.AttackEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.combat.KillAura;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.util.ArrayList;

@ModuleInfo(name = "HitBubbles", category = ModuleCategory.Visual)
public class HitBubbles extends Module {
    static final ArrayList<Bubble> bubbles;
    private final Tessellator tessellator = Tessellator.getInstance();
    private final WorldRenderer buffer = this.tessellator.getWorldRenderer();
    private final ResourceLocation BUBBLE_TEXTURE = new ResourceLocation("moonlight/texture/hitbubble/bubble.png");

    private float getAlphaPC() {
        return 1f;
    }

    private static float getMaxTime() {
        return 1000.0f;
    }

    @EventTarget
    public void onAttack(AttackEvent event) {
        if (event.getTargetEntity() instanceof EntityLivingBase base && getModule(KillAura.class).target == null) {
            if (!base.isEntityAlive()) {
                return;
            }
            Vec3 to = base.getPositionVector().addVector(0.0, base.height / 1.6f, 0.0);
            addBubble(to);
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (getModule(KillAura.class).target != null) {
            if (getModule(KillAura.class).target.hurtTime == 9) {
                Vec3 to = getModule(KillAura.class).target.getPositionVector().addVector(0.0, getModule(KillAura.class).target.height / 1.6f, 0.0);
                addBubble(to);
            }
        }
    }

    private static void addBubble(Vec3 addToCoord) {
        RenderManager manager = mc.getRenderManager();
        bubbles.add(new Bubble(manager.playerViewX, -manager.playerViewY, addToCoord));
    }

    private void setupDrawsBubbles3D(Runnable render) {
        RenderManager manager = mc.getRenderManager();
        Vec3 conpense = new Vec3(manager.renderPosX, manager.renderPosY, manager.renderPosZ);
        boolean light = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GlStateManager.pushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
        if (light)
            GlStateManager.disableLighting();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.tryBlendFuncSeparate(770, 32772, 1, 0);
        GL11.glTranslated(-conpense.xCoord, -conpense.yCoord, -conpense.zCoord);
        mc.getTextureManager().bindTexture(this.BUBBLE_TEXTURE);
        render.run();

        GL11.glTranslated(conpense.xCoord, conpense.yCoord, conpense.zCoord);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.resetColor();
        GL11.glShadeModel(GL11.GL_FLAT);
        if (light)
            GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    private void drawBubble(Bubble bubble, float alphaPC) {
        GL11.glPushMatrix();
        GL11.glTranslated(bubble.pos.xCoord, bubble.pos.yCoord, bubble.pos.zCoord);
        float extS = bubble.getDeltaTime();
        GlStateManager.translate(-Math.sin(Math.toRadians(bubble.viewPitch)) * (double) extS / 3.0, Math.sin(Math.toRadians(bubble.viewYaw)) * (double) extS / 2.0, -Math.cos(Math.toRadians(bubble.viewPitch)) * (double) extS / 3.0);
        GL11.glNormal3d(1.0, 1.0, 1.0);
        GL11.glRotated(bubble.viewPitch, 0.0, 1.0, 0.0);
        GL11.glRotated(bubble.viewYaw, mc.gameSettings.thirdPersonView == 2 ? -1.0 : 1.0, 0.0, 0.0);
        GL11.glScaled(-0.1, -0.1, 0.1);
        this.drawBeginsNullCoord(bubble, alphaPC);
        GL11.glPopMatrix();
    }

    private void drawBeginsNullCoord(Bubble bubble, float alphaPC) {
        float r = 50.0f * bubble.getDeltaTime() * (1.0f - bubble.getDeltaTime());
        int speedRotate = 3;
        float III = (float) (System.currentTimeMillis() % (long) (3600 / speedRotate)) / 10.0f * (float) speedRotate;
        RenderUtils.customRotatedObject2D(-1.0f, -1.0f, 2.0f, 2.0f, -III);
        this.buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        this.buffer.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(ColorUtils.applyOpacity(getModule(Interface.class).color(0), alphaPC)).endVertex();
        this.buffer.pos(0.0, r, 0.0).tex(0.0, 1.0).color(ColorUtils.applyOpacity(getModule(Interface.class).color(90), alphaPC)).endVertex();
        this.buffer.pos(r, r, 0.0).tex(1.0, 1.0).color(ColorUtils.applyOpacity(getModule(Interface.class).color(180), alphaPC)).endVertex();
        this.buffer.pos(r, 0.0, 0.0).tex(1.0, 0.0).color(ColorUtils.applyOpacity(getModule(Interface.class).color(270), alphaPC)).endVertex();
        GlStateManager.blendFunc(770, 772);
        GlStateManager.translate(-r / 2.0f, -r / 2.0f, 0.0f);
        GlStateManager.shadeModel(7425);
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        this.tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.translate(r / 2.0f, r / 2.0f, 0.0f);
        GlStateManager.blendFunc(770, 771);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        float aPC = this.getAlphaPC();
        if ((double) aPC < 0.05) {
            return;
        }
        if (bubbles.isEmpty()) {
            return;
        }
        this.removeAuto();
        this.setupDrawsBubbles3D(() -> bubbles.forEach(bubble -> {
            if (bubble != null && bubble.getDeltaTime() <= 1.0f) {
                this.drawBubble(bubble, aPC);
            }
        }));
    }

    private void removeAuto() {
        bubbles.removeIf(bubble -> bubble.getDeltaTime() >= 1.0f);
    }

    static {
        bubbles = new ArrayList<>();
    }

    private static final class Bubble {
        Vec3 pos;
        long time = System.currentTimeMillis();
        float maxTime = getMaxTime();
        float viewYaw;
        float viewPitch;

        public Bubble(float viewYaw, float viewPitch, Vec3 pos) {
            this.viewYaw = viewYaw;
            this.viewPitch = viewPitch;
            this.pos = pos;
        }

        private float getDeltaTime() {
            return (float) (System.currentTimeMillis() - this.time) / this.maxTime;
        }
    }
}