package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.entity.Entity;

public class BlockPos extends Vec3i
{
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
    private static final int NUM_X_BITS = 1 + MathHelper.calculateLogBaseTwo(MathHelper.roundUpToPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    public BlockPos(int x, int y, int z)
    {
        super(x, y, z);
    }

    public BlockPos(double x, double y, double z)
    {
        super(x, y, z);
    }

    public BlockPos(Entity source)
    {
        this(source.posX, source.posY, source.posZ);
    }

    public BlockPos(Vec3 source)
    {
        this(source.xCoord, source.yCoord, source.zCoord);
    }

    public BlockPos(Vec3i source)
    {
        this(source.getX(), source.getY(), source.getZ());
    }

    public BlockPos add(double x, double y, double z)
    {
        return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
    }

    public BlockPos add(int x, int y, int z)
    {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos add(Vec3i vec)
    {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0 ? this : new BlockPos(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
    }

    public BlockPos subtract(Vec3i vec)
    {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0 ? this : new BlockPos(this.getX() - vec.getX(), this.getY() - vec.getY(), this.getZ() - vec.getZ());
    }

    public BlockPos up()
    {
        return this.up(1);
    }

    public BlockPos up(int n)
    {
        return this.offset(EnumFacing.UP, n);
    }

    public BlockPos down()
    {
        return this.down(1);
    }

    public BlockPos down(int n)
    {
        return this.offset(EnumFacing.DOWN, n);
    }

    public BlockPos north()
    {
        return this.north(1);
    }

    public BlockPos north(int n)
    {
        return this.offset(EnumFacing.NORTH, n);
    }

    public BlockPos south()
    {
        return this.south(1);
    }

    public BlockPos south(int n)
    {
        return this.offset(EnumFacing.SOUTH, n);
    }

    public BlockPos west()
    {
        return this.west(1);
    }

    public BlockPos west(int n)
    {
        return this.offset(EnumFacing.WEST, n);
    }

    public BlockPos east()
    {
        return this.east(1);
    }

    public BlockPos east(int n)
    {
        return this.offset(EnumFacing.EAST, n);
    }

    public BlockPos offset(EnumFacing facing)
    {
        return this.offset(facing, 1);
    }

    public BlockPos offset(EnumFacing facing, int n)
    {
        return n == 0 ? this : new BlockPos(this.getX() + facing.getFrontOffsetX() * n, this.getY() + facing.getFrontOffsetY() * n, this.getZ() + facing.getFrontOffsetZ() * n);
    }

    public BlockPos crossProduct(Vec3i vec)
    {
        return new BlockPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    public long toLong()
    {
        return ((long)this.getX() & X_MASK) << X_SHIFT | ((long)this.getY() & Y_MASK) << Y_SHIFT | ((long)this.getZ() & Z_MASK) << 0;
    }

    public static BlockPos fromLong(long serialized)
    {
        int i = (int)(serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int j = (int)(serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int k = (int)(serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPos(i, j, k);
    }

    public static Iterable<BlockPos> getAllInBox(BlockPos from, BlockPos to)
    {
        final BlockPos blockpos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPos blockpos1 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<BlockPos>()
        {
            public Iterator<BlockPos> iterator()
            {
                return new AbstractIterator<BlockPos>()
                {
                    private BlockPos lastReturned = null;
                    protected BlockPos computeNext()
                    {
                        if (this.lastReturned == null)
                        {
                            this.lastReturned = blockpos;
                            return this.lastReturned;
                        }
                        else if (this.lastReturned.equals(blockpos1))
                        {
                            return this.endOfData();
                        }
                        else
                        {
                            int i = this.lastReturned.getX();
                            int j = this.lastReturned.getY();
                            int k = this.lastReturned.getZ();

                            if (i < blockpos1.getX())
                            {
                                ++i;
                            }
                            else if (j < blockpos1.getY())
                            {
                                i = blockpos.getX();
                                ++j;
                            }
                            else if (k < blockpos1.getZ())
                            {
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.lastReturned = new BlockPos(i, j, k);
                            return this.lastReturned;
                        }
                    }
                };
            }
        };
    }

    public static Iterable<BlockPos.MutableBlockPos> getAllInBoxMutable(BlockPos from, BlockPos to)
    {
        final BlockPos blockpos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPos blockpos1 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<BlockPos.MutableBlockPos>()
        {
            public Iterator<BlockPos.MutableBlockPos> iterator()
            {
                return new AbstractIterator<BlockPos.MutableBlockPos>()
                {
                    private BlockPos.MutableBlockPos theBlockPos = null;
                    protected BlockPos.MutableBlockPos computeNext()
                    {
                        if (this.theBlockPos == null)
                        {
                            this.theBlockPos = new BlockPos.MutableBlockPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                            return this.theBlockPos;
                        }
                        else if (this.theBlockPos.equals(blockpos1))
                        {
                            return this.endOfData();
                        }
                        else
                        {
                            int i = this.theBlockPos.getX();
                            int j = this.theBlockPos.getY();
                            int k = this.theBlockPos.getZ();

                            if (i < blockpos1.getX())
                            {
                                ++i;
                            }
                            else if (j < blockpos1.getY())
                            {
                                i = blockpos.getX();
                                ++j;
                            }
                            else if (k < blockpos1.getZ())
                            {
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.theBlockPos.x = i;
                            this.theBlockPos.y = j;
                            this.theBlockPos.z = k;
                            return this.theBlockPos;
                        }
                    }
                };
            }
        };
    }

    public Vec3 toVec3(){
        return new Vec3(this.getX(), this.getY(), this.getZ());
    }

    public static final class MutableBlockPos extends BlockPos
    {
        private int x;
        private int y;
        private int z;

        public MutableBlockPos()
        {
            this(0, 0, 0);
        }

        public MutableBlockPos(int x_, int y_, int z_)
        {
            super(0, 0, 0);
            this.x = x_;
            this.y = y_;
            this.z = z_;
        }

        public int getX()
        {
            return this.x;
        }

        public int getY()
        {
            return this.y;
        }

        public int getZ()
        {
            return this.z;
        }

        public BlockPos.MutableBlockPos set(int xIn, int yIn, int zIn)
        {
            this.x = xIn;
            this.y = yIn;
            this.z = zIn;
            return this;
        }
    }
    public boolean equalsBlockPos(BlockPos blockPos) {
        if (blockPos == null) {
            return false;
        } else {
            return this.getX() == blockPos.getX() && this.getY() == blockPos.getY() && this.getZ() == blockPos.getZ();
        }
    }
}
