package wtf.moonlight.features.modules.impl.misc.hackerdetector.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.features.modules.impl.misc.hackerdetector.Check;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.player.MovementUtils;

public class LegitScaffoldCheck extends Check {
    private final TimerUtils timer = new TimerUtils();
    private int sneakFlag;

    @Override
    public String getName() {
        return "Legit Scaffold";
    }

    @Override
    public void onPacketReceive(PacketEvent event, EntityPlayer player) {

    }

    @Override
    public void onUpdate(EntityPlayer player) {
        if (player.isSneaking()) {
            timer.reset();
            sneakFlag += 1;
        }

        if (timer.hasTimeElapsed(140)) {
            sneakFlag = 0;
        }
        if (player.rotationPitch > 75 && player.rotationPitch < 90 && player.isSwingInProgress) {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock) {
                if (MovementUtils.getSpeed(player) >= 0.10 && player.onGround && sneakFlag > 5) {
                    flag(player, "Sneak too fast");
                }
                if (MovementUtils.getSpeed(player) >= 0.21 && !player.onGround && sneakFlag > 5) {
                    flag(player, "Sneak too fast");
                }
            }
        }
    }


}

