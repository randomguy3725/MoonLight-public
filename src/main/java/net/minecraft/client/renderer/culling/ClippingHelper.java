package net.minecraft.client.renderer.culling;

public class ClippingHelper
{
    public float[][] frustum = new float[6][4];
    public float[] projectionMatrix = new float[16];
    public float[] modelviewMatrix = new float[16];
    public float[] clippingMatrix = new float[16];
    public boolean disabled = false;

    private float dot(float[] p_dot_1_, float p_dot_2_, float p_dot_3_, float p_dot_4_)
    {
        return p_dot_1_[0] * p_dot_2_ + p_dot_1_[1] * p_dot_3_ + p_dot_1_[2] * p_dot_4_ + p_dot_1_[3];
    }

    public boolean isBoxInFrustum(double p_78553_1_, double p_78553_3_, double p_78553_5_, double p_78553_7_, double p_78553_9_, double p_78553_11_)
    {
        if (this.disabled)
        {
            return true;
        }
        else
        {
            float f = (float)p_78553_1_;
            float f1 = (float)p_78553_3_;
            float f2 = (float)p_78553_5_;
            float f3 = (float)p_78553_7_;
            float f4 = (float)p_78553_9_;
            float f5 = (float)p_78553_11_;

            for (int i = 0; i < 6; ++i)
            {
                float[] afloat = this.frustum[i];
                float f6 = afloat[0];
                float f7 = afloat[1];
                float f8 = afloat[2];
                float f9 = afloat[3];

                if (f6 * f + f7 * f1 + f8 * f2 + f9 <= 0.0F && f6 * f3 + f7 * f1 + f8 * f2 + f9 <= 0.0F && f6 * f + f7 * f4 + f8 * f2 + f9 <= 0.0F && f6 * f3 + f7 * f4 + f8 * f2 + f9 <= 0.0F && f6 * f + f7 * f1 + f8 * f5 + f9 <= 0.0F && f6 * f3 + f7 * f1 + f8 * f5 + f9 <= 0.0F && f6 * f + f7 * f4 + f8 * f5 + f9 <= 0.0F && f6 * f3 + f7 * f4 + f8 * f5 + f9 <= 0.0F)
                {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean isBoxInFrustumFully(double p_isBoxInFrustumFully_1_, double p_isBoxInFrustumFully_3_, double p_isBoxInFrustumFully_5_, double p_isBoxInFrustumFully_7_, double p_isBoxInFrustumFully_9_, double p_isBoxInFrustumFully_11_)
    {
        if (this.disabled)
        {
            return true;
        }
        else
        {
            float f = (float)p_isBoxInFrustumFully_1_;
            float f1 = (float)p_isBoxInFrustumFully_3_;
            float f2 = (float)p_isBoxInFrustumFully_5_;
            float f3 = (float)p_isBoxInFrustumFully_7_;
            float f4 = (float)p_isBoxInFrustumFully_9_;
            float f5 = (float)p_isBoxInFrustumFully_11_;

            for (int i = 0; i < 6; ++i)
            {
                float[] afloat = this.frustum[i];
                float f6 = afloat[0];
                float f7 = afloat[1];
                float f8 = afloat[2];
                float f9 = afloat[3];

                if (i < 4)
                {
                    if (f6 * f + f7 * f1 + f8 * f2 + f9 <= 0.0F || f6 * f3 + f7 * f1 + f8 * f2 + f9 <= 0.0F || f6 * f + f7 * f4 + f8 * f2 + f9 <= 0.0F || f6 * f3 + f7 * f4 + f8 * f2 + f9 <= 0.0F || f6 * f + f7 * f1 + f8 * f5 + f9 <= 0.0F || f6 * f3 + f7 * f1 + f8 * f5 + f9 <= 0.0F || f6 * f + f7 * f4 + f8 * f5 + f9 <= 0.0F || f6 * f3 + f7 * f4 + f8 * f5 + f9 <= 0.0F)
                    {
                        return false;
                    }
                }
                else if (f6 * f + f7 * f1 + f8 * f2 + f9 <= 0.0F && f6 * f3 + f7 * f1 + f8 * f2 + f9 <= 0.0F && f6 * f + f7 * f4 + f8 * f2 + f9 <= 0.0F && f6 * f3 + f7 * f4 + f8 * f2 + f9 <= 0.0F && f6 * f + f7 * f1 + f8 * f5 + f9 <= 0.0F && f6 * f3 + f7 * f1 + f8 * f5 + f9 <= 0.0F && f6 * f + f7 * f4 + f8 * f5 + f9 <= 0.0F && f6 * f3 + f7 * f4 + f8 * f5 + f9 <= 0.0F)
                {
                    return false;
                }
            }

            return true;
        }
    }
}
