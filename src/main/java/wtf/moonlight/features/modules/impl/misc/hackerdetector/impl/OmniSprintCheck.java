package wtf.moonlight.features.modules.impl.misc.hackerdetector.impl;

import net.minecraft.entity.player.EntityPlayer;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.features.modules.impl.misc.hackerdetector.Check;

public class OmniSprintCheck extends Check {
    @Override
    public String getName() {
        return "Omni Sprint";
    }

    @Override
    public void onPacketReceive(PacketEvent event, EntityPlayer player) {

    }

    @Override
    public void onUpdate(EntityPlayer player) {
        if (player.isSprinting() && (player.moveForward < 0.0f || player.moveForward == 0.0f && player.moveStrafing != 0.0f)) {
            flag(player, "Sprinting when moving backward");
        }
    }
}
