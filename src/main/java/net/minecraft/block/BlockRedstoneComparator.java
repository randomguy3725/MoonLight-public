package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider
{
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyEnum<BlockRedstoneComparator.Mode> MODE = PropertyEnum.create("mode", BlockRedstoneComparator.Mode.class);

    public BlockRedstoneComparator(boolean powered)
    {
        super(powered);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, Boolean.valueOf(false)).withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE));
        this.isBlockContainer = true;
    }

    public String getLocalizedName()
    {
        return StatCollector.translateToLocal("item.comparator.name");
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.comparator;
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return Items.comparator;
    }

    protected int getDelay(IBlockState state)
    {
        return 2;
    }

    protected IBlockState getPoweredState(IBlockState unpoweredState)
    {
        Boolean obool = unpoweredState.getValue(POWERED);
        BlockRedstoneComparator.Mode blockredstonecomparator$mode = unpoweredState.getValue(MODE);
        EnumFacing enumfacing = unpoweredState.getValue(FACING);
        return Blocks.powered_comparator.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, obool).withProperty(MODE, blockredstonecomparator$mode);
    }

    protected IBlockState getUnpoweredState(IBlockState poweredState)
    {
        Boolean obool = poweredState.getValue(POWERED);
        BlockRedstoneComparator.Mode blockredstonecomparator$mode = poweredState.getValue(MODE);
        EnumFacing enumfacing = poweredState.getValue(FACING);
        return Blocks.unpowered_comparator.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, obool).withProperty(MODE, blockredstonecomparator$mode);
    }

    protected boolean isPowered(IBlockState state)
    {
        return this.isRepeaterPowered || state.getValue(POWERED).booleanValue();
    }

    protected int getActiveSignal(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityComparator ? ((TileEntityComparator)tileentity).getOutputSignal() : 0;
    }

    private int calculateOutput(World worldIn, BlockPos pos, IBlockState state)
    {
        return state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT ? Math.max(this.calculateInputStrength(worldIn, pos, state) - this.getPowerOnSides(worldIn, pos, state), 0) : this.calculateInputStrength(worldIn, pos, state);
    }

    protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state)
    {
        int i = this.calculateInputStrength(worldIn, pos, state);

        if (i >= 15)
        {
            return true;
        }
        else if (i == 0)
        {
            return false;
        }
        else
        {
            int j = this.getPowerOnSides(worldIn, pos, state);
            return j == 0 || i >= j;
        }
    }

    protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state)
    {
        int i = super.calculateInputStrength(worldIn, pos, state);
        EnumFacing enumfacing = state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing);
        Block block = worldIn.getBlockState(blockpos).getBlock();

        if (block.hasComparatorInputOverride())
        {
            i = block.getComparatorInputOverride(worldIn, blockpos);
        }
        else if (i < 15 && block.isNormalCube())
        {
            blockpos = blockpos.offset(enumfacing);
            block = worldIn.getBlockState(blockpos).getBlock();

            if (block.hasComparatorInputOverride())
            {
                i = block.getComparatorInputOverride(worldIn, blockpos);
            }
            else if (block.getMaterial() == Material.air)
            {
                EntityItemFrame entityitemframe = this.findItemFrame(worldIn, enumfacing, blockpos);

                if (entityitemframe != null)
                {
                    i = entityitemframe.func_174866_q();
                }
            }
        }

        return i;
    }

    private EntityItemFrame findItemFrame(World worldIn, final EnumFacing facing, BlockPos pos)
    {
        List<EntityItemFrame> list = worldIn.getEntitiesWithinAABB(EntityItemFrame.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), new Predicate<Entity>()
        {
            public boolean apply(Entity p_apply_1_)
            {
                return p_apply_1_ != null && p_apply_1_.getHorizontalFacing() == facing;
            }
        });
        return list.size() == 1 ? list.get(0) : null;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!playerIn.capabilities.allowEdit)
        {
            return false;
        }
        else
        {
            state = state.cycleProperty(MODE);
            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "random.click", 0.3F, state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT ? 0.55F : 0.5F);
            worldIn.setBlockState(pos, state, 2);
            this.onStateChange(worldIn, pos, state);
            return true;
        }
    }

    protected void updateState(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isBlockTickPending(pos, this))
        {
            int i = this.calculateOutput(worldIn, pos, state);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            int j = tileentity instanceof TileEntityComparator ? ((TileEntityComparator)tileentity).getOutputSignal() : 0;

            if (i != j || this.isPowered(state) != this.shouldBePowered(worldIn, pos, state))
            {
                if (this.isFacingTowardsRepeater(worldIn, pos, state))
                {
                    worldIn.updateBlockTick(pos, this, 2, -1);
                }
                else
                {
                    worldIn.updateBlockTick(pos, this, 2, 0);
                }
            }
        }
    }

    private void onStateChange(World worldIn, BlockPos pos, IBlockState state)
    {
        int i = this.calculateOutput(worldIn, pos, state);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        int j = 0;

        if (tileentity instanceof TileEntityComparator tileentitycomparator)
        {
            j = tileentitycomparator.getOutputSignal();
            tileentitycomparator.setOutputSignal(i);
        }

        if (j != i || state.getValue(MODE) == BlockRedstoneComparator.Mode.COMPARE)
        {
            boolean flag1 = this.shouldBePowered(worldIn, pos, state);
            boolean flag = this.isPowered(state);

            if (flag && !flag1)
            {
                worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 2);
            }
            else if (!flag && flag1)
            {
                worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 2);
            }

            this.notifyNeighbors(worldIn, pos, state);
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (this.isRepeaterPowered)
        {
            worldIn.setBlockState(pos, this.getUnpoweredState(state).withProperty(POWERED, Boolean.valueOf(true)), 4);
        }

        this.onStateChange(worldIn, pos, state);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        worldIn.setTileEntity(pos, this.createNewTileEntity(worldIn, 0));
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
        this.notifyNeighbors(worldIn, pos, state);
    }

    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam)
    {
        super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(eventID, eventParam);
    }

    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityComparator();
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0)).withProperty(MODE, (meta & 4) > 0 ? BlockRedstoneComparator.Mode.SUBTRACT : BlockRedstoneComparator.Mode.COMPARE);
    }

    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(POWERED).booleanValue())
        {
            i |= 8;
        }

        if (state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT)
        {
            i |= 4;
        }

        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, FACING, MODE, POWERED);
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, Boolean.valueOf(false)).withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE);
    }

    public enum Mode implements IStringSerializable
    {
        COMPARE("compare"),
        SUBTRACT("subtract");

        private final String name;

        Mode(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}
