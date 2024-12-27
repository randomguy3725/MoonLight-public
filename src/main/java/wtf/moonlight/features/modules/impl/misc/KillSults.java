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
package wtf.moonlight.features.modules.impl.misc;

import net.minecraft.entity.player.EntityPlayer;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.player.AttackEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;

import java.util.concurrent.ThreadLocalRandom;

@ModuleInfo(name = "KillSults", category = ModuleCategory.Misc)
public class KillSults extends Module {
    private EntityPlayer currentTarget;

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if (currentTarget.isDead && !mc.thePlayer.isDead && !mc.thePlayer.isSpectator()) {
            sendMessage(currentTarget.getName());
            currentTarget = null;
        }
    }

    @EventTarget
    private void onWorld(WorldEvent event) {
        currentTarget = null;
    }

    @EventTarget
    private void onAttack(AttackEvent event) {
        if (event.getTargetEntity() instanceof EntityPlayer)
            currentTarget = (EntityPlayer) event.getTargetEntity();
    }

    public void sendMessage(String name) {
        final String[] text = {"人生自古谁无死，"};
        final int randomIndex = ThreadLocalRandom.current().nextInt(0, text.length);
        mc.thePlayer.sendChatMessage("@" + name + " " + text[randomIndex] + " 你已被MoonLight击败");
    }
}
