package net.optifine.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectorRaw
{
    public static Field getField(Class cls, Class fieldType)
    {
        try
        {
            Field[] afield = cls.getDeclaredFields();

            for (int i = 0; i < afield.length; ++i)
            {
                Field field = afield[i];

                if (field.getType() == fieldType)
                {
                    field.setAccessible(true);
                    return field;
                }
            }

            return null;
        }
        catch (Exception var5)
        {
            return null;
        }
    }

    public static Field[] getFields(Class cls, Class fieldType)
    {
        try
        {
            Field[] afield = cls.getDeclaredFields();
            return getFields(afield, fieldType);
        }
        catch (Exception var3)
        {
            return null;
        }
    }

    public static Field[] getFields(Field[] fields, Class fieldType)
    {
        try
        {
            List list = new ArrayList();

            for (int i = 0; i < fields.length; ++i)
            {
                Field field = fields[i];

                if (field.getType() == fieldType)
                {
                    field.setAccessible(true);
                    list.add(field);
                }
            }

            Field[] afield = (Field[]) list.toArray(new Field[list.size()]);
            return afield;
        }
        catch (Exception var5)
        {
            return null;
        }
    }

    public static Field[] getFieldsAfter(Class cls, Field field, Class fieldType)
    {
        try
        {
            Field[] afield = cls.getDeclaredFields();
            List<Field> list = Arrays.asList(afield);
            int i = list.indexOf(field);

            if (i < 0)
            {
                return new Field[0];
            }
            else
            {
                List<Field> list1 = list.subList(i + 1, list.size());
                Field[] afield1 = list1.toArray(new Field[list1.size()]);
                return getFields(afield1, fieldType);
            }
        }
        catch (Exception var8)
        {
            return null;
        }
    }

    public static Field[] getFields(Object obj, Field[] fields, Class fieldType, Object value)
    {
        try
        {
            List<Field> list = new ArrayList();

            for (int i = 0; i < fields.length; ++i)
            {
                Field field = fields[i];

                if (field.getType() == fieldType)
                {
                    boolean flag = Modifier.isStatic(field.getModifiers());

                    if ((obj != null || flag) && (obj == null || !flag))
                    {
                        field.setAccessible(true);
                        Object object = field.get(obj);

                        if (object == value)
                        {
                            list.add(field);
                        }
                        else if (object != null && object.equals(value))
                        {
                            list.add(field);
                        }
                    }
                }
            }

            Field[] afield = list.toArray(new Field[list.size()]);
            return afield;
        }
        catch (Exception var9)
        {
            return null;
        }
    }

    public static Field getField(Class cls, Class fieldType, int index)
    {
        Field[] afield = getFields(cls, fieldType);
        return index >= 0 && index < afield.length ? afield[index] : null;
    }

    public static Field getFieldAfter(Class cls, Field field, Class fieldType, int index)
    {
        Field[] afield = getFieldsAfter(cls, field, fieldType);
        return index >= 0 && index < afield.length ? afield[index] : null;
    }

    public static Object getFieldValue(Object obj, Class cls, Class fieldType)
    {
        ReflectorField reflectorfield = getReflectorField(cls, fieldType);
        return reflectorfield == null ? null : (!reflectorfield.exists() ? null : Reflector.getFieldValue(obj, reflectorfield));
    }

    public static Object getFieldValue(Object obj, Class cls, Class fieldType, int index)
    {
        ReflectorField reflectorfield = getReflectorField(cls, fieldType, index);
        return reflectorfield == null ? null : (!reflectorfield.exists() ? null : Reflector.getFieldValue(obj, reflectorfield));
    }

    public static boolean setFieldValue(Object obj, Class cls, Class fieldType, Object value)
    {
        ReflectorField reflectorfield = getReflectorField(cls, fieldType);
        return reflectorfield != null && (reflectorfield.exists() && Reflector.setFieldValue(obj, reflectorfield, value));
    }

    public static boolean setFieldValue(Object obj, Class cls, Class fieldType, int index, Object value)
    {
        ReflectorField reflectorfield = getReflectorField(cls, fieldType, index);
        return reflectorfield != null && (reflectorfield.exists() && Reflector.setFieldValue(obj, reflectorfield, value));
    }

    public static ReflectorField getReflectorField(Class cls, Class fieldType)
    {
        Field field = getField(cls, fieldType);

        if (field == null)
        {
            return null;
        }
        else
        {
            ReflectorClass reflectorclass = new ReflectorClass(cls);
            return new ReflectorField(reflectorclass, field.getName());
        }
    }

    public static ReflectorField getReflectorField(Class cls, Class fieldType, int index)
    {
        Field field = getField(cls, fieldType, index);

        if (field == null)
        {
            return null;
        }
        else
        {
            ReflectorClass reflectorclass = new ReflectorClass(cls);
            return new ReflectorField(reflectorclass, field.getName());
        }
    }
}
