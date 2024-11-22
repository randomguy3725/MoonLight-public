package wtf.moonlight.features.modules.impl.world;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.MultiBoolValue;
import wtf.moonlight.features.values.impl.SliderValue;

import java.util.Arrays;

@ModuleInfo(name = "Scaffold2", category = ModuleCategory.World, key = Keyboard.KEY_B)
public class Scaffold2 extends Module {
    private final ModeValue switchBlock = new ModeValue("Switch Block", new String[]{"Silent", "Switch", "Spoof"}, "Normal", this);
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Normal", "Telly", "Snap", "Watchdog", "God Bridge", "Grim 1.17","Fruit Bridge"}, "Normal", this);
    private final SliderValue minTellyTicks = new SliderValue("Min Telly Ticks", 2, 1, 5, this, () -> mode.is("Telly"));
    private final SliderValue maxTellyTicks = new SliderValue("Max Telly Ticks", 4, 1, 5, this, () -> mode.is("Telly"));
    private final SliderValue fruitBTicks = new SliderValue("Fruit Bridge Ticks", 2, 1, 5, this, () -> mode.is("Fruit Bridge"));
    private final SliderValue blocksToJump = new SliderValue("Blocks To Jump", 7, 1, 8, this, () -> mode.is("God Bridge"));
    private final BoolValue biggestStack = new BoolValue("Biggest Stack", false, this);
    private final BoolValue rayCast = new BoolValue("Ray Cast", true, this);
    private final ModeValue rotations = new ModeValue("Rotations", new String[]{"Normal", "Normal 2","Normal 3","Normal 4", "God Bridge", "Reverse", "Smart","Bruteforce","Custom","Sex"}, "Normal", this, () -> !mode.is("Grim 1.17"));
    private final ModeValue precision = new ModeValue("Precision", new String[]{"Very Low", "Low", "Moderate", "High", "Very High", "Unlimited"}, "Moderate", this, () -> !mode.is("Grim 1.17") && rotations.is("Normal"));
    private final BoolValue normalRTest = new BoolValue("Test", false, this, () -> !mode.is("Grim 1.17") && rotations.is("Normal"));
    private final ModeValue godBridgePitch = new ModeValue("God Bridge Pitch Mode", new String[]{"Static", "In Range"}, "In Range", this, () -> rotations.is("God Bridge") && !mode.is("Grim 1.17"));
    private final SliderValue customYaw = new SliderValue("Custom Yaw", 180, 0, 180 , 1, this, () -> rotations.is("Custom"));
    private final SliderValue pitch = new SliderValue("Static Pitch", 76, 50, 90, .1f, this, () -> godBridgePitch.canDisplay() && godBridgePitch.is("Static"));
    private final SliderValue minPitch = new SliderValue("Min Pitch Range", 55, 50, 90, .1f, this, () -> godBridgePitch.canDisplay() && godBridgePitch.is("In Range"));
    public final SliderValue maxPitch = new SliderValue("Max Pitch Range", 75, 50, 90, .1f, this, () -> godBridgePitch.canDisplay() && godBridgePitch.is("In Range"));
    private final BoolValue alwaysRot = new BoolValue("Always Rotate", false, this, () -> !mode.is("Grim 1.17") && (rotations.is("Normal 2") || rotations.is("Normal 4") || rotations.is("Smart")));
    private final BoolValue clutchRot = new BoolValue("Clutch Rotation", false, this, () -> !mode.is("Grim 1.17"));
    private final BoolValue customRotationSetting = new BoolValue("Custom Rotation Setting", false, this, () -> !mode.is("Grim 1.17"));
    private final ModeValue calcRotSpeedMode = new ModeValue("Calculate Rotate Speed Mode", new String[]{"Linear", "Acceleration"}, "Linear", this, () -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final SliderValue minYawRotSpeed = new SliderValue("Min Yaw Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && calcRotSpeedMode.is("Linear"));
    private final SliderValue minPitchRotSpeed = new SliderValue("Min Pitch Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && calcRotSpeedMode.is("Linear"));
    private final SliderValue maxYawRotSpeed = new SliderValue("Max Yaw Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && calcRotSpeedMode.is("Linear"));
    private final SliderValue maxPitchRotSpeed = new SliderValue("Max Pitch Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && calcRotSpeedMode.is("Linear"));
    public final SliderValue maxYawAcceleration = new SliderValue("Max Yaw Acceleration", 100, 0f, 100f, 1f, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && calcRotSpeedMode.is("Acceleration"));
    public final SliderValue maxPitchAcceleration = new SliderValue("Max Pitch Acceleration", 100, 0f, 100f, 1f, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && calcRotSpeedMode.is("Acceleration"));
    public final SliderValue accelerationError = new SliderValue("Acceleration Error", 0f, 0f, 1f, 0.01f, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && calcRotSpeedMode.is("Acceleration"));
    public final SliderValue constantError = new SliderValue("Constant Error", 0f, 0f, 10f, 0.01f, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && calcRotSpeedMode.is("Acceleration"));
    public final BoolValue smoothlyResetRotation = new BoolValue("Smoothly Reset Rotation", true, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final BoolValue sprint = new BoolValue("Sprint", true, this);
    private final BoolValue swing = new BoolValue("Swing", true, this);
    private final MultiBoolValue addons = new MultiBoolValue("Addons", Arrays.asList(
            new BoolValue("Movement Fix", true),
            new BoolValue("Keep Y", false),
            new BoolValue("Speed Keep Y", false),
            new BoolValue("Safe Walk", false),
            new BoolValue("AD Strafe", false),
            new BoolValue("Hover", false),
            new BoolValue("Sneak", false)
    ), this);
    // private final BoolValue intaveSussy = new BoolValue("Intave Sussy", false, this);
    private final SliderValue blocksToSneak = new SliderValue("Blocks To Sneak", 7, 1, 8, this, () -> addons.isEnabled("Sneak"));
    private final SliderValue sneakDistance = new SliderValue("Sneak Distance", 0, 0, 0.5f, 0.01f, this, () -> addons.isEnabled("Sneak"));
    private final ModeValue tower = new ModeValue("Tower", new String[]{"Jump", "Watchdog","Watchdog Test"}, "Jump", this, () -> mode.is("Watchdog"));
    private final BoolValue calcPos = new BoolValue("Calculate Position", true, this, () -> tower.canDisplay() && (tower.is("Watchdog") || tower.is("Watchdog Test")));
    private final ModeValue towerMove = new ModeValue("Tower Move", new String[]{"Jump", "Vanilla","2.5 Tick"}, "Jump", this, () -> mode.is("Watchdog"));
    private final BoolValue stop = new BoolValue("Stop", false, this, () -> mode.is("Watchdog") && towerMove.is("Vanilla"));
    private final SliderValue towerSpeedWhenDiagonal = new SliderValue("Tower Move Diagonal Speed", 0.22f, 0.2f, 0.28f, 0.005f, this, () -> mode.is("Watchdog") && towerMove.is("Vanilla"));
    private final BoolValue boost = new BoolValue("Tower Move Boost", true, this, () -> mode.is("Watchdog") && towerMove.is("Vanilla"));
    private final SliderValue speedBoost = new SliderValue("Tower Move Speed Boost", 0.06f, 0.01f, 0.1f, 0.01f, this, () -> mode.is("Watchdog") && towerMove.is("Vanilla") && boost.get());
    private final ModeValue wdSprint = new ModeValue("WD Sprint Mode", new String[]{"Beside", "Bottom", "Low","Spoof","Blink"}, "Bottom", this, () -> mode.is("Watchdog") && sprint.get() && !addons.isEnabled("Keep Y"));
    private final ModeValue wdKeepY = new ModeValue("WD Keep Y Mode", new String[]{"Normal", "Opal","None"}, "Opal", this, () -> mode.is("Watchdog") && sprint.get() && addons.isEnabled("Keep Y"));
    private final BoolValue unPatch = new BoolValue("Un Patch Test", true, this, () -> mode.is("Watchdog") && sprint.get() && addons.isEnabled("Keep Y"));
    private final SliderValue straightSpeed = new SliderValue("Keep Y Straight Speed", 1, 0.5f, 1f, 0.01f, this, () -> mode.is("Watchdog") && sprint.get() && addons.isEnabled("Keep Y"));
    private final SliderValue diagonalSpeed = new SliderValue("Keep Y Diagonal Speed", 0.95f, 0.5f, 1f, 0.01f, this, () -> mode.is("Watchdog") && sprint.get() && addons.isEnabled("Keep Y"));
    public final ModeValue lowMode = new ModeValue("Low Hop",new String[]{"None","1","2","3"},"1",this, () -> mode.is("Watchdog") && sprint.get() && addons.isEnabled("Keep Y"));
    public final ModeValue counter = new ModeValue("Counter", new String[]{"None", "Simple", "Normal", "Exhibition"}, "Normal", this);
    private float[] rotation;
    private float[] previousRotation;
    public static BlockData getBlockInfo(BlockPos belowBlockPos) {
        if (mc.theWorld.getBlockState(belowBlockPos).getBlock() instanceof BlockAir) {
            for (int x = 0; x < 4; x++) {
                for (int z = 0; z < 4; z++) {
                    for (int i = 1; i > -3; i -= 2) {
                        final BlockPos blockPos = belowBlockPos.add(x * i, 0, z * i);
                        if (mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockAir) {
                            for (EnumFacing direction : EnumFacing.values()) {
                                final BlockPos block = blockPos.offset(direction);
                                final Material material = mc.theWorld.getBlockState(block).getBlock().getMaterial();
                                if (material.isSolid() && !material.isLiquid()) {
                                    return new BlockData(block, direction.getOpposite());
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Getter
    @AllArgsConstructor
    public static class BlockData {
        private final BlockPos position;
        private final EnumFacing facing;
    }
}
