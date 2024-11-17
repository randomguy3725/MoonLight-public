package wtf.moonlight.features.modules.impl.movement;

import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;

@ModuleInfo(name = "NoJumpDelay", category = ModuleCategory.Movement)
public class NoJumpDelay extends Module {

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.jumpTicks = 0;
    }
}
