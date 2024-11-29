package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.client.settings.KeyBinding;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.utils.player.MovementCorrection;
import wtf.moonlight.utils.player.RotationUtils;

@ModuleInfo(name = "Sprint", category = ModuleCategory.Movement)
public class Sprint extends Module {

    private final BoolValue silent = new BoolValue("Silent", false, this);
    private final BoolValue rotate = new BoolValue("Rotate", false, this);

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);

        if (silent.get()) {
            mc.thePlayer.serverSprintState = false;
        }

        if (rotate.get()) {
            float yaw;

            if (mc.gameSettings.keyBindForward.isKeyDown() && mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown()) {
                yaw = 45f;
            } else if (mc.gameSettings.keyBindForward.isKeyDown() && mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown()) {
                yaw = -45f;
            } else if (mc.gameSettings.keyBindBack.isKeyDown() && mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown()) {
                yaw = 135f;
            } else if (mc.gameSettings.keyBindBack.isKeyDown() && mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown()) {
                yaw = -135f;
            } else if (mc.gameSettings.keyBindBack.isKeyDown()) {
                yaw = 180f;
            } else if (mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown()) {
                yaw = 90f;
            } else if (mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown()) {
                yaw = -90f;
            } else {
                yaw = 0f;
            }

            float[] finalRotation = new float[]{(mc.thePlayer.cameraRotationYaw - yaw), mc.thePlayer.cameraRotationPitch};

            RotationUtils.setRotation(finalRotation, MovementCorrection.SILENT);
        }

    }
}