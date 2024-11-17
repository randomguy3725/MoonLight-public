package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate
{
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    private final BlockPressurePlate.Sensitivity sensitivity;

    protected BlockPressurePlate(Material materialIn, BlockPressurePlate.Sensitivity sensitivityIn)
    {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, Boolean.valueOf(false)));
        this.sensitivity = sensitivityIn;
    }

    protected int getRedstoneStrength(IBlockState state)
    {
        return state.getValue(POWERED).booleanValue() ? 15 : 0;
    }

    protected IBlockState setRedstoneStrength(IBlockState state, int strength)
    {
        return state.withProperty(POWERED, Boolean.valueOf(strength > 0));
    }

    protected int computeRedstoneStrength(World worldIn, BlockPos pos)
    {
        AxisAlignedBB axisalignedbb = this.getSensitiveAABB(pos);
        List <? extends Entity > list;

        switch (this.sensitivity)
        {
            case EVERYTHING:
                list = worldIn.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb);
                break;

            case MOBS:
                list = worldIn.<Entity>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
                break;

            default:
                return 0;
        }

        if (!list.isEmpty())
        {
            for (Entity entity : list)
            {
                if (!entity.doesEntityNotTriggerPressurePlate())
                {
                    return 15;
                }
            }
        }

        return 0;
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(POWERED, Boolean.valueOf(meta == 1));
    }

    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(POWERED).booleanValue() ? 1 : 0;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, POWERED);
    }

    public enum Sensitivity
    {
        EVERYTHING,
        MOBS
    }
}
