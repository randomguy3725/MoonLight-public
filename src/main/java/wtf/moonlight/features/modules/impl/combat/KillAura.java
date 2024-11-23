package wtf.moonlight.features.modules.impl.combat;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.fixes.AttackOrder;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.*;
import net.optifine.shaders.Shaders;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.MoonLight;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.events.impl.render.Shader2DEvent;
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
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.ContinualAnimation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.packet.BlinkComponent;
import wtf.moonlight.utils.packet.PacketUtils;
import wtf.moonlight.utils.player.*;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.GLUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
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
    private final BoolValue shake = new BoolValue("Shake", false, this);
    private final BoolValue intaveShake = new BoolValue("Intave Shake", false, this, shake::get);
    private final SliderValue pitchShakeRange = new SliderValue("Pitch Shake Range", 0, 0, 0.5f, 0.01f, this, shake::get);
    private final SliderValue yawShakeRange = new SliderValue("Yaw Shake Range", 0, 0, 0.5f, 0.01f, this, shake::get);
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
    public final ModeValue autoBlock = new ModeValue("AutoBlock", new String[]{"None", "Vanilla", "Via", "Watchdog", "Release"}, "Fake", this);
    public final BoolValue interact = new BoolValue("Interact", false, this, () -> !autoBlock.is("None"));
    public final BoolValue slow = new BoolValue("Slowdown", false, this, () -> !autoBlock.is("None"));
    public final SliderValue releaseBlockRate = new SliderValue("Block Rate", 100, 1, 100, 1, this, () -> autoBlock.is("Release"));
    public final BoolValue forceDisplayBlocking = new BoolValue("Force Display Blocking", false, this);
    private final MultiBoolValue targetOption = new MultiBoolValue("Targets", Arrays.asList(new BoolValue("Players", true), new BoolValue("Mobs", false),
            new BoolValue("Animals", false), new BoolValue("Invisible", true), new BoolValue("Dead", false)), this);
    public final MultiBoolValue filter = new MultiBoolValue("Filter", Arrays.asList(new BoolValue("Teams", true), new BoolValue("Friends", true)), this);
    public final ModeValue movementFix = new ModeValue("Movement", new String[]{"Silent", "Strict"}, "Silent", this, () -> addons.isEnabled("Movement Fix"));
    private final BoolValue mark = new BoolValue("Target Mark", false, this);
    private final ModeValue markMode = new ModeValue("Mark Mode", new String[]{"Points", "Rectangle", "Exhi"}, "Points", this, mark::get);
    private final BoolValue aimPoint = new BoolValue("Aim Point", false, this);
    private final SliderValue dotSize = new SliderValue("Size", 0.1f, 0.05f, 0.2f, 0.05f, this, aimPoint::get);
    public final BoolValue noScaffold = new BoolValue("No Scaffold", false, this);
    public final BoolValue noInventory = new BoolValue("No Inventory", false, this);
    public final BoolValue noBedNuker = new BoolValue("No Bed Nuker", false, this);
    public List<EntityLivingBase> targets = new ArrayList<>();
    public EntityLivingBase target;
    private final TimerUtils attackTimer = new TimerUtils();
    private final TimerUtils switchTimer = new TimerUtils();
    private int index;
    private int clicks;
    private int maxClicks;
    public boolean isBlocking;
    public boolean renderBlocking;
    public boolean blinked;
    public boolean lag;
    public Vec3 aimVec;
    public boolean damaged = false;
    private final long startTime = System.currentTimeMillis();
    private final Animation alphaAnim = new DecelerateAnimation(400, 1);
    private final ContinualAnimation animatedX = new ContinualAnimation();
    private final ContinualAnimation animatedY = new ContinualAnimation();
    private final ContinualAnimation animatedZ = new ContinualAnimation();
    private final ResourceLocation glowCircle = new ResourceLocation("moonlight/texture/targetesp/glow_circle.png");
    private final ResourceLocation rectangle = new ResourceLocation("moonlight/texture/targetesp/rectangle.png");

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
        resetVariables();
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

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        targets.clear();
        resetVariables();

        if ((target == null || !shouldBlock()) && renderBlocking) {
            renderBlocking = false;
        }

        setTag(mode.get());

        if (((isEnabled(Scaffold.class) && noScaffold.get() ||
                !noScaffold.get() && isEnabled(Scaffold.class) && mc.theWorld.getBlockState(getModule(Scaffold.class).data.getPosition()).getBlock() instanceof BlockAir) ||
                noInventory.get() && mc.currentScreen instanceof GuiContainer ||
                noBedNuker.get() && isEnabled(BedNuker.class) && getModule(BedNuker.class).bedPos != null
        ) && autoBlock.is("Watchdog") && target != null) {
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
                alphaAnim.setDirection(Direction.FORWARDS);
            }

        } else {
            target = null;
            if (autoBlock.is("Watchdog")) {
                if (blinked) {
                    BlinkComponent.dispatch();
                    blinked = false;
                }
                unblock();
            }
            resetVariables();
            clicks = 0;
            return;
        }

        if (mc.thePlayer.isSpectator() || mc.thePlayer.isDead || (isEnabled(Scaffold.class) && noScaffold.get() ||
                !noScaffold.get() && isEnabled(Scaffold.class) && mc.theWorld.getBlockState(getModule(Scaffold.class).data.getPosition()).getBlock() instanceof BlockAir) ||
                noInventory.get() && mc.currentScreen instanceof GuiContainer ||
                noBedNuker.get() && isEnabled(BedNuker.class) && getModule(BedNuker.class).bedPos != null
        ) return;

        if (target != null) {

            if (PlayerUtils.getDistanceToEntityBox(target) < rotationRange.get()) {

                float[] finalRotation = calcToEntity(target);

                if (customRotationSetting.get()) {
                    switch (calcRotSpeedMode.get()) {
                        case "Linear":
                            RotationUtils.setRotation(finalRotation, addons.isEnabled("Movement Fix") ? movementFix.is("Strict") ? MovementCorrection.STRICT : MovementCorrection.SILENT : MovementCorrection.OFF, MathUtils.randomizeInt(minYawRotSpeed.get(), maxYawRotSpeed.get()), MathUtils.randomizeInt(minPitchRotSpeed.get(), maxPitchRotSpeed.get()), smoothlyResetRotation.get());
                            break;
                        case "Acceleration":
                            RotationUtils.setRotation(finalRotation, addons.isEnabled("Movement Fix") ? movementFix.is("Strict") ? MovementCorrection.STRICT : MovementCorrection.SILENT : MovementCorrection.OFF, maxYawAcceleration.get(), maxPitchAcceleration.get(), accelerationError.get(), constantError.get(), smoothlyResetRotation.get());
                            break;
                    }
                } else {
                    RotationUtils.setRotation(finalRotation, addons.isEnabled("Movement Fix") ? movementFix.is("Strict") ? MovementCorrection.STRICT : MovementCorrection.SILENT : MovementCorrection.OFF);
                }
                if (preSwingWithRotationRange.get()) {
                    if (PlayerUtils.getDistanceToEntityBox(target) < (mc.thePlayer.canEntityBeSeen(target) ? rotationRange.get() : 0) &&
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
            if (PlayerUtils.getDistanceToEntityBox(target) < blockRange.get()) {

                if (!autoBlock.is("Watchdog")) {

                    if (shouldAttack()) {
                        maxClicks = clicks;

                        for (int i = 0; i < maxClicks; i++) {
                            attack();
                            clicks--;
                        }
                    }

                    if (!autoBlock.is("None") && isHoldingSword()) {
                        if (Mouse.isButtonDown(2))
                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                        block();
                    }
                }

                if (autoBlock.is("Watchdog")) {
                    if (isHoldingSword() && !getModule(BedNuker.class).rotate && getModule(BedNuker.class).bedPos == null && !getModule(Scaffold.class).isEnabled()) {
                        if (lag) {
                            BlinkComponent.blinking = true;
                            blinked = true;
                            unblock();
                            lag = false;
                        } else {
                            if (shouldAttack()) {
                                maxClicks = clicks;

                                for (int i = 0; i < maxClicks; i++) {
                                    attack();
                                    sendPacket(new C02PacketUseEntity(target, new Vec3(0, 0, 0)));
                                    sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                                    clicks--;
                                }
                            }
                            BlinkComponent.dispatch();
                            block();
                            lag = true;
                        }
                    } else {
                        if (blinked) {
                            BlinkComponent.dispatch();
                            blinked = false;
                        }
                        if (shouldAttack()) {
                            maxClicks = clicks;

                            for (int i = 0; i < maxClicks; i++) {
                                attack();
                                clicks--;
                            }
                        }
                    }
                }
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

        int index = 3;
        if (mark.get() && (markMode.is("Rounded") || markMode.is("Rectangle")) && target != null) {
            float dst = mc.thePlayer.getSmoothDistanceToEntity(target);
            drawTargetESP2D(Objects.requireNonNull(targetESPSPos(target))[0], Objects.requireNonNull(targetESPSPos(target))[1],
                    (1.0f - MathHelper.clamp_float(Math.abs(dst - 6.0f) / 60.0f, 0.0f, 0.75f)) * 1, index);
        }
    }

    @EventTarget
    public void onShader2D(Shader2DEvent event) {
        if (event.getShaderType() == Shader2DEvent.ShaderType.GLOW) {
            int index = 3;
            if (mark.get() && markMode.is("Rectangle") && target != null) {
                float dst = mc.thePlayer.getSmoothDistanceToEntity(target);
                drawTargetESP2D(Objects.requireNonNull(targetESPSPos(target))[0], Objects.requireNonNull(targetESPSPos(target))[1],
                        (1.0f - MathHelper.clamp_float(Math.abs(dst - 6.0f) / 60.0f, 0.0f, 0.75f)) * 1, index);
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (mark.get() && target != null) {
            if (markMode.is("Points"))
                points();

            if (markMode.is("Exhi")) {
                int color = this.target.hurtTime > 3 ? new Color(200, 255, 100, 75).getRGB() : this.target.hurtTime < 3 ? new Color(235, 40, 40, 75).getRGB() : new Color(255, 255, 255, 75).getRGB();
                GlStateManager.pushMatrix();
                GL11.glShadeModel(7425);
                GL11.glHint(3154, 4354);
                mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
                double x = target.prevPosX + (target.posX - target.prevPosX) * (double) event.getPartialTicks() - mc.getRenderManager().renderPosX;
                double y = target.prevPosY + (target.posY - target.prevPosY) * (double) event.getPartialTicks() - mc.getRenderManager().renderPosY;
                double z = target.prevPosZ + (target.posZ - target.prevPosZ) * (double) event.getPartialTicks() - mc.getRenderManager().renderPosZ;
                double xMoved = target.posX - target.prevPosX;
                double yMoved = target.posY - target.prevPosY;
                double zMoved = target.posZ - target.prevPosZ;
                double motionX = 0.0;
                double motionY = 0.0;
                double motionZ = 0.0;
                GlStateManager.translate(x + (xMoved + motionX + (mc.thePlayer.motionX + 0.005)), y + (yMoved + motionY + (mc.thePlayer.motionY - 0.002)), z + (zMoved + motionZ + (mc.thePlayer.motionZ + 0.005)));
                AxisAlignedBB axisAlignedBB = target.getEntityBoundingBox();
                RenderUtils.drawAxisAlignedBB(new AxisAlignedBB(axisAlignedBB.minX - 0.1 - target.posX, axisAlignedBB.minY - 0.1 - target.posY, axisAlignedBB.minZ - 0.1 - target.posZ, axisAlignedBB.maxX + 0.1 - target.posX, axisAlignedBB.maxY + 0.2 - target.posY, axisAlignedBB.maxZ + 0.1 - target.posZ), true, color);
                GlStateManager.popMatrix();
            }
        }

        if (aimPoint.get() && target != null && PlayerUtils.getDistanceToEntityBox(target) < rotationRange.get() && aimVec != null) {
            animatedX.animate((float) aimVec.xCoord, 20);
            animatedY.animate((float) aimVec.yCoord, 20);
            animatedZ.animate((float) aimVec.zCoord, 20);

            drawDot(new Vec3(animatedX.getOutput(), animatedY.getOutput(), animatedZ.getOutput()), dotSize.get(), getModule(Interface.class).color());
        }

    }

    public void block() {
        if (!isBlocking) {

            if (interact.get()) {
                PacketUtils.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
            }

            switch (autoBlock.get()) {
                case "Vanilla":
                case "Watchdog":
                    sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    isBlocking = true;
                    break;
                case "Release":
                    if (clicks + 1 == maxClicks) {
                        if (!(releaseBlockRate.get() > 0 && RandomUtils.nextInt(0, 100) <= releaseBlockRate.get()))
                            break;
                        sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        isBlocking = true;
                    }
                    break;
                case "Interact":
                    sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                    sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    isBlocking = true;
                    break;
                case "Via":
                    if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47) {
                        sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
                        PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                        useItem.write(Type.VAR_INT, 1);
                        com.viaversion.viarewind.utils.PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                    }
                    isBlocking = true;
                    break;
            }
        }
    }

    public void unblock() {
        if (isBlocking) {
            sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            isBlocking = false;
        }
    }

    public void attack() {
        if (autoBlock.is("Release"))
            unblock();
        MovingObjectPosition rayCast = RotationUtils.rayCast(RotationUtils.currentRotation, attackRange.get());
        if (addons.isEnabled("Ray Cast") && rayCast.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && rayCast.entityHit instanceof EntityLivingBase) {
            if ((((EntityLivingBase) rayCast.entityHit).hurtTime <= 2 || mc.thePlayer.hurtTime != 0) && addons.isEnabled("Perfect Hit") || !addons.isEnabled("Perfect Hit"))
                AttackOrder.sendFixedAttack(mc.thePlayer, target);
        } else {
            if ((target.hurtTime <= 2 || mc.thePlayer.hurtTime != 0) && addons.isEnabled("Perfect Hit") || !addons.isEnabled("Perfect Hit"))
                AttackOrder.sendFixedAttack(mc.thePlayer, target);
        }
    }

    public boolean isHoldingSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public List<EntityLivingBase> getTargets() {
        final List<EntityLivingBase> entities = new ArrayList<>();
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase e) {
                if (isValid(e) && PlayerUtils.getDistanceToEntityBox(e) < searchRange.get() && (RotationUtils.getRotationDifference(e) >= fov.get() || fov.get() == 180)) entities.add(e);
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
                    if (filter.isEnabled("Friends") && MoonLight.INSTANCE.getFriendManager().isFriend((EntityPlayer) entity))
                        return false;
                    return !isEnabled(AntiBot.class) || !getModule(AntiBot.class).bots.contains(entity);
                }
            }
            return (targetOption.isEnabled("Mobs") && isMob(entity)) || (targetOption.isEnabled("Animals") && isAnimal(entity));
        }
        return false;
    }

    public boolean isMob(Entity entity) {
        return entity instanceof EntityMob
                || entity instanceof EntityVillager
                || entity instanceof EntitySlime
                || entity instanceof EntityGhast
                || entity instanceof EntityDragon;
    }

    public boolean isAnimal(Entity entity) {
        return entity instanceof EntityAnimal
                || entity instanceof EntitySquid
                || entity instanceof EntityGolem
                || entity instanceof EntityBat;
    }

    public boolean shouldAttack() {
        return PlayerUtils.getDistanceToEntityBox(target) < (!mc.thePlayer.canEntityBeSeen(target) ? wallAttackRange.get() : attackRange.get()) && (!addons.isEnabled("Hit Select") || addons.isEnabled("Hit Select") && damaged);
    }

    public boolean shouldBlock() {
        return PlayerUtils.getDistanceToEntityBox(target) < blockRange.get() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public float[] calcToEntity(EntityLivingBase entity) {

        float yaw;
        float pitch;

        Vec3 playerPos = mc.thePlayer.getPositionEyes(1);
        Vec3 entityPos = entity.getPositionVector();
        AxisAlignedBB boundingBox = entity.getEntityBoundingBox();

        switch (aimMode.get()) {
            case "Head":
                aimVec = entityPos.add(0.0, entity.getEyeHeight(), 0.0);
                break;
            case "Torso":
                aimVec = entityPos.add(0.0, entity.height * 0.75, 0.0);
                break;
            case "Legs":
                aimVec = entityPos.add(0.0, entity.height * 0.45, 0.0);
                break;
            case "Nearest":
                aimVec = RotationUtils.getBestHitVec(entity);
                break;
            case "Test":

                Vec3 test = new Vec3(entity.posX, entity.posY, entity.posZ);

                double diffY;
                for (diffY = boundingBox.minY + 0.7D; diffY < boundingBox.maxY - 0.1D; diffY += 0.1D) {
                    if (mc.thePlayer.getPositionEyes(1).distanceTo(new Vec3(entity.posX, diffY, entity.posZ)) < mc.thePlayer.getPositionEyes(1).distanceTo(test)) {
                        test = new Vec3(entity.posX, diffY, entity.posZ);
                    }
                }
                aimVec = test;
                break;
            default:
                aimVec = entityPos;
        }

        if(heuristics.get()){
            aimVec = RotationUtils.heuristics(entity,aimVec);
        }

        if(bruteforce.get()) {
            if (RotationUtils.rayCast(RotationUtils.getRotations(aimVec), rotationRange.get()).typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                final double xWidth = boundingBox.maxX - boundingBox.minX;
                final double zWidth = boundingBox.maxZ - boundingBox.minZ;
                final double height = boundingBox.maxY - boundingBox.minY;
                for (double x = 0.0; x < 1.0; x += 0.2) {
                    for (double y = 0.0; y < 1.0; y += 0.2) {
                        for (double z = 0.0; z < 1.0; z += 0.2) {
                            final Vec3 hitVec = new Vec3(boundingBox.minX + xWidth * x, boundingBox.minY + height * y, boundingBox.minZ + zWidth * z);
                            if (RotationUtils.rayCast(RotationUtils.getRotations(hitVec), rotationRange.get()).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                aimVec = hitVec;
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
                aimVec.yCoord = minAimY;
            }

            if (RotationUtils.getBestHitVec(entity).yCoord > maxAimY) {
                aimVec.yCoord = maxAimY;
            }
        }

        if (shake.get() && (MovementUtils.isMoving() || MovementUtils.isMoving(entity))) {
            if (intaveShake.get()) {
                aimVec = aimVec.addVector(
                        MathUtils.nextSecureFloat(1.0, 2.0) * Math.sin(aimVec.xCoord * 3.141592653589793) * yawShakeRange.get(),
                        MathUtils.nextSecureFloat(1.0, 2.0) * Math.sin(aimVec.yCoord * 3.141592653589793) * pitchShakeRange.get(),
                        MathUtils.nextSecureFloat(1.0, 2.0) * Math.sin(aimVec.zCoord * 3.141592653589793) * yawShakeRange.get()
                );
            } else {
                aimVec = aimVec.addVector(
                        MathUtils.randomizeDouble(-yawShakeRange.get(), yawShakeRange.get()),
                        MathUtils.randomizeDouble(-pitchShakeRange.get(), pitchShakeRange.get()),
                        MathUtils.randomizeDouble(-yawShakeRange.get(), yawShakeRange.get())
                );
            }
        }

        double deltaX = aimVec.xCoord - playerPos.xCoord;
        double deltaY = aimVec.yCoord - playerPos.yCoord;
        double deltaZ = aimVec.zCoord - playerPos.zCoord;

        yaw = (float) -(Math.atan2(deltaX, deltaZ) * (180.0 / Math.PI));
        pitch = (float) (-Math.toDegrees(Math.atan2(deltaY, Math.hypot(deltaX, deltaZ))));

        if (pitch > 90.0f) {
            pitch = 90.0f;
        } else if (pitch < -90.0f) {
            pitch = -90.0f;
        }
        return new float[]{yaw, pitch};
    }
    public static void drawDot(@NotNull Vec3 pos, double size, int color) {
        double d = size / 2;
        AxisAlignedBB bbox = new AxisAlignedBB(pos.xCoord - d, pos.yCoord - d, pos.zCoord - d, pos.xCoord + d, pos.yCoord + d, pos.zCoord + d);

        AxisAlignedBB axis = new AxisAlignedBB(bbox.minX - mc.thePlayer.posX, bbox.minY - mc.thePlayer.posY, bbox.minZ - mc.thePlayer.posZ, bbox.maxX - mc.thePlayer.posX, bbox.maxY - mc.thePlayer.posY, bbox.maxZ - mc.thePlayer.posZ);
        RenderUtils.drawAxisAlignedBB(axis, true, color);
    }

    private void resetVariables() {
        alphaAnim.setDirection(Direction.BACKWARDS);
        aimVec = null;
        if (!autoBlock.is("Watchdog"))
            unblock();
    }

    private void points() {
        if (target != null) {
            double markerX = MathUtils.interporate(mc.timer.renderPartialTicks, target.lastTickPosX, target.posX);
            double markerY = MathUtils.interporate(mc.timer.renderPartialTicks, target.lastTickPosY, target.posY) + target.height / 1.6f;
            double markerZ = MathUtils.interporate(mc.timer.renderPartialTicks, target.lastTickPosZ, target.posZ);
            float time = (float) ((((System.currentTimeMillis() - startTime) / 1500F)) + (Math.sin((((System.currentTimeMillis() - startTime) / 1500F))) / 10f));
            float alpha = ((Shaders.shaderPackLoaded ? 1 : 0.5f) * 1);
            float pl = 0;
            boolean fa = false;
            for (int iteration = 0; iteration < 3; iteration++) {
                for (float i = time * 360; i < time * 360 + 90; i += 2) {
                    float max = time * 360 + 90;
                    float dc = MathUtils.normalize(i, time * 360 - 45, max);
                    float rf = 0.6f;
                    double radians = Math.toRadians(i);
                    double plY = pl + Math.sin(radians * 1.2f) * 0.1f;
                    int firstColor = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(0)), (float) alphaAnim.getOutput()).getRGB();
                    int secondColor = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(90)), (float) alphaAnim.getOutput()).getRGB();
                    GlStateManager.pushMatrix();
                    RenderUtils.setupOrientationMatrix(markerX, markerY, markerZ);

                    float[] idk = new float[]{mc.getRenderManager().playerViewY, mc.getRenderManager().playerViewX};

                    GL11.glRotated(-idk[0], 0.0, 1.0, 0.0);
                    GL11.glRotated(idk[1], 1.0, 0.0, 0.0);

                    GlStateManager.depthMask(false);
                    float q = (!fa ? 0.25f : 0.15f) * (Math.max(fa ? 0.25f : 0.15f, fa ? dc : (1f + (0.4f - dc)) / 2f) + 0.45f);
                    float size = q * (2f + ((0.5f - alpha) * 2));
                    RenderUtils.drawImage(
                            glowCircle,
                            Math.cos(radians) * rf - size / 2f,
                            plY - 0.7,
                            Math.sin(radians) * rf - size / 2f, size, size,
                            firstColor,
                            secondColor,
                            secondColor,
                            firstColor);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GlStateManager.depthMask(true);
                    GlStateManager.popMatrix();
                }
                time *= -1.025f;
                fa = !fa;
                pl += 0.45f;
            }
        }
    }

    private void drawTargetESP2D(float x, float y, float scale, int index) {
        long millis = System.currentTimeMillis() + index * 400L;
        double angle = MathHelper.clamp_double((Math.sin(millis / 150.0) + 1.0) / 2.0 * 30.0, 0.0, 30.0);
        double scaled = MathHelper.clamp_double((Math.sin(millis / 500.0) + 1.0) / 2.0, 0.8, 1.0);
        double rotate = MathHelper.clamp_double((Math.sin(millis / 1000.0) + 1.0) / 2.0 * 360.0, 0.0, 360.0);
        int color = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(0)), (float) alphaAnim.getOutput()).getRGB();
        int color2 = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(90)), (float) alphaAnim.getOutput()).getRGB();
        int color3 = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(180)), (float) alphaAnim.getOutput()).getRGB();
        int color4 = ColorUtils.applyOpacity(new Color(getModule(Interface.class).color(270)), (float) alphaAnim.getOutput()).getRGB();

        rotate = 45 - (angle - 15.0) + rotate;
        float size = 128.0f * scale * (float) scaled;
        float x2 = (x -= size / 2.0f) + size;
        float y2 = (y -= size / 2.0f) + size;
        GlStateManager.pushMatrix();
        RenderUtils.customRotatedObject2D(x, y, size, size, (float) rotate);
        GL11.glDisable(3008);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425);
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        RenderUtils.drawImage(rectangle, x, y, x2, y2, color, color2, color3, color4);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.resetColor();
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GL11.glEnable(3008);
        GlStateManager.popMatrix();
    }

    private float[] targetESPSPos(EntityLivingBase entity) {
        EntityRenderer entityRenderer = mc.entityRenderer;
        float partialTicks = mc.timer.renderPartialTicks;
        double x = MathUtils.interpolate(entity.prevPosX, entity.posX, partialTicks);
        double y = MathUtils.interpolate(entity.prevPosY, entity.posY, partialTicks);
        double z = MathUtils.interpolate(entity.prevPosZ, entity.posZ, partialTicks);
        double height = entity.height / (entity.isChild() ? 1.75f : 1.0f) / 2.0f;
        AxisAlignedBB bb = new AxisAlignedBB(x - 0.0, y, z - 0.0, x + 0.0, y + height, z + 0.0);
        final double[][] vectors = {{bb.minX, bb.minY, bb.minZ},
                {bb.minX, bb.maxY, bb.minZ},
                {bb.minX, bb.maxY, bb.maxZ},
                {bb.minX, bb.minY, bb.maxZ},
                {bb.maxX, bb.minY, bb.minZ},
                {bb.maxX, bb.maxY, bb.minZ},
                {bb.maxX, bb.maxY, bb.maxZ},
                {bb.maxX, bb.minY, bb.maxZ}};
        entityRenderer.setupCameraTransform(partialTicks, 0);
        float[] projection;
        final float[] position = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F};
        for (final double[] vec : vectors) {
            projection = GLUtils.project2D((float) (vec[0] - mc.getRenderManager().viewerPosX), (float) (vec[1] - mc.getRenderManager().viewerPosY), (float) (vec[2] - mc.getRenderManager().viewerPosZ), new ScaledResolution(mc).getScaleFactor());
            if (projection != null && projection[2] >= 0.0F && projection[2] < 1.0F) {
                position[0] = Math.min(projection[0], position[0]);
                position[1] = Math.min(projection[1], position[1]);
                position[2] = Math.max(projection[0], position[2]);
                position[3] = Math.max(projection[1], position[3]);
            }
        }
        entityRenderer.setupOverlayRendering();
        return new float[]{position[0], position[1]};
    }
}
