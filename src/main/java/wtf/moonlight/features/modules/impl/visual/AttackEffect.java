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

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.combat.KillAura;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static wtf.moonlight.utils.render.RenderUtils.drawFilledCircleNoGL;
import static wtf.moonlight.utils.render.RenderUtils.glDrawTriangle;

@ModuleInfo(name = "AttackEffect", category = ModuleCategory.Visual)
public class AttackEffect extends Module {

    public final ModeValue modeValue = new ModeValue("Mode", new String[]{"Triangle", "Circle"}, "Triangle", this);
    private final SliderValue amount = new SliderValue("Amount", 3, 0, 10, this);
    final BoolValue physics = new BoolValue("Physics", true, this);
    private final List<AttackParticle> particles = new LinkedList<>();
    private final TimerUtils timer = new TimerUtils();

    @EventTarget
    private void onUpdate(UpdateEvent event) {

        KillAura killAura = getModule(KillAura.class);
        if (killAura.isEnabled()) {
            if (killAura.target.hurtTime != 0) {
                for (int i = 1; i < amount.get(); ++i) {
                    particles.add(new AttackParticle(new Vec3(killAura.target.posX + (Math.random() - 0.5) * 0.5, killAura.target.posY + Math.random() + 0.5, killAura.target.posZ + (Math.random() - 0.5) * 0.5)));
                }
            }
        }
    }

    @EventTarget
    private void onRender3D(Render3DEvent event) {
        if (particles.isEmpty()) {
            return;
        }
        int i = 0;
        while ((double) i <= (double) timer.getTime() / 1.0E11) {
            if (physics.get())
                particles.forEach(AttackParticle::update);
            else
                particles.forEach(AttackParticle::updateWithoutPhysics);
            ++i;
        }
        particles.removeIf(particle -> mc.thePlayer.getDistanceSq(particle.position.xCoord, particle.position.yCoord, particle.position.zCoord) > 300.0);
        timer.reset();
        renderParticles(particles);
    }

    public void renderParticles(List<AttackParticle> particles) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        long currentMillis = System.currentTimeMillis();
        int i = 0;
        int count = 0;
        for (AttackParticle particle : particles) {
            Vec3 v = particle.position;
            boolean draw = true;
            float aOffset = (float) ((currentMillis + (long) (++i) * 100L) % 2000L) / 1000.0f;
            double x = v.xCoord - mc.getRenderManager().renderPosX;
            double y = v.yCoord - mc.getRenderManager().renderPosY;
            double z = v.zCoord - mc.getRenderManager().renderPosZ;
            double distanceFromPlayer = mc.thePlayer.getDistance(v.xCoord, v.yCoord - 1.0, v.zCoord);
            Color color = new Color(getModule(Interface.class).color(count));
            int quality = (int) (distanceFromPlayer * 4.0 + 10.0);
            if (quality > 350) {
                quality = 350;
            }
            if (!RenderUtils.isBBInFrustum(new EntityEgg(mc.theWorld, v.xCoord, v.yCoord, v.zCoord).getEntityBoundingBox())) {
                draw = false;
            }
            if (i % 10 != 0 && distanceFromPlayer > 25.0) {
                draw = false;
            }
            if (i % 3 == 0 && distanceFromPlayer > 15.0) {
                draw = false;
            }
            if (!draw) continue;
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            GL11.glScalef(-0.04f, -0.04f, -0.04f);
            GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0, 1.0, 0.0);
            GL11.glRotated(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0 : 1.0, 0.0, 0.0);
            if (modeValue.get().equals("Triangle")) {
                glDrawTriangle(0.0, -1.5, -1.0, 0.0, 1.0, 0.0, color.hashCode());
                if (distanceFromPlayer < 4.0) {
                    glDrawTriangle(0.0, -1.5, -1.0, 0.0, 1.0, 0.0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 50).hashCode());
                }
                if (distanceFromPlayer < 20.0) {
                    glDrawTriangle(0.0, -1.5, -1.0, 0.0, 1.0, 0.0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 30).hashCode());
                }
            }
            if (Objects.equals(modeValue.get(), "Circle")) {
                drawFilledCircleNoGL(0, 0, 0.7, color.hashCode(), quality);

                if (distanceFromPlayer < 4)
                    drawFilledCircleNoGL(0, 0, 1.4, new Color(color.getRed(), color.getGreen(), color.getBlue(), 50).hashCode(), quality);

                if (distanceFromPlayer < 20)
                    drawFilledCircleNoGL(0, 0, 2.3, new Color(color.getRed(), color.getGreen(), color.getBlue(), 30).hashCode(), quality);
            }
            GL11.glScalef(0.8f, 0.8f, 0.8f);
            GL11.glPopMatrix();
            count += 15;
        }

        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glColor3d(255.0, 255.0, 255.0);
    }


    public static class AttackParticle {
        private final TimerUtils removeTimer = new TimerUtils();
        public final Vec3 position;
        private final Vec3 delta;

        public AttackParticle(Vec3 position) {
            this.position = position;
            this.delta = new Vec3((Math.random() * 2.5 - 1.25) * 0.04 + 0, (Math.random() * 0.5 - 0.2) * 0.04 + 0, (Math.random() * 2.5 - 1.25) * 0.04 + 0);
            this.removeTimer.reset();
        }

        public void update() {
            Block block3;
            Block block2;
            Block block1 = getBlock(position.xCoord, position.yCoord, position.zCoord + delta.zCoord);
            if (!(block1 instanceof BlockAir || block1 instanceof BlockBush || block1 instanceof BlockLiquid)) {
                delta.zCoord *= -0.8;
            }
            if (!((block2 = getBlock(position.xCoord, position.yCoord + delta.yCoord, position.zCoord)) instanceof BlockAir || block2 instanceof BlockBush || block2 instanceof BlockLiquid)) {
                delta.xCoord *= 0.99f;
                delta.zCoord *= 0.99f;
                delta.yCoord *= -0.5;
            }
            if (!((block3 = getBlock(position.xCoord + delta.xCoord, position.yCoord, position.zCoord)) instanceof BlockAir || block3 instanceof BlockBush || block3 instanceof BlockLiquid)) {
                delta.xCoord *= -0.8;
            }
            updateWithoutPhysics();
        }

        public void updateWithoutPhysics() {
            position.xCoord += delta.xCoord;
            position.yCoord += delta.yCoord;
            position.zCoord += delta.zCoord;
            delta.xCoord *= 0.998f;
            delta.yCoord -= 3.1E-5;
            delta.zCoord *= 0.998f;
        }

        public static Block getBlock(double offsetX, double offsetY, double offsetZ) {
            return Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(offsetX, offsetY, offsetZ)).getBlock();
        }
    }
}
