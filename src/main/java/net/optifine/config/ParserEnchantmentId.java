package net.optifine.config;

import net.minecraft.enchantment.Enchantment;

public class ParserEnchantmentId implements IParserInt
{
    public int parse(String str, int defVal)
    {
        Enchantment enchantment = Enchantment.getEnchantmentByLocation(str);
        return enchantment == null ? defVal : enchantment.effectId;
    }
}
