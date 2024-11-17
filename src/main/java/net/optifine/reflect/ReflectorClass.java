package net.optifine.reflect;

import net.optifine.Log;

public class ReflectorClass implements IResolvable
{
    private String targetClassName = null;
    private boolean checked = false;
    private Class targetClass = null;

    public ReflectorClass(String targetClassName)
    {
        this.targetClassName = targetClassName;
        ReflectorResolver.register(this);
    }

    public ReflectorClass(Class targetClass)
    {
        this.targetClass = targetClass;
        this.targetClassName = targetClass.getName();
        this.checked = true;
    }

    public Class getTargetClass()
    {
        if (this.checked)
        {
            return this.targetClass;
        }
        else
        {
            this.checked = true;

            try
            {
                this.targetClass = Class.forName(this.targetClassName);
            }
            catch (ClassNotFoundException var2)
            {
                Log.log("(Reflector) Class not present: " + this.targetClassName);
            }
            catch (Throwable throwable)
            {
                throwable.printStackTrace();
            }

            return this.targetClass;
        }
    }

    public boolean exists()
    {
        return this.getTargetClass() != null;
    }

    public String getTargetClassName()
    {
        return this.targetClassName;
    }

    public boolean isInstance(Object obj)
    {
        return this.getTargetClass() != null && this.getTargetClass().isInstance(obj);
    }

    public ReflectorField makeField(String name)
    {
        return new ReflectorField(this, name);
    }

    public ReflectorMethod makeMethod(String name)
    {
        return new ReflectorMethod(this, name);
    }

    public ReflectorMethod makeMethod(String name, Class[] paramTypes)
    {
        return new ReflectorMethod(this, name, paramTypes);
    }

    public void resolve()
    {
        Class oclass = this.getTargetClass();
    }
}
