package net.minecraft.item.crafting;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesDyes
{
    public void addRecipes(CraftingManager p_77607_1_)
    {
        for (int i = 0; i < 16; ++i)
        {
            p_77607_1_.addShapelessRecipe(new ItemStack(Blocks.wool, 1, i), new ItemStack(Items.dye, 1, 15 - i), new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 0));
            p_77607_1_.addRecipe(new ItemStack(Blocks.stained_hardened_clay, 8, 15 - i), "###", "#X#", "###", '#', new ItemStack(Blocks.hardened_clay), 'X', new ItemStack(Items.dye, 1, i));
            p_77607_1_.addRecipe(new ItemStack(Blocks.stained_glass, 8, 15 - i), "###", "#X#", "###", '#', new ItemStack(Blocks.glass), 'X', new ItemStack(Items.dye, 1, i));
            p_77607_1_.addRecipe(new ItemStack(Blocks.stained_glass_pane, 16, i), "###", "###", '#', new ItemStack(Blocks.stained_glass, 1, i));
        }

        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 1, EnumDyeColor.YELLOW.getDyeDamage()), new ItemStack(Blocks.yellow_flower, 1, BlockFlower.EnumFlowerType.DANDELION.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, BlockFlower.EnumFlowerType.POPPY.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 3, EnumDyeColor.WHITE.getDyeDamage()), Items.bone);
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.PINK.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.ORANGE.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.YELLOW.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.LIME.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.GREEN.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.GRAY.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.SILVER.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.GRAY.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 3, EnumDyeColor.SILVER.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.LIGHT_BLUE.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.CYAN.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.GREEN.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.PURPLE.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.MAGENTA.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.PURPLE.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.PINK.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 3, EnumDyeColor.MAGENTA.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.PINK.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 4, EnumDyeColor.MAGENTA.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 1, EnumDyeColor.LIGHT_BLUE.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, BlockFlower.EnumFlowerType.BLUE_ORCHID.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 1, EnumDyeColor.MAGENTA.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, BlockFlower.EnumFlowerType.ALLIUM.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 1, EnumDyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, BlockFlower.EnumFlowerType.HOUSTONIA.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, BlockFlower.EnumFlowerType.RED_TULIP.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 1, EnumDyeColor.ORANGE.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, BlockFlower.EnumFlowerType.ORANGE_TULIP.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 1, EnumDyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, BlockFlower.EnumFlowerType.WHITE_TULIP.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 1, EnumDyeColor.PINK.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, BlockFlower.EnumFlowerType.PINK_TULIP.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 1, EnumDyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, BlockFlower.EnumFlowerType.OXEYE_DAISY.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.YELLOW.getDyeDamage()), new ItemStack(Blocks.double_plant, 1, BlockDoublePlant.EnumPlantType.SUNFLOWER.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.MAGENTA.getDyeDamage()), new ItemStack(Blocks.double_plant, 1, BlockDoublePlant.EnumPlantType.SYRINGA.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Blocks.double_plant, 1, BlockDoublePlant.EnumPlantType.ROSE.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.dye, 2, EnumDyeColor.PINK.getDyeDamage()), new ItemStack(Blocks.double_plant, 1, BlockDoublePlant.EnumPlantType.PAEONIA.getMeta()));

        for (int j = 0; j < 16; ++j)
        {
            p_77607_1_.addRecipe(new ItemStack(Blocks.carpet, 3, j), "##", '#', new ItemStack(Blocks.wool, 1, j));
        }
    }
}
