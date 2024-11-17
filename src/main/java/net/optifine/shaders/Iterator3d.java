package net.optifine.shaders;

import java.util.Iterator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.optifine.BlockPosM;

public class Iterator3d implements Iterator<BlockPos>
{
    private final IteratorAxis iteratorAxis;
    private final BlockPosM blockPos = new BlockPosM(0, 0, 0);
    private int axis = 0;
    private final int kX;
    private final int kY;
    private final int kZ;
    private static final int AXIS_X = 0;
    private static final int AXIS_Y = 1;
    private static final int AXIS_Z = 2;

    public Iterator3d(BlockPos posStart, BlockPos posEnd, int width, int height)
    {
        boolean flag = posStart.getX() > posEnd.getX();
        boolean flag1 = posStart.getY() > posEnd.getY();
        boolean flag2 = posStart.getZ() > posEnd.getZ();
        posStart = this.reverseCoord(posStart, flag, flag1, flag2);
        posEnd = this.reverseCoord(posEnd, flag, flag1, flag2);
        this.kX = flag ? -1 : 1;
        this.kY = flag1 ? -1 : 1;
        this.kZ = flag2 ? -1 : 1;
        Vec3 vec3 = new Vec3(posEnd.getX() - posStart.getX(), posEnd.getY() - posStart.getY(), posEnd.getZ() - posStart.getZ());
        Vec3 vec31 = vec3.normalize();
        Vec3 vec32 = new Vec3(1.0D, 0.0D, 0.0D);
        double d0 = vec31.dotProduct(vec32);
        double d1 = Math.abs(d0);
        Vec3 vec33 = new Vec3(0.0D, 1.0D, 0.0D);
        double d2 = vec31.dotProduct(vec33);
        double d3 = Math.abs(d2);
        Vec3 vec34 = new Vec3(0.0D, 0.0D, 1.0D);
        double d4 = vec31.dotProduct(vec34);
        double d5 = Math.abs(d4);

        if (d5 >= d3 && d5 >= d1)
        {
            this.axis = 2;
            BlockPos blockpos3 = new BlockPos(posStart.getZ(), posStart.getY() - width, posStart.getX() - height);
            BlockPos blockpos5 = new BlockPos(posEnd.getZ(), posStart.getY() + width + 1, posStart.getX() + height + 1);
            int k = posEnd.getZ() - posStart.getZ();
            double d9 = (double)(posEnd.getY() - posStart.getY()) / ((double) k);
            double d11 = (double)(posEnd.getX() - posStart.getX()) / ((double) k);
            this.iteratorAxis = new IteratorAxis(blockpos3, blockpos5, d9, d11);
        }
        else if (d3 >= d1 && d3 >= d5)
        {
            this.axis = 1;
            BlockPos blockpos2 = new BlockPos(posStart.getY(), posStart.getX() - width, posStart.getZ() - height);
            BlockPos blockpos4 = new BlockPos(posEnd.getY(), posStart.getX() + width + 1, posStart.getZ() + height + 1);
            int j = posEnd.getY() - posStart.getY();
            double d8 = (double)(posEnd.getX() - posStart.getX()) / ((double) j);
            double d10 = (double)(posEnd.getZ() - posStart.getZ()) / ((double) j);
            this.iteratorAxis = new IteratorAxis(blockpos2, blockpos4, d8, d10);
        }
        else
        {
            this.axis = 0;
            BlockPos blockpos = new BlockPos(posStart.getX(), posStart.getY() - width, posStart.getZ() - height);
            BlockPos blockpos1 = new BlockPos(posEnd.getX(), posStart.getY() + width + 1, posStart.getZ() + height + 1);
            int i = posEnd.getX() - posStart.getX();
            double d6 = (double)(posEnd.getY() - posStart.getY()) / ((double) i);
            double d7 = (double)(posEnd.getZ() - posStart.getZ()) / ((double) i);
            this.iteratorAxis = new IteratorAxis(blockpos, blockpos1, d6, d7);
        }
    }

    private BlockPos reverseCoord(BlockPos pos, boolean revX, boolean revY, boolean revZ)
    {
        if (revX)
        {
            pos = new BlockPos(-pos.getX(), pos.getY(), pos.getZ());
        }

        if (revY)
        {
            pos = new BlockPos(pos.getX(), -pos.getY(), pos.getZ());
        }

        if (revZ)
        {
            pos = new BlockPos(pos.getX(), pos.getY(), -pos.getZ());
        }

        return pos;
    }

    public boolean hasNext()
    {
        return this.iteratorAxis.hasNext();
    }

    public BlockPos next()
    {
        BlockPos blockpos = this.iteratorAxis.next();

        switch (this.axis)
        {
            case 0:
                this.blockPos.setXyz(blockpos.getX() * this.kX, blockpos.getY() * this.kY, blockpos.getZ() * this.kZ);
                return this.blockPos;

            case 1:
                this.blockPos.setXyz(blockpos.getY() * this.kX, blockpos.getX() * this.kY, blockpos.getZ() * this.kZ);
                return this.blockPos;

            case 2:
                this.blockPos.setXyz(blockpos.getZ() * this.kX, blockpos.getY() * this.kY, blockpos.getX() * this.kZ);
                return this.blockPos;

            default:
                this.blockPos.setXyz(blockpos.getX() * this.kX, blockpos.getY() * this.kY, blockpos.getZ() * this.kZ);
                return this.blockPos;
        }
    }

    public void remove()
    {
        throw new RuntimeException("Not supported");
    }

    public static void main(String[] args)
    {
        BlockPos blockpos = new BlockPos(10, 20, 30);
        BlockPos blockpos1 = new BlockPos(30, 40, 20);
        Iterator3d iterator3d = new Iterator3d(blockpos, blockpos1, 1, 1);

        while (iterator3d.hasNext())
        {
            BlockPos blockpos2 = iterator3d.next();
            System.out.println("" + blockpos2);
        }
    }
}
