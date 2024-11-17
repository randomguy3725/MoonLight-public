package net.optifine.override;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PlayerControllerOF extends PlayerControllerMP
{
    private boolean acting = false;
    private BlockPos lastClickBlockPos = null;
    private Entity lastClickEntity = null;

    public PlayerControllerOF(Minecraft mcIn, NetHandlerPlayClient netHandler)
    {
        super(mcIn, netHandler);
    }

    public boolean clickBlock(BlockPos loc, EnumFacing face)
    {
        this.acting = true;
        this.lastClickBlockPos = loc;
        boolean flag = super.clickBlock(loc, face);
        this.acting = false;
        return flag;
    }

    public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing)
    {
        this.acting = true;
        this.lastClickBlockPos = posBlock;
        boolean flag = super.onPlayerDamageBlock(posBlock, directionFacing);
        this.acting = false;
        return flag;
    }

    public boolean sendUseItem(EntityPlayer player, World worldIn, ItemStack stack)
    {
        this.acting = true;
        boolean flag = super.sendUseItem(player, worldIn, stack);
        this.acting = false;
        return flag;
    }

    public boolean onPlayerRightClick(EntityPlayerSP p_178890_1, WorldClient p_178890_2, ItemStack p_178890_3, BlockPos p_178890_4, EnumFacing p_178890_5, Vec3 p_178890_6)
    {
        this.acting = true;
        this.lastClickBlockPos = p_178890_4;
        boolean flag = super.onPlayerRightClick(p_178890_1, p_178890_2, p_178890_3, p_178890_4, p_178890_5, p_178890_6);
        this.acting = false;
        return flag;
    }

    public boolean interactWithEntitySendPacket(EntityPlayer player, Entity target)
    {
        this.lastClickEntity = target;
        return super.interactWithEntitySendPacket(player, target);
    }

    public boolean isPlayerRightClickingOnEntity(EntityPlayer player, Entity target, MovingObjectPosition ray)
    {
        this.lastClickEntity = target;
        return super.isPlayerRightClickingOnEntity(player, target, ray);
    }

    public boolean isActing()
    {
        return this.acting;
    }

    public BlockPos getLastClickBlockPos()
    {
        return this.lastClickBlockPos;
    }

    public Entity getLastClickEntity()
    {
        return this.lastClickEntity;
    }
}
