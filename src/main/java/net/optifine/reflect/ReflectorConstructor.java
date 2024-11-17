package net.optifine.reflect;

import java.lang.reflect.Constructor;
import net.optifine.Log;
import net.optifine.util.ArrayUtils;

public class ReflectorConstructor implements IResolvable
{
    private ReflectorClass reflectorClass = null;
    private Class[] parameterTypes = null;
    private boolean checked = false;
    private Constructor targetConstructor = null;

    public ReflectorConstructor(ReflectorClass reflectorClass, Class[] parameterTypes)
    {
        this.reflectorClass = reflectorClass;
        this.parameterTypes = parameterTypes;
        ReflectorResolver.register(this);
    }

    public Constructor getTargetConstructor()
    {
        if (this.checked)
        {
            return this.targetConstructor;
        }
        else
        {
            this.checked = true;
            Class oclass = this.reflectorClass.getTargetClass();

            if (oclass == null)
            {
                return null;
            }
            else
            {
                try
                {
                    this.targetConstructor = findConstructor(oclass, this.parameterTypes);

                    if (this.targetConstructor == null)
                    {
                        Log.dbg("(Reflector) Constructor not present: " + oclass.getName() + ", params: " + ArrayUtils.arrayToString(this.parameterTypes));
                    }

                    if (this.targetConstructor != null)
                    {
                        this.targetConstructor.setAccessible(true);
                    }
                }
                catch (Throwable throwable)
                {
                    throwable.printStackTrace();
                }

                return this.targetConstructor;
            }
        }
    }

    private static Constructor findConstructor(Class cls, Class[] paramTypes)
    {
        Constructor[] aconstructor = cls.getDeclaredConstructors();

        for (int i = 0; i < aconstructor.length; ++i)
        {
            Constructor constructor = aconstructor[i];
            Class[] aclass = constructor.getParameterTypes();

            if (Reflector.matchesTypes(paramTypes, aclass))
            {
                return constructor;
            }
        }

        return null;
    }

    public boolean exists()
    {
        return this.checked ? this.targetConstructor != null : this.getTargetConstructor() != null;
    }

    public void deactivate()
    {
        this.checked = true;
        this.targetConstructor = null;
    }

    public Object newInstance(Object... params)
    {
        return Reflector.newInstance(this, params);
    }

    public void resolve()
    {
        Constructor constructor = this.getTargetConstructor();
    }
}
