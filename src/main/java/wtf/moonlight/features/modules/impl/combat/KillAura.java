package wtf.moonlight.features.modules.impl.combat;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.fixes.AttackOrder;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.features.modules.impl.world.BedNuker;
import wtf.moonlight.features.modules.impl.world.Scaffold;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.MultiBoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.animations.ContinualAnimation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.packet.BlinkComponent;
import wtf.moonlight.utils.player.*;
import wtf.moonlight.utils.render.RenderUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.*;

@ModuleInfo(name = "KillAura", category = ModuleCategory.Combat, key = Keyboard.KEY_R)
public class KillAura extends Module {
    private final SliderValue fov = new SliderValue("FOV",180,1,180,this);
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Switch", "Single"}, "Switch", this);
    public final SliderValue switchDelayValue = new SliderValue("SwitchDelay", 15, 0, 20, this, () -> mode.is("Switch"));
    private final ModeValue priority = new ModeValue("Priority", new String[]{"Range", "Armor", "Health", "HurtTime", "FOV"}, "Health", this);
    private final ModeValue aimMode = new ModeValue("Aim Position", new String[]{"Head", "Torso", "Legs", "Nearest", "Test"}, "Nearest", this);
    private final BoolValue inRange = new BoolValue("Rotation In Range", false, this);
    private final SliderValue minAimRange = new SliderValue("Lowest Aim Range", 1, 0, 1, 0.05f, this, inRange::get);
    private final SliderValue maxAimRange = new SliderValue("Highest Aim Range", 1, 0, 1, 0.05f, this, inRange::get);
    private final BoolValue heuristics = new BoolValue("Heuristics", false, this);
    private final BoolValue bruteforce = new BoolValue("Bruteforce", true, this);
    private final BoolValue customRotationSetting = new BoolValue("Custom Rotation Setting", false, this);
    private final SliderValue minYawRotSpeed = new SliderValue("Min Yaw Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.get());
    private final SliderValue minPitchRotSpeed = new SliderValue("Min Pitch Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.get());
    private final SliderValue maxYawRotSpeed = new SliderValue("Max Yaw Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.get());
    private final SliderValue maxPitchRotSpeed = new SliderValue("Max Pitch Rotation Speed", 180, 0, 180, 1, this, () -> customRotationSetting.get());
    public final SliderValue maxYawAcceleration = new SliderValue("Max Yaw Acceleration", 100, 0f, 100f, 1f, this, () -> customRotationSetting.get());
    public final SliderValue maxPitchAcceleration = new SliderValue("Max Pitch Acceleration", 100, 0f, 100f, 1f, this, () -> customRotationSetting.get());
   public final SliderValue accelerationError = new SliderValue("Acceleration Error", 0f, 0f, 1f, 0.01f, this, () -> customRotationSetting.get());
    public final SliderValue constantError = new SliderValue("Constant Error", 0f, 0f, 10f, 0.01f, this, () -> customRotationSetting.get());
    public final BoolValue smoothlyResetRotation = new BoolValue("Smoothly Reset Rotation", true, this, customRotationSetting::get);
    private final BoolValue shake = new BoolValue("Shake", false, this);
    public final ModeValue shakeMode = new ModeValue("Shake Mode", new String[]{"Random", "Secure Random", "Noise"}, "Noise", this,shake::get);
    private final SliderValue pitchShakeRange = new SliderValue("Pitch Shake Range", 0, 0, 0.5f, 0.01f, this, () -> shake.get() && !shakeMode.is("Noise"));
    private final SliderValue yawShakeRange = new SliderValue("Yaw Shake Range", 0, 0, 0.5f, 0.01f, this, () -> shake.get() && !shakeMode.is("Noise"));
    private final SliderValue minPitchFactor = new SliderValue("Min Pitch Factor", 0, 0, 1, 0.01f, this, () -> shake.get() && shakeMode.is("Noise"));
    private final SliderValue minYawFactor = new SliderValue("Min Yaw Factor", 0, 0, 1, 0.01f, this, () -> shake.get() && shakeMode.is("Noise"));
    private final SliderValue maxPitchFactor = new SliderValue("Max Pitch Factor", 0, 0, 1, 0.01f, this, () -> shake.get() && shakeMode.is("Noise"));
    private final SliderValue maxYawFactor = new SliderValue("Max Yaw Factor", 0, 0, 1, 0.01f, this, () -> shake.get() && shakeMode.is("Noise"));
    private final SliderValue dynamicPitchFactor = new SliderValue("Dynaimc Pitch Factor", 0, 0, 1, 0.01f, this, () -> shake.get() && shakeMode.is("Noise"));
    private final SliderValue dynamicYawFactor = new SliderValue("Dynaimc Yaw Factor", 0, 0, 1, 0.01f, this, () -> shake.get() && shakeMode.is("Noise"));
    private final SliderValue tolerance = new SliderValue("Tolerance",0.1f,0.01f,0.1f,0.01f,this, () -> shake.get() && shakeMode.is("Noise"));
    private final SliderValue minSpeed = new SliderValue("Min Speed", 0.1f, 0.01f, 1, 0.01f, this, () -> shake.get() && shakeMode.is("Noise"));
    private final SliderValue maxSpeed = new SliderValue("Max Speed", 0.2f, 0.01f, 1, 0.01f, this, () -> shake.get() && shakeMode.is("Noise"));
    private final SliderValue minAps = new SliderValue("Min Aps", 9, 1, 20, this);
    private final SliderValue maxAps = new SliderValue("Max Aps", 11, 1, 20, this);
    private final ModeValue apsMode = new ModeValue("Aps Mode", new String[]{"Random", "Secure Random", "Full Random"}, "Random", this);
    public final SliderValue searchRange = new SliderValue("Search Range", 6.0F, 2.0F, 16F, .1f, this);
    public final SliderValue rotationRange = new SliderValue("Rotation Range", 3.0F, 2.0F, 16F, .1f, this);
    public final BoolValue preSwingWithRotationRange = new BoolValue("Pre Swing With Rotation Range", true, this);
    public final MultiBoolValue addons = new MultiBoolValue("Addons", Arrays.asList(new BoolValue("Movement Fix", false), new BoolValue("Perfect Hit", true), new BoolValue("Ray Cast", true), new BoolValue("Hit Select", true)), this);
    public final SliderValue attackRange = new SliderValue("Attack Range", 3.0F, 2.0F, 6F, .1f, this);
    public final SliderValue wallAttackRange = new SliderValue("Wall Attack Range", 0.0F, 0.0F, 6F, .1f, this);
    public final SliderValue blockRange = new SliderValue("Block Range", 5.0F, 2.0F, 16F, .1f, this);
    public final ModeValue autoBlock = new ModeValue("AutoBlock", new String[]{"None", "Vanilla","HYT", "Watchdog", "Release","Interact"}, "Fake", this);
    public final BoolValue interact = new BoolValue("Interact", false, this, () -> !autoBlock.is("None"));
    public final BoolValue via = new BoolValue("Via", false, this, () -> !autoBlock.is("None"));
    public final BoolValue slow = new BoolValue("Slowdown", false, this, () -> !autoBlock.is("None"));
    public final SliderValue releaseBlockRate = new SliderValue("Block Rate", 100, 1, 100, 1, this, () -> autoBlock.is("Release"));
    public final BoolValue forceDisplayBlocking = new BoolValue("Force Display Blocking", false, this);
    private final MultiBoolValue targetOption = new MultiBoolValue("Targets", Arrays.asList(new BoolValue("Players", true), new BoolValue("Mobs", false),
            new BoolValue("Animals", false), new BoolValue("Invisible", true), new BoolValue("Dead", false)), this);
    public final MultiBoolValue filter = new MultiBoolValue("Filter", Arrays.asList(new BoolValue("Teams", true), new BoolValue("Friends", true)), this);
    public final ModeValue movementFix = new ModeValue("Movement", new String[]{"Silent", "Strict"}, "Silent", this, () -> addons.isEnabled("Movement Fix"));
    private final BoolValue aimPoint = new BoolValue("Aim Point", false, this);
    private final SliderValue dotSize = new SliderValue("Size", 0.1f, 0.05f, 0.2f, 0.05f, this, aimPoint::get);
    private final SliderValue interpolation = new SliderValue("Interpolation", 0.15f, 0.01f, 1, 0.01f, this, aimPoint::get);
    private final SliderValue delay = new SliderValue("Delay", 20, 1, 100, 1, this, aimPoint::get);
    public final BoolValue noScaffold = new BoolValue("No Scaffold", false, this);
    public final BoolValue noInventory = new BoolValue("No Inventory", false, this);
    public final BoolValue noBedNuker = new BoolValue("No Bed Nuker", false, this);
    public List<EntityLivingBase> targets = new ArrayList<>();
    public EntityLivingBase target;
    private final TimerUtils attackTimer = new TimerUtils();
    private final TimerUtils switchTimer = new TimerUtils();
    private final TimerUtils perfectHitTimer = new TimerUtils();
    private Random random = new Random();
    private int index;
    private int clicks;
    private int maxClicks;
    public boolean isBlocking;
    public boolean renderBlocking;
    public boolean blinked;
    public boolean lag;
    public Vec3 currentVec;
    public Vec3 targetVec;
    public boolean damaged = false;
    private final ContinualAnimation animatedX = new ContinualAnimation();
    private final ContinualAnimation animatedY = new ContinualAnimation();
    private final ContinualAnimation animatedZ = new ContinualAnimation();

    @Override
    public void onEnable() {
        clicks = 0;
        attackTimer.reset();
    }

    @Override
    public void onDisable() {
        unblock();
        if (renderBlocking) {
            renderBlocking = false;
        }
        if (blinked) {
            BlinkComponent.dispatch();
        }
        target = null;
        targets.clear();
        index = 0;
        switchTimer.reset();
        currentVec = targetVec = null;
        Iterator<Map.Entry<EntityPlayer, DecelerateAnimation>> iterator = getModule(Interface.class).animationEntityPlayerMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<EntityPlayer, DecelerateAnimation> entry = iterator.next();
            DecelerateAnimation animation = entry.getValue();

            animation.setDirection(Direction.BACKWARDS);
            if (animation.finished(Direction.BACKWARDS)) {
                iterator.remove();
            }
        }
    }

    private float lerp(float start, float end, float factor) {
        return start + factor * (end - start);
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        targets.clear();

        if ((target == null || !shouldBlock()) && renderBlocking) {
            renderBlocking = false;
        }

        setTag(mode.get());

        if (((isEnabled(Scaffold.class) && noScaffold.get() ||
                !noScaffold.get() && isEnabled(Scaffold.class) && mc.theWorld.getBlockState(getModule(Scaffold.class).data.blockPos).getBlock() instanceof BlockAir) ||
                noInventory.get() && mc.currentScreen instanceof GuiContainer ||
                (noBedNuker.get() && isEnabled(BedNuker.class) && getModule(BedNuker.class).bedPos != null || !noBedNuker.get() && isEnabled(BedNuker.class) && getModule(BedNuker.class).rotate)
        ) && target != null) {
            if (blinked) {
                BlinkComponent.dispatch();
                blinked = false;
            }
        }

        targets = getTargets();
        if (!targets.isEmpty()) {
            if (targets.size() > 1) {
                switch (priority.get()) {
                    case "Armor":
                        targets.sort(Comparator.comparingInt(EntityLivingBase::getTotalArmorValue));
                        break;
                    case "Range":
                        targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
                        break;
                    case "Health":
                        targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                        break;
                    case "HurtTime":
                        targets.sort(Comparator.comparingInt(entity -> entity.hurtTime));
                        break;
                    case "FOV":
                        targets.sort(Comparator.comparingDouble(RotationUtils::distanceFromYaw));
                        break;
                }
            }

            if (switchTimer.hasTimeElapsed((long) (switchDelayValue.get() * 100L)) && targets.size() > 1) {
                ++index;
                switchTimer.reset();
            }

            if (index >= targets.size()) {
                index = 0;
            }
            if (!targets.isEmpty()) {
                target = targets.get(Objects.equals(mode.get(), "Switch") ? index : 0);
            }

        } else {
            target = null;
            currentVec = targetVec = null;
            unblock();
            lag = false;
            if (blinked) {
                BlinkComponent.dispatch();
                blinked = false;
            }
            clicks = 0;
            return;
        }

        if (mc.thePlayer.isSpectator() || mc.thePlayer.isDead || (isEnabled(Scaffold.class) && noScaffold.get() ||
                !noScaffold.get() && isEnabled(Scaffold.class) && mc.theWorld.getBlockState(getModule(Scaffold.class).data.blockPos).getBlock() instanceof BlockAir) ||
                noInventory.get() && mc.currentScreen instanceof GuiContainer ||
                (noBedNuker.get() && isEnabled(BedNuker.class) && getModule(BedNuker.class).bedPos != null || !noBedNuker.get() && isEnabled(BedNuker.class) && getModule(BedNuker.class).rotate)
        ) return;

        if (target != null) {

            if (PlayerUtils.getDistanceToEntityBox(target) < rotationRange.get()) {

                float[] finalRotation = calcToEntity(target);

                if (customRotationSetting.get()) {
                    RotationUtils.setRotation(finalRotation, addons.isEnabled("Movement Fix") ? movementFix.is("Strict") ? MovementCorrection.STRICT : MovementCorrection.SILENT : MovementCorrection.OFF, MathUtils.randomizeInt(minYawRotSpeed.get(), maxYawRotSpeed.get()), MathUtils.randomizeInt(minPitchRotSpeed.get(), maxPitchRotSpeed.get()), maxYawAcceleration.get(), maxPitchAcceleration.get(), accelerationError.get(), constantError.get(), smoothlyResetRotation.get());
                } else {
                    RotationUtils.setRotation(finalRotation, addons.isEnabled("Movement Fix") ? movementFix.is("Strict") ? MovementCorrection.STRICT : MovementCorrection.SILENT : MovementCorrection.OFF);
                }
                if (preSwingWithRotationRange.get()) {
                    if (PlayerUtils.getDistanceToEntityBox(target) <= (mc.thePlayer.canEntityBeSeen(target) ? rotationRange.get() : 0) &&
                            PlayerUtils.getDistanceToEntityBox(target) > (!mc.thePlayer.canEntityBeSeen(target) ? wallAttackRange.get() : attackRange.get())
                    ) {
                        maxClicks = clicks;

                        for (int i = 0; i < maxClicks; i++) {
                            mc.thePlayer.swingItem();
                            clicks--;
                        }
                    }
                }
            }

            if (shouldBlock()) {
                renderBlocking = true;
            }

            if (preTickBlock()) return;

            if (clicks == 0) return;

            if (isBlocking || autoBlock.is("HYT"))
                if (preAttack()) return;

            if (shouldAttack()) {
                maxClicks = clicks;
                for (int i = 0; i < maxClicks; i++) {
                    attack();
                    clicks--;
                }
            }

            if (!autoBlock.is("None") && (shouldBlock() || autoBlock.is("HYT"))) {
                if (Mouse.isButtonDown(2))
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                postAttack();
            }
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {

        Packet packet = event.getPacket();

        if (target != null && packet instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) packet).getEntityID() == mc.thePlayer.getEntityId()) {
            this.damaged = true;
        } else if (target == null) {
            this.damaged = false;
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        double min = minAps.get();
        double max = maxAps.get();
        switch (apsMode.get()) {
            case "Random":
                if (attackTimer.hasTimeElapsed(1000L / (MathUtils.nextInt((int) min, (int) max))) && target != null) {
                    clicks++;
                    attackTimer.reset();
                }
                break;

            case "Secure Random": {
                double time = MathHelper.clamp_double(
                        min + ((max - min) * new SecureRandom().nextDouble()), min, max);

                if (attackTimer.hasTimeElapsed((float) (1000L / time))) {
                    clicks++;
                    attackTimer.reset();
                }
                break;
            }
            case "Full Random": {
                min *= MathUtils.nextDouble(0, 1);
                max *= MathUtils.nextDouble(0, 1);

                double time = (max / min) * (MathUtils.nextDouble(min, max));

                if (attackTimer.hasTimeElapsed((float) (1000L / time))) {
                    clicks++;
                    attackTimer.reset();
                }

                break;
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (aimPoint.get() && target != null && PlayerUtils.getDistanceToEntityBox(target) < rotationRange.get() && currentVec != null) {
            float interpolatedX = lerp(animatedX.getOutput(), (float) currentVec.xCoord, interpolation.get());
            float interpolatedY = lerp(animatedY.getOutput(), (float) currentVec.yCoord, interpolation.get());
            float interpolatedZ = lerp(animatedZ.getOutput(), (float) currentVec.zCoord, interpolation.get());

            animatedX.animate(interpolatedX, (int) delay.get());
            animatedY.animate(interpolatedY, (int) delay.get());
            animatedZ.animate(interpolatedZ, (int) delay.get());

            drawDot(new Vec3(animatedX.getOutput(), animatedY.getOutput(), animatedZ.getOutput()), dotSize.get(), getModule(Interface.class).color());
        }
    }

    private boolean preTickBlock() {
        switch (autoBlock.get()){
            case "Watchdog":
                switch (mc.thePlayer.ticksExisted % 3) {
                    case 0:
                        unblock();
                        return true;
                    case 1:
                        return false;
                    case 2:
                        block(false);
                        if (!BlinkComponent.blinking)
                            BlinkComponent.blinking = true;
                        BlinkComponent.release(true);
                        blinked = true;
                        return true;
                }
                break;
        }
        return false;
    }

    private boolean preAttack() {

        switch (autoBlock.get()) {
            case "Release":
                if (clicks + 1 == maxClicks) {
                    if (!(releaseBlockRate.get() > 0 && RandomUtils.nextInt(0, 100) <= releaseBlockRate.get()))
                        break;
                    block();
                    isBlocking = true;
                }
                break;

            case "Interact":
                if (isBlocking) {
                    unblock();
                    return true;
                }
                break;
            case "HYT":
                if (this.isBlocking && !getModule(AutoGap.class).eating) {
                    unblock();
                }

                if (isBlocking) {
                    unblock();
                }
                break;
        }
        return false;
    }

    private void postAttack() {
        switch (autoBlock.get()) {
            case "Vanilla":
                block();
                break;
            case "Interact":
                block(true);
                break;
            case "HYT":
                if (!shouldBlock() && isBlocking)
                    unblock();
                break;
        }
    }
    private void block() {
        block(interact.get());
    }

    public void block(boolean interact) {
        if (!isBlocking) {

            if (interact) {
                sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
            }

            if (via.get()) {
                if (!getModule(AutoGap.class).eating && ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47) {
                    sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                    useItem.write(Type.VAR_INT, 1);
                    com.viaversion.viarewind.utils.PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                }
            } else {
                sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            }
            isBlocking = true;
        }
    }

    public void unblock() {
        if (isBlocking) {
            if(mode.is("HYT")){
                sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            } else {
            sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
            isBlocking = false;
        }
    }

    public void attack() {
        if (autoBlock.is("Release"))
            unblock();
        MovingObjectPosition rayCast = RotationUtils.rayCast(RotationUtils.currentRotation, attackRange.get());
        if (addons.isEnabled("Ray Cast") && rayCast.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && rayCast.entityHit instanceof EntityLivingBase) {
            //if ((((EntityLivingBase) rayCast.entityHit).hurtTime <= 2 || mc.thePlayer.hurtTime != 0) && addons.isEnabled("Perfect Hit") || !addons.isEnabled("Perfect Hit"))
            if (canAttack((EntityLivingBase) rayCast.entityHit)) {
                if (getModule(AutoGap.class).isEnabled() && getModule(AutoGap.class).alwaysAttack.get() && getModule(AutoGap.class).eating) {
                    AttackOrder.sendFixedAttackNoPacketEvent(mc.thePlayer, rayCast.entityHit);
                } else {
                    AttackOrder.sendFixedAttack(mc.thePlayer, rayCast.entityHit);
                }
            }
        } else {
            //if ((target.hurtTime <= 2 || mc.thePlayer.hurtTime != 0) && addons.isEnabled("Perfect Hit") || !addons.isEnabled("Perfect Hit"))
            if (canAttack(target)) {
                if (getModule(AutoGap.class).isEnabled() && getModule(AutoGap.class).alwaysAttack.get() && getModule(AutoGap.class).eating) {
                    AttackOrder.sendFixedAttackNoPacketEvent(mc.thePlayer, target);
                } else {
                    AttackOrder.sendFixedAttack(mc.thePlayer, target);
                }
            }
        }
        perfectHitTimer.reset();
    }

    public boolean canAttack(EntityLivingBase entity){
        return !addons.isEnabled("Perfect Hit") || addons.isEnabled("Perfect Hit") && (entity.hurtTime == 0 || entity.hurtTime == 1 || perfectHitTimer.hasTimeElapsed(1000L));
    }

    public boolean isHoldingSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public List<EntityLivingBase> getTargets() {
        final List<EntityLivingBase> entities = new ArrayList<>();
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase e) {
                if (isValid(e) && PlayerUtils.getDistanceToEntityBox(e) <= searchRange.get() && (RotationUtils.getRotationDifference(e) <= fov.get() || fov.get() == 180)) entities.add(e);
                else entities.remove(e);

            }
        }
        return entities;
    }

    public boolean isValid(Entity entity) {
        if ((filter.isEnabled("Teams") && PlayerUtils.isInTeam(entity))) {
            return false;
        }
        if (entity instanceof EntityLivingBase && (targetOption.isEnabled("Dead") || entity.isEntityAlive()) && entity != mc.thePlayer) {
            if (targetOption.isEnabled("Invisible") || !entity.isInvisible()) {
                if (targetOption.isEnabled("Players") && entity instanceof EntityPlayer) {
                    if (filter.isEnabled("Friends") && Moonlight.INSTANCE.getFriendManager().isFriend((EntityPlayer) entity))
                        return false;
                    return !isEnabled(AntiBot.class) || !getModule(AntiBot.class).bots.contains(entity);
                }
            }
            return (targetOption.isEnabled("Mobs") && PlayerUtils.isMob(entity)) || (targetOption.isEnabled("Animals") && PlayerUtils.isAnimal(entity));
        }
        return false;
    }

    public boolean shouldAttack() {
        return PlayerUtils.getDistanceToEntityBox(target) <= (!mc.thePlayer.canEntityBeSeen(target) ? wallAttackRange.get() : attackRange.get()) && (!addons.isEnabled("Hit Select") || addons.isEnabled("Hit Select") && damaged);
    }

    public boolean shouldBlock() {
        return PlayerUtils.getDistanceToEntityBox(target) <= blockRange.get() && isHoldingSword();
    }

    public float[] calcToEntity(EntityLivingBase entity) {

        float yaw;
        float pitch;

        Vec3 playerPos = mc.thePlayer.getPositionEyes(1);
        Vec3 entityPos = entity.getPositionVector();
        AxisAlignedBB boundingBox = entity.getEntityBoundingBox();

        switch (aimMode.get()) {
            case "Head":
                targetVec = entityPos.add(0.0, entity.getEyeHeight(), 0.0);
                break;
            case "Torso":
                targetVec = entityPos.add(0.0, entity.height * 0.75, 0.0);
                break;
            case "Legs":
                targetVec = entityPos.add(0.0, entity.height * 0.45, 0.0);
                break;
            case "Nearest":
                targetVec = RotationUtils.getBestHitVec(entity);
                break;
            case "Test":

                Vec3 test = new Vec3(entity.posX, entity.posY, entity.posZ);

                double diffY;
                for (diffY = boundingBox.minY + 0.7D; diffY < boundingBox.maxY - 0.1D; diffY += 0.1D) {
                    if (mc.thePlayer.getPositionEyes(1).distanceTo(new Vec3(entity.posX, diffY, entity.posZ)) < mc.thePlayer.getPositionEyes(1).distanceTo(test)) {
                        test = new Vec3(entity.posX, diffY, entity.posZ);
                    }
                }
                targetVec = test;
                break;
            default:
                targetVec = entityPos;
        }

        if(heuristics.get()){
            targetVec = RotationUtils.heuristics(entity,targetVec);
        }

        if(bruteforce.get()) {
            if (RotationUtils.rayCast(RotationUtils.getRotations(targetVec), rotationRange.get()).typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                final double xWidth = boundingBox.maxX - boundingBox.minX;
                final double zWidth = boundingBox.maxZ - boundingBox.minZ;
                final double height = boundingBox.maxY - boundingBox.minY;
                for (double x = 0.0; x < 1.0; x += 0.2) {
                    for (double y = 0.0; y < 1.0; y += 0.2) {
                        for (double z = 0.0; z < 1.0; z += 0.2) {
                            final Vec3 hitVec = new Vec3(boundingBox.minX + xWidth * x, boundingBox.minY + height * y, boundingBox.minZ + zWidth * z);
                            if (RotationUtils.rayCast(RotationUtils.getRotations(hitVec), rotationRange.get()).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                targetVec = hitVec;
                            }
                        }
                    }
                }
            }
        }

        if (inRange.get()) {
            double minAimY = entity.posY + entity.getEyeHeight() * minAimRange.get();
            double maxAimY = entity.posY + entity.getEyeHeight() * maxAimRange.get();

            if (RotationUtils.getBestHitVec(entity).yCoord < minAimY) {
                targetVec.yCoord = minAimY;
            }

            if (RotationUtils.getBestHitVec(entity).yCoord > maxAimY) {
                targetVec.yCoord = maxAimY;
            }
        }

        currentVec = targetVec;

        if (shake.get()) {
            switch (shakeMode.get()){
                case "Random":
                    currentVec = currentVec.addVector(
                            MathUtils.randomizeDouble(-yawShakeRange.get(), yawShakeRange.get()),
                            MathUtils.randomizeDouble(-pitchShakeRange.get(), pitchShakeRange.get()),
                            MathUtils.randomizeDouble(-yawShakeRange.get(), yawShakeRange.get())
                    );
                    break;
                case "Secure Random":
                    currentVec = currentVec.addVector(
                            MathUtils.nextSecureFloat(1.0, 2.0) * Math.sin(targetVec.xCoord * 3.141592653589793) * yawShakeRange.get(),
                            MathUtils.nextSecureFloat(1.0, 2.0) * Math.sin(targetVec.yCoord * 3.141592653589793) * pitchShakeRange.get(),
                            MathUtils.nextSecureFloat(1.0, 2.0) * Math.sin(targetVec.zCoord * 3.141592653589793) * yawShakeRange.get()
                    );
                    break;
                case "Noise":
                    if (gaussianHasReachedTarget(currentVec,targetVec,tolerance.get())) {

                        double yawFactor = dynamicYawFactor.get() > 0f ? (MathUtils.randomizeDouble(minYawFactor.get(), maxYawFactor.get()) + MovementUtils.getSpeed() * dynamicYawFactor.get()) : (MathUtils.randomizeDouble(minYawFactor.get(), maxYawFactor.get()));

                        double pitchFactor = dynamicPitchFactor.get() > 0f ? (MathUtils.randomizeDouble(minPitchFactor.get(), maxPitchFactor.get()) + MovementUtils.getSpeed() * dynamicPitchFactor.get()) : (MathUtils.randomizeDouble(minPitchFactor.get(), minPitchFactor.get()));

                        currentVec = currentVec.addVector(
                                random.nextGaussian(0.00942273861037109, 0.23319837528201348) * yawFactor,
                                random.nextGaussian(0.30075078007595923, 0.3492437109081718) * pitchFactor,
                                random.nextGaussian(0.013282929419023442, 0.24453708645460387) * yawFactor
                        );
                    } else {
                        currentVec = new Vec3(
                                MathUtils.interpolate(currentVec.xCoord, targetVec.xCoord, MathUtils.randomizeDouble(minSpeed.get(), maxSpeed.get())),
                                MathUtils.interpolate(currentVec.yCoord, targetVec.yCoord, MathUtils.randomizeDouble(minSpeed.get(), maxSpeed.get())),
                                MathUtils.interpolate(currentVec.zCoord, targetVec.zCoord, MathUtils.randomizeDouble(minSpeed.get(), maxSpeed.get()))
                        );
                    }
                    break;
            }
        }

        double deltaX = currentVec.xCoord - playerPos.xCoord;
        double deltaY = currentVec.yCoord - playerPos.yCoord;
        double deltaZ = currentVec.zCoord - playerPos.zCoord;

        yaw = (float) -(Math.atan2(deltaX, deltaZ) * (180.0 / Math.PI));
        pitch = (float) (-Math.toDegrees(Math.atan2(deltaY, Math.hypot(deltaX, deltaZ))));

        if (pitch > 90.0f) {
            pitch = 90;
        } else if (pitch < -90.0f) {
            pitch = -90;
        }
        return new float[]{yaw, pitch};
    }
    private boolean gaussianHasReachedTarget(Vec3 vec1, Vec3 vec2, float tolerance){
        return MathHelper.abs((float) (vec1.xCoord - vec2.xCoord)) < tolerance &&
                MathHelper.abs((float) (vec1.yCoord - vec2.yCoord)) < tolerance &&
                MathHelper.abs((float) (vec1.zCoord - vec2.zCoord)) < tolerance;
    }
    public static void drawDot(@NotNull Vec3 pos, double size, int color) {
        double d = size / 2;
        AxisAlignedBB bbox = new AxisAlignedBB(pos.xCoord - d, pos.yCoord - d, pos.zCoord - d, pos.xCoord + d, pos.yCoord + d, pos.zCoord + d);

        AxisAlignedBB axis = new AxisAlignedBB(bbox.minX - mc.thePlayer.posX, bbox.minY - mc.thePlayer.posY, bbox.minZ - mc.thePlayer.posZ, bbox.maxX - mc.thePlayer.posX, bbox.maxY - mc.thePlayer.posY, bbox.maxZ - mc.thePlayer.posZ);
        RenderUtils.drawAxisAlignedBB(axis, true, color);
    }
}
