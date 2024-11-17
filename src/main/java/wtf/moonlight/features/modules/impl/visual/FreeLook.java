package wtf.moonlight.features.modules.impl.visual;

import org.lwjglx.input.Mouse;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;

@ModuleInfo(name = "FreeLook", category = ModuleCategory.Visual)
public class FreeLook extends Module {
    private boolean released;

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.isPost()) {
            if (Mouse.isButtonDown(2)) {
                mc.gameSettings.thirdPersonView = 1;
                released = false;
            } else {
                if (!released) {
                    mc.gameSettings.thirdPersonView = 0;
                    released = true;
                }
            }
        }
    }
}
