package wtf.moonlight.features.modules.impl.player;

import de.florianmichael.viamcp.fixes.AttackOrder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.ItemFireball;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.movement.LongJump;
import wtf.moonlight.features.modules.impl.world.Scaffold;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.player.MovementCorrection;
import wtf.moonlight.utils.player.RotationUtils;

@ModuleInfo(name = "AntiFireball", category = ModuleCategory.Player)
public class AntiFireball extends Module {
    private final SliderValue aps = new SliderValue("Aps", 9, 1, 20, this);
    public final SliderValue range = new SliderValue("Range", 6.0F, 2.0F, 6F, .1f, this);
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
    private final BoolValue moveFix = new BoolValue("Move Fix", false, this);
    private final TimerUtils attackTimer = new TimerUtils();

    @EventTarget
    public void onUpdate(UpdateEvent event) {

        if (isEnabled(LongJump.class) || isEnabled(Scaffold.class) || mc.thePlayer.getHeldItem().getItem() instanceof ItemFireball)
            return;

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityFireball && entity.getDistanceToEntity(mc.thePlayer) < range.get()) {
                if (attackTimer.hasTimeElapsed((long) (1000L / (aps.get() + 2)))) {

                    float[] finalRotation = RotationUtils.getAngles(entity);

                    if (customRotationSetting.get()) {
                        switch (calcRotSpeedMode.get()) {
                            case "Linear":
                                RotationUtils.setRotation(finalRotation, moveFix.get() ?  MovementCorrection.SILENT : MovementCorrection.OFF, MathUtils.randomizeInt(minYawRotSpeed.get(), maxYawRotSpeed.get()), MathUtils.randomizeInt(minPitchRotSpeed.get(), maxPitchRotSpeed.get()), smoothlyResetRotation.get());
                                break;
                            case "Acceleration":
                                RotationUtils.setRotation(finalRotation, moveFix.get() ?  MovementCorrection.SILENT : MovementCorrection.OFF, maxYawAcceleration.get(), maxPitchAcceleration.get(), accelerationError.get(), constantError.get(), smoothlyResetRotation.get());
                                break;
                        }
                    } else {
                        RotationUtils.setRotation(finalRotation, moveFix.get() ?  MovementCorrection.SILENT : MovementCorrection.OFF);
                    }
                    
                    AttackOrder.sendFixedAttack(mc.thePlayer,entity);
                    attackTimer.reset();
                }
            }
        }
    }
}
