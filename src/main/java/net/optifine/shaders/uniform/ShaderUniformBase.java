package net.optifine.shaders.uniform;

import java.util.Arrays;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.ARBShaderObjects;

public abstract class ShaderUniformBase
{
    private final String name;
    private int program = 0;
    private int[] locations = new int[] { -1};
    private static final int LOCATION_UNDEFINED = -1;
    private static final int LOCATION_UNKNOWN = Integer.MIN_VALUE;

    public ShaderUniformBase(String name)
    {
        this.name = name;
    }

    public void setProgram(int program)
    {
        if (this.program != program)
        {
            this.program = program;
            this.expandLocations();
            this.onProgramSet(program);
        }
    }

    private void expandLocations()
    {
        if (this.program >= this.locations.length)
        {
            int[] aint = new int[this.program * 2];
            Arrays.fill(aint, Integer.MIN_VALUE);
            System.arraycopy(this.locations, 0, aint, 0, this.locations.length);
            this.locations = aint;
        }
    }

    protected abstract void onProgramSet(int var1);

    public String getName()
    {
        return this.name;
    }

    public int getProgram()
    {
        return this.program;
    }

    public int getLocation()
    {
        if (this.program <= 0)
        {
            return -1;
        }
        else
        {
            int i = this.locations[this.program];

            if (i == Integer.MIN_VALUE)
            {
                i = ARBShaderObjects.glGetUniformLocationARB(this.program, this.name);
                this.locations[this.program] = i;
            }

            return i;
        }
    }

    public boolean isDefined()
    {
        return this.getLocation() >= 0;
    }

    public void disable()
    {
        this.locations[this.program] = -1;
    }

    public void reset()
    {
        this.program = 0;
        this.locations = new int[] { -1};
        this.resetValue();
    }

    protected abstract void resetValue();

    protected void checkGLError()
    {
        if (Shaders.checkGLError(this.name) != 0)
        {
            this.disable();
        }
    }

    public String toString()
    {
        return this.name;
    }
}
