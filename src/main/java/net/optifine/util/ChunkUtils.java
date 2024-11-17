package net.optifine.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorClass;
import net.optifine.reflect.ReflectorField;

public class ChunkUtils
{
    private static final ReflectorClass chunkClass = new ReflectorClass(Chunk.class);
    private static final ReflectorField fieldHasEntities = findFieldHasEntities();
    private static final ReflectorField fieldPrecipitationHeightMap = new ReflectorField(chunkClass, int[].class, 0);

    public static boolean hasEntities(Chunk chunk)
    {
        return Reflector.getFieldValueBoolean(chunk, fieldHasEntities, true);
    }

    public static int getPrecipitationHeight(Chunk chunk, BlockPos pos)
    {
        int[] aint = (int[]) Reflector.getFieldValue(chunk, fieldPrecipitationHeightMap);

        if (aint != null && aint.length == 256)
        {
            int i = pos.getX() & 15;
            int j = pos.getZ() & 15;
            int k = i | j << 4;
            int l = aint[k];

            if (l >= 0)
            {
                return l;
            }
            else
            {
                BlockPos blockpos = chunk.getPrecipitationHeight(pos);
                return blockpos.getY();
            }
        }
        else
        {
            return -1;
        }
    }

    private static ReflectorField findFieldHasEntities()
    {
        try
        {
            Chunk chunk = new Chunk(null, 0, 0);
            List list = new ArrayList();
            List list1 = new ArrayList();
            Field[] afield = Chunk.class.getDeclaredFields();

            for (int i = 0; i < afield.length; ++i)
            {
                Field field = afield[i];

                if (field.getType() == Boolean.TYPE)
                {
                    field.setAccessible(true);
                    list.add(field);
                    list1.add(field.get(chunk));
                }
            }

            chunk.setHasEntities(false);
            List list2 = new ArrayList();

            for (Object o : list)
            {
                Field field1 = (Field) o;
                list2.add(field1.get(chunk));
            }

            chunk.setHasEntities(true);
            List list3 = new ArrayList();

            for (Object o : list)
            {
                Field field2 = (Field) o;
                list3.add(field2.get(chunk));
            }

            List list4 = new ArrayList();

            for (int j = 0; j < list.size(); ++j)
            {
                Field field3 = (Field)list.get(j);
                Boolean obool = (Boolean)list2.get(j);
                Boolean obool1 = (Boolean)list3.get(j);

                if (!obool.booleanValue() && obool1.booleanValue())
                {
                    list4.add(field3);
                    Boolean obool2 = (Boolean)list1.get(j);
                    field3.set(chunk, obool2);
                }
            }

            if (list4.size() == 1)
            {
                Field field4 = (Field)list4.get(0);
                return new ReflectorField(field4);
            }
        }
        catch (Exception exception)
        {
            Config.warn(exception.getClass().getName() + " " + exception.getMessage());
        }

        Config.warn("Error finding Chunk.hasEntities");
        return new ReflectorField(new ReflectorClass(Chunk.class), "hasEntities");
    }
}
