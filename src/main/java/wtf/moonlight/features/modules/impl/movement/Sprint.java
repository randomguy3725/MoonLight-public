package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.client.settings.KeyBinding;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.utils.player.MovementCorrection;
import wtf.moonlight.utils.player.MovementUtils;
import wtf.moonlight.utils.player.RotationUtils;

@ModuleInfo(name = "Sprint", category = ModuleCategory.Movement)
public class Sprint extends Module {

    private final BoolValue omni = new BoolValue("Omni", false, this);
    private final BoolValue silent = new BoolValue("Silent", false, this);
    private final BoolValue rotate = new BoolValue("Rotate", false, this);
    private final BoolValue onlyOnGround = new BoolValue("Only On Ground", true, this, rotate::get);

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (!isEnabled(Scaffold.class))
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);

        if(omni.get()){
            mc.thePlayer.omniSprint = MovementUtils.isMoving();
        }

        if (silent.get()) {
            mc.thePlayer.serverSprintState = false;
        }

        if (rotate.get()) {
            if (onlyOnGround.get() && !mc.thePlayer.onGround) {
                return;
            }

            float[] finalRotation = new float[]{MovementUtils.getRawDirection(), mc.thePlayer.rotationPitch};

            RotationUtils.setRotation(finalRotation, MovementCorrection.SILENT);
        }

    }

    @Override
    public void onDisable(){
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        mc.thePlayer.omniSprint = false;
    }
}