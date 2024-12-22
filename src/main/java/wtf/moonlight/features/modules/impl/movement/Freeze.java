package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.joml.Vector2f;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.utils.packet.PacketUtils;

@ModuleInfo(name = "Freeze", category = ModuleCategory.Movement)
public class Freeze extends Module {
    private double x;
    private double y;
    private double z;
    private boolean onGround = false;
    private Vector2f rotation;

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) {
            return;
        }
        this.onGround = mc.thePlayer.onGround;
        this.x = mc.thePlayer.posX;
        this.y = mc.thePlayer.posY;
        this.z = mc.thePlayer.posZ;
        this.rotation = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        final float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float gcd = f * f * f * 1.2f;
        final Vector2f rotation = this.rotation;
        rotation.x -= this.rotation.x % gcd;
        final Vector2f rotation2 = this.rotation;
        rotation2.y -= this.rotation.y % gcd;
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            final Vector2f current = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            final float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            final float gcd = f * f * f * 1.2f;
            current.x -= current.x % gcd;
            current.y -= current.y % gcd;
            if (this.rotation.equals(current)) {
                return;
            }
            this.rotation = current;
            event.setCancelled(true);
            sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.x, current.y, this.onGround));
            sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
        }
        if (event.getPacket() instanceof C03PacketPlayer) {
            event.setCancelled(true);
        }

        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            toggle();
        }
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        mc.thePlayer.motionX = 0.0;
        mc.thePlayer.motionY = 0.0;
        mc.thePlayer.motionZ = 0.0;
        mc.thePlayer.setPosition(this.x, this.y, this.z);
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        setEnabled(false);
    }

    public static void throwPearl(final Vector2f current) {
        if (!Moonlight.INSTANCE.getModuleManager().getModule(Freeze.class).isEnabled()) {
            return;
        }
        mc.thePlayer.rotationYaw = current.x;
        mc.thePlayer.rotationPitch = current.y;
        final float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float gcd = f * f * f * 1.2f;
        current.x -= current.x % gcd;
        current.y -= current.y % gcd;
        if (!Moonlight.INSTANCE.getModuleManager().getModule(Freeze.class).rotation.equals(current)) {
            PacketUtils.sendPacket(new C03PacketPlayer.C05PacketPlayerLook(current.x, current.y, Moonlight.INSTANCE.getModuleManager().getModule(Freeze.class).onGround));
        }
        Moonlight.INSTANCE.getModuleManager().getModule(Freeze.class).rotation = current;
        PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
    }
}