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
import net.minecraft.item.ItemBlock;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.features.modules.impl.misc.hackerdetector.Check;

public class ScaffoldCheck extends Check {

    private float yaw;
    private float cacheYaw;
    private boolean rotate;

    @Override
    public String getName() {
        return "Scaffold";
    }

    @Override
    public void onPacketReceive(PacketEvent event, EntityPlayer player) {

    }

    @Override
    public void onUpdate(EntityPlayer player) {
        cacheYaw = yaw;
        yaw = player.rotationYaw;
        if (cacheYaw == yaw + 180) {
            rotate = true;
        }
        if (player.isSwingInProgress && player.rotationPitch > 70 && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock && !player.isSneaking() && rotate) {
            flag(player, "Scaffold");
            rotate = false;
        }
    }


}

