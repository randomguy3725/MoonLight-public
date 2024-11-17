package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform1f extends ShaderUniformBase
{
    private float[] programValues;
    private static final float VALUE_UNKNOWN = -3.4028235E38F;

    public ShaderUniform1f(String name)
    {
        super(name);
        this.resetValue();
    }

    public void setValue(float valueNew)
    {
        int i = this.getProgram();
        float f = this.programValues[i];

        if (valueNew != f)
        {
            this.programValues[i] = valueNew;
            int j = this.getLocation();

            if (j >= 0)
            {
                ARBShaderObjects.glUniform1fARB(j, valueNew);
                this.checkGLError();
            }
        }
    }

    public float getValue()
    {
        int i = this.getProgram();
        float f = this.programValues[i];
        return f;
    }

    protected void onProgramSet(int program)
    {
        if (program >= this.programValues.length)
        {
            float[] afloat = this.programValues;
            float[] afloat1 = new float[program + 10];
            System.arraycopy(afloat, 0, afloat1, 0, afloat.length);

            for (int i = afloat.length; i < afloat1.length; ++i)
            {
                afloat1[i] = -3.4028235E38F;
            }

            this.programValues = afloat1;
        }
    }

    protected void resetValue()
    {
        this.programValues = new float[] { -3.4028235E38F};
    }
}
