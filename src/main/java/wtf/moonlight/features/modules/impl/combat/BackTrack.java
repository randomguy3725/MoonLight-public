/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight-public
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.animations.ContinualAnimation;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.packet.PingSpoofComponent;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;

@ModuleInfo(name = "BackTrack", category = ModuleCategory.Combat)
public class BackTrack extends Module {
    private final ModeValue esp = new ModeValue("Mode", new String[]{"Off", "Box"}, "Box", this);
    public final BoolValue cancelClientP = new BoolValue("Cancel Client Packet",false,this);
    public final BoolValue swingCheck = new BoolValue("Swing Check",false,this);;
    private final ModeValue activeMode = new ModeValue("Active Mode", new String[]{"Hit", "Not Hit","Always"}, "Always", this);
    public final BoolValue releaseOnVelocity = new BoolValue("Release On Velocity",false,this);
    public SliderValue minMS = new SliderValue("Min MS", 50, 0, 5000, 5, this);
    public SliderValue maxMS = new SliderValue("Max MS", 200, 0, 5000, 5, this);
    public EntityPlayer target;
    public Vec3 realPosition = new Vec3(0, 0, 0);
    private final ContinualAnimation animatedX = new ContinualAnimation();
    private final ContinualAnimation animatedY = new ContinualAnimation();
    private final ContinualAnimation animatedZ = new ContinualAnimation();
    private int ping;

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(ping + " ms");
    }

    @EventTarget
    public void onMotion(MotionEvent event) {

        if (event.isPost()) {

            if (mc.thePlayer.isDead)
                return;

            target = PlayerUtils.getTarget(9);

            if (target == null)
                return;


            if (swingCheck.get() && !mc.thePlayer.isSwingInProgress)
                return;


            double realDistance = realPosition.distanceTo(mc.thePlayer);
            double clientDistance = target.getDistanceToEntity(mc.thePlayer);

            boolean on = realDistance > clientDistance && realDistance > 2.3 && realDistance < 5.9 && shouldActive(target) && (releaseOnVelocity.get() && mc.thePlayer.hurtTime == 0 || !releaseOnVelocity.get());

            if (on) {
                if (shouldActive(target)) {
                    ping = MathUtils.randomizeInt(minMS.get(), maxMS.get());
                    PingSpoofComponent.spoof(ping, true, true, true, true, cancelClientP.get(), cancelClientP.get());
                } else {
                    PingSpoofComponent.disable();
                    PingSpoofComponent.dispatch();
                }
            } else {
                PingSpoofComponent.disable();
                PingSpoofComponent.dispatch();
            }
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {

        if (event.getState() == PacketEvent.State.INCOMING) {
            final Packet<?> packet = event.getPacket();

            if (target == null) {
                return;
            }

            double realDistance = realPosition.distanceTo(mc.thePlayer);
            double clientDistance = target.getDistanceToEntity(mc.thePlayer);

            boolean on = realDistance > clientDistance && realDistance > 2.3 && realDistance < 5.9;

            if(on) {
                if (packet instanceof S14PacketEntity s14PacketEntity) {
                    if (s14PacketEntity.getEntityId() == target.getEntityId()) {
                        realPosition = realPosition.addVector(s14PacketEntity.getX() / 32.0D, s14PacketEntity.getY() / 32.0D,
                                s14PacketEntity.getZ() / 32.0D);
                    }
                } else if (packet instanceof S18PacketEntityTeleport s18PacketEntityTeleport) {

                    if (s18PacketEntityTeleport.getEntityId() == target.getEntityId()) {
                        realPosition = new Vec3(s18PacketEntityTeleport.getX() / 32D, s18PacketEntityTeleport.getY() / 32D, s18PacketEntityTeleport.getZ() / 32D);
                    }
                }
            } else {
                realPosition = target.getPositionVector();
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (target != null && shouldActive(target)) {
            switch (esp.get()){
                case "Box":
                    double x = realPosition.xCoord - mc.getRenderManager().viewerPosX;
                    double y = realPosition.yCoord - mc.getRenderManager().viewerPosY;
                    double z = realPosition.zCoord - mc.getRenderManager().viewerPosZ;

                    animatedX.animate((float) x, 20);
                    animatedY.animate((float) y, 20);
                    animatedZ.animate((float) z, 20);

                    AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox().expand(0.1D, 0.1, 0.1);
                    AxisAlignedBB axis = new AxisAlignedBB(box.minX - mc.thePlayer.posX + animatedX.getOutput(), box.minY - mc.thePlayer.posY + animatedY.getOutput(), box.minZ - mc.thePlayer.posZ + animatedZ.getOutput(), box.maxX - mc.thePlayer.posX + animatedX.getOutput(), box.maxY - mc.thePlayer.posY + animatedY.getOutput(), box.maxZ - mc.thePlayer.posZ + animatedZ.getOutput());
                    RenderUtils.drawAxisAlignedBB(axis, true, new Color(50, 255, 255, 150).getRGB());
                    break;
            }
        }
    }
    
    public boolean shouldActive(EntityPlayer target){
        return activeMode.is("Always") || activeMode.is("Hit") && target.hurtTime != 0 || activeMode.is("Not Hit") && target.hurtTime == 0;
    }
}