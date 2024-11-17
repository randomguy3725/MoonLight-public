package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform3f extends ShaderUniformBase
{
    private float[][] programValues;
    private static final float VALUE_UNKNOWN = -3.4028235E38F;

    public ShaderUniform3f(String name)
    {
        super(name);
        this.resetValue();
    }

    public void setValue(float v0, float v1, float v2)
    {
        int i = this.getProgram();
        float[] afloat = this.programValues[i];

        if (afloat[0] != v0 || afloat[1] != v1 || afloat[2] != v2)
        {
            afloat[0] = v0;
            afloat[1] = v1;
            afloat[2] = v2;
            int j = this.getLocation();

            if (j >= 0)
            {
                ARBShaderObjects.glUniform3fARB(j, v0, v1, v2);
                this.checkGLError();
            }
        }
    }

    public float[] getValue()
    {
        int i = this.getProgram();
        float[] afloat = this.programValues[i];
        return afloat;
    }

    protected void onProgramSet(int program)
    {
        if (program >= this.programValues.length)
        {
            float[][] afloat = this.programValues;
            float[][] afloat1 = new float[program + 10][];
            System.arraycopy(afloat, 0, afloat1, 0, afloat.length);
            this.programValues = afloat1;
        }

        if (this.programValues[program] == null)
        {
            this.programValues[program] = new float[] { -3.4028235E38F, -3.4028235E38F, -3.4028235E38F};
        }
    }

    protected void resetValue()
    {
        this.programValues = new float[][] {{ -3.4028235E38F, -3.4028235E38F, -3.4028235E38F}};
    }
}
