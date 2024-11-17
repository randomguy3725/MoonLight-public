package wtf.moonlight.features.modules.impl.movement;

import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.PreStepEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.utils.player.MovementUtils;

@ModuleInfo(name = "Step", category = ModuleCategory.Movement)
public class Step extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"Watchdog Test"}, "Watchdog Test", this);
    public int stepTick = -1;

    @EventTarget
    public void onPreStep(PreStepEvent event) {
        if (mode.is("Watchdog Test")) {
            if (stepTick == -1) {
                if (event.height > 0.6) {
                    stepTick = 0;
                }
            }
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.isPre()) {
            if (stepTick != -1) {
                stepTick++;
            }

            switch (mode.get()) {
                case "Watchdog Test":
                    switch (stepTick) {
                        case 0:
                        case 1:
                            mc.thePlayer.jump();
                            break;
                        case 5:
                            mc.thePlayer.motionY = MovementUtils.predictedMotionY(mc.thePlayer.motionY, 2);
                            break;
                        case 6:
                            stepTick = -1;
                            break;
                    }
                    break;
            }
        }
    }
}
