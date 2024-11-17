package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform4i extends ShaderUniformBase
{
    private int[][] programValues;
    private static final int VALUE_UNKNOWN = Integer.MIN_VALUE;

    public ShaderUniform4i(String name)
    {
        super(name);
        this.resetValue();
    }

    public void setValue(int v0, int v1, int v2, int v3)
    {
        int i = this.getProgram();
        int[] aint = this.programValues[i];

        if (aint[0] != v0 || aint[1] != v1 || aint[2] != v2 || aint[3] != v3)
        {
            aint[0] = v0;
            aint[1] = v1;
            aint[2] = v2;
            aint[3] = v3;
            int j = this.getLocation();

            if (j >= 0)
            {
                ARBShaderObjects.glUniform4iARB(j, v0, v1, v2, v3);
                this.checkGLError();
            }
        }
    }

    public int[] getValue()
    {
        int i = this.getProgram();
        int[] aint = this.programValues[i];
        return aint;
    }

    protected void onProgramSet(int program)
    {
        if (program >= this.programValues.length)
        {
            int[][] aint = this.programValues;
            int[][] aint1 = new int[program + 10][];
            System.arraycopy(aint, 0, aint1, 0, aint.length);
            this.programValues = aint1;
        }

        if (this.programValues[program] == null)
        {
            this.programValues[program] = new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
        }
    }

    protected void resetValue()
    {
        this.programValues = new int[][] {{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE}};
    }
}
