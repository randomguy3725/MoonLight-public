package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.client.settings.KeyBinding;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;

@ModuleInfo(name = "Sprint", category = ModuleCategory.Movement)
public class Sprint extends Module {

    public final BoolValue silent = new BoolValue("Silent", false, this);
    // todo: omnisprint

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);

        if (silent.get()) {
            mc.thePlayer.serverSprintState = false;
        }
    }
}