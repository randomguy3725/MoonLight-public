package wtf.moonlight.utils.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
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
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.*;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.modules.impl.combat.AntiBot;
import wtf.moonlight.features.modules.impl.combat.KillAura;
import wtf.moonlight.utils.InstanceAccess;
import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerUtils implements InstanceAccess {
    private static final HashMap<Integer, Integer> GOOD_POTIONS = new HashMap<>() {{
        put(6, 1); // Instant Health
        put(10, 2); // Regeneration
        put(11, 3); // Resistance
        put(21, 4); // Health Boost
        put(22, 5); // Absorption
        put(23, 6); // Saturation
        put(5, 7); // Strength
        put(1, 8); // Speed
        put(12, 9); // Fire Resistance
        put(14, 10); // Invisibility
        put(3, 11); // Haste
        put(13, 12); // Water Breathing
    }};

    public static boolean nullCheck() {
        return mc.thePlayer != null && mc.theWorld != null;
    }

    public static boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0.0) {
            return false;
        } else {
            for (int offset = 0; offset < (int) mc.thePlayer.posY + 2; offset += 2) {
                AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0, (-offset), 0.0);
                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isBlockUnder(int distance) {
        for (int y = (int) mc.thePlayer.posY; y >= (int) mc.thePlayer.posY - distance; --y) {
            if (!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isBlockUnder(final double height, final boolean boundingBox) {
        if (boundingBox) {
            final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -height, 0);

            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        } else {
            for (int offset = 0; offset < height; offset++) {
                if (PlayerUtils.blockRelativeToPlayer(0, -offset, 0).isFullBlock()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int findTool(final BlockPos blockPos) {
        float bestSpeed = 1;
        int bestSlot = -1;

        final IBlockState blockState = mc.theWorld.getBlockState(blockPos);

        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack == null) {
                continue;
            }

            final float speed = itemStack.getStrVsBlock(blockState.getBlock());

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    public static boolean overVoid(double posX, double posY, double posZ) {
        for (int i = (int) posY; i > -1; i--) {
            if (!(mc.theWorld.getBlockState(new BlockPos(posX, i, posZ)).getBlock() instanceof BlockAir)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInTeam(Entity entity) {
        if (mc.thePlayer.getDisplayName() != null && entity.getDisplayName() != null) {
            String targetName = entity.getDisplayName().getFormattedText().replace("§r", "");
            String clientName = mc.thePlayer.getDisplayName().getFormattedText().replace("§r", "");
            return targetName.startsWith("§" + clientName.charAt(1));
        }
        return false;
    }

    public static double getDistanceToEntityBox(Entity entity) {
        Vec3 eyes = mc.thePlayer.getPositionEyes(1f);
        Vec3 pos = RotationUtils.getBestHitVec(entity);
        double xDist = Math.abs(pos.xCoord - eyes.xCoord);
        double yDist = Math.abs(pos.yCoord - eyes.yCoord);
        double zDist = Math.abs(pos.zCoord - eyes.zCoord);
        return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2) + Math.pow(zDist, 2));
    }

    public static boolean goodPotion(final int id) {
        return GOOD_POTIONS.containsKey(id);
    }

    public static boolean inLiquid() {
        return mc.thePlayer.isInWater() || mc.thePlayer.isInLava();
    }

    public static Block getBlock(final double x, final double y, final double z) {
        return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return getBlock(mc.thePlayer.posX + offsetX, mc.thePlayer.posY + offsetY, mc.thePlayer.posZ + offsetZ);
    }

    public static boolean isBlockOver(final double height) {
        final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, height / 2f, 0).expand(0, height - mc.thePlayer.height, 0);

        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty();
    }

    public static EntityPlayer getTarget(double distance) {
        EntityPlayer target = null;
        if (mc.theWorld == null) {
            return null;
        }

        KillAura aura = INSTANCE.getModuleManager().getModule(KillAura.class);

        if (aura.isEnabled() && aura.target instanceof EntityPlayer) {
            return (EntityPlayer) aura.target;
        }

        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            if (isInTeam(entity))
                continue;

            if (Moonlight.INSTANCE.getModuleManager().getModule(AntiBot.class).isEnabled() && Moonlight.INSTANCE.getModuleManager().getModule(AntiBot.class).bots.contains(entity))
                continue;

            float tempDistance = mc.thePlayer.getDistanceToEntity(entity);
            if (entity != mc.thePlayer && tempDistance <= distance) {
                target = entity;
                distance = tempDistance;
            }
        }
        return target;
    }

    public static Block getBlock(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    public static boolean isReplaceable(BlockPos blockPos) {
        return getBlock(blockPos).isReplaceable(mc.theWorld, blockPos);
    }

    public static String getName(final NetworkPlayerInfo networkPlayerInfoIn) {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() :
                ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }
    public static Vec3 getPredictedPos(float forward, float strafe) {
        strafe *= 0.98F;
        forward *= 0.98F;
        float f4 = 0.91F;
        double motionX = mc.thePlayer.motionX;
        double motionZ = mc.thePlayer.motionZ;
        double motionY = mc.thePlayer.motionY;
        boolean isSprinting = mc.thePlayer.isSprinting();

        if (mc.thePlayer.isJumping && mc.thePlayer.onGround) {
            motionY = mc.thePlayer.getJumpUpwardsMotion();
            if (mc.thePlayer.isPotionActive(Potion.jump)) {
                motionY += (float)(mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
            }

            if (isSprinting) {
                float f = mc.thePlayer.rotationYaw * (float) (Math.PI / 180.0);
                motionX -= MathHelper.sin(f) * 0.2F;
                motionZ += MathHelper.cos(f) * 0.2F;
            }
        }

        if (mc.thePlayer.onGround) {
            f4 = mc.thePlayer
                    .worldObj
                    .getBlockState(
                            new BlockPos(
                                    MathHelper.floor_double(mc.thePlayer.posX),
                                    MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1,
                                    MathHelper.floor_double(mc.thePlayer.posZ)
                            )
                    )
                    .getBlock()
                    .slipperiness
                    * 0.91F;
        }

        float f3 = 0.16277136F / (f4 * f4 * f4);
        float friction;
        if (mc.thePlayer.onGround) {
            friction = mc.thePlayer.getAIMoveSpeed() * f3;
            if (mc.thePlayer == Minecraft.getMinecraft().thePlayer
                    && mc.thePlayer.isSprinting()) {
                friction = 0.12999998F;
            }
        } else {
            friction = mc.thePlayer.jumpMovementFactor;
        }

        float f = strafe * strafe + forward * forward;
        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float(f);
            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe *= f;
            forward *= f;
            float f1 = MathHelper.sin(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F);
            float f2 = MathHelper.cos(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F);
            motionX += strafe * f2 - forward * f1;
            motionZ += forward * f2 + strafe * f1;
        }

        f4 = 0.91F;
        if (mc.thePlayer.onGround) {
            f4 = mc.thePlayer
                    .worldObj
                    .getBlockState(
                            new BlockPos(
                                    MathHelper.floor_double(mc.thePlayer.posX),
                                    MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1,
                                    MathHelper.floor_double(mc.thePlayer.posZ)
                            )
                    )
                    .getBlock()
                    .slipperiness
                    * 0.91F;
        }

        motionY *= 0.98F;
        motionX *= f4;
        motionZ *= f4;
        return new Vec3(motionX, motionY, motionZ);
    }

    public static boolean isFullBlock(BlockPos blockPos) {
        AxisAlignedBB axisAlignedBB = getBlock(blockPos) != null ? getBlock(blockPos).getCollisionBoundingBox(mc.theWorld, blockPos, mc.theWorld.getBlockState(blockPos)) : null;
        if (axisAlignedBB == null) {
            return false;
        } else {
            return axisAlignedBB.maxX - axisAlignedBB.minX == 1.0D && axisAlignedBB.maxY - axisAlignedBB.minY == 1.0D && axisAlignedBB.maxZ - axisAlignedBB.minZ == 1.0D;
        }
    }

    public static boolean isOverAir(double x, double y, double z) {
        return isAir(new BlockPos(x, y - 1.0D, z));
    }

    public static boolean isAir(BlockPos blockPos) {
        Material material = getBlock(blockPos).getMaterial();
        return material == Material.air;
    }

    public static boolean isMob(Entity entity) {
        return entity instanceof EntityMob
                || entity instanceof EntityVillager
                || entity instanceof EntitySlime
                || entity instanceof EntityGhast
                || entity instanceof EntityDragon;
    }

    public static boolean isAnimal(Entity entity) {
        return entity instanceof EntityAnimal
                || entity instanceof EntitySquid
                || entity instanceof EntityGolem
                || entity instanceof EntityBat;
    }
    public static List<EntityPlayer> getLivingPlayers(Predicate<EntityPlayer> validator) {
        List<EntityPlayer> entities = new ArrayList<>();
        if(mc.theWorld == null) return entities;
        for (Entity entity : mc.theWorld.playerEntities) {
            if (entity instanceof EntityPlayer player) {
                if (validator.apply(player))
                    entities.add(player);
            }
        }
        return entities;
    }
    public static double calculatePerfectRangeToEntity(Entity entity) {
        double range = 1000;
        Vec3 eyes = mc.thePlayer.getPositionEyes(1);
        float[] rotations = RotationUtils.getRotations(entity.getPositionVector());
        final Vec3 rotationVector = mc.thePlayer.getVectorForRotation(rotations[1], rotations[0]);
        MovingObjectPosition movingObjectPosition = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1).calculateIntercept(eyes,
                eyes.addVector(rotationVector.xCoord * range, rotationVector.yCoord * range, rotationVector.zCoord * range));

        return movingObjectPosition.hitVec.distanceTo(eyes);
    }
}
