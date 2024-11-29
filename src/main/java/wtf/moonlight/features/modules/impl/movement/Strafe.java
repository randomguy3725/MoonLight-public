package wtf.moonlight.features.modules.impl.movement;

import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.player.MovementUtils;

@ModuleInfo(name = "Strafe", category = ModuleCategory.Movement)
public class Strafe extends Module {

    public final BoolValue ground = new BoolValue("Ground", true, this);
    public final SliderValue groundSpeed = new SliderValue("Ground Speed", 1, 0.01f, 10f, 0.1f, this);
    public final BoolValue air = new BoolValue("Air", true, this);
    public final SliderValue airSpeed = new SliderValue("Air Speed", 1, 0.01f, 10f, 0.1f, this);

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.onGround && ground.get()) MovementUtils.strafe(groundSpeed.get());
        if (!mc.thePlayer.onGround && air.get()) MovementUtils.strafe(airSpeed.get());
    }
}