package wtf.moonlight.features.modules.impl.movement;

import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.player.MovementUtils;

@ModuleInfo(name = "Fly", category = ModuleCategory.Movement)
public class Fly extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"Vanilla"}, "Vanilla", this);
    private final SliderValue horizontalSpeed = new SliderValue("Horizontal speed", 2, 0, 9, 0.1f, this, () -> mode.is("Vanilla"));
    private final SliderValue verticalSpeed = new SliderValue("Vertical speed", 2, 0, 9, 0.1f, this, () -> mode.is("Vanilla"));

    //todo: fix this shit
    public void onUpdate(UpdateEvent event) {
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.motionY = 0.3 * verticalSpeed.get();
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.thePlayer.motionY = -0.3 * verticalSpeed.get();
        } else {
            mc.thePlayer.motionY = 0.0;
        }

        if (MovementUtils.isMoving()) MovementUtils.strafe(0.85 * horizontalSpeed.get());
        else MovementUtils.stopXZ();
    }
}