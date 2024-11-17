package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;

public class PropertyBool extends PropertyHelper<Boolean>
{
    private final ImmutableSet<Boolean> allowedValues = ImmutableSet.of(Boolean.valueOf(true), Boolean.valueOf(false));

    protected PropertyBool(String name)
    {
        super(name, Boolean.class);
    }

    public Collection<Boolean> getAllowedValues()
    {
        return this.allowedValues;
    }

    public static PropertyBool create(String name)
    {
        return new PropertyBool(name);
    }

    public String getName(Boolean value)
    {
        return value.toString();
    }
}
