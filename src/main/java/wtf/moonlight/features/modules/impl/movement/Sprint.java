package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.client.settings.KeyBinding;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.TickEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.MoveEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.world.Scaffold;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.utils.player.MovementUtils;

@ModuleInfo(name = "Sprint", category = ModuleCategory.Movement)
public class Sprint extends Module {

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (!isEnabled(Scaffold.class))
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
    }
}
