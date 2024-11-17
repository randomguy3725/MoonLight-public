package net.optifine.reflect;

import java.lang.reflect.Field;

public class FieldLocatorFixed implements IFieldLocator
{
    private final Field field;

    public FieldLocatorFixed(Field field)
    {
        this.field = field;
    }

    public Field getField()
    {
        return this.field;
    }
}
