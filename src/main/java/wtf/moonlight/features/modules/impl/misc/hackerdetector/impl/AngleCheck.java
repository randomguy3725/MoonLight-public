/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.features.modules.impl.misc.hackerdetector.impl;

import net.minecraft.entity.player.EntityPlayer;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.features.modules.impl.misc.hackerdetector.Check;

public class AngleCheck extends Check {

    @Override
    public void onUpdate(EntityPlayer player) {
        if (Math.abs(player.rotationYaw - player.prevRotationYaw) > 50 && player.swingProgress != 0F) {
            flag(player, "Too fast rotate speed");
        }

        if (player.rotationPitch > 90 || player.rotationPitch < -90) {
            flag(player, "Invalid rotation pitch");
        }
    }

    @Override
    public String getName() {
        return "Angle";
    }

    @Override
    public void onPacketReceive(PacketEvent event, EntityPlayer player) {

    }
}
