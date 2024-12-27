/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.utils.player;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.commons.lang3.tuple.Pair;
import wtf.moonlight.utils.InstanceAccess;

import java.util.List;

public class SimulatedPlayer implements InstanceAccess {
    private final EntityPlayerSP player;
    public AxisAlignedBB box;
    public final MovementInput movementInput;
    private int jumpTicks;
    public double motionZ;
    public double motionY;
    public double motionX;
    private boolean inWater;
    public boolean onGround;
    private boolean isAirBorne;
    public float rotationYaw;
    private double posX;
    private double posY;
    private double posZ;
    private final PlayerCapabilities capabilities;
    private final Entity ridingEntity;
    private float jumpMovementFactor;
    private final World worldObj;
    public boolean isCollidedHorizontally;
    private boolean isCollidedVertically;
    private final WorldBorder worldBorder;
    private final IChunkProvider chunkProvider;
    private boolean isOutsideBorder;
    private final Entity riddenByEntity;
    private BaseAttributeMap attributeMap;
    private final boolean isSpectator;
    public float fallDistance;
    private final float stepHeight;
    private boolean isCollided;
    private int fire;
    private float distanceWalkedModified;
    private float distanceWalkedOnStepModified;
    private int nextStepDistance;
    public final float height;
    private final float width;
    private final int fireResistance;
    private boolean isInWeb;
    private boolean noClip;
    private boolean isSprinting;
    private final FoodStats foodStats;

    public SimulatedPlayer(EntityPlayerSP player, AxisAlignedBB box, MovementInput movementInput, int jumpTicks, double motionZ, double motionY, double motionX, boolean inWater, boolean onGround, boolean isAirBorne, float rotationYaw, double posX, double posY, double posZ, PlayerCapabilities capabilities, Entity ridingEntity, float jumpMovementFactor, World worldObj, boolean isCollidedHorizontally, boolean isCollidedVertically, WorldBorder worldBorder, IChunkProvider chunkProvider, boolean isOutsideBorder, Entity riddenByEntity, BaseAttributeMap attributeMap, boolean isSpectator, float fallDistance, float stepHeight, boolean isCollided, int fire, float distanceWalkedModified, float distanceWalkedOnStepModified, int nextStepDistance, float height, float width, int fireResistance, boolean isInWeb, boolean noClip, boolean isSprinting, FoodStats foodStats) {
        this.player = player;
        this.box = box;
        this.movementInput = movementInput;
        this.jumpTicks = jumpTicks;
        this.motionZ = motionZ;
        this.motionY = motionY;
        this.motionX = motionX;
        this.inWater = inWater;
        this.onGround = onGround;
        this.isAirBorne = isAirBorne;
        this.rotationYaw = rotationYaw;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.capabilities = capabilities;
        this.ridingEntity = ridingEntity;
        this.jumpMovementFactor = jumpMovementFactor;
        this.worldObj = worldObj;
        this.isCollidedHorizontally = isCollidedHorizontally;
        this.isCollidedVertically = isCollidedVertically;
        this.worldBorder = worldBorder;
        this.chunkProvider = chunkProvider;
        this.isOutsideBorder = isOutsideBorder;
        this.riddenByEntity = riddenByEntity;
        this.attributeMap = attributeMap;
        this.isSpectator = isSpectator;
        this.fallDistance = fallDistance;
        this.stepHeight = stepHeight;
        this.isCollided = isCollided;
        this.fire = fire;
        this.distanceWalkedModified = distanceWalkedModified;
        this.distanceWalkedOnStepModified = distanceWalkedOnStepModified;
        this.nextStepDistance = nextStepDistance;
        this.height = height;
        this.width = width;
        this.fireResistance = fireResistance;
        this.isInWeb = isInWeb;
        this.noClip = noClip;
        this.isSprinting = isSprinting;
        this.foodStats = foodStats;
    }

    public Vec3 getPos() {
        return new Vec3(posX, posY, posZ);
    }

    private float moveForward = 0f;
    private float moveStrafing = 0f;
    private boolean isJumping = false;

    public boolean safeWalk = false;

    private static final float SPEED_IN_AIR = 0.02F;

    public static SimulatedPlayer fromClientPlayer(MovementInput input) {
        EntityPlayerSP player = mc.thePlayer;

        PlayerCapabilities capabilities = createCapabilitiesCopy(player);
        FoodStats foodStats = createFoodStatsCopy(player);

        MovementInput movementInput = new MovementInput();
        movementInput.jump = input.jump;
        movementInput.moveForward = input.moveForward;
        movementInput.moveStrafe = input.moveStrafe;
        movementInput.sneak = input.sneak;

        return new SimulatedPlayer(player,
                player.getEntityBoundingBox(),
                movementInput,
                player.jumpTicks,
                player.motionZ,
                player.motionY,
                player.motionX,
                player.isInWater(),
                player.onGround,
                player.isAirBorne,
                player.rotationYaw,
                player.posX,
                player.posY,
                player.posZ,
                capabilities,
                player.ridingEntity,
                player.jumpMovementFactor,
                player.worldObj,
                player.isCollidedHorizontally,
                player.isCollidedVertically,
                player.worldObj.getWorldBorder(),
                player.worldObj.getChunkProvider(),
                player.isOutsideBorder(),
                player.riddenByEntity,
                player.getAttributeMap(),
                player.isSpectator(),
                player.fallDistance,
                player.stepHeight,
                player.isCollided,
                player.fire,
                player.distanceWalkedModified,
                player.distanceWalkedOnStepModified,
                player.nextStepDistance,
                player.height,
                player.width,
                player.fireResistance,
                player.isInWeb,
                player.noClip,
                player.isSprinting(),
                foodStats
        );
    }

    private static FoodStats createFoodStatsCopy(EntityPlayerSP player) {
        NBTTagCompound foodStatsNBT = new NBTTagCompound();
        FoodStats foodStats = new FoodStats();

        player.getFoodStats().writeNBT(foodStatsNBT);
        foodStats.readNBT(foodStatsNBT);
        return foodStats;
    }

    private static PlayerCapabilities createCapabilitiesCopy(EntityPlayerSP player) {
        NBTTagCompound capabilitiesNBT = new NBTTagCompound();
        PlayerCapabilities capabilities = new PlayerCapabilities();

        player.capabilities.writeCapabilitiesToNBT(capabilitiesNBT);
        capabilities.readCapabilitiesFromNBT(capabilitiesNBT);

        return capabilities;
    }

    private boolean onEntityUpdate() {
        handleWaterMovement();
        if (worldObj.isRemote) {
            fire = 0;
        } else if (fire > 0) {
            --fire;
        }

        if (isInLava()) {
            setOnFireFromLava();
            fallDistance *= 0.5f;
        }

        return !(posY < -64.0);
    }

    public void tick() {
        if (!onEntityUpdate() || player.isRiding()) {
            return;
        }

        playerUpdate(false);
        clientPlayerLivingUpdate();
        playerUpdate(true);
    }

    private void clientPlayerLivingUpdate() {
        pushOutOfBlocks(posX - width * 0.35, getEntityBoundingBox().minY + 0.5, posZ + width * 0.35);
        pushOutOfBlocks(posX - width * 0.35, getEntityBoundingBox().minY + 0.5, posZ - width * 0.35);
        pushOutOfBlocks(posX + width * 0.35, getEntityBoundingBox().minY + 0.5, posZ - width * 0.35);
        pushOutOfBlocks(posX + width * 0.35, getEntityBoundingBox().minY + 0.5, posZ + width * 0.35);

        boolean flag3 = this.foodStats.getFoodLevel() > 6 || capabilities.allowFlying;
        float f = 0.8f;

        boolean shouldSprint = player.isSprinting();

        if (onGround && movementInput.moveForward >= f && !isSprinting() && flag3 && !player.isUsingItem() && !isPotionActive(Potion.blindness) && shouldSprint) {
            setSprinting(true);
        }

        if (!isSprinting() && movementInput.moveForward >= f && flag3 && !player.isUsingItem() && !isPotionActive(Potion.blindness) && shouldSprint) {
            setSprinting(true);
        }

        if (movementInput.sneak) {
            setSprinting(false);
        }

        if (isSprinting() && (movementInput.moveForward < 0.8f || isCollidedHorizontally || !flag3)) {
            setSprinting(false);
        }

        if (capabilities.allowFlying) {
            if (mc.playerController.isSpectatorMode()) {
                if (!capabilities.isFlying) {
                    capabilities.isFlying = true;
                }
            }
        }

        if (capabilities.isFlying) {
            if (movementInput.sneak) {
                motionY -= (capabilities.getFlySpeed() * 3.0f);
            }
            if (movementInput.jump) {
                motionY += (capabilities.getFlySpeed() * 3.0f);
            }
        }

        livingEntityUpdate();
    }

    private void playerUpdate(boolean post) {
        if (!post) {
            noClip = this.isSpectator;

            if (this.isSpectator) {
                onGround = false;
            }
        } else {
            clampPositionFromEntityPlayer();
        }
    }

    private void livingEntityUpdate() {
        if (this.jumpTicks > 0) {
            --this.jumpTicks;
        }

        if (Math.abs(this.motionX) < 0.005) {
            this.motionX = 0.0;
        }

        if (Math.abs(this.motionY) < 0.005) {
            this.motionY = 0.0;
        }

        if (Math.abs(this.motionZ) < 0.005) {
            this.motionZ = 0.0;
        }

        if (this.isMovementBlocked()) {
            this.isJumping = false;
            this.moveStrafing = 0.0f;
            this.moveForward = 0.0f;
        } else if (this.isServerWorld()) {
            this.updateLivingEntityInput();
        }

        if (this.isJumping) {
            if (this.isInWater() || this.isInLava()) {
                this.updateAITick();
            } else if (this.onGround && this.jumpTicks == 0) {
                this.jump();
            }
        } else {
            this.jumpTicks = 0;
        }

        this.moveStrafing *= 0.98f;
        this.moveForward *= 0.98f;
        this.playerSideMoveEntityWithHeading(this.moveStrafing, this.moveForward);

        jumpMovementFactor = SPEED_IN_AIR;
        if (isSprinting()) {
            jumpMovementFactor = (float) (jumpMovementFactor + SPEED_IN_AIR * 0.3);
        }

        if (this.onGround && this.capabilities.isFlying && !isSpectator) {
            this.capabilities.isFlying = false;
        }
    }

    private void clampPositionFromEntityPlayer() {
        double d3 = MathHelper.clamp_double(posX, -2.9999999E7, 2.9999999E7);
        double d4 = MathHelper.clamp_double(posZ, -2.9999999E7, 2.9999999E7);
        if (d3 != posX || d4 != posZ) {
            setPosition(d3, posY, d4);
        }
    }

    private void setPosition(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;
        float f = width / 2.0f;
        float f1 = height;
        setEntityBoundingBox(new AxisAlignedBB(x - f, y, z - f, x + f, y + f1, z + f));
    }

    private void setSprinting(boolean state) {
        isSprinting = state;
    }

    private boolean pushOutOfBlocks(double x, double y, double z) {
        if (noClip) {
            return false;
        } else {
            BlockPos blockPos = new BlockPos(x, y, z);
            double d0 = x - blockPos.getX();
            double d1 = z - blockPos.getZ();
            int entHeight = (int) Math.ceil(height);
            boolean inTranslucentBlock = !this.isHeadspaceFree(blockPos, entHeight);
            if (inTranslucentBlock) {
                int i = -1;
                double d2 = 9999.0;
                if (this.isHeadspaceFree(blockPos.west(), entHeight) && d0 < d2) {
                    d2 = d0;
                    i = 0;
                }
                if (this.isHeadspaceFree(blockPos.east(), entHeight) && 1.0 - d0 < d2) {
                    d2 = 1.0 - d0;
                    i = 1;
                }
                if (this.isHeadspaceFree(blockPos.north(), entHeight) && d1 < d2) {
                    d2 = d1;
                    i = 4;
                }
                if (this.isHeadspaceFree(blockPos.south(), entHeight) && 1.0 - d1 < d2) {
                    i = 5;
                }

                float f = 0.1f;
                if (i == 0) {
                    motionX = -f;
                }
                if (i == 1) {
                    motionX = f;
                }
                if (i == 4) {
                    motionZ = -f;
                }
                if (i == 5) {
                    motionZ = f;
                }
            }
            return false;
        }
    }

    private boolean isHeadspaceFree(BlockPos pos, int height) {
        for (int y = 0; y < height; y++) {
            if (!this.isOpenBlockSpace(pos.add(0, y, 0))) {
                return false;
            }
        }
        return true;
    }

    private boolean isOpenBlockSpace(BlockPos pos) {
        return getBlockState(pos).getBlock().isNormalCube();
    }

    private void playerSideMoveEntityWithHeading(float moveStrafing, float moveForward) {
        if (capabilities.isFlying && ridingEntity == null) {
            double d3 = motionY;
            float f = jumpMovementFactor;
            jumpMovementFactor = capabilities.getFlySpeed() * (isSprinting() ? 2 : 1);
            livingEntitySideMoveEntityWithHeading(moveStrafing, moveForward);
            motionY = d3 * 0.6;
            jumpMovementFactor = f;
        } else {
            livingEntitySideMoveEntityWithHeading(moveStrafing, moveForward);
        }
    }

    private void livingEntitySideMoveEntityWithHeading(float strafing, float forwards) {
        double d0;
        float f3;
        if (isServerWorld()) {
            float f5;
            float f6;
            if (!isInWater() || this.capabilities.isFlying) {
                if (isInLava() && !this.capabilities.isFlying) {
                    d0 = posY;
                    moveFlying(strafing, forwards, 0.02f);
                    moveEntity(motionX, motionY, motionZ);
                    motionX *= 0.5;
                    motionY *= 0.5;
                    motionZ *= 0.5;
                    motionY -= 0.02;
                    if (isCollidedHorizontally && isOffsetPositionInLiquid(motionX, motionY + 0.6000000238418579 - posY + d0, motionZ)) {
                        motionY = 0.30000001192092896;
                    }
                } else {
                    float f4 = 0.91f;
                    if (onGround) {
                        f4 = worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(posZ))).getBlock().slipperiness * 0.91f;
                    }

                    float f = 0.16277136f / (f4 * f4 * f4);
                    f5 = onGround ? getAIMoveSpeed() * f : jumpMovementFactor;

                    moveFlying(strafing, forwards, f5);
                    f4 = 0.91f;
                    if (onGround) {
                        f4 = worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(posZ))).getBlock().slipperiness * 0.91f;
                    }

                    if (isOnLadder()) {
                        f6 = 0.15f;
                        motionX = MathHelper.clamp_double(motionX, -f6, f6);
                        motionZ = MathHelper.clamp_double(motionZ, -f6, f6);
                        fallDistance = 0.0f;
                        if (motionY < -0.15) {
                            motionY = -0.15;
                        }

                        boolean flag = isSneaking();
                        if (flag && motionY < 0.0) {
                            motionY = 0.0;
                        }
                    }

                    moveEntity(motionX, motionY, motionZ);
                    if (isCollidedHorizontally && isOnLadder()) {
                        motionY = 0.2;
                    }

                    if (worldObj.isRemote && (!worldObj.isBlockLoaded(new BlockPos(posX, 0, posZ)) || !worldObj.getChunkFromBlockCoords(new BlockPos(posX, 0, posZ)).isLoaded())) {
                        motionY = posY > 0.0 ? -0.1 : 0.0;
                    } else {
                        motionY -= 0.08;
                    }

                    motionY *= 0.9800000190734863;
                    motionX *= f4;
                    motionZ *= f4;
                }
            } else {
                d0 = posY;
                f5 = 0.8f;
                f6 = 0.02f;
                f3 = EnchantmentHelper.getDepthStriderModifier(player);
                if (f3 > 3.0f) {
                    f3 = 3.0f;
                }

                if (!onGround) {
                    f3 *= 0.5f;
                }

                if (f3 > 0.0f) {
                    f5 += (0.54600006f - f5) * f3 / 3.0f;
                    f6 += (getAIMoveSpeed() - f6) * f3 / 3.0f;
                }

                moveFlying(strafing, forwards, f6);
                moveEntity(motionX, motionY, motionZ);
                motionX *= f5;
                motionY *= 0.800000011920929;
                motionZ *= f5;
                motionY -= 0.02;
                if (isCollidedHorizontally && isOffsetPositionInLiquid(motionX, motionY + 0.6000000238418579 - posY + d0, motionZ)) {
                    motionY = 0.30000001192092896;
                }
            }
        }
    }

    public void moveEntity(double xMotion, double yMotion, double zMotion) {
        double velocityX = xMotion;
        double velocityY = yMotion;
        double velocityZ = zMotion;
        if (noClip) {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(velocityX, velocityY, velocityZ));
            resetPositionToBB();
        } else {
            double d0 = posX;
            double d1 = posY;
            double d2 = posZ;
            if (isInWeb) {
                isInWeb = false;
                velocityX *= 0.25;
                velocityY *= 0.05000000074505806;
                velocityZ *= 0.25;
                motionX = 0.0;
                motionY = 0.0;
                motionZ = 0.0;
            }
            double d3 = velocityX;
            double d4 = velocityY;
            double d5 = velocityZ;

            boolean flag = onGround && (isSneaking() || safeWalk);

            if (flag) {
                d3 = checkForCollision(this, velocityX, velocityZ).getLeft();
                d5 = checkForCollision(this, velocityX, velocityZ).getRight();
            }

            List<AxisAlignedBB> list1 = worldObj.getCollidingBoundingBoxes(player,
                    getEntityBoundingBox().addCoord(velocityX, velocityY, velocityZ));
            AxisAlignedBB axisalignedbb = getEntityBoundingBox();

            for (AxisAlignedBB axisalignedbb1 : list1) {
                velocityY = axisalignedbb1.calculateYOffset(getEntityBoundingBox(), velocityY);
            }

            setEntityBoundingBox(getEntityBoundingBox().offset(0.0, velocityY, 0.0));
            boolean flag1 = onGround || d4 != velocityY && d4 < 0;

            for (AxisAlignedBB axisalignedbb2 : list1) {
                velocityX = axisalignedbb2.calculateXOffset(getEntityBoundingBox(), velocityX);
            }

            setEntityBoundingBox(getEntityBoundingBox().offset(velocityX, 0.0, 0.0));

            for (AxisAlignedBB axisalignedbb13 : list1) {
                velocityZ = axisalignedbb13.calculateZOffset(getEntityBoundingBox(), velocityZ);
            }

            setEntityBoundingBox(getEntityBoundingBox().offset(0.0, 0.0, velocityZ));
            if (stepHeight > 0.0f && flag1 && (d3 != velocityX || d5 != velocityZ)) {
                double d11 = velocityX;
                double d7 = velocityY;
                double d8 = velocityZ;
                AxisAlignedBB axisalignedbb3 = getEntityBoundingBox();
                setEntityBoundingBox(axisalignedbb);
                velocityY = stepHeight;
                List<AxisAlignedBB> list = worldObj.getCollidingBoundingBoxes(player,
                        getEntityBoundingBox().addCoord(d3, velocityY, d5));
                AxisAlignedBB axisalignedbb4 = getEntityBoundingBox();
                AxisAlignedBB axisalignedbb5 = axisalignedbb4.addCoord(d3, 0.0, d5);
                double d9 = velocityY;

                for (AxisAlignedBB axisalignedbb6 : list) {
                    d9 = axisalignedbb6.calculateYOffset(axisalignedbb5, d9);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0, d9, 0.0);
                double d15 = d3;

                for (AxisAlignedBB axisalignedbb7 : list) {
                    d15 = axisalignedbb7.calculateXOffset(axisalignedbb4, d15);
                }

                axisalignedbb4 = axisalignedbb4.offset(d15, 0.0, 0.0);
                double d16 = d5;

                for (AxisAlignedBB axisalignedbb8 : list) {
                    d16 = axisalignedbb8.calculateZOffset(axisalignedbb4, d16);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0, 0.0, d16);
                AxisAlignedBB axisalignedbb14 = getEntityBoundingBox();
                double d17 = velocityY;

                for (AxisAlignedBB axisalignedbb9 : list) {
                    d17 = axisalignedbb9.calculateYOffset(axisalignedbb14, d17);
                }

                axisalignedbb14 = axisalignedbb14.offset(0.0, d17, 0.0);
                double d18 = d3;

                for (AxisAlignedBB axisalignedbb10 : list) {
                    d18 = axisalignedbb10.calculateXOffset(axisalignedbb14, d18);
                }

                axisalignedbb14 = axisalignedbb14.offset(d18, 0.0, 0.0);
                double d19 = d5;

                for (AxisAlignedBB axisalignedbb11 : list) {
                    d19 = axisalignedbb11.calculateZOffset(axisalignedbb14, d19);
                }

                axisalignedbb14 = axisalignedbb14.offset(0.0, 0.0, d19);
                double d20 = d15 * d15 + d16 * d16;
                double d10 = d18 * d18 + d19 * d19;

                if (d20 > d10) {
                    velocityX = d15;
                    velocityZ = d16;
                    velocityY = -d9;
                    setEntityBoundingBox(axisalignedbb4);
                } else {
                    velocityX = d18;
                    velocityZ = d19;
                    velocityY = -d17;
                    setEntityBoundingBox(axisalignedbb14);
                }

                for (AxisAlignedBB axisalignedbb12 : list) {
                    velocityY = axisalignedbb12.calculateYOffset(getEntityBoundingBox(), velocityY);
                }

                setEntityBoundingBox(getEntityBoundingBox().offset(0.0, velocityY, 0.0));

                if (d11 * d11 + d8 * d8 >= velocityX * velocityX + velocityZ * velocityZ) {
                    velocityX = d11;
                    velocityY = d7;
                    velocityZ = d8;
                    setEntityBoundingBox(axisalignedbb3);
                }
            }
            resetPositionToBB();
            isCollidedHorizontally = d3 != velocityX || d5 != velocityZ;
            isCollidedVertically = d4 != velocityY;
            onGround = isCollidedVertically && d4 < 0.0;
            isCollided = isCollidedHorizontally || isCollidedVertically;
            int i = MathHelper.floor_double(posX);
            int j = MathHelper.floor_double(posY - 0.20000000298023224);
            int k = MathHelper.floor_double(posZ);
            BlockPos blockPos = new BlockPos(i, j, k);
            Block block1 = worldObj.getBlockState(blockPos).getBlock();
            if (block1.getMaterial() == Material.air) {
                Block block = worldObj.getBlockState(blockPos.down()).getBlock();
                if (block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockFenceGate) {
                    block1 = block;
                }
            }
            updateFallState(velocityY, onGround);
            if (d3 != velocityX) {
                motionX = 0.0;
            }
            if (d5 != velocityZ) {
                motionZ = 0.0;
            }
            if (d4 != velocityY) {
                onLanded(block1);
            }
            if (canTriggerWalking() && !flag && ridingEntity == null) {
                double d12 = posX - d0;
                double d13 = posY - d1;
                double d14 = posZ - d2;
                if (block1 != Blocks.ladder) {
                    d13 = 0.0;
                }
                if (block1 != null && onGround) {
                    onEntityCollidedWithBlock(block1);
                }
                distanceWalkedModified = (float) (distanceWalkedModified + MathHelper.sqrt_double(d12 * d12 + d14 * d14) * 0.6);
                distanceWalkedOnStepModified = (float) (distanceWalkedOnStepModified + MathHelper.sqrt_double(d12 * d12 + d13 * d13 + d14 * d14) * 0.6);
                if (distanceWalkedOnStepModified > nextStepDistance && block1.getMaterial() != Material.air) {
                    nextStepDistance = (int) distanceWalkedOnStepModified + 1;
                }
            }

            try {
                doBlockCollisions();
            } catch (Throwable var52) {
                var52.printStackTrace();
            }

            boolean flag2 = isWet();

            if (worldObj.isFlammableWithin(this.getEntityBoundingBox().contract(0.001, 0.001, 0.001))) {
                //this.dealFireDamage(1);
                if (!flag2) {
                    ++fire;
                    if (fire == 0) {
                        setFire(8);
                    }
                }
            } else if (fire <= 0) {
                fire = -fireResistance;
            }

            if (flag2 && fire > 0) {
                fire = -fireResistance;
            }
        }
    }

    public AxisAlignedBB getEntityBoundingBox() {
        return box;
    }

    public void setEntityBoundingBox(AxisAlignedBB box) {
        this.box = box;
    }

    public void setOnFireFromLava() {
        setFire(15);
    }

    public void setFire(int seconds) {
        int i = seconds * 20;
        i = EnchantmentProtection.getFireTimeForEntity(player, i);
        if (fire < i) {
            fire = i;
        }
    }

    public boolean isWet() {
        return inWater || isRainingAt(new BlockPos(posX, posY, posZ))
                || isRainingAt(new BlockPos(posX, posY + height, posZ));
    }

    public void doBlockCollisions() {
        BlockPos blockpos = new BlockPos(getEntityBoundingBox().minX + 0.001,
                getEntityBoundingBox().minY + 0.001,
                getEntityBoundingBox().minZ + 0.001);
        BlockPos blockpos1 = new BlockPos(getEntityBoundingBox().maxX - 0.001,
                getEntityBoundingBox().maxY - 0.001,
                getEntityBoundingBox().maxZ - 0.001);
        if (isAreaLoaded(blockpos.getX(), blockpos.getY(), blockpos.getZ(), blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), true)) {
            for (int i = blockpos.getX(); i <= blockpos1.getX(); i++) {
                for (int j = blockpos.getY(); j <= blockpos1.getY(); j++) {
                    for (int k = blockpos.getZ(); k <= blockpos1.getZ(); k++) {
                        BlockPos pos = new BlockPos(i, j, k);
                        IBlockState state = worldObj.getBlockState(pos);
                        try {
                            Block block = state.getBlock();
                            if (block instanceof BlockWeb) {
                                isInWeb = true;
                            } else if (block instanceof BlockSoulSand) {
                                motionX *= 0.4;
                                motionZ *= 0.4;
                            }
                        } catch (Throwable var11) {
                            var11.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void updateFallState(double motionY, boolean onGround) {
        if (!isInWater()) {
            handleWaterMovement();
        }

        if (onGround) {
            if (fallDistance > 0.0f) {
                fallDistance = 0.0f;
            }
        } else if (motionY < 0.0) {
            fallDistance = (float) (fallDistance - motionY);
        }
    }

    public boolean handleWaterMovement() {
        if (handleMaterialAcceleration(getEntityBoundingBox().expand(0.0, -0.4000000059604645, 0.0)
                .contract(0.001, 0.001, 0.001), Material.water)) {
            fallDistance = 0.0f;
            inWater = true;
            fire = 0;
        } else {
            inWater = false;
        }

        return inWater;
    }

    public boolean handleMaterialAcceleration(AxisAlignedBB boundingBox, Material material) {
        int i = MathHelper.floor_double(boundingBox.minX);
        int j = MathHelper.floor_double(boundingBox.maxX + 1.0);
        int k = MathHelper.floor_double(boundingBox.minY);
        int l = MathHelper.floor_double(boundingBox.maxY + 1.0);
        int i1 = MathHelper.floor_double(boundingBox.minZ);
        int j1 = MathHelper.floor_double(boundingBox.maxZ + 1.0);
        if (!isAreaLoaded(i, k, i1, j, l, j1, true)) {
            return false;
        } else {
            boolean flag = false;
            Vec3 vec3 = new Vec3(0.0, 0.0, 0.0);
            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
            for (int k1 = i; k1 < j; k1++) {
                for (int l1 = k; l1 < l; l1++) {
                    for (int i2 = i1; i2 < j1; i2++) {
                        blockPos.set(k1, l1, i2);
                        IBlockState state = getBlockState(blockPos);
                        if (state == null) continue;
                        Block block = state.getBlock();
                        if (block == null) continue;
                        if (block.getMaterial() == material) {
                            double d0 = ((l1 + 1) - BlockLiquid.getLiquidHeightPercent(state.getValue(BlockLiquid.LEVEL)));
                            if (l >= d0) {
                                flag = true;
                                vec3 = block.modifyAcceleration(worldObj, blockPos, player, vec3);
                            }
                        }
                    }
                }
            }
            if (vec3.lengthVector() > 0.0 && isPushedByWater()) {
                vec3 = vec3.normalize();
                double d1 = 0.014;
                motionX += vec3.xCoord * d1;
                motionY += vec3.yCoord * d1;
                motionZ += vec3.zCoord * d1;
            }
            return flag;
        }
    }

    public boolean isAreaLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean idfk) {
        if (maxY >= 0 && minY < 256) {
            minX >>= 4;
            minZ >>= 4;
            maxX >>= 4;
            maxZ >>= 4;
            for (int i = minX; i <= maxX; i++) {
                for (int j = minZ; j <= maxZ; j++) {
                    if (!isChunkLoaded(i, j, idfk)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void onEntityCollidedWithBlock(Block block) {
        if (block instanceof BlockSlime) {
            if (Math.abs(motionY) < 0.1 && !isSneaking()) {
                double motion = 0.4 + Math.abs(motionY) * 0.2;
                motionX *= motion;
                motionZ *= motion;
            }
        }
    }

    public boolean canTriggerWalking() {
        return !capabilities.isFlying;
    }

    public boolean isOnLadder() {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.getEntityBoundingBox().minY);
        int k = MathHelper.floor_double(this.posZ);
        Block block = this.worldObj.getBlockState(new BlockPos(i, j, k)).getBlock();
        return (block == Blocks.ladder || block == Blocks.vine) && (!isSpectator);
    }

    public void moveFlying(float strafe, float forward, float friction) {
        float newStrafe = strafe;
        float newForward = forward;
        float f = newStrafe * newStrafe + newForward * newForward;
        if (f >= 1.0E-4f) {
            f = MathHelper.sqrt_float(f);
            if (f < 1.0f) {
                f = 1.0f;
            }
            f = friction / f;
            newStrafe *= f;
            newForward *= f;
            float f1 = MathHelper.sin(rotationYaw * 3.1415927f / 180.0f);
            float f2 = MathHelper.cos(rotationYaw * 3.1415927f / 180.0f);
            motionX += (newStrafe * f2 - newForward * f1);
            motionZ += (newForward * f2 + newStrafe * f1);
        }
    }

    public void jump() {
        motionY = getJumpUpwardsMotion();
        if (isPotionActive(Potion.jump)) {
            motionY += ((getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f);
        }

        if (isSprinting()) {
            float f = rotationYaw * 0.017453292f;
            motionX -= (MathHelper.sin(f) * 0.2f);
            motionZ += (MathHelper.cos(f) * 0.2f);
        }

        isAirBorne = true;
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public boolean isPotionActive(Potion potion) {
        return player.getActivePotionEffect(potion) != null;
    }

    public PotionEffect getActivePotionEffect(Potion potion) {
        return player.getActivePotionEffect(potion);
    }

    public float getJumpUpwardsMotion() {
        return 0.42f;
    }

    public boolean isInWater() {
        return inWater;
    }

    public void updateLivingEntityInput() {
        moveForward = movementInput.moveForward;
        moveStrafing = movementInput.moveStrafe;
        isJumping = movementInput.jump;
    }

    public boolean isServerWorld() {
        return true;
    }

    public boolean isMovementBlocked() {
        return player.getHealth() <= 0f || player.isPlayerSleeping();
    }

    public boolean isInLava() {
        return worldObj.isMaterialInBB(getEntityBoundingBox().expand(-0.10000000149011612, -0.4000000059604645, -0.10000000149011612), Material.lava);
    }

    public void updateAITick() {
        motionY += 0.03999999910593033;
    }

    public boolean isOffsetPositionInLiquid(double x, double y, double z) {
        AxisAlignedBB box = getEntityBoundingBox().offset(x, y, z);
        return isLiquidPresentInAABB(box);
    }

    public boolean isLiquidPresentInAABB(AxisAlignedBB box) {
        return worldObj.getCollidingBoundingBoxes(player, box).isEmpty() && !worldObj.isAnyLiquid(box);
    }

    public List<AxisAlignedBB> getCollidingBoundingBoxes(AxisAlignedBB box) {
        List<AxisAlignedBB> list = Lists.newArrayList();
        int i = MathHelper.floor_double(box.minX);
        int j = MathHelper.floor_double(box.maxX + 1.0);
        int k = MathHelper.floor_double(box.minY);
        int l = MathHelper.floor_double(box.maxY + 1.0);
        int i1 = MathHelper.floor_double(box.minZ);
        int j1 = MathHelper.floor_double(box.maxZ + 1.0);
        WorldBorder worldBorder = getWorldBorder();
        boolean flag = isOutsideBorder;
        boolean flag1 = isInsideBorder(worldBorder, flag);
        IBlockState iblockstate = Blocks.stone.getDefaultState();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (int k1 = i; k1 < j; k1++) {
            for (int l1 = i1; l1 < j1; l1++) {
                if (isBlockLoaded(blockPos.set(k1, 64, l1))) {
                    for (int i2 = k - 1; i2 < l; i2++) {
                        blockPos.set(k1, i2, l1);
                        if (flag && flag1) {
                            isOutsideBorder = false;
                        } else if (!flag && !flag1) {
                            isOutsideBorder = true;
                        }
                        IBlockState state = iblockstate;
                        if (worldBorder.contains(blockPos) || !flag1) {
                            state = getBlockState(blockPos);
                        }
                        state.getBlock().addCollisionBoxesToList(worldObj, blockPos, state, box, list, player);
                    }
                }
            }
        }
        double d0 = 0.25;
        List<Entity> entities = getEntitiesWithinAABBExcludingEntity(player, box.expand(d0, d0, d0));
        for (Entity entity : entities) {
            if (riddenByEntity != entity && ridingEntity != entity) {
                AxisAlignedBB boundingBox = entity.getCollisionBoundingBox();
                if (boundingBox != null && boundingBox.intersectsWith(box)) {
                    list.add(boundingBox);
                }
                boundingBox = getCollisionBox(player, entity);
                if (boundingBox != null && boundingBox.intersectsWith(box)) {
                    list.add(boundingBox);
                }
            }
        }
        return list;
    }

    public IBlockState getBlockState(BlockPos blockPos) {
        return worldObj.getBlockState(blockPos);
    }

    private Chunk getChunkFromBlockCoords(BlockPos blockPos) {
        return getChunkFromChunkCoords(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    private Chunk getChunkFromChunkCoords(int x, int z) {
        return chunkProvider.provideChunk(x, z);
    }

    private boolean isValid(BlockPos pos) {
        return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000 && pos.getY() >= 0 && pos.getY() < 256;
    }

    private WorldBorder getWorldBorder() {
        return worldBorder;
    }

    private boolean isInsideBorder(WorldBorder border, boolean insideBorder) {
        double d0 = border.minX();
        double d1 = border.minZ();
        double d2 = border.maxX();
        double d3 = border.maxZ();
        if (insideBorder) {
            ++d0;
            ++d1;
            --d2;
            --d3;
        } else {
            --d0;
            --d1;
            ++d2;
            ++d3;
        }
        return posX > d0 && posX < d2 && posZ > d1 && posZ < d3;
    }

    private boolean isBlockLoaded(BlockPos pos) {
        return isBlockLoaded(pos, true);
    }

    private boolean isBlockLoaded(BlockPos pos, boolean check2) {
        return isValid(pos) && isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, check2);
    }

    private boolean isChunkLoaded(int x, int z, boolean flag) {
        return chunkProvider.chunkExists(x, z) && (flag || !chunkProvider.provideChunk(x, z).isEmpty());
    }

    private List<Entity> getEntitiesWithinAABBExcludingEntity(Entity entity, AxisAlignedBB box) {
        return getEntitiesInAABBexcluding(entity, box, EntitySelectors.NOT_SPECTATING);
    }

    private List<Entity> getEntitiesInAABBexcluding(Entity entity, AxisAlignedBB bb, Predicate<Entity> predicate) {
        List<Entity> list = Lists.newArrayList();
        int i = MathHelper.floor_double((bb.minX - 2.0) / 16.0);
        int j = MathHelper.floor_double((bb.maxX + 2.0) / 16.0);
        int k = MathHelper.floor_double((bb.minZ - 2.0) / 16.0);
        int l = MathHelper.floor_double((bb.maxZ + 2.0) / 16.0);
        for (int i1 = i; i1 <= j; i1++) {
            for (int j1 = k; j1 <= l; j1++) {
                if (isChunkLoaded(i1, j1, true)) {
                    getChunkFromChunkCoords(i1, j1).getEntitiesWithinAABBForEntity(entity, bb, list, predicate);
                }
            }
        }
        return list;
    }

    private AxisAlignedBB getCollisionBox(Entity player, Entity entity) {
        if (entity instanceof EntityBoat) {
            return entity.getEntityBoundingBox();
        } else if (entity instanceof EntityMinecart) {
            return player.getCollisionBox(entity);
        } else {
            return null;
        }
    }

    private float getAIMoveSpeed() {
        return (float) getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
    }

    private IAttributeInstance getEntityAttribute(IAttribute iAttribute) {
        return getAttributeMap().getAttributeInstance(iAttribute);
    }

    private BaseAttributeMap getAttributeMap() {
        if (attributeMap == null) {
            attributeMap = new ServersideAttributeMap();
        }
        return attributeMap;
    }

    private void resetPositionToBB() {
        posX = (getEntityBoundingBox().minX + getEntityBoundingBox().maxX) / 2.0;
        posY = getEntityBoundingBox().minY;
        posZ = (getEntityBoundingBox().minZ + getEntityBoundingBox().maxZ) / 2.0;
    }

    private void onLanded(Block block) {
        if (block instanceof BlockSlime) {
            if (isSneaking()) {
                motionY = 0.0;
            } else if (motionY < 0.0) {
                motionY = -motionY;
            }
        } else {
            motionY = 0.0;
        }
    }

    public boolean isSneaking() {
        return movementInput.sneak && !player.isPlayerSleeping();
    }

    private boolean isRainingAt(BlockPos pos) {
        if (worldObj.getRainStrength(1.0F) <= 0.2) {
            return false;
        } else if (!canSeeSky(pos)) {
            return false;
        } else if (worldObj.getPrecipitationHeight(pos).getY() > pos.getY()) {
            return false;
        } else {
            BiomeGenBase base = worldObj.getBiomeGenForCoords(pos);
            if (base.enableSnow) return false;
            else if (worldObj.canSnowAt(pos, false)) return false;
            else return base.canRain();
        }
    }

    private boolean canSeeSky(BlockPos pos) {
        return getChunkFromBlockCoords(pos).canSeeSky(pos);
    }

    private boolean isPushedByWater() {
        return !capabilities.isFlying;
    }

    public Pair<Double, Double> checkForCollision(SimulatedPlayer simPlayer, double velocityX, double velocityZ) {
        EntityPlayerSP player = mc.thePlayer;
        World worldObj = player.worldObj;

        double d6;

        double d3 = velocityX;
        double d5 = velocityZ;

        for (d6 = 0.05; velocityX != 0 && worldObj.getCollidingBoundingBoxes(player, simPlayer.box.offset(velocityX, -1, 0)).isEmpty(); d3 = velocityX) {
            if (velocityX < d6 && velocityX >= -d6) {
                velocityX = 0;
            } else if (velocityX > 0) {
                velocityX -= d6;
            } else {
                velocityX += d6;
            }
        }

        //noinspection ConstantConditions
        for (; velocityZ != 0 && worldObj.getCollidingBoundingBoxes(player, simPlayer.box.offset(0, -1, velocityZ)).isEmpty(); d5 = velocityZ) {
            if (velocityZ < d6 && velocityZ >= -d6) {
                velocityZ = 0;
            } else if (velocityZ > 0) {
                velocityZ -= d6;
            } else {
                velocityZ += d6;
            }
        }

        //noinspection ConstantConditions
        for (; velocityX != 0 && velocityZ != 0 && worldObj.getCollidingBoundingBoxes(player, simPlayer.box.offset(velocityX, -1, velocityZ)).isEmpty(); d5 = velocityZ) {
            if (velocityX < d6 && velocityX >= -d6) {
                velocityX = 0;
            } else if (velocityX > 0) {
                velocityX -= d6;
            } else {
                velocityX += d6;
            }

            d3 = velocityX;

            if (velocityZ < d6 && velocityZ >= -d6) {
                velocityZ = 0;
            } else if (velocityZ > 0) {
                velocityZ -= d6;
            } else {
                velocityZ += d6;
            }
        }

        return Pair.of(d3, d5);
    }
    public float getEyeHeight()
    {
        return this.height * 0.85F;
    }
}
