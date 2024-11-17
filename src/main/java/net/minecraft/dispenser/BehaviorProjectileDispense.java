package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem
{
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
    {
        World world = source.getWorld();
        IPosition iposition = BlockDispenser.getDispensePosition(source);
        EnumFacing enumfacing = BlockDispenser.getFacing(source.getBlockMetadata());
        IProjectile iprojectile = this.getProjectileEntity(world, iposition);
        iprojectile.setThrowableHeading(enumfacing.getFrontOffsetX(), (float)enumfacing.getFrontOffsetY() + 0.1F, enumfacing.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
        world.spawnEntityInWorld((Entity)iprojectile);
        stack.splitStack(1);
        return stack;
    }

    protected void playDispenseSound(IBlockSource source)
    {
        source.getWorld().playAuxSFX(1002, source.getBlockPos(), 0);
    }

    protected abstract IProjectile getProjectileEntity(World worldIn, IPosition position);

    protected float func_82498_a()
    {
        return 6.0F;
    }

    protected float func_82500_b()
    {
        return 1.1F;
    }
}
