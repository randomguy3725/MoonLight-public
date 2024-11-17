package net.minecraft.block;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLadder extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    protected BlockLadder()
    {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getSelectedBoundingBox(worldIn, pos);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        float needMinus = 0.125f;
        if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47) {
            needMinus = 0.1875F;
        }

        if (iblockstate.getBlock() == this)
            switch (iblockstate.getValue(FACING))
            {
                case NORTH:
                    this.setBlockBounds(0f, 0f, 1f - needMinus, 1f, 1f, 1f);
                    break;

                case SOUTH:
                    this.setBlockBounds(0f, 0f, 0f, 1f, 1f, needMinus);
                    break;

                case WEST:
                    this.setBlockBounds(1f - needMinus, 0f, 0f, 1f, 1f, 1f);
                    break;

                case EAST:
                default:
                    this.setBlockBounds(0f, 0f, 0f, needMinus, 1f, 1f);
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

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.west()).getBlock().isNormalCube() || (worldIn.getBlockState(pos.east()).getBlock().isNormalCube() || (worldIn.getBlockState(pos.north()).getBlock().isNormalCube() || worldIn.getBlockState(pos.south()).getBlock().isNormalCube()));
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (facing.getAxis().isHorizontal() && this.canBlockStay(worldIn, pos, facing))
        {
            return this.getDefaultState().withProperty(FACING, facing);
        }
        else
        {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                if (this.canBlockStay(worldIn, pos, enumfacing))
                {
                    return this.getDefaultState().withProperty(FACING, enumfacing);
                }
            }

            return this.getDefaultState();
        }
    }

    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        EnumFacing enumfacing = state.getValue(FACING);

        if (!this.canBlockStay(worldIn, pos, enumfacing))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }

        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
    }

    protected boolean canBlockStay(World worldIn, BlockPos pos, EnumFacing facing)
    {
        return worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock().isNormalCube();
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, FACING);
    }
}
