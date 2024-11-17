package net.minecraft.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRail extends BlockRailBase
{
    public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class);

    protected BlockRail()
    {
        super(false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
    }

    protected void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (neighborBlock.canProvidePower() && (new BlockRailBase.Rail(worldIn, pos, state)).countAdjacentRails() == 3)
        {
            this.func_176564_a(worldIn, pos, state, false);
        }
    }

    public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty()
    {
        return SHAPE;
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta));
    }

    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(SHAPE).getMetadata();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, SHAPE);
    }
}
