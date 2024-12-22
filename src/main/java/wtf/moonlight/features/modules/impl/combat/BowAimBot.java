package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.player.MovementCorrection;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.player.RotationUtils;

import java.util.Objects;

@ModuleInfo(name = "BowAimBot", category = ModuleCategory.Combat)
public class BowAimBot extends Module {

    private final SliderValue fov = new SliderValue("FOV",180,1,180,this);
    private final SliderValue range = new SliderValue("Range", 30, 3, 200, 1, this);
    private final SliderValue predictSize = new SliderValue("Predict Size", 2, 0.1f, 5, 0.1f, this);
    private final BoolValue customRotationSetting = new BoolValue("Custom Rotation Setting", false, this);
    private final ModeValue calcRotSpeedMode = new ModeValue("Calculate Rotate Speed Mode", new String[]{"Linear", "Acceleration"}, "Linear", this, customRotationSetting::get);
    private final SliderValue minYawRotSpeed = new SliderValue("Min Yaw Rotation Speed", 180, 0, 180, 1, this, () -> calcRotSpeedMode.is("Linear") && customRotationSetting.get());
    private final SliderValue minPitchRotSpeed = new SliderValue("Min Pitch Rotation Speed", 180, 0, 180, 1, this, () -> calcRotSpeedMode.is("Linear") && customRotationSetting.get());
    private final SliderValue maxYawRotSpeed = new SliderValue("Max Yaw Rotation Speed", 180, 0, 180, 1, this, () -> calcRotSpeedMode.is("Linear") && customRotationSetting.get());
    private final SliderValue maxPitchRotSpeed = new SliderValue("Max Pitch Rotation Speed", 180, 0, 180, 1, this, () -> calcRotSpeedMode.is("Linear") && customRotationSetting.get());
    public final SliderValue maxYawAcceleration = new SliderValue("Max Yaw Acceleration", 100, 0f, 100f, 1f, this, () -> calcRotSpeedMode.is("Acceleration") && customRotationSetting.get());
    public final SliderValue maxPitchAcceleration = new SliderValue("Max Pitch Acceleration", 100, 0f, 100f, 1f, this, () -> calcRotSpeedMode.is("Acceleration") && customRotationSetting.get());
    public final SliderValue accelerationError = new SliderValue("Acceleration Error", 0f, 0f, 1f, 0.01f, this, () -> calcRotSpeedMode.is("Acceleration") && customRotationSetting.get());
    public final SliderValue constantError = new SliderValue("Constant Error", 0f, 0f, 10f, 0.01f, this, () -> calcRotSpeedMode.is("Acceleration") && customRotationSetting.get());
    public final BoolValue smoothlyResetRotation = new BoolValue("Smoothly Reset Rotation", true, this, customRotationSetting::get);
    private final BoolValue moveFix = new BoolValue("Move Fix", true, this);
    public final ModeValue moveFixMode = new ModeValue("Move Fix Mode", new String[]{"Silent", "Strict"}, "Silent", this);
    private EntityPlayer target;

    @EventTarget
    public void onUpdate(UpdateEvent event){

        target = PlayerUtils.getTarget(range.get());

        if(target == null)
            return;
        if((RotationUtils.getRotationDifference(target) <= fov.get() || fov.get() == 180) && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && mc.thePlayer.isUsingItem()){
            float[] finalRotation = RotationUtils.faceTrajectory(target, true, predictSize.get());

            if (customRotationSetting.get()) {
                switch (calcRotSpeedMode.get()) {
                    case "Linear":
                        RotationUtils.setRotation(finalRotation,moveFix.get() ? (Objects.equals(moveFixMode.get(), "Silent") ? MovementCorrection.SILENT : MovementCorrection.STRICT) : MovementCorrection.OFF, MathUtils.randomizeInt(minYawRotSpeed.get(), maxYawRotSpeed.get()), MathUtils.randomizeInt(minPitchRotSpeed.get(), maxPitchRotSpeed.get()), smoothlyResetRotation.get());
                        break;
                    case "Acceleration":
                        RotationUtils.setRotation(finalRotation,moveFix.get() ? (Objects.equals(moveFixMode.get(), "Silent") ? MovementCorrection.SILENT : MovementCorrection.STRICT) : MovementCorrection.OFF, maxYawAcceleration.get(), maxPitchAcceleration.get(), accelerationError.get(), constantError.get(), smoothlyResetRotation.get());
                        break;
                }
            } else {
                RotationUtils.setRotation(finalRotation,moveFix.get() ? (Objects.equals(moveFixMode.get(), "Silent") ? MovementCorrection.SILENT : MovementCorrection.STRICT) : MovementCorrection.OFF);
            }
        }
    }
}
