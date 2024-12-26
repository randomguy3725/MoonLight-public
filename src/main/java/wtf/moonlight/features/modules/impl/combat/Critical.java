package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.movement.Freeze;
import wtf.moonlight.features.modules.impl.movement.Speed;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.utils.player.MovementUtils;

@ModuleInfo(name = "Critical", category = ModuleCategory.Combat)
public class Critical extends Module {
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Jump", "AutoFreeze", "AutoSpeed"}, "Jump", this);
    private boolean attacking;
    public static boolean stuckEnabled;

    @Override
    public void onEnable() {
        stuckEnabled = false;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());
        switch (mode.get()) {
            case "GrimAC":
                if (mc.thePlayer.onGround && attacking) {
                    attacking = false;
                }
                break;
            case "AutoFreeze":
                if (getModule(KillAura.class).target != null && mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
                if (mc.thePlayer.fallDistance > 0) {
                    getModule(Freeze.class).setEnabled(true);
                    stuckEnabled = true;
                }
                if (getModule(KillAura.class).target == null && stuckEnabled) {
                    getModule(Freeze.class).setEnabled(false);
                    stuckEnabled = false;
                }
                break;
            case "AutoSpeed":
                if (getModule(KillAura.class).target != null) {
                    if (isDisabled(Speed.class)) {
                        getModule(Speed.class).setEnabled(true);
                    } else {
                        if (!MovementUtils.isMoving() && mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                        }
                    }
                } else {
                    if (isEnabled(Speed.class)) {
                        getModule(Speed.class).setEnabled(false);
                    }
                }
                break;
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        switch (mode.get()) {
            case "GrimAC":
                if (packet instanceof C02PacketUseEntity) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                    attacking = true;
                }
                break;
        }
    }
}
