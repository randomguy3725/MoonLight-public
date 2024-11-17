package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneDiode
{
    public static final PropertyBool LOCKED = PropertyBool.create("locked");
    public static final PropertyInteger DELAY = PropertyInteger.create("delay", 1, 4);

    protected BlockRedstoneRepeater(boolean powered)
    {
        super(powered);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DELAY, Integer.valueOf(1)).withProperty(LOCKED, Boolean.valueOf(false)));
    }

    public String getLocalizedName()
    {
        return StatCollector.translateToLocal("item.diode.name");
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(LOCKED, Boolean.valueOf(this.isLocked(worldIn, pos, state)));
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!playerIn.capabilities.allowEdit)
        {
            return false;
        }
        else
        {
            worldIn.setBlockState(pos, state.cycleProperty(DELAY), 3);
            return true;
        }
    }

    protected int getDelay(IBlockState state)
    {
        return state.getValue(DELAY).intValue() * 2;
    }

    protected IBlockState getPoweredState(IBlockState unpoweredState)
    {
        Integer integer = unpoweredState.getValue(DELAY);
        Boolean obool = unpoweredState.getValue(LOCKED);
        EnumFacing enumfacing = unpoweredState.getValue(FACING);
        return Blocks.powered_repeater.getDefaultState().withProperty(FACING, enumfacing).withProperty(DELAY, integer).withProperty(LOCKED, obool);
    }

    protected IBlockState getUnpoweredState(IBlockState poweredState)
    {
        Integer integer = poweredState.getValue(DELAY);
        Boolean obool = poweredState.getValue(LOCKED);
        EnumFacing enumfacing = poweredState.getValue(FACING);
        return Blocks.unpowered_repeater.getDefaultState().withProperty(FACING, enumfacing).withProperty(DELAY, integer).withProperty(LOCKED, obool);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.repeater;
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return Items.repeater;
    }

    public boolean isLocked(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        return this.getPowerOnSides(worldIn, pos, state) > 0;
    }

    protected boolean canPowerSide(Block blockIn)
    {
        return isRedstoneRepeaterBlockID(blockIn);
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (this.isRepeaterPowered)
        {
            EnumFacing enumfacing = state.getValue(FACING);
            double d0 = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
            float f = -5.0F;

            if (rand.nextBoolean())
            {
                f = (float)(state.getValue(DELAY).intValue() * 2 - 1);
            }

            f = f / 16.0F;
            double d3 = f * (float)enumfacing.getFrontOffsetX();
            double d4 = f * (float)enumfacing.getFrontOffsetZ();
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        this.notifyNeighbors(worldIn, pos, state);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(LOCKED, Boolean.valueOf(false)).withProperty(DELAY, Integer.valueOf(1 + (meta >> 2)));
    }

    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();
        i = i | state.getValue(DELAY).intValue() - 1 << 2;
        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, FACING, DELAY, LOCKED);
    }
}
