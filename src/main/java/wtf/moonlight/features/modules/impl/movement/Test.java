package wtf.moonlight.features.modules.impl.movement;

import org.lwjglx.input.Keyboard;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.utils.misc.DebugUtils;
import wtf.moonlight.utils.player.MovementUtils;

@ModuleInfo(name = "Test", category = ModuleCategory.Movement, key = Keyboard.KEY_V)
public class Test extends Module {

    @EventTarget
    public void onMotion(MotionEvent event){
        if(event.isPre()){
            boolean down = false;
            boolean slow = false;
            int simpleY = (int) Math.round((event.y % 1) * 10000);

            DebugUtils.sendMessage(simpleY + "Value, " + mc.thePlayer.offGroundTicks);

            //0
            //4200
            //7532
            //13
            //1413
            //2000
            //1792
            //804
            //9051
            //6550
            //3315


            if (simpleY == 13) {
                mc.thePlayer.motionY = mc.thePlayer.motionY - 0.01;
            }

            if (simpleY == 13) down = true;
            if (down) {
                event.y -= 1E-5;
                mc.thePlayer.motionY = mc.thePlayer.motionY + 0.03;
            }

            if(down) slow = true;


            if(slow){
                mc.thePlayer.motionY = mc.thePlayer.motionY - 0.07;
            }
        }
    }
}
