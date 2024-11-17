package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class BlockSand extends BlockFalling
{
    public static final PropertyEnum<BlockSand.EnumType> VARIANT = PropertyEnum.create("variant", BlockSand.EnumType.class);

    public BlockSand()
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockSand.EnumType.SAND));
    }

    public int damageDropped(IBlockState state)
    {
        return state.getValue(VARIANT).getMetadata();
    }

    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (BlockSand.EnumType blocksand$enumtype : BlockSand.EnumType.values())
        {
            list.add(new ItemStack(itemIn, 1, blocksand$enumtype.getMetadata()));
        }
    }

    public MapColor getMapColor(IBlockState state)
    {
        return state.getValue(VARIANT).getMapColor();
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, BlockSand.EnumType.byMetadata(meta));
    }

    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(VARIANT).getMetadata();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, VARIANT);
    }

    public enum EnumType implements IStringSerializable
    {
        SAND(0, "sand", "default", MapColor.sandColor),
        RED_SAND(1, "red_sand", "red", MapColor.adobeColor);

        private static final BlockSand.EnumType[] META_LOOKUP = new BlockSand.EnumType[values().length];
        private final int meta;
        private final String name;
        private final MapColor mapColor;
        private final String unlocalizedName;

        EnumType(int meta, String name, String unlocalizedName, MapColor mapColor)
        {
            this.meta = meta;
            this.name = name;
            this.mapColor = mapColor;
            this.unlocalizedName = unlocalizedName;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public MapColor getMapColor()
        {
            return this.mapColor;
        }

        public static BlockSand.EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        static {
            for (BlockSand.EnumType blocksand$enumtype : values())
            {
                META_LOOKUP[blocksand$enumtype.getMetadata()] = blocksand$enumtype;
            }
        }
    }
}
