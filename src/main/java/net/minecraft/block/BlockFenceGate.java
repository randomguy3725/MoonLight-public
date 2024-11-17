package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional
{
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyBool IN_WALL = PropertyBool.create("in_wall");

    public BlockFenceGate(BlockPlanks.EnumType p_i46394_1_)
    {
        super(Material.wood, p_i46394_1_.getMapColor());
        this.setDefaultState(this.blockState.getBaseState().withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)).withProperty(IN_WALL, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        EnumFacing.Axis enumfacing$axis = state.getValue(FACING).getAxis();

        if (enumfacing$axis == EnumFacing.Axis.Z && (worldIn.getBlockState(pos.west()).getBlock() == Blocks.cobblestone_wall || worldIn.getBlockState(pos.east()).getBlock() == Blocks.cobblestone_wall) || enumfacing$axis == EnumFacing.Axis.X && (worldIn.getBlockState(pos.north()).getBlock() == Blocks.cobblestone_wall || worldIn.getBlockState(pos.south()).getBlock() == Blocks.cobblestone_wall))
        {
            state = state.withProperty(IN_WALL, Boolean.valueOf(true));
        }

        return state;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).getBlock().getMaterial().isSolid() && super.canPlaceBlockAt(worldIn, pos);
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        if (state.getValue(OPEN).booleanValue())
        {
            return null;
        }
        else
        {
            EnumFacing.Axis enumfacing$axis = state.getValue(FACING).getAxis();
            return enumfacing$axis == EnumFacing.Axis.Z ? new AxisAlignedBB(pos.getX(), pos.getY(), (float)pos.getZ() + 0.375F, pos.getX() + 1, (float)pos.getY() + 1.5F, (float)pos.getZ() + 0.625F) : new AxisAlignedBB((float)pos.getX() + 0.375F, pos.getY(), pos.getZ(), (float)pos.getX() + 0.625F, (float)pos.getY() + 1.5F, pos.getZ() + 1);
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        EnumFacing.Axis enumfacing$axis = worldIn.getBlockState(pos).getValue(FACING).getAxis();

        if (enumfacing$axis == EnumFacing.Axis.Z)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
        }
        else
        {
            this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
        }
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean isFullCube()
    {
        return false;
    }

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getValue(OPEN).booleanValue();
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)).withProperty(IN_WALL, Boolean.valueOf(false));
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (state.getValue(OPEN).booleanValue())
        {
            state = state.withProperty(OPEN, Boolean.valueOf(false));
            worldIn.setBlockState(pos, state, 2);
        }
        else
        {
            EnumFacing enumfacing = EnumFacing.fromAngle(playerIn.rotationYaw);

            if (state.getValue(FACING) == enumfacing.getOpposite())
            {
                state = state.withProperty(FACING, enumfacing);
            }

            state = state.withProperty(OPEN, Boolean.valueOf(true));
            worldIn.setBlockState(pos, state, 2);
        }

        worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN).booleanValue() ? 1003 : 1006, pos, 0);
        return true;
    }

    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!worldIn.isRemote)
        {
            boolean flag = worldIn.isBlockPowered(pos);

            if (flag || neighborBlock.canProvidePower())
            {
                if (flag && !state.getValue(OPEN).booleanValue() && !state.getValue(POWERED).booleanValue())
                {
                    worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(true)).withProperty(POWERED, Boolean.valueOf(true)), 2);
                    worldIn.playAuxSFXAtEntity(null, 1003, pos, 0);
                }
                else if (!flag && state.getValue(OPEN).booleanValue() && state.getValue(POWERED).booleanValue())
                {
                    worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)), 2);
                    worldIn.playAuxSFXAtEntity(null, 1006, pos, 0);
                }
                else if (flag != state.getValue(POWERED).booleanValue())
                {
                    worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(flag)), 2);
                }
            }
        }
    }

    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(OPEN, Boolean.valueOf((meta & 4) != 0)).withProperty(POWERED, Boolean.valueOf((meta & 8) != 0));
    }

    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(POWERED).booleanValue())
        {
            i |= 8;
        }

        if (state.getValue(OPEN).booleanValue())
        {
            i |= 4;
        }

        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, FACING, OPEN, POWERED, IN_WALL);
    }
}
