package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockCommandBlock extends BlockContainer
{
    public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");

    public BlockCommandBlock()
    {
        super(Material.iron, MapColor.adobeColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TRIGGERED, Boolean.valueOf(false)));
    }

    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityCommandBlock();
    }

    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!worldIn.isRemote)
        {
            boolean flag = worldIn.isBlockPowered(pos);
            boolean flag1 = state.getValue(TRIGGERED).booleanValue();

            if (flag && !flag1)
            {
                worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(true)), 4);
                worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            }
            else if (!flag && flag1)
            {
                worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(false)), 4);
            }
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityCommandBlock)
        {
            ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().trigger(worldIn);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
    }

    public int tickRate(World worldIn)
    {
        return 1;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityCommandBlock && ((TileEntityCommandBlock) tileentity).getCommandBlockLogic().tryOpenEditCommandBlock(playerIn);
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().getSuccessCount() : 0;
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityCommandBlock)
        {
            CommandBlockLogic commandblocklogic = ((TileEntityCommandBlock)tileentity).getCommandBlockLogic();

            if (stack.hasDisplayName())
            {
                commandblocklogic.setName(stack.getDisplayName());
            }

            if (!worldIn.isRemote)
            {
                commandblocklogic.setTrackOutput(worldIn.getGameRules().getBoolean("sendCommandFeedback"));
            }
        }
    }

    public int quantityDropped(Random random)
    {
        return 0;
    }

    public int getRenderType()
    {
        return 3;
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TRIGGERED, Boolean.valueOf((meta & 1) > 0));
    }

    public int getMetaFromState(IBlockState state)
    {
        int i = 0;

        if (state.getValue(TRIGGERED).booleanValue())
        {
            i |= 1;
        }

        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, TRIGGERED);
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(TRIGGERED, Boolean.valueOf(false));
    }
}
