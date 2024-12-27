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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import org.joml.Vector2f;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.math.MathUtils;

import java.util.List;
import java.util.Objects;

public class ProjectileUtils implements InstanceAccess {
    public static ProjectileHit predict(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, final double motionSlowdown, final double size, final double gravity, final boolean draw) {
        MovingObjectPosition landingPosition = null;
        boolean hasLanded = false;
        boolean hitEntity = false;
        while (!hasLanded && posY > -60.0) {
            Vec3 posBefore = new Vec3(posX, posY, posZ);
            Vec3 posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
            landingPosition = mc.theWorld.rayTraceBlocks(posBefore, posAfter, false, true, false);
            posBefore = new Vec3(posX, posY, posZ);
            posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
            if (landingPosition != null) {
                hasLanded = true;
                posAfter = new Vec3(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
            }
            final AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
            final List<Entity> entityList = mc.theWorld.getEntitiesWithinAABB(Entity.class, arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
            for (int i = 0; i < entityList.size(); ++i) {
                final Entity var18 = entityList.get(i);
                if (var18.canBeCollidedWith() && var18 != mc.thePlayer) {
                    final AxisAlignedBB var19 = var18.getEntityBoundingBox().expand(size, size, size);
                    final MovingObjectPosition possibleEntityLanding = var19.calculateIntercept(posBefore, posAfter);
                    if (possibleEntityLanding != null) {
                        hitEntity = true;
                        hasLanded = true;
                        landingPosition = possibleEntityLanding;
                    }
                }
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            final BlockPos var20 = new BlockPos(posX, posY, posZ);
            final Block var21 = mc.theWorld.getBlockState(var20).getBlock();
            if (var21.getBlockState().getBlock().getMaterial() == Material.water) {
                motionX *= 0.6;
                motionY *= 0.6;
                motionZ *= 0.6;
            } else {
                motionX *= motionSlowdown;
                motionY *= motionSlowdown;
                motionZ *= motionSlowdown;
            }
            motionY -= gravity;
        }
        return new ProjectileHit(posX, posY, posZ, hitEntity, hasLanded, landingPosition);
    }

    public static class EnderPearlPredictor {
        public double predictX;
        public double predictY;
        public double predictZ;
        public double minMotionY;
        public double maxMotionY;

        public EnderPearlPredictor(final double predictX, final double predictY, final double predictZ, final double minMotionY, final double maxMotionY) {
            this.predictX = predictX;
            this.predictY = predictY;
            this.predictZ = predictZ;
            this.minMotionY = minMotionY;
            this.maxMotionY = maxMotionY;
        }

        public double assessRotation(final Vector2f rotation) {
            double mul = 1.0;
            int cnt = 0;
            for (double rate = 0.0; rate <= 1.0; rate += 0.3333) {
                for (int yaw = -1; yaw <= 1; ++yaw) {
                    for (int pitch = -1; pitch <= 1; ++pitch) {
                        mul *= this.assessSingleRotation(new Vector2f(rotation.x + yaw * 0.5f, rotation.y + pitch * 0.5f), MathUtils.interpolate(this.minMotionY, this.maxMotionY, rate));
                        ++cnt;
                    }
                }
                if (this.minMotionY == this.maxMotionY) {
                    break;
                }
            }
            return Math.pow(mul, 1.0 / cnt);
        }

        private double assessSingleRotation(final Vector2f rotation, final double motionYOffset) {
            if (rotation.y > 90.0f) {
                rotation.y = 90.0f;
            }
            if (rotation.y < -90.0f) {
                rotation.y = -90.0f;
            }
            final float motionFactor = 1.5f;
            final float gravity = 0.03f;
            final float size = 0.25f;
            final float motionSlowdown = 0.99f;
            final double posX = this.predictX - MathHelper.cos(rotation.x / 180.0f * 3.1415927f) * 0.16f;
            final double posY = this.predictY + mc.thePlayer.getEyeHeight() - 0.10000000149011612;
            final double posZ = this.predictZ - MathHelper.sin(rotation.y / 180.0f * 3.1415927f) * 0.16f;
            double motionX = -MathHelper.sin(rotation.x / 180.0f * 3.1415927f) * MathHelper.cos(rotation.y / 180.0f * 3.1415927f) * 0.4;
            double motionY = -MathHelper.sin(rotation.y / 180.0f * 3.1415927f) * 0.4;
            double motionZ = MathHelper.cos(rotation.x / 180.0f * 3.1415927f) * MathHelper.cos(rotation.y / 180.0f * 3.1415927f) * 0.4;
            final float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= distance;
            motionY /= distance;
            motionZ /= distance;
            motionX *= 1.5;
            motionY *= 1.5;
            motionZ *= 1.5;
            motionY += motionYOffset;
            final ProjectileHit projectileHit = ProjectileUtils.predict(posX, posY, posZ, motionX, motionY, motionZ, 0.9900000095367432, 0.25, 0.029999999329447746, false);
            if (!projectileHit.hasLanded) {
                return 0.05;
            }
            final EnumFacing facing = projectileHit.landingPosition.sideHit;
            final BlockPos landPos = projectileHit.landingPosition.getBlockPos().add(facing.getDirectionVec());
            return ((facing == EnumFacing.UP || facing == EnumFacing.DOWN) ? this.assessPlainBlockPos(landPos) : this.assessSideBlockPos(landPos, facing)) * this.distanceFunction(new Vec3(this.predictX, this.predictY, this.predictZ).distanceTo(new Vec3(projectileHit.posX, projectileHit.posY, projectileHit.posZ)));
        }

        private double assessPlainBlockPos(final BlockPos pos) {
            double mul = 1.0;
            mul *= Math.pow(this.assessSingleBlockPos(pos.add(0, 0, 0)), 2.0);
            mul *= this.assessSingleBlockPos(pos.add(1, 0, 0));
            mul *= this.assessSingleBlockPos(pos.add(-1, 0, 0));
            mul *= this.assessSingleBlockPos(pos.add(0, 0, 1));
            mul *= this.assessSingleBlockPos(pos.add(0, 0, -1));
            return Math.pow(mul, 0.16666666666666666);
        }

        private double assessSideBlockPos(final BlockPos pos, final EnumFacing facing) {
            double mul = 1.0;
            mul *= Math.pow(this.assessSingleBlockPos(pos.add(0, 0, 0)), 2.0);
            mul *= this.assessSingleBlockPos(pos.add(1, 0, 0));
            mul *= this.assessSingleBlockPos(pos.add(facing.getDirectionVec()));
            return Math.pow(mul, 0.3333333333333333);
        }

        private double assessSingleBlockPos(final BlockPos pos) {
            for (int y = 0; y >= -5; --y) {
                final IBlockState blockState = mc.theWorld.getBlockState(pos.add(0, y, 0));
                if (y == 0 && blockState.getBlock().isFullBlock()) {
                    return 0.4;
                }
                if (blockState.getBlock().isFullBlock()) {
                    return 1.0;
                }
            }
            return 0.05;
        }

        private double distanceFunction(double d) {
            d /= 1000.0;
            return (d + 3.0) / (d + 2.0) / 1.5;
        }
    }

    public static class ProjectileHit {
        private final double posX;
        private final double posY;
        private final double posZ;
        private final boolean hitEntity;
        private final boolean hasLanded;
        private final MovingObjectPosition landingPosition;

        public ProjectileHit(final double posX, final double posY, final double posZ, final boolean hitEntity, final boolean hasLanded, final MovingObjectPosition landingPosition) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.hitEntity = hitEntity;
            this.hasLanded = hasLanded;
            this.landingPosition = landingPosition;
        }

        public double getPosX() {
            return this.posX;
        }

        public double getPosY() {
            return this.posY;
        }

        public double getPosZ() {
            return this.posZ;
        }

        public boolean isHitEntity() {
            return this.hitEntity;
        }

        public boolean isHasLanded() {
            return this.hasLanded;
        }

        public MovingObjectPosition getLandingPosition() {
            return this.landingPosition;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.posX, this.posY, this.posZ, this.hitEntity, this.hasLanded, this.landingPosition);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            final ProjectileHit that = (ProjectileHit) obj;
            return Double.compare(that.posX, this.posX) == 0 && Double.compare(that.posY, this.posY) == 0 && Double.compare(that.posZ, this.posZ) == 0 && this.hitEntity == that.hitEntity && this.hasLanded == that.hasLanded && Objects.equals(this.landingPosition, that.landingPosition);
        }

        @Override
        public String toString() {
            return "ProjectileHit{posX=" + this.posX + ", posY=" + this.posY + ", posZ=" + this.posZ + ", hitEntity=" + this.hitEntity + ", hasLanded=" + this.hasLanded + ", landingPosition=" + this.landingPosition + '}';
        }
    }
}
