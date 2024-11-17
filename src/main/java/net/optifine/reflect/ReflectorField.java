package net.optifine.reflect;

import java.lang.reflect.Field;

public class ReflectorField implements IResolvable
{
    private IFieldLocator fieldLocator;
    private boolean checked;
    private Field targetField;

    public ReflectorField(ReflectorClass reflectorClass, String targetFieldName)
    {
        this(new FieldLocatorName(reflectorClass, targetFieldName));
    }

    public ReflectorField(ReflectorClass reflectorClass, Class targetFieldType)
    {
        this(reflectorClass, targetFieldType, 0);
    }

    public ReflectorField(ReflectorClass reflectorClass, Class targetFieldType, int targetFieldIndex)
    {
        this(new FieldLocatorType(reflectorClass, targetFieldType, targetFieldIndex));
    }

    public ReflectorField(Field field)
    {
        this(new FieldLocatorFixed(field));
    }

    public ReflectorField(IFieldLocator fieldLocator)
    {
        this.fieldLocator = null;
        this.checked = false;
        this.targetField = null;
        this.fieldLocator = fieldLocator;
        ReflectorResolver.register(this);
    }

    public Field getTargetField()
    {
        if (this.checked)
        {
            return this.targetField;
        }
        else
        {
            this.checked = true;
            this.targetField = this.fieldLocator.getField();

            if (this.targetField != null)
            {
                this.targetField.setAccessible(true);
            }

            return this.targetField;
        }
    }

    public Object getValue()
    {
        return Reflector.getFieldValue(null, this);
    }

    public void setValue(Object value)
    {
        Reflector.setFieldValue(null, this, value);
    }

    public void setValue(Object obj, Object value)
    {
        Reflector.setFieldValue(obj, this, value);
    }

    public boolean exists()
    {
        return this.getTargetField() != null;
    }

    public void resolve()
    {
        Field field = this.getTargetField();
    }
}
