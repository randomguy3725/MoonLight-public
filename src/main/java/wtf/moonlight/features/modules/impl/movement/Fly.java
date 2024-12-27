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
package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.client.Minecraft;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;

@ModuleInfo(name = "Fly", category = ModuleCategory.Movement)
public class Fly extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"Vanilla"}, "Vanilla", this);
    private final SliderValue speed = new SliderValue("Speed", 2f, 1f, 5f, 0.1f, this, () -> mode.is("Vanilla"));

    public void onDisable() {
        if (mode.get().equals("Vanilla")) {
            if (Minecraft.getMinecraft().thePlayer == null)
                return;

            if (Minecraft.getMinecraft().thePlayer.capabilities.isFlying) {
                Minecraft.getMinecraft().thePlayer.capabilities.isFlying = false;
            }

            Minecraft.getMinecraft().thePlayer.capabilities.setFlySpeed(0.05F);
        }

    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (mode.get().equals("Vanilla")) {
            mc.thePlayer.motionY = 0.0D;
            mc.thePlayer.capabilities.setFlySpeed((float) (0.05000000074505806D * speed.get()));
            mc.thePlayer.capabilities.isFlying = true;
        }
    }
}