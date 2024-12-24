package wtf.moonlight.features.modules.impl.world;

import net.minecraft.block.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.player.*;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.combat.KillAura;
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

import java.util.*;

@ModuleInfo(name = "Scaffold", category = ModuleCategory.World)
public class Scaffold extends Module {
    private final ModeValue switchBlock = new ModeValue("Switch Block", new String[]{"Silent", "Switch", "Spoof"}, "Spoof", this);
    private final BoolValue biggestStack = new BoolValue("Biggest Stack", false, this);
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Normal", "Telly", "Watchdog"}, "Normal", this);
    private final SliderValue minTellyTicks = new SliderValue("Min Telly Ticks", 2, 1, 5, this, () -> mode.is("Telly"));
    private final SliderValue maxTellyTicks = new SliderValue("Max Telly Ticks", 4, 1, 5, this, () -> mode.is("Telly"));
    private final ModeValue rotations = new ModeValue("Rotations", new String[]{"Normal", "God Bridge", "Reverse", "Custom", "Unfair Pitch","Hypixel Test"}, "Normal", this);
    private final ModeValue rotationsHitVec = new ModeValue("Rotation Hit Vec", new String[]{"Centre","Closest"}, "Centre", this,() -> rotations.is("Normal"));
    private final ModeValue godBridgePitch = new ModeValue("God Bridge Pitch Mode", new String[]{"Normal", "Custom"}, "Custom", this, () -> rotations.is("God Bridge") && !mode.is("Grim 1.17"));
    private final SliderValue customYaw = new SliderValue("Custom Yaw", 180, 0, 180, 1, this, () -> rotations.is("Custom"));
    private final SliderValue minPitch = new SliderValue("Min Pitch Range", 55, 50, 90, .1f, this, () -> godBridgePitch.canDisplay() && godBridgePitch.is("Custom"));
    public final SliderValue maxPitch = new SliderValue("Max Pitch Range", 75, 50, 90, .1f, this, () -> godBridgePitch.canDisplay() && godBridgePitch.is("Custom"));
    private final BoolValue customRotationSetting = new BoolValue("Custom Rotation Setting", false, this);
    private final SliderValue minYawRotSpeed = new SliderValue("Min Yaw Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final SliderValue minPitchRotSpeed = new SliderValue("Min Pitch Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final SliderValue maxYawRotSpeed = new SliderValue("Max Yaw Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final SliderValue maxPitchRotSpeed = new SliderValue("Max Pitch Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get());
    public final SliderValue maxYawAcceleration = new SliderValue("Max Yaw Acceleration", 100, 0f, 100f, 1f, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get());
    public final SliderValue maxPitchAcceleration = new SliderValue("Max Pitch Acceleration", 100, 0f, 100f, 1f, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get());
    public final SliderValue accelerationError = new SliderValue("Acceleration Error", 0f, 0f, 1f, 0.01f, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get());
    public final SliderValue constantError = new SliderValue("Constant Error", 0f, 0f, 10f, 0.01f, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get());
    public final BoolValue smoothlyResetRotation = new BoolValue("Smoothly Reset Rotation", true, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final MultiBoolValue addons = new MultiBoolValue("Addons", Arrays.asList(
            new BoolValue("Sprint", true),
            new BoolValue("Swing", true),
            new BoolValue("Movement Fix", true),
            new BoolValue("Ray Trace", true),
            new BoolValue("Keep Y", false),
            new BoolValue("Speed Keep Y", false),
            new BoolValue("Safe Walk", false),
            new BoolValue("Safe Walk When No Data", false),
            new BoolValue("Snap", false),
            new BoolValue("AD Strafe", false),
            new BoolValue("Hover", false),
            new BoolValue("Sneak", false),
            new BoolValue("Jump", false),
            new BoolValue("Target Block ESP", false)
    ), this);
    private final SliderValue blocksToJump = new SliderValue("Blocks To Jump", 7, 1, 8, this, () -> addons.isEnabled("Jump"));
    private final SliderValue blocksToSneak = new SliderValue("Blocks To Sneak", 7, 1, 8, this, () -> addons.isEnabled("Sneak"));
    private final SliderValue sneakDistance = new SliderValue("Sneak Distance", 0, 0, 0.5f, 0.01f, this, () -> addons.isEnabled("Sneak"));
    private final ModeValue tower = new ModeValue("Tower", new String[]{"Jump", "Vanilla","Watchdog","Watchdog Test"}, "Jump", this,() -> !mode.is("Telly"));
    private final ModeValue towerMove = new ModeValue("Tower Move", new String[]{"Jump", "Vanilla","Watchdog","Watchdog Test","Low"}, "Jump", this,() -> !mode.is("Telly"));
    private final ModeValue wdSprint = new ModeValue("WD Sprint Mode", new String[]{"Beside", "Bottom","Offset"}, "Bottom", this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && !addons.isEnabled("Keep Y"));
    private final BoolValue sprintBoost = new BoolValue("Sprint Boost Test", true, this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && !addons.isEnabled("Keep Y"));
    private final ModeValue wdKeepY = new ModeValue("WD Keep Y Mode", new String[]{"Normal", "Opal", "None"}, "Opal", this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && addons.isEnabled("Keep Y"));
    private final BoolValue unPatch = new BoolValue("Un Patch Test", true, this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && (addons.isEnabled("Keep Y") || addons.isEnabled("Speed Keep Y")));
    private final SliderValue straightSpeed = new SliderValue("Keep Y Straight Speed", 1, 0.5f, 1f, 0.01f, this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && addons.isEnabled("Keep Y"));
    private final SliderValue diagonalSpeed = new SliderValue("Keep Y Diagonal Speed", 0.95f, 0.5f, 1f, 0.01f, this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && addons.isEnabled("Keep Y"));
    public final ModeValue counter = new ModeValue("Counter", new String[]{"None", "Simple", "Normal", "Exhibition"}, "Normal", this);
    public PlaceData data;
    private int oloSlot = -1;
    private double onGroundY;
    private BlockPos targetBlock;
    private BlockPos previousBlock;
    private float[] previousRotation;
    private int towerTick;
    private int towerMoveTick;
    private int blocksPlaced;
    private boolean placing;
    private int tellyTicks;
    private boolean start;
    private boolean placed;
    private boolean isOnRightSide;
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

        oloSlot = mc.thePlayer.inventory.currentItem;
        onGroundY = mc.thePlayer.getEntityBoundingBox().minY;
        previousRotation = new float[]{mc.thePlayer.rotationYaw + 180,82};

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
    }

    @Override
    public void onDisable() {
        switch (switchBlock.get()) {
            case "Silent":
                sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                break;
            case "Switch":
                mc.thePlayer.inventory.currentItem = oloSlot;
                break;
            case "Spoof":
                mc.thePlayer.inventory.currentItem = oloSlot;
                SpoofSlotUtils.stopSpoofing();
                break;
        }
        previousRotation = null;
        blocksPlaced = 0;
        placing = false;
        tellyTicks = 0;
        start = false;

        if (wdSprint.canDisplay() && sprintBoost.get()) {
            mc.thePlayer.motionX *= .8;
            mc.thePlayer.motionZ *= .8;
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());

        if (getBlockSlot() == -1)
            return;

        switch (switchBlock.get()) {
            case "Silent":
                sendPacketNoEvent(new C09PacketHeldItemChange(getBlockSlot()));
                break;
            case "Switch":
                mc.thePlayer.inventory.currentItem = getBlockSlot();
                break;
            case "Spoof":
                mc.thePlayer.inventory.currentItem = getBlockSlot();
                SpoofSlotUtils.startSpoofing(oloSlot);
                break;
        }

        data = null;

        if (mc.thePlayer.onGround) {
            onGroundY = mc.thePlayer.getEntityBoundingBox().minY;
        }

        double posY = mc.thePlayer.getEntityBoundingBox().minY;

        if (hoverState != HoverState.DONE || (addons.isEnabled("Keep Y") || addons.isEnabled("Speed Keep Y") && isEnabled(Speed.class) && !mc.gameSettings.keyBindJump.isKeyDown())) {
            posY = onGroundY;
        }

        if (wdKeepY.canDisplay() && wdKeepY.is("Normal") && !towering() && !towering()) {
            posY = mc.thePlayer.ticksExisted % 6 != 0 ? onGroundY : mc.thePlayer.getEntityBoundingBox().minY;
        }

        if (towerMoving() || towering()) {
            onGroundY = posY = mc.thePlayer.getEntityBoundingBox().minY;
        }

        if (wdKeepY.canDisplay() &&  wdKeepY.is("Opal") && !towering() && !towering()) {
            if (FallDistanceComponent.distance > 0 && !start) {
                posY = mc.thePlayer.getEntityBoundingBox().minY;
                start = true;
            }
        }

        targetBlock = new BlockPos(mc.thePlayer.posX, posY - 1, mc.thePlayer.posZ);


        if (wdKeepY.canDisplay() &&  wdKeepY.is("Opal")) {
            if (start && !towering() && !towerMoving()) {
                targetBlock = targetBlock.add(0, -1, 0);
                if (mc.thePlayer.fallDistance > 0 && groundDistance() > 0 && MovementUtils.isMoving()) {
                    targetBlock = targetBlock.add(0, 1, 0);
                }
            }
        }

        if (tower.canDisplay() && towering() && (tower.is("Watchdog") || tower.is("Watchdog Test")) && !placing) {
            targetBlock = targetBlock.add(Math.max(-1, Math.min(1, Math.round(mc.thePlayer.posX) - mc.thePlayer.posX)), 0, 0);
        }

        if (mode.is("Telly") && mc.thePlayer.onGround) {
            tellyTicks = MathUtils.randomizeInt((int) minTellyTicks.get(), (int) maxTellyTicks.get());
        }

        data = findBlock(targetBlock);

        if (data == null || data.blockPos == null || data.facing == null || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(targetBlock).getBlock() instanceof BlockAir)) {
            return;
        }

        if (mode.is("Telly") && mc.thePlayer.onGround) {
            tellyTicks = MathUtils.randomizeInt((int) minTellyTicks.get(), (int) maxTellyTicks.get());
        }

        if (addons.isEnabled("Sprint")) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
            mc.thePlayer.setSprinting(false);
        }

        if (tower.canDisplay() && (!tower.is("Jump") && towering() || !towerMove.is("Jump") && towerMoving())) {
            hoverState = HoverState.JUMP;
            blocksPlaced = 0;
        }

        if(!MovementUtils.isMoving()){
            start = false;
        }

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

        float[] rotation = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};

        switch (rotations.get()) {
            case "Normal": {
                Vec3 hitVec = null;

                switch (rotationsHitVec.get()) {
                    case "Centre":
                        hitVec = getVec3(data);
                        break;
                    case "Closest":
                        hitVec = getHitVecOptimized(data.blockPos, data.facing);
                        break;
                }

                assert hitVec != null;
                rotation = RotationUtils.getRotations(hitVec);
            }
            break;

            case "God Bridge": {
                float yaw;
                float pitch;
                float finalYaw;
                float movingYaw = MovementUtils.getRawDirection() + 180;
                switch (godBridgePitch.get()) {
                    case "Custom": {

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

                        pitch = getYawBasedPitch(data.blockPos, data.facing, finalYaw, previousRotation[1], minPitch.get(), maxPitch.get());

                        rotation = new float[]{finalYaw, pitch};
                    }
                    break;


                    case "Normal": {
                        yaw = MovementUtils.isMovingStraight() ? (movingYaw + (isOnRightSide ? 45 : -45)) : movingYaw;
                        finalYaw = Math.round(yaw / 45f) * 45f;
                        pitch = getYawBasedPitch(data.blockPos, data.facing, finalYaw, previousRotation[1], 74, RotationUtils.getRotations(getVec3(data))[1]);
                        rotation = new float[]{finalYaw, pitch};
                    }
                    break;
                }
            }
            break;
            case "Custom": {
                rotation = new float[]{mc.thePlayer.rotationYaw + customYaw.get(), getYawBasedPitch(data.blockPos, data.facing, mc.thePlayer.rotationYaw + 180, previousRotation[1], 50, RotationUtils.getRotations(getVec3(data))[1])};
            }
            break;
            case "Reverse": {
                rotation = new float[]{MovementUtils.getRawDirection() + 180, getYawBasedPitch(data.blockPos, data.facing, MovementUtils.getRawDirection() + 180, previousRotation[1], 50, RotationUtils.getRotations(getVec3(data))[1])};
            }
            break;
            case "Hypixel Test": {
                rotation = RotationUtils.getRotations(getVec3(data));
                if (Math.abs(MathHelper.wrapAngleTo180_double(RotationUtils.getRotations(getVec3(data))[0] - MovementUtils.getRawDirection() - 102)) < Math.abs(MathHelper.wrapAngleTo180_double(RotationUtils.getRotations(getVec3(data))[0] - MovementUtils.getRawDirection() + 102))) {
                    rotation[0] = (float) (MovementUtils.getRawDirection() + 139 + Math.random());
                } else {
                    rotation[0] = (float) (MovementUtils.getRawDirection() - 139 - Math.random());
                }
            }
            break;
            case "Unfair Pitch": {
                rotation = new float[]{mc.thePlayer.rotationYaw, getYawBasedPitch(data.blockPos, data.facing, mc.thePlayer.rotationYaw, previousRotation[1], 91, 100)};
            }

            break;
        }


        if (unPatch.canDisplay() && addons.isEnabled("Speed Keep Y") && isEnabled(Speed.class) && unPatch.get() && !towering() && !towerMoving()) {
            rotation = RotationUtils.getRotations(getVec3(data));
        }

        if (unPatch.canDisplay() && (addons.isEnabled("Speed Keep Y") && isEnabled(Speed.class) || !addons.isEnabled("Speed Keep Y")) && unPatch.get() && mc.thePlayer.onGround && !towering() && !towerMoving()) {
            rotation = new float[]{mc.thePlayer.rotationYaw, 0f};
        }

        if (tower.canDisplay() && (tower.is("Watchdog") || tower.is("Watchdog Test")) && towering()
               // || towerMoving() && (towerMove.is("Watchdog") || towerMove.is("Watchdog Test"))
        ) {
            rotation = RotationUtils.getRotations(getVec3(data));
        }

        previousRotation = rotation;

        if (addons.isEnabled("Snap") && PlayerUtils.getBlock(targetBlock) instanceof BlockAir || !addons.isEnabled("Snap") && !mode.is("Telly") || mode.is("Telly") && mc.thePlayer.offGroundTicks >= tellyTicks) {

            if (customRotationSetting.get()) {
                RotationUtils.setRotation(rotation, addons.isEnabled("Movement Fix") ? MovementCorrection.SILENT : MovementCorrection.OFF, MathUtils.randomizeInt(minYawRotSpeed.get(), maxYawRotSpeed.get()), MathUtils.randomizeInt(minPitchRotSpeed.get(), maxPitchRotSpeed.get()) , maxYawAcceleration.get(), maxPitchAcceleration.get(), accelerationError.get(), constantError.get(), smoothlyResetRotation.get());
            } else {
                RotationUtils.setRotation(rotation, addons.isEnabled("Movement Fix") ? MovementCorrection.SILENT : MovementCorrection.OFF);
            }

            place(data.blockPos, data.facing, getVec3(data));
        }
    }

    @EventTarget
    public void onSafeWalk(SafeWalkEvent event) {
        if (addons.isEnabled("Safe Walk") && mc.thePlayer.onGround || addons.isEnabled("Safe Walk When No Data") && data == null) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onMovementInput(MoveInputEvent event) {

        if (data == null || data.blockPos == null || data.facing == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(targetBlock).getBlock() instanceof BlockAir)) {
            return;
        }

        if (mode.is("Watchdog") && towerMoving() && towerMove.is("Jump")) {
            event.setJumping(placing);
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
    public void onStrafe(StrafeEvent event) {

        if (data == null || data.blockPos == null || data.facing == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir)) {
            return;
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
            if (((addons.isEnabled("Keep Y") || mode.is("Telly")|| wdKeepY.canDisplay())) && MovementUtils.isMoving() && !towering() && !towerMoving() && (!isEnabled(Speed.class))) {
                mc.thePlayer.jump();
            }
        }

        if (addons.isEnabled("Jump")) {
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
    public void onMove(MoveEvent event) {

        if (data == null || data.blockPos == null || data.facing == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir))
            return;

        if (tower.canDisplay()) {
            switch (tower.get()) {
                case "Vanilla":
                    if (!mc.thePlayer.isPotionActive(Potion.jump)) {
                        if (towering()) {
                            event.setY(mc.thePlayer.motionY = 0.42);
                        }
                    }
                    break;
                case "Watchdog":
                    if (!mc.thePlayer.isPotionActive(Potion.jump)) {

                        if (towering()) {
                            if (mc.thePlayer.onGround) {
                                event.setY(mc.thePlayer.motionY = 0.42);
                            }
                            switch (mc.thePlayer.offGroundTicks % 3) {
                                case 0:
                                    event.setY(mc.thePlayer.motionY = 0.4198499917984009);
                                    MovementUtils.strafe((float) 0.26 + MovementUtils.getSpeedEffect() * 0.03);
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

        if (towerMove.canDisplay()) {
            switch (towerMove.get()) {
                case "Vanilla":
                    if (MovementUtils.isMoving() && MovementUtils.getSpeed() > 0.1 && !mc.thePlayer.isPotionActive(Potion.jump)) {
                        if (towerMoving()) {
                            mc.thePlayer.motionY = 0.42f;
                        }
                    }
                    break;
                case "Watchdog":
                    if (MovementUtils.isMoving() && MovementUtils.getSpeed() > 0.1 && !mc.thePlayer.isPotionActive(Potion.jump)) {
                        if (towerMoving()) {
                                if (mc.thePlayer.onGround) {
                                    event.setY(mc.thePlayer.motionY = 0.42);
                                }
                                switch (mc.thePlayer.offGroundTicks % 3) {
                                    case 0:
                                        event.setY(mc.thePlayer.motionY = 0.41985F);
                                        MovementUtils.strafe((float) 0.26 + MovementUtils.getSpeedEffect() * 0.03);
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
    public void onAfterJump(AfterJumpEvent event) {

        if (data == null || data.blockPos == null || data.facing == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir)) {
            return;
        }

        if (wdKeepY.canDisplay() && !towering() && !towerMoving() && addons.isEnabled("Sprint") && mc.thePlayer.onGround && MovementUtils.isMoving()) {
            MovementUtils.strafe(MovementUtils.getSpeed() * (MovementUtils.isMovingStraight() ? straightSpeed.get() : diagonalSpeed.get()));
        }

        if (mode.is("Watchdog") && mc.gameSettings.keyBindJump.isKeyDown() && towerMove.is("Jump") && placing) {
            MovementUtils.strafe(0.4);
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {

        if (event.isPost())
            return;

        if (data == null || data.blockPos == null || data.facing == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir))
            return;

        if (wdSprint.canDisplay() && !(PlayerUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockLiquid)) {
            if (wdSprint.is("Offset")) {
                if (mc.thePlayer.onGround) {
                    event.setY(event.getY() + 1E-13);
                }
            }
            if (mc.thePlayer.onGround && sprintBoost.get()) {
                mc.thePlayer.motionX *= 1.12 - MovementUtils.getSpeedEffect() * .01 - Math.random() * 1E-4;
                mc.thePlayer.motionZ *= 1.12 - MovementUtils.getSpeedEffect() * .01 - Math.random() * 1E-4;
            }
        }
        if (tower.canDisplay()) {
            switch (tower.get()) {
                case "Watchdog Test":
                    if (!mc.thePlayer.isPotionActive(Potion.jump)) {
                        if (towering()) {
                            int valY = (int) Math.round((event.y % 1) * 10000);
                            if (valY == 0) {
                                mc.thePlayer.motionY = 0.42F;
                            } else if (valY > 4000 && valY < 4300) {
                                mc.thePlayer.motionY = 0.33;
                            } else if (valY > 7000) {
                                mc.thePlayer.motionY = 1 - mc.thePlayer.posY % 1;
                            }
                        }

                    }
                    break;
            }
        }

        if (towerMove.canDisplay()) {
            switch (towerMove.get()) {
                case "Watchdog Test":
                    if (MovementUtils.isMoving() && MovementUtils.getSpeed() > 0.1 && !mc.thePlayer.isPotionActive(Potion.jump)) {
                        if (towerMoving()) {
                            int valY = (int) Math.round((event.y % 1) * 10000);
                            if (valY == 0) {
                                mc.thePlayer.motionY = 0.42F;
                                MovementUtils.strafe((float) 0.26 + MovementUtils.getSpeedEffect() * 0.03);
                            } else if (valY > 4000 && valY < 4300) {
                                mc.thePlayer.motionY = 0.33;
                                MovementUtils.strafe((float) 0.26 + MovementUtils.getSpeedEffect() * 0.03);
                            } else if (valY > 7000) {
                                mc.thePlayer.motionY = 1 - mc.thePlayer.posY % 1;
                            }
                        }
                    }
                    break;
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event){
        if (data == null || data.blockPos == null || data.facing == null || getBlockSlot() == -1 || isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && !(mc.theWorld.getBlockState(getModule(Scaffold.class).targetBlock).getBlock() instanceof BlockAir))
            return;

        if(addons.isEnabled("Target Block ESP")){
            RenderUtils.renderBlock(data.blockPos,getModule(Interface.class).color(0,100),false,true);
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

    public double groundDistance() {
        for (int i = 1; i <= 20; i++) {
            if (!mc.thePlayer.onGround && !(PlayerUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - ((double) i / 10), mc.thePlayer.posZ)) instanceof BlockAir)) {
                return ((double) i / 10);
            }
        }
        return -1;
    }

    private static boolean isInteractable(Block block) {
        return block instanceof BlockFurnace || block instanceof BlockFenceGate || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockEnchantmentTable || block instanceof BlockBrewingStand || block instanceof BlockBed || block instanceof BlockDropper || block instanceof BlockDispenser || block instanceof BlockHopper || block instanceof BlockAnvil || block == Blocks.crafting_table;
    }

    private void place(BlockPos pos,EnumFacing facing,Vec3 hitVec) {

        placing = false;
        if (!addons.isEnabled("Ray Trace")) {
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, facing, hitVec)) {
                if (addons.isEnabled("Swing")) {
                    mc.thePlayer.swingItem();
                    mc.getItemRenderer().resetEquippedProgress();
                } else
                    mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                placing = true;
                blocksPlaced += 1;
                placed = true;

                if (facing == EnumFacing.UP && wdKeepY.canDisplay() && wdKeepY.is("Opal")) {
                    start = true;
                }
            }
            previousBlock = data.blockPos.offset(data.facing);
        } else {
            MovingObjectPosition ray = RotationUtils.rayTrace(4.5, 1);
            if (ray.getBlockPos().equalsBlockPos(pos)) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), ray.getBlockPos(), ray.sideHit, ray.hitVec)) {
                    if (addons.isEnabled("Swing")) {
                        mc.thePlayer.swingItem();
                        mc.getItemRenderer().resetEquippedProgress();
                    } else
                        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                    placing = true;
                    blocksPlaced += 1;
                    placed = true;

                    if (facing == EnumFacing.UP && wdKeepY.canDisplay() && wdKeepY.is("Opal")) {
                        start = true;
                    }
                }
            }
            previousBlock = ray.getBlockPos().offset(ray.sideHit);
        }
    }

    public float getYawBasedPitch(BlockPos blockPos, EnumFacing facing, float currentYaw, float lastPitch,float minPitch, float maxPitch) {
        float increment = (float) (Math.random() / 20.0) + 0.05F;

        for (float i = maxPitch; i > minPitch; i -= increment) {
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

    private Vec3 getVec3(PlaceData data) {
        BlockPos pos = data.blockPos;
        EnumFacing face = data.facing;
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

    private PlaceData findBlock(BlockPos pos) {
        EnumFacing[] facings = {EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.UP};
        BlockPos[] offsets = {new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};

        if (previousBlock != null && previousBlock.getY() > mc.thePlayer.posY) {
            previousBlock = null;
        }
        for (int lastCheck = 0; lastCheck < 2; lastCheck++) {
            for (int i = 0; i < offsets.length; i++) {
                BlockPos newPos = pos.add(offsets[i]);
                Block block = mc.theWorld.getBlockState(newPos).getBlock();
                if (newPos.equals(previousBlock)) {
                    return new PlaceData(facings[i], newPos);
                }
                if (lastCheck == 0) {
                    continue;
                }
                if (!block.getMaterial().isReplaceable() && !isInteractable(block)) {
                    return new PlaceData(facings[i], newPos);
                }
            }
        }

        BlockPos[] additionalOffsets = { // adjust these for perfect placement
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
                    Block block = mc.theWorld.getBlockState(newPos).getBlock();
                    if (newPos.equals(previousBlock)) {
                        return new PlaceData(facings[i], newPos);
                    }
                    if (lastCheck == 0) {
                        continue;
                    }
                    if (!block.getMaterial().isReplaceable() && !isInteractable(block)) {
                        return new PlaceData(facings[i], newPos);
                    }
                }
            }
        }
        BlockPos[] additionalOffsets2 = { // adjust these for perfect placement
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
                        Block block = mc.theWorld.getBlockState(newPos).getBlock();
                        if (newPos.equals(previousBlock)) {
                            return new PlaceData(facings[i], newPos);
                        }
                        if (lastCheck == 0) {
                            continue;
                        }
                        if (!block.getMaterial().isReplaceable() && !isInteractable(block)) {
                            return new PlaceData(facings[i], newPos);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static class PlaceData {
        public EnumFacing facing;
        public BlockPos blockPos;

        PlaceData(EnumFacing enumFacing, BlockPos blockPos) {
            this.facing = enumFacing;
            this.blockPos = blockPos;
        }
    }

    enum HoverState {
        JUMP,
        FALL,
        DONE
    }
}
