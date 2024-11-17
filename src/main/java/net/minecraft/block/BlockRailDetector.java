package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase
{
    public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, new Predicate<BlockRailBase.EnumRailDirection>()
    {
        public boolean apply(BlockRailBase.EnumRailDirection p_apply_1_)
        {
            return p_apply_1_ != BlockRailBase.EnumRailDirection.NORTH_EAST && p_apply_1_ != BlockRailBase.EnumRailDirection.NORTH_WEST && p_apply_1_ != BlockRailBase.EnumRailDirection.SOUTH_EAST && p_apply_1_ != BlockRailBase.EnumRailDirection.SOUTH_WEST;
        }
    });
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockRailDetector()
    {
        super(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, Boolean.valueOf(false)).withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
        this.setTickRandomly(true);
    }

    public int tickRate(World worldIn)
    {
        return 20;
    }

    public boolean canProvidePower()
    {
        return true;
    }

    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!worldIn.isRemote)
        {
            if (!state.getValue(POWERED).booleanValue())
            {
                this.updatePoweredState(worldIn, pos, state);
            }
        }
    }

    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote && state.getValue(POWERED).booleanValue())
        {
            this.updatePoweredState(worldIn, pos, state);
        }
    }

    public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return state.getValue(POWERED).booleanValue() ? 15 : 0;
    }

    public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return !state.getValue(POWERED).booleanValue() ? 0 : (side == EnumFacing.UP ? 15 : 0);
    }

    private void updatePoweredState(World worldIn, BlockPos pos, IBlockState state)
    {
        boolean flag = state.getValue(POWERED).booleanValue();
        boolean flag1 = false;
        List<EntityMinecart> list = this.findMinecarts(worldIn, pos, EntityMinecart.class);

        if (!list.isEmpty())
        {
            flag1 = true;
        }

        if (flag1 && !flag)
        {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
            worldIn.notifyNeighborsOfStateChange(pos, this);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
        }

        if (!flag1 && flag)
        {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 3);
            worldIn.notifyNeighborsOfStateChange(pos, this);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
        }

        if (flag1)
        {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
        }

        worldIn.updateComparatorOutputLevel(pos, this);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        this.updatePoweredState(worldIn, pos, state);
    }

    public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty()
    {
        return SHAPE;
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
        if (worldIn.getBlockState(pos).getValue(POWERED).booleanValue())
        {
            List<EntityMinecartCommandBlock> list = this.findMinecarts(worldIn, pos, EntityMinecartCommandBlock.class);

            if (!list.isEmpty())
            {
                return list.get(0).getCommandBlockLogic().getSuccessCount();
            }

            List<EntityMinecart> list1 = this.findMinecarts(worldIn, pos, EntityMinecart.class, EntitySelectors.selectInventories);

            if (!list1.isEmpty())
            {
                return Container.calcRedstoneFromInventory((IInventory)list1.get(0));
            }
        }

        return 0;
    }

    protected <T extends EntityMinecart> List<T> findMinecarts(World worldIn, BlockPos pos, Class<T> clazz, Predicate<Entity>... filter)
    {
        AxisAlignedBB axisalignedbb = this.getDectectionBox(pos);
        return filter.length != 1 ? worldIn.getEntitiesWithinAABB(clazz, axisalignedbb) : worldIn.getEntitiesWithinAABB(clazz, axisalignedbb, filter[0]);
    }

    private AxisAlignedBB getDectectionBox(BlockPos pos)
    {
        float f = 0.2F;
        return new AxisAlignedBB((float)pos.getX() + 0.2F, pos.getY(), (float)pos.getZ() + 0.2F, (float)(pos.getX() + 1) - 0.2F, (float)(pos.getY() + 1) - 0.2F, (float)(pos.getZ() + 1) - 0.2F);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
    }

    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(SHAPE).getMetadata();

        if (state.getValue(POWERED).booleanValue())
        {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, SHAPE, POWERED);
    }
}
