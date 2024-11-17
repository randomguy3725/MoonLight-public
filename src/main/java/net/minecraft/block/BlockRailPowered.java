package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase
{
    public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, new Predicate<BlockRailBase.EnumRailDirection>()
    {
        public boolean apply(BlockRailBase.EnumRailDirection p_apply_1_)
        {
            return p_apply_1_ != BlockRailBase.EnumRailDirection.NORTH_EAST && p_apply_1_ != BlockRailBase.EnumRailDirection.NORTH_WEST && p_apply_1_ != BlockRailBase.EnumRailDirection.SOUTH_EAST && p_apply_1_ != BlockRailBase.EnumRailDirection.SOUTH_WEST;
        }
    });
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    protected BlockRailPowered()
    {
        super(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH).withProperty(POWERED, Boolean.valueOf(false)));
    }

    @SuppressWarnings("incomplete-switch")
    protected boolean func_176566_a(World worldIn, BlockPos pos, IBlockState state, boolean p_176566_4_, int p_176566_5_)
    {
        if (p_176566_5_ >= 8)
        {
            return false;
        }
        else
        {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            boolean flag = true;
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = state.getValue(SHAPE);

            switch (blockrailbase$enumraildirection)
            {
                case NORTH_SOUTH:
                    if (p_176566_4_)
                    {
                        ++k;
                    }
                    else
                    {
                        --k;
                    }

                    break;

                case EAST_WEST:
                    if (p_176566_4_)
                    {
                        --i;
                    }
                    else
                    {
                        ++i;
                    }

                    break;

                case ASCENDING_EAST:
                    if (p_176566_4_)
                    {
                        --i;
                    }
                    else
                    {
                        ++i;
                        ++j;
                        flag = false;
                    }

                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
                    break;

                case ASCENDING_WEST:
                    if (p_176566_4_)
                    {
                        --i;
                        ++j;
                        flag = false;
                    }
                    else
                    {
                        ++i;
                    }

                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
                    break;

                case ASCENDING_NORTH:
                    if (p_176566_4_)
                    {
                        ++k;
                    }
                    else
                    {
                        --k;
                        ++j;
                        flag = false;
                    }

                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                    break;

                case ASCENDING_SOUTH:
                    if (p_176566_4_)
                    {
                        ++k;
                        ++j;
                        flag = false;
                    }
                    else
                    {
                        --k;
                    }

                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            return this.func_176567_a(worldIn, new BlockPos(i, j, k), p_176566_4_, p_176566_5_, blockrailbase$enumraildirection) || flag && this.func_176567_a(worldIn, new BlockPos(i, j - 1, k), p_176566_4_, p_176566_5_, blockrailbase$enumraildirection);
        }
    }

    protected boolean func_176567_a(World worldIn, BlockPos p_176567_2_, boolean p_176567_3_, int distance, BlockRailBase.EnumRailDirection p_176567_5_)
    {
        IBlockState iblockstate = worldIn.getBlockState(p_176567_2_);

        if (iblockstate.getBlock() != this)
        {
            return false;
        }
        else
        {
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate.getValue(SHAPE);
            return (p_176567_5_ != EnumRailDirection.EAST_WEST || blockrailbase$enumraildirection != EnumRailDirection.NORTH_SOUTH && blockrailbase$enumraildirection != EnumRailDirection.ASCENDING_NORTH && blockrailbase$enumraildirection != EnumRailDirection.ASCENDING_SOUTH) && ((p_176567_5_ != EnumRailDirection.NORTH_SOUTH || blockrailbase$enumraildirection != EnumRailDirection.EAST_WEST && blockrailbase$enumraildirection != EnumRailDirection.ASCENDING_EAST && blockrailbase$enumraildirection != EnumRailDirection.ASCENDING_WEST) && (iblockstate.getValue(POWERED).booleanValue() && (worldIn.isBlockPowered(p_176567_2_) || this.func_176566_a(worldIn, p_176567_2_, iblockstate, p_176567_3_, distance + 1))));
        }
    }

    protected void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        boolean flag = state.getValue(POWERED).booleanValue();
        boolean flag1 = worldIn.isBlockPowered(pos) || this.func_176566_a(worldIn, pos, state, true, 0) || this.func_176566_a(worldIn, pos, state, false, 0);

        if (flag1 != flag)
        {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(flag1)), 3);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);

            if (state.getValue(SHAPE).isAscending())
            {
                worldIn.notifyNeighborsOfStateChange(pos.up(), this);
            }
        }
    }

    public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty()
    {
        return SHAPE;
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
