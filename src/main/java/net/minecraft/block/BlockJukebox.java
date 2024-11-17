package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer
{
    public static final PropertyBool HAS_RECORD = PropertyBool.create("has_record");

    protected BlockJukebox()
    {
        super(Material.wood, MapColor.dirtColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(HAS_RECORD, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (state.getValue(HAS_RECORD).booleanValue())
        {
            this.dropRecord(worldIn, pos, state);
            state = state.withProperty(HAS_RECORD, Boolean.valueOf(false));
            worldIn.setBlockState(pos, state, 2);
            return true;
        }
        else
        {
            return false;
        }
    }

    public void insertRecord(World worldIn, BlockPos pos, IBlockState state, ItemStack recordStack)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof BlockJukebox.TileEntityJukebox)
            {
                ((BlockJukebox.TileEntityJukebox)tileentity).setRecord(new ItemStack(recordStack.getItem(), 1, recordStack.getMetadata()));
                worldIn.setBlockState(pos, state.withProperty(HAS_RECORD, Boolean.valueOf(true)), 2);
            }
        }
    }

    private void dropRecord(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityJukebox blockjukebox$tileentityjukebox)
            {
                ItemStack itemstack = blockjukebox$tileentityjukebox.getRecord();

                if (itemstack != null)
                {
                    worldIn.playAuxSFX(1005, pos, 0);
                    worldIn.playRecord(pos, null);
                    blockjukebox$tileentityjukebox.setRecord(null);
                    float f = 0.7F;
                    double d0 = (double)(worldIn.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(worldIn.rand.nextFloat() * f) + (double)(1.0F - f) * 0.2D + 0.6D;
                    double d2 = (double)(worldIn.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    ItemStack itemstack1 = itemstack.copy();
                    EntityItem entityitem = new EntityItem(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, itemstack1);
                    entityitem.setDefaultPickupDelay();
                    worldIn.spawnEntityInWorld(entityitem);
                }
            }
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        this.dropRecord(worldIn, pos, state);
        super.breakBlock(worldIn, pos, state);
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        if (!worldIn.isRemote)
        {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
        }
    }

    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new BlockJukebox.TileEntityJukebox();
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof BlockJukebox.TileEntityJukebox)
        {
            ItemStack itemstack = ((BlockJukebox.TileEntityJukebox)tileentity).getRecord();

            if (itemstack != null)
            {
                return Item.getIdFromItem(itemstack.getItem()) + 1 - Item.getIdFromItem(Items.record_13);
            }
        }

        return 0;
    }

    public int getRenderType()
    {
        return 3;
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(HAS_RECORD, Boolean.valueOf(meta > 0));
    }

    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(HAS_RECORD).booleanValue() ? 1 : 0;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, HAS_RECORD);
    }

    public static class TileEntityJukebox extends TileEntity
    {
        private ItemStack record;

        public void readFromNBT(NBTTagCompound compound)
        {
            super.readFromNBT(compound);

            if (compound.hasKey("RecordItem", 10))
            {
                this.setRecord(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("RecordItem")));
            }
            else if (compound.getInteger("Record") > 0)
            {
                this.setRecord(new ItemStack(Item.getItemById(compound.getInteger("Record")), 1, 0));
            }
        }

        public void writeToNBT(NBTTagCompound compound)
        {
            super.writeToNBT(compound);

            if (this.getRecord() != null)
            {
                compound.setTag("RecordItem", this.getRecord().writeToNBT(new NBTTagCompound()));
            }
        }

        public ItemStack getRecord()
        {
            return this.record;
        }

        public void setRecord(ItemStack recordStack)
        {
            this.record = recordStack;
            this.markDirty();
        }
    }
}
