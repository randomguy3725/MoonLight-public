package net.minecraft.entity.ai.attributes;

import java.util.Collection;
import java.util.UUID;

public interface IAttributeInstance
{
    IAttribute getAttribute();

    double getBaseValue();

    void setBaseValue(double baseValue);

    Collection<AttributeModifier> getModifiersByOperation(int operation);

    Collection<AttributeModifier> func_111122_c();

    boolean hasModifier(AttributeModifier modifier);

    AttributeModifier getModifier(UUID uuid);

    void applyModifier(AttributeModifier modifier);

    void removeModifier(AttributeModifier modifier);

    void removeAllModifiers();

    double getAttributeValue();
}
