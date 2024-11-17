package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.glu.GLU;

public class GLAllocation
{

    public static synchronized int generateDisplayLists(int range)
    {
        int i = GL11.glGenLists(range);

        if (i == 0)
        {
            int j = GL11.glGetError();
            String s = "No error code reported";

            if (j != 0)
            {
                s = GLU.gluErrorString(j);
            }

            throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + range + ", GL error (" + j + "): " + s);
        }
        else
        {
            return i;
        }
    }

    public static synchronized void deleteDisplayLists(int list, int range)
    {
        GL11.glDeleteLists(list, range);
    }

    public static synchronized void deleteDisplayLists(int list)
    {
        GL11.glDeleteLists(list, 1);
    }

    public static synchronized ByteBuffer createDirectByteBuffer(int capacity)
    {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public static IntBuffer createDirectIntBuffer(int capacity)
    {
        return createDirectByteBuffer(capacity << 2).asIntBuffer();
    }

    public static FloatBuffer createDirectFloatBuffer(int capacity)
    {
        return createDirectByteBuffer(capacity << 2).asFloatBuffer();
    }
}
