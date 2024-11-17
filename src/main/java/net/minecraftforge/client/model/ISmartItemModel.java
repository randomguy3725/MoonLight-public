package net.minecraftforge.client.model;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;

public interface ISmartItemModel extends IBakedModel
{
    IBakedModel handleItemState(ItemStack var1);
}
