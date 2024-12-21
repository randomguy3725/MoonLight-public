package wtf.moonlight.features.modules.impl.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.*;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.combat.KillAura;
import wtf.moonlight.features.modules.impl.exploit.Disabler;
import wtf.moonlight.features.modules.impl.movement.Speed;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.MultiBoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.misc.SpoofSlotUtils;
import wtf.moonlight.utils.player.*;
import wtf.moonlight.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ModuleInfo(name = "Scaffold", category = ModuleCategory.World, key = Keyboard.KEY_B)
public class Scaffold extends Module {
    private final ModeValue switchBlock = new ModeValue("Switch Block", new String[]{"Silent", "Switch", "Spoof"}, "Normal", this);
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Normal", "Telly", "Snap", "Watchdog", "God Bridge", "Fruit Bridge"}, "Normal", this);
    private final SliderValue minTellyTicks = new SliderValue("Min Telly Ticks", 2, 1, 5, this, () -> mode.is("Telly"));
    private final SliderValue maxTellyTicks = new SliderValue("Max Telly Ticks", 4, 1, 5, this, () -> mode.is("Telly"));
    private final SliderValue fruitBTicks = new SliderValue("Fruit Bridge Ticks", 2, 1, 5, this, () -> mode.is("Fruit Bridge"));
    private final SliderValue blocksToJump = new SliderValue("Blocks To Jump", 7, 1, 8, this, () -> mode.is("God Bridge"));
    private final BoolValue biggestStack = new BoolValue("Biggest Stack", false, this);
    private final ModeValue rotations = new ModeValue("Rotations", new String[]{"Center","Closest", "God Bridge", "Reverse", "Smart", "Custom", "Unfair Pitch","Hypixel Test"}, "Normal", this);
    private final ModeValue godBridgePitch = new ModeValue("God Bridge Pitch Mode", new String[]{"Static", "In Range"}, "In Range", this, () -> rotations.is("God Bridge") && !mode.is("Grim 1.17"));
    private final SliderValue customYaw = new SliderValue("Custom Yaw", 180, 0, 180, 1, this, () -> rotations.is("Custom"));
    private final SliderValue pitch = new SliderValue("Static Pitch", 76, 50, 90, .1f, this, () -> godBridgePitch.canDisplay() && godBridgePitch.is("Static"));
    private final SliderValue minPitch = new SliderValue("Min Pitch Range", 55, 50, 90, .1f, this, () -> godBridgePitch.canDisplay() && godBridgePitch.is("In Range"));
    public final SliderValue maxPitch = new SliderValue("Max Pitch Range", 75, 50, 90, .1f, this, () -> godBridgePitch.canDisplay() && godBridgePitch.is("In Range"));
    private final BoolValue customRotationSetting = new BoolValue("Custom Rotation Setting", false, this);
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
            new BoolValue("Ray Trace", true),
            new BoolValue("Keep Y", false),
            new BoolValue("Speed Keep Y", false),
            new BoolValue("Safe Walk", false),
            new BoolValue("Safe Walk When No Data", false),
            new BoolValue("AD Strafe", false),
            new BoolValue("Hover", false),
            new BoolValue("Sneak", false),
            new BoolValue("Block ESP", false)
    ), this);
    // private final BoolValue intaveSussy = new BoolValue("Intave Sussy", false, this);
    private final SliderValue blocksToSneak = new SliderValue("Blocks To Sneak", 7, 1, 8, this, () -> addons.isEnabled("Sneak"));
    private final SliderValue sneakDistance = new SliderValue("Sneak Distance", 0, 0, 0.5f, 0.01f, this, () -> addons.isEnabled("Sneak"));
    private final ModeValue tower = new ModeValue("Tower", new String[]{"Jump", "Vanilla","Watchdog Test"}, "Jump", this);
    public final BoolValue tellyWhenDiagonal = new BoolValue("Telly When Diagonal",true,this,() -> mode.is("Watchdog") && sprint.get());
    private final BoolValue calcPos = new BoolValue("Calculate Position", true, this, () -> tower.canDisplay() && (tower.is("Watchdog Test") || tower.is("Watchdog Test2")));
    private final ModeValue towerMove = new ModeValue("Tower Move", new String[]{"Jump", "Vanilla","Watchdog Test","Low"}, "Jump", this);
    private final ModeValue wdSprint = new ModeValue("WD Sprint Mode", new String[]{"Beside", "Bottom","Offset"}, "Bottom", this, () -> mode.is("Watchdog") && sprint.get() && !addons.isEnabled("Keep Y"));
    private final BoolValue sprintBoost = new BoolValue("Sprint Boost Test", true, this, () -> mode.is("Watchdog") && sprint.get() && !addons.isEnabled("Keep Y"));
    private final ModeValue wdKeepY = new ModeValue("WD Keep Y Mode", new String[]{"Normal", "Opal", "None"}, "Opal", this, () -> mode.is("Watchdog") && sprint.get() && addons.isEnabled("Keep Y"));
    private final BoolValue unPatch = new BoolValue("Un Patch Test", true, this, () -> mode.is("Watchdog") && sprint.get() && addons.isEnabled("Keep Y"));
    private final SliderValue straightSpeed = new SliderValue("Keep Y Straight Speed", 1, 0.5f, 1f, 0.01f, this, () -> mode.is("Watchdog") && sprint.get() && addons.isEnabled("Keep Y"));
    private final SliderValue diagonalSpeed = new SliderValue("Keep Y Diagonal Speed", 0.95f, 0.5f, 1f, 0.01f, this, () -> mode.is("Watchdog") && sprint.get() && addons.isEnabled("Keep Y"));
    public final ModeValue lowMode = new ModeValue("Low Hop", new String[]{"None", "1", "2", "3"}, "1", this, () -> mode.is("Watchdog") && sprint.get() && addons.isEnabled("Keep Y"));
    public final ModeValue counter = new ModeValue("Counter", new String[]{"None", "Simple", "Normal", "Exhibition"}, "Normal", this);
    private final EnumFacing[] facings = {EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.UP};
    private final BlockPos[] offsets = {new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};
    public BlockData data;
    public BlockPos targetBlock;
    private int prevSlot = -1;
    private double onGroundY = 0;
    private float[] rotation;
    private float[] previousRotation;
    private BlockPos previousBlock;
    private int towerTick;
    private int towerMoveTick;
    private boolean canJump;
    private int blocksPlaced;
    private boolean placed;
    private boolean start;
    private boolean placing;
    private boolean isOnRightSide;
    private int jumpCount;
    private int tellyTicks;
    private HoverState hoverState = HoverState.DONE;
    private final List<Block> blacklistedBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.wooden_slab, Blocks.chest, Blocks.flowing_lava,
            Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.skull, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice,
            Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.trapped_chest, Blocks.torch, Blocks.anvil,
            Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore,
            Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
            Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower,
            Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus,
            Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall,
            Blocks.oak_fence, Blocks.activator_rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.redstone_torch, Blocks.acacia_stairs,
            Blocks.birch_stairs, Blocks.brick_stairs, Blocks.dark_oak_stairs, Blocks.jungle_stairs, Blocks.nether_brick_stairs, Blocks.oak_stairs,
            Blocks.quartz_stairs, Blocks.red_sandstone_stairs, Blocks.sandstone_stairs, Blocks.spruce_stairs, Blocks.stone_brick_stairs, Blocks.stone_stairs, Blocks.double_wooden_slab, Blocks.stone_slab, Blocks.double_stone_slab, Blocks.stone_slab2, Blocks.double_stone_slab2,
            Blocks.web, Blocks.gravel, Blocks.daylight_detector_inverted, Blocks.daylight_detector, Blocks.soul_sand, Blocks.piston, Blocks.piston_extension,
            Blocks.piston_head, Blocks.sticky_piston, Blocks.iron_trapdoor, Blocks.ender_chest, Blocks.end_portal, Blocks.end_portal_frame, Blocks.standing_banner,
            Blocks.wall_banner, Blocks.deadbush, Blocks.slime_block, Blocks.acacia_fence_gate, Blocks.birch_fence_gate, Blocks.dark_oak_fence_gate,
            Blocks.jungle_fence_gate, Blocks.spruce_fence_gate, Blocks.oak_fence_gate);

    @Override
    public void onEnable() {

        if (addons.isEnabled("Hover") && mc.thePlayer.onGround && !isEnabled(Speed.class)) {
            hoverState = HoverState.JUMP;
        } else {
            hoverState = HoverState.DONE;
        }

        onGroundY = mc.thePlayer.getEntityBoundingBox().minY;
        prevSlot = mc.thePlayer.inventory.currentItem;
        if (!sprint.get())
            mc.thePlayer.setSprinting(false);

        placing = false;

        previousRotation = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};

        if (wdSprint.canDisplay() && wdSprint.is("Offset") && !(PlayerUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockLiquid) && !addons.isEnabled("Hover")) {
            if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                MovementUtils.stop();
            }
            if (mc.thePlayer.onGround) {
                hoverState = HoverState.JUMP;
            } else {
                hoverState = HoverState.DONE;
            }
        }

        rotation = new float[]{mc.thePlayer.rotationYaw + 180,85};

        previousRotation = rotation;

        towerTick = 0;

        if(!addons.isEnabled("Movement Fix")){
            RotationUtils.fixSprint = false;
        }
        previousBlock = null;
    }

    @Override
    public void onDisable() {
        switch (switchBlock.get()) {
            case "Silent":
                sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                break;
            case "Switch":
                mc.thePlayer.inventory.currentItem = prevSlot;
                break;
            case "Spoof":
                mc.thePlayer.inventory.currentItem = prevSlot;
                SpoofSlotUtils.stopSpoofing();
                break;
        }
        previousRotation = rotation = null;
        start = false;
        targetBlock = null;
        canJump = false;
        blocksPlaced = 0;
        placed = false;
        tellyTicks = 0;

        if (wdSprint.canDisplay() && wdSprint.is("Offset")) {
            mc.thePlayer.motionX *= .8;
            mc.thePlayer.motionZ *= .8;
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {

        data = null;

        setTag(mode.get());

        if (sprint.get()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
            mc.thePlayer.setSprinting(false);
        }

        if (getBlockSlot() == -1)
            return;

        if (!tower.is("Jump") && towering() || !towerMove.is("Jump") && towerMoving()) {
            hoverState = HoverState.JUMP;
            blocksPlaced = 0;
            start = false;
        }

        if (mode.is("Watchdog") && mc.gameSettings.keyBindJump.isKeyDown() && towerMove.is("Jump")) {
            if (!mc.thePlayer.onGround && canJump)
                canJump = false;
        }

        if (mc.thePlayer.onGround || towerMoving() || towering())
            onGroundY = mc.thePlayer.getEntityBoundingBox().minY;

        switch (hoverState) {
            case JUMP:
                if (mc.thePlayer.onGround && !isEnabled(Speed.class) && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.jump();
                }
                hoverState = HoverState.FALL;
                break;
            case FALL:
                if (mc.thePlayer.onGround)
                    hoverState = HoverState.DONE;
                break;
        }

        double posY = (addons.isEnabled("Keep Y") || addons.isEnabled("Speed Keep Y") && isEnabled(Speed.class) || hoverState != HoverState.DONE || mode.is("Watchdog") && tellyWhenDiagonal.canDisplay() && tellyWhenDiagonal.get() && !MovementUtils.isMovingStraight() && !mc.gameSettings.keyBindJump.isKeyDown()) && !towering() && !towerMoving() ? mode.is("Watchdog") && wdKeepY.canDisplay() ? getWatchdogKeepY() : onGroundY : mc.thePlayer.getEntityBoundingBox().minY;

        targetBlock = new BlockPos(mc.thePlayer.posX, posY - 1, mc.thePlayer.posZ);

        if (wdSprint.canDisplay() && !towering() && !towerMoving()) {
            if (wdSprint.is("Beside")) {
                if (mc.thePlayer.ticksExisted % 7 == 0) {
                    if (mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST || mc.thePlayer.getHorizontalFacing() == EnumFacing.EAST)
                        targetBlock = targetBlock.add(0, 0, 1);
                    if (mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH || mc.thePlayer.getHorizontalFacing() == EnumFacing.SOUTH)
                        targetBlock = targetBlock.add(1, 0, 0);
                }
            }
            if (wdSprint.is("Bottom")) {
                if ((blocksPlaced == 0 || blocksPlaced == 2) && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    targetBlock = targetBlock.add(0, -1, 0);
                    if (blocksPlaced == 2)
                        blocksPlaced = 0;
                }
            }
        }

        if (wdKeepY.canDisplay() && !towering() && !towerMoving()) {

            if (wdKeepY.is("Opal")) {
                if (start) {
                    targetBlock = targetBlock.add(0, -1, 0);
                    if (mc.thePlayer.fallDistance > 0 && groundDistance() > 0 && MovementUtils.isMoving()) {
                        targetBlock = targetBlock.add(0, 1, 0);
                    }
                }
            }
        }

        if (towering() && (tower.is("Watchdog Test") || tower.is("Watchdog Test2")) && !placing) {
            if (mc.thePlayer.getHorizontalFacing() == EnumFacing.EAST || mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST) {
                targetBlock = targetBlock.add(0,0,Math.max(-1, Math.min(1, Math.round(mc.thePlayer.posZ)- mc.thePlayer.posZ)));
            } else {
                targetBlock = targetBlock.add(Math.max(-1, Math.min(1, Math.round(mc.thePlayer.posX) - mc.thePlayer.posX)),0,0);
            }
        }

        if ((mode.is("Normal") || mode.is("Telly") && mc.thePlayer.offGroundTicks >= tellyTicks || mode.is("God Bridge") || mode.is("Grim 1.17") || mode.is("Fruit Bridge") || mode.is("Watchdog")))
            data = getBlockData(targetBlock);

        final MovingObjectPosition[] rayCasted = {null};

        if (data == null || data.getPosition() == null || data.getFacing() == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir)) {
            return;
        }

        if (mode.is("Telly") && mc.thePlayer.onGround) {
            tellyTicks = MathUtils.randomizeInt((int) minTellyTicks.get(), (int) maxTellyTicks.get());
        }

        switch (switchBlock.get()) {
            case "Silent":
                sendPacketNoEvent(new C09PacketHeldItemChange(getBlockSlot()));
                break;
            case "Switch":
                mc.thePlayer.inventory.currentItem = getBlockSlot();
                break;
            case "Spoof":
                mc.thePlayer.inventory.currentItem = getBlockSlot();
                SpoofSlotUtils.startSpoofing(prevSlot);
                break;
        }

        if (mode.is("Watchdog") && !sprint.get() && !towerMoving() && !towering()) {
            mc.thePlayer.motionX *= 0.95;
            mc.thePlayer.motionZ *= 0.95;
        }

        if (wdSprint.canDisplay() && wdSprint.is("Offset") && !(PlayerUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockLiquid)) {
            if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                MovementUtils.preventDiagonalSpeed();
            }

            if (mc.gameSettings.keyBindJump.isPressed() && mc.thePlayer.onGround) {
                MovementUtils.stopXZ();
            }
        }

        final MovingObjectPosition[] raycast = {null};

        switch (rotations.get()) {
            case "God Bridge": {
                float yaw;
                float finalYaw;
                float movingYaw = MovementUtils.getRawDirection() + 180;
                switch (godBridgePitch.get()) {
                    case "In Range": {

                        List<Float> pitchList = new ArrayList<>();
                        float startPitch = minPitch.get();
                        float endPitch = maxPitch.get();
                        for (float i = startPitch; i <= endPitch; i++) {
                            pitchList.add(i);
                        }

                        if (mc.thePlayer.onGround) {
                            isOnRightSide = Math.floor(mc.thePlayer.posX + Math.cos(Math.toRadians(movingYaw)) * 0.5) != Math.floor(mc.thePlayer.posX) ||
                                    Math.floor(mc.thePlayer.posZ + Math.sin(Math.toRadians(movingYaw)) * 0.5) != Math.floor(mc.thePlayer.posZ);

                            BlockPos posInDirection = mc.thePlayer.getPosition().offset(EnumFacing.fromAngle(movingYaw), 1);

                            boolean isLeaningOffBlock = mc.theWorld.getBlockState(mc.thePlayer.getPosition().down()) instanceof BlockAir;
                            boolean nextBlockIsAir = mc.theWorld.getBlockState(posInDirection.down()).getBlock() instanceof BlockAir;

                            if (isLeaningOffBlock && nextBlockIsAir) {
                                isOnRightSide = !isOnRightSide;
                            }
                        }

                        yaw = MovementUtils.isMovingStraight() ? (movingYaw + (isOnRightSide ? 45 : -45)) : movingYaw;

                        finalYaw = Math.round(yaw / 45f) * 45f;
                        float finalYaw1 = finalYaw;
                        pitchList.forEach(pitches -> {
                            rotation = new float[]{finalYaw1, pitches};
                            raycast[0] = RotationUtils.rayTrace(rotation, mc.playerController.getBlockReachDistance(), 1);
                            if (rayCasted[0] == null || raycast[0] != null && raycast[0].typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                                rayCasted[0] = raycast[0];
                            }
                        });
                    }
                    break;


                    case "Static": {
                        yaw = MovementUtils.isMovingStraight() ? (movingYaw + (isOnRightSide ? 45 : -45)) : movingYaw;
                        finalYaw = Math.round(yaw / 45f) * 45f;
                        rotation = new float[]{finalYaw, pitch.get()};
                        raycast[0] = RotationUtils.rayTrace(rotation, mc.playerController.getBlockReachDistance(), 1);
                        if (rayCasted[0] == null || raycast[0] != null && raycast[0].typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                            rayCasted[0] = raycast[0];
                        }
                    }
                    break;
                }
            }
            break;
            case "Center": {
                rotation = RotationUtils.getRotations(getVec3(data));
                raycast[0] = RotationUtils.rayTrace(rotation, mc.playerController.getBlockReachDistance(), 1);
                if (rayCasted[0] == null || raycast[0] != null && raycast[0].typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && raycast[0].getBlockPos().equals(data.getPosition())) {
                    rayCasted[0] = raycast[0];
                }
            }
            break;
            case "Closest": {
                rotation = RotationUtils.getRotations(getHitVecOptimized(data.position, data.facing));
                raycast[0] = RotationUtils.rayTrace(rotation, mc.playerController.getBlockReachDistance(), 1);
                if (rayCasted[0] == null || raycast[0] != null && raycast[0].typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && raycast[0].getBlockPos().equals(data.getPosition())) {
                    rayCasted[0] = raycast[0];
                }
            }
            break;
            case "Smart": {
                EntityPlayer player = mc.thePlayer;
                double difference = player.posY + player.getEyeHeight() - targetBlock.getY() - 0.5 - (Math.random() - 0.5) * 0.1;

                for (int offset = -180; offset <= 180; offset += 45) {
                    player.setPosition(player.posX, player.posY - difference, player.posZ);
                    raycast[0] = RotationUtils.rayCast(new float[]{player.rotationYaw + offset, 0}, mc.playerController.getBlockReachDistance());
                    player.setPosition(player.posX, player.posY + difference, player.posZ);

                    if (raycast[0] == null || raycast[0].hitVec == null) return;

                    rotation = RotationUtils.getRotations(raycast[0].hitVec);

                    if (RotationUtils.rayTrace(rotation, mc.playerController.getBlockReachDistance(), 1).typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        if (rayCasted[0] == null || raycast[0] != null && raycast[0].getBlockPos().equals(data.getPosition())) {
                            rayCasted[0] = raycast[0];
                        }
                    }
                }
            }
            break;
            case "Unfair Pitch": {
                rotation = new float[]{mc.thePlayer.rotationYaw, getYawBasedPitch(data.getPosition(), data.getFacing(), mc.thePlayer.rotationYaw, previousRotation[1], 100)};
                raycast[0] = RotationUtils.rayTrace(rotation, mc.playerController.getBlockReachDistance(), 1);
                if (rayCasted[0] == null || raycast[0] != null && raycast[0].typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    rayCasted[0] = raycast[0];
                }
            }
            break;
            case "Custom": {
                rotation = new float[]{mc.thePlayer.rotationYaw + customYaw.get(), getYawBasedPitch(data.getPosition(), data.getFacing(), mc.thePlayer.rotationYaw + 180, previousRotation[1], 90)};
                raycast[0] = RotationUtils.rayTrace(rotation, mc.playerController.getBlockReachDistance(), 1);
                if (rayCasted[0] == null || raycast[0] != null && raycast[0].typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    rayCasted[0] = raycast[0];
                }
            }
            break;
            case "Reverse": {
                rotation = new float[]{MovementUtils.getRawDirection() + 180, getYawBasedPitch(data.getPosition(), data.getFacing(), mc.thePlayer.rotationYaw + 180, previousRotation[1], 85)};
                raycast[0] = RotationUtils.rayTrace(rotation, mc.playerController.getBlockReachDistance(), 1);
                if (rayCasted[0] == null || raycast[0] != null && raycast[0].typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    rayCasted[0] = raycast[0];
                }
            }
            break;
            case "Hypixel Test": {
                float movingYaw = MovementUtils.getRawDirection() + 180;
                if (mc.thePlayer.onGround) {
                    isOnRightSide = Math.floor(mc.thePlayer.posX + Math.cos(Math.toRadians(movingYaw)) * 0.5) != Math.floor(mc.thePlayer.posX) ||
                            Math.floor(mc.thePlayer.posZ + Math.sin(Math.toRadians(movingYaw)) * 0.5) != Math.floor(mc.thePlayer.posZ);

                    BlockPos posInDirection = mc.thePlayer.getPosition().offset(EnumFacing.fromAngle(movingYaw), 1);

                    boolean isLeaningOffBlock = mc.theWorld.getBlockState(mc.thePlayer.getPosition().down()) instanceof BlockAir;
                    boolean nextBlockIsAir = mc.theWorld.getBlockState(posInDirection.down()).getBlock() instanceof BlockAir;

                    if (isLeaningOffBlock && nextBlockIsAir) {
                        isOnRightSide = !isOnRightSide;
                    }
                }

                float yaw = (movingYaw + (isOnRightSide ? 89 : -89));

                rotation = new float[]{yaw, getYawBasedPitch(data.getPosition(), data.getFacing(), yaw, previousRotation[1], 87)};
                raycast[0] = RotationUtils.rayTrace(rotation, mc.playerController.getBlockReachDistance(), 1);
                if (rayCasted[0] == null || raycast[0] != null && raycast[0].typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    rayCasted[0] = raycast[0];
                }
            }
            break;
        }

        if(mode.is("Watchdog") && tellyWhenDiagonal.canDisplay() && tellyWhenDiagonal.get()) {
            if (!MovementUtils.isMovingStraight() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                if (mc.thePlayer.offGroundTicks >= 3) {
                    if (!isEnabled(Speed.class))
                        RotationUtils.fixSprint = true;
                    rotation = RotationUtils.getRotations(getVec3(data));
                    raycast[0] = RotationUtils.rayTrace(rotation, mc.playerController.getBlockReachDistance(), 1);
                    if (rayCasted[0] == null || raycast[0] != null && raycast[0].typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && raycast[0].getBlockPos().equals(data.getPosition())) {
                        rayCasted[0] = raycast[0];
                    }
                } else {
                    rotation = null;
                    RotationUtils.fixSprint = false;
                }
            } else {
                RotationUtils.fixSprint = false;
            }
        }

        if (unPatch.canDisplay() && unPatch.get() && mc.thePlayer.onGround && !towering() && !towerMoving()) {
            rotation = new float[]{mc.thePlayer.rotationYaw, 0f};
        }

        if((tower.is("Watchdog Test") || tower.is("Watchdog Test2")) && towering()){
            rotation = RotationUtils.getRotations(getVec3(data));
            raycast[0] = RotationUtils.rayTrace(rotation, mc.playerController.getBlockReachDistance(), 1);
            if (rayCasted[0] == null || raycast[0] != null && raycast[0].typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && raycast[0].getBlockPos().equals(data.getPosition())) {
                rayCasted[0] = raycast[0];
            }
        }

        placing = false;

        if (customRotationSetting.get()) {
            switch (calcRotSpeedMode.get()) {
                case "Linear":
                    RotationUtils.setRotation(rotation, addons.isEnabled("Movement Fix") ? MovementCorrection.SILENT : MovementCorrection.OFF, MathUtils.randomizeInt(minYawRotSpeed.get(), maxYawRotSpeed.get()), MathUtils.randomizeInt(minPitchRotSpeed.get(), maxPitchRotSpeed.get()), smoothlyResetRotation.get());
                    break;
                case "Acceleration":
                    RotationUtils.setRotation(rotation, addons.isEnabled("Movement Fix") ? MovementCorrection.SILENT : MovementCorrection.OFF, maxYawAcceleration.get(), maxPitchAcceleration.get(), accelerationError.get(), constantError.get(), smoothlyResetRotation.get());
                    break;
            }
        } else {
            RotationUtils.setRotation(rotation, addons.isEnabled("Movement Fix") ? MovementCorrection.SILENT : MovementCorrection.OFF);
        }

        if(mode.is("Fruit Bridge") && mc.thePlayer.offGroundTicks >= fruitBTicks.get() ||
                mode.is("Watchdog") &&
                        (tellyWhenDiagonal.canDisplay() && tellyWhenDiagonal.get() && mc.thePlayer.offGroundTicks >= 2 && !MovementUtils.isMovingStraight() || !tellyWhenDiagonal.get())
                || !mode.is("Fruit Bridge")) {

            if (!addons.isEnabled("Ray Trace")) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), data.position, data.facing, getVec3(data))) {
                    if (swing.get()) {
                        mc.thePlayer.swingItem();
                        mc.getItemRenderer().resetEquippedProgress();
                    } else
                        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                    placing = true;
                    blocksPlaced += 1;
                    previousRotation = rotation;
                    if (data.facing == EnumFacing.UP && wdKeepY.canDisplay() && wdKeepY.is("Opal")) {
                        start = true;
                    }
                    placed = true;
                    previousBlock = data.position;
                }
            } else {
                MovingObjectPosition placeBlock = rayCasted[0];
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), placeBlock.getBlockPos(), placeBlock.sideHit, placeBlock.hitVec)) {
                    if (swing.get()) {
                        mc.thePlayer.swingItem();
                        mc.getItemRenderer().resetEquippedProgress();
                    } else
                        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                    placing = true;
                    blocksPlaced += 1;
                    previousRotation = rotation;
                    if (placeBlock.sideHit == EnumFacing.UP && wdKeepY.canDisplay() && wdKeepY.is("Opal")) {
                        start = true;
                    }
                    placed = true;
                    previousBlock = data.position;
                }
            }
        }
    }

    @EventTarget
    public void onMove(MoveEvent event){

        if (data == null || data.getPosition() == null || data.getFacing() == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir))
            return;

        if (tower.canDisplay()) {
            switch (tower.get()) {
                case "Vanilla":
                    if (!mc.thePlayer.isPotionActive(Potion.jump)) {
                        if (towering()) {
                            mc.thePlayer.motionY = 0.42f;
                        }
                    }
                    break;
                case "Watchdog Test":
                    if (!mc.thePlayer.isPotionActive(Potion.jump)) {

                        if (towering()) {
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.motionY = 0.42f;
                            }
                            switch (mc.thePlayer.offGroundTicks % 3) {
                                case 0:
                                    event.setY(mc.thePlayer.motionY = 0.4198499917984009);
                                    MovementUtils.strafe((float) 0.26 - randomAmount() + MovementUtils.getSpeedEffect() * 0.03);
                                    break;
                                case 2:
                                    event.setY(Math.floor(mc.thePlayer.posY + 1) - mc.thePlayer.posY);
                                    break;
                            }

                            if (calcPos.get()) {
                                MovementUtils.stopXZ();
                                if (mc.thePlayer.getHorizontalFacing() == EnumFacing.EAST || mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST) {
                                    event.setX(mc.thePlayer.motionX = Math.max(-0.2, Math.min(0.2, Math.round(mc.thePlayer.posX) - mc.thePlayer.posX)));
                                } else {
                                    event.setZ(mc.thePlayer.motionZ = Math.max(-0.2, Math.min(0.2, Math.round(mc.thePlayer.posZ) - mc.thePlayer.posZ)));
                                }
                            }
                        }
                    }
                    break;
            }
        }

        if (towerMove.canDisplay()) {
            switch (towerMove.get()) {
                case "Vanilla":
                    if (MovementUtils.isMoving() && MovementUtils.getSpeed() > 0.1 && !mc.thePlayer.isPotionActive(Potion.jump)) {
                        if (towerMoving()) {
                            mc.thePlayer.motionY = 0.42f;
                        }
                    }
                    break;
                case "Watchdog Test":
                    if (MovementUtils.isMoving() && MovementUtils.getSpeed() > 0.1 && !mc.thePlayer.isPotionActive(Potion.jump)) {
                        if (towerMoving()) {
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.motionY = 0.42f;
                            }
                            switch (mc.thePlayer.offGroundTicks % 3) {
                                case 0:
                                    event.setY(mc.thePlayer.motionY = 0.41985F);
                                    MovementUtils.strafe((float) 0.26 - randomAmount() + MovementUtils.getSpeedEffect() * 0.03);
                                    break;
                                case 2:
                                    event.setY(Math.floor(mc.thePlayer.posY + 1) - mc.thePlayer.posY);
                                    break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {

        if (mode.is("Grim 1.17")) {
            if (event.isPost()) {
                sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
            }
        }

        if (event.isPost())
            return;

        if (data == null || data.getPosition() == null || data.getFacing() == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir))
            return;

        if (wdSprint.canDisplay() && !(PlayerUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockLiquid)) {
            if (wdSprint.is("Offset")) {
                if (mc.thePlayer.onGround) {
                    event.setY(event.getY() + 1E-13);
                }

                /*if (mc.thePlayer.onGround && !setOffset && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    MovementUtils.stopXZ();
                    event.setY(event.getY() + 1E-13);
                    setOffset = true;
                }*/
            }
            if (mc.thePlayer.onGround && sprintBoost.get()) {
                mc.thePlayer.motionX *= 1.114 - MovementUtils.getSpeedEffect() * .01 - Math.random() * 1E-4;
                mc.thePlayer.motionZ *= 1.114 - MovementUtils.getSpeedEffect() * .01 - Math.random() * 1E-4;
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event){
        if (data == null || data.getPosition() == null || data.getFacing() == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir)) {
            return;
        }

        if(addons.isEnabled("Block ESP"))
            RenderUtils.renderBlock(data.position,getModule(Interface.class).color(0),true,false);
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {

        if (data == null || data.getPosition() == null || data.getFacing() == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir)) {
            return;
        }

        if (wdKeepY.canDisplay() && !towering() && !towerMoving()) {
            if (isEnabled(Disabler.class) && getModule(Disabler.class).options.isEnabled("Watchdog Motion") && !getModule(Disabler.class).disabled) {
                if (jumpCount >= 1 && blocksPlaced >= 1 && mc.thePlayer.hurtTime == 0 && !isEnabled(Speed.class)) {
                    switch (lowMode.get()) {
                        case "1":
                            switch (mc.thePlayer.offGroundTicks) {
                                case 5:
                                    mc.thePlayer.motionY = -0.1523351824467155;
                                    break;
                                case 8:
                                    mc.thePlayer.motionY = -0.3;
                                    break;
                            }
                            break;
                        case "2":
                            if (mc.thePlayer.offGroundTicks == 5) {
                                mc.thePlayer.motionY = MovementUtils.predictedMotionY(mc.thePlayer.motionY, 2);
                            }
                            break;
                        case "3":
                            switch (mc.thePlayer.offGroundTicks) {
                                case 4:
                                    mc.thePlayer.motionY -= 0.045;
                                    break;
                                case 5:
                                    mc.thePlayer.motionY = -0.19;
                                    break;
                                case 6:
                                    mc.thePlayer.motionY = -0.269;
                                    break;
                                case 7:
                                    mc.thePlayer.motionY = -0.347;
                                    break;
                            }
                            break;
                    }
                }
            }
        }
        
        if(towerMove.is("Low")){
            if(towerMoving()) {
                if (mc.thePlayer.offGroundTicks == 1) {
                    mc.thePlayer.motionY += 0.057f;


                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && !(getModule(Scaffold.class).isEnabled() && mc.gameSettings.keyBindJump.isKeyDown()) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 >= 2) {
                        MovementUtils.strafe(0.48);
                    } else if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 >= 2) {
                        MovementUtils.strafe(0.4);
                    } else if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 == 1) {
                        MovementUtils.strafe(0.405);
                    } else {
                        MovementUtils.strafe(0.33);
                    }
                }

                if (mc.thePlayer.offGroundTicks == 3) {
                    mc.thePlayer.motionY -= 0.1309f;
                }

                if (mc.thePlayer.offGroundTicks == 4) {
                    mc.thePlayer.motionY -= 0.2;
                }
            }
        }

        if (mc.thePlayer.onGround) {
            if ((addons.isEnabled("Keep Y") && !isEnabled(Speed.class) && (mode.is("Telly") || wdKeepY.canDisplay() && !towering() && !towerMoving() && wdKeepY.is("Opal") && start || placed && !start) || addons.isEnabled("Speed Keep Y") && isEnabled(Speed.class) || mode.is("Watchdog") && tellyWhenDiagonal.canDisplay() && tellyWhenDiagonal.get() && !MovementUtils.isMovingStraight()) && MovementUtils.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.thePlayer.jump();
                jumpCount += 1;
            } else {
                jumpCount = 0;
            }
        }

        if (mode.is("God Bridge")) {
            if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && MovementUtils.isMoving() && MovementUtils.isMovingStraight()
                    && !mc.thePlayer.isSneaking()) {
                if (blocksPlaced >= blocksToJump.get()) {
                    mc.thePlayer.jump();
                    blocksPlaced = 0;
                }
            } else {
                blocksPlaced = 0;
            }
        }
    }

    @EventTarget
    public void onMovementInput(MoveInputEvent event) {

        if (data == null || data.getPosition() == null || data.getFacing() == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir)) {
            return;
        }

        if (addons.isEnabled("AD Strafe") && MovementUtils.isMoving() && MovementUtils.isMovingStraight() && mc.currentScreen == null && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCodeDefault()) && mc.thePlayer.onGround) {
            final BlockPos b = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ);
            if (mc.thePlayer.getHorizontalFacing(mc.thePlayer.rotationYaw + 180) == EnumFacing.EAST) {
                if (b.getZ() + 0.5 > mc.thePlayer.posZ) {
                    event.setStrafe(1.0f);
                } else {
                    event.setStrafe(-1.0f);
                }
            } else if (mc.thePlayer.getHorizontalFacing(mc.thePlayer.rotationYaw + 180) == EnumFacing.WEST) {
                if (b.getZ() + 0.5 < mc.thePlayer.posZ) {
                    event.setStrafe(1.0f);
                } else {
                    event.setStrafe(-1.0f);
                }
            } else if (mc.thePlayer.getHorizontalFacing(mc.thePlayer.rotationYaw + 180) == EnumFacing.SOUTH) {
                if (b.getX() + 0.5 < mc.thePlayer.posX) {
                    event.setStrafe(1.0f);
                } else {
                    event.setStrafe(-1.0f);
                }
            } else if (b.getX() + 0.5 > mc.thePlayer.posX) {
                event.setStrafe(1.0f);
            } else {
                event.setStrafe(-1.0f);
            }
        }

        if (addons.isEnabled("Sneak")) {

            double dif = 0.5;
            BlockPos blockPos = new BlockPos(mc.thePlayer).down();

            for (EnumFacing side : EnumFacing.values()) {
                if (side.getAxis() == EnumFacing.Axis.Y) {
                    continue;
                }

                BlockPos neighbor = blockPos.offset(side);

                if (PlayerUtils.isReplaceable(neighbor)) {
                    double calcDif = (side.getAxis() == EnumFacing.Axis.Z) ?
                            Math.abs(neighbor.getZ() + 0.5 - mc.thePlayer.posZ) :
                            Math.abs(neighbor.getX() + 0.5 - mc.thePlayer.posX) - 0.5;

                    if (calcDif < dif) {
                        dif = calcDif;
                    }
                }
            }

            if (mc.thePlayer.onGround && (PlayerUtils.isReplaceable(blockPos) || dif < sneakDistance.get()) && blocksPlaced == blocksToSneak.get()) {
                event.setSneaking(true);
            }
            if (blocksPlaced > blocksToSneak.get())
                blocksPlaced = 0;
        }
    }

    @EventTarget
    public void onJump(JumpEvent event) {

        if (data == null || data.getPosition() == null || data.getFacing() == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir)) {
            return;
        }

        if (mode.is("Watchdog")) {
            if (towerMoving()) {
                if (towerMove.get().equals("Jump")) {
                    event.setCancelled(!canJump);
                }
            }
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {

        if (Objects.equals(switchBlock.get(), "Silent")) {
            if (event.getPacket() instanceof C09PacketHeldItemChange) {
                if (((C09PacketHeldItemChange) event.getPacket()).getSlotId() != getBlockSlot()) {
                    event.setCancelled(true);
                }
            }
        }

        if (mode.is("Watchdog") && mc.thePlayer.onGround && mc.gameSettings.keyBindJump.isKeyDown() && towerMove.is("Jump") && event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            canJump = true;
        }
    }

    @EventTarget
    public void onAfterJump(AfterJumpEvent event) {

        if (data == null || data.getPosition() == null || data.getFacing() == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir)) {
            return;
        }

        if (wdKeepY.canDisplay() && !towering() && !towerMoving() && sprint.get() && mc.thePlayer.onGround && MovementUtils.isMoving()) {
            MovementUtils.strafe(MovementUtils.getSpeed() * (MovementUtils.isMovingStraight() ? straightSpeed.get() : diagonalSpeed.get()));
        }

        if (mode.is("Watchdog") && mc.gameSettings.keyBindJump.isKeyDown() && towerMove.is("Jump") && canJump) {
            MovementUtils.strafe(0.4);
        }
    }

    @EventTarget
    public void onSafeWalk(SafeWalkEvent event) {
        if (addons.isEnabled("Safe Walk") && mc.thePlayer.onGround || addons.isEnabled("Safe Walk When No Data") && data == null) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        setEnabled(false);
    }

    public boolean towering() {
        return Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && !MovementUtils.isMoving();
    }

    public boolean towerMoving() {
        return Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && MovementUtils.isMoving();
    }

    private double getWatchdogKeepY() {
        if (towering() || towerMoving()) {
            return mc.thePlayer.getEntityBoundingBox().minY;
        }
        if (wdKeepY.is("Normal")) {
            return mc.thePlayer.ticksExisted % 6 != 0 ? onGroundY : mc.thePlayer.getEntityBoundingBox().minY;
        }
        if (wdKeepY.is("Opal")) {
            if (FallDistanceComponent.distance > 0 && !start) {
                start = true;
                return mc.thePlayer.getEntityBoundingBox().minY;
            }
            return onGroundY;
        }

        if (wdKeepY.is("None")) {
            return onGroundY;
        }

        return mc.thePlayer.getEntityBoundingBox().minY;
    }

    public double groundDistance() {
        for (int i = 1; i <= 20; i++) {
            if (!mc.thePlayer.onGround && !(PlayerUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - ((double) i / 10), mc.thePlayer.posZ)) instanceof BlockAir)) {
                return ((double) i / 10);
            }
        }
        return -1;
    }

    public int getBlockSlot() {
        int slot = -1, size = 0;

        if (getBlockCount() == 0) {
            return -1;
        }

        for (int i = 36; i < 45; i++) {
            final Slot s = mc.thePlayer.inventoryContainer.getSlot(i);

            if (s.getHasStack()) {
                final Item item = s.getStack().getItem();
                final ItemStack is = s.getStack();

                if (item instanceof ItemBlock && !blacklistedBlocks.contains(((ItemBlock) item).getBlock()) && (biggestStack.get() && is.stackSize > size || !biggestStack.get())) {
                    size = is.stackSize;
                    slot = i;
                }
            }
        }

        return slot - 36;
    }

    public int getBlockCount() {
        int blockCount = 0;

        for (int i = 36; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;

            final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (!(is.getItem() instanceof ItemBlock && !blacklistedBlocks.contains(((ItemBlock) is.getItem()).getBlock()))) {
                continue;
            }

            blockCount += is.stackSize;
        }

        return blockCount;
    }

    public BlockData getBlockData(BlockPos pos) {
        if (previousBlock != null && previousBlock.getY() > mc.thePlayer.posY) {
            previousBlock = null;
        }
        for (int lastCheck = 0; lastCheck < 2; lastCheck++) {
            for (int i = 0; i < offsets.length; i++) {
                BlockPos newPos = pos.add(offsets[i]);
                Block block = PlayerUtils.getBlock(newPos);
                if (newPos.equals(previousBlock)) {
                    return new BlockData(newPos, facings[i]);
                }
                if (lastCheck == 0) {
                    continue;
                }
                if (!block.getMaterial().isReplaceable() && !isInteractable(block)) {
                    return new BlockData(newPos, facings[i]);
                }
            }
        }
        BlockPos[] additionalOffsets = {
                pos.add(-1, 0, 0),
                pos.add(1, 0, 0),
                pos.add(0, 0, 1),
                pos.add(0, 0, -1),
                pos.add(0, -1, 0),
        };
        for (int lastCheck = 0; lastCheck < 2; lastCheck++) {
            for (BlockPos additionalPos : additionalOffsets) {
                for (int i = 0; i < offsets.length; i++) {
                    BlockPos newPos = additionalPos.add(offsets[i]);
                    Block block = PlayerUtils.getBlock(newPos);
                    if (newPos.equals(previousBlock)) {
                        return new BlockData(newPos, facings[i]);
                    }
                    if (lastCheck == 0) {
                        continue;
                    }
                    if (!block.getMaterial().isReplaceable() && !isInteractable(block)) {
                        return new BlockData(newPos, facings[i]);
                    }
                }
            }
        }
        BlockPos[] additionalOffsets2 = {
                new BlockPos(-1, 0, 0),
                new BlockPos(1, 0, 0),
                new BlockPos(0, 0, 1),
                new BlockPos(0, 0, -1),
                new BlockPos(0, -1, 0),
        };
        for (int lastCheck = 0; lastCheck < 2; lastCheck++) {
            for (BlockPos additionalPos2 : additionalOffsets2) {
                for (BlockPos additionalPos : additionalOffsets) {
                    for (int i = 0; i < offsets.length; i++) {
                        BlockPos newPos = additionalPos2.add(additionalPos.add(offsets[i]));
                        Block block = PlayerUtils.getBlock(newPos);
                        if (newPos.equals(previousBlock)) {
                            return new BlockData(newPos, facings[i]);
                        }
                        if (lastCheck == 0) {
                            continue;
                        }
                        if (!block.getMaterial().isReplaceable() && !isInteractable(block)) {
                            return new BlockData(newPos, facings[i]);
                        }
                    }
                }
            }
        }
        return null;
    }
    public static boolean isInteractable(Block block) {
        return block instanceof BlockFurnace || block instanceof BlockTrapDoor || block instanceof BlockDoor || block instanceof BlockJukebox || block instanceof BlockFenceGate || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockEnchantmentTable || block instanceof BlockBrewingStand || block instanceof BlockBed || block instanceof BlockDispenser || block instanceof BlockHopper || block instanceof BlockAnvil || block instanceof BlockNote || block instanceof BlockWorkbench;
    }


    public float getYawBasedPitch(BlockPos blockPos, EnumFacing facing, float currentYaw, float lastPitch, int maxPitch) {
        float increment = (float) (Math.random() / 20.0) + 0.05F;

        for (float i = (float) maxPitch; i > 45.0F; i -= increment) {
            MovingObjectPosition ray = RotationUtils.rayTrace(new float[]{currentYaw, i}, mc.playerController.getBlockReachDistance(), 1);
            if (ray.getBlockPos() == null || ray.sideHit == null) {
                return lastPitch;
            }

            if (ray.getBlockPos().equalsBlockPos(blockPos) && ray.sideHit == facing) {
                return i;
            }
        }

        return lastPitch;
    }

    private Vec3 getVec3(BlockData data) {
        BlockPos pos = data.getPosition();
        EnumFacing face = data.getFacing();
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        x += face.getFrontOffsetX() / 2.0D;
        z += face.getFrontOffsetZ() / 2.0D;
        y += face.getFrontOffsetY() / 2.0D;

        return new Vec3(x, y, z);
    }
    public static Vec3 getHitVecOptimized(BlockPos blockPos, EnumFacing facing) {
        Vec3 eyes = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        return MathUtils.closestPointOnFace(new AxisAlignedBB(blockPos, blockPos.add(1, 1, 1)), facing, eyes);
    }

    private double randomAmount() {
        return 0.0008 + Math.random() * 0.008;
    }

    @Getter
    @AllArgsConstructor
    public static class BlockData {
        private final BlockPos position;
        private final EnumFacing facing;
    }

    enum HoverState {
        JUMP,
        FALL,
        DONE
    }
}
