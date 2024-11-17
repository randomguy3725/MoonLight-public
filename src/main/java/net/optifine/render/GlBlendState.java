package net.optifine.render;

public class GlBlendState
{
    private boolean enabled;
    private int srcFactor;
    private int dstFactor;
    private int srcFactorAlpha;
    private int dstFactorAlpha;

    public GlBlendState()
    {
        this(false, 1, 0);
    }

    public GlBlendState(boolean enabled)
    {
        this(enabled, 1, 0);
    }

    public GlBlendState(boolean enabled, int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha)
    {
        this.enabled = enabled;
        this.srcFactor = srcFactor;
        this.dstFactor = dstFactor;
        this.srcFactorAlpha = srcFactorAlpha;
        this.dstFactorAlpha = dstFactorAlpha;
    }

    public GlBlendState(boolean enabled, int srcFactor, int dstFactor)
    {
        this(enabled, srcFactor, dstFactor, srcFactor, dstFactor);
    }

    public void setState(boolean enabled, int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha)
    {
        this.enabled = enabled;
        this.srcFactor = srcFactor;
        this.dstFactor = dstFactor;
        this.srcFactorAlpha = srcFactorAlpha;
        this.dstFactorAlpha = dstFactorAlpha;
    }

    public void setState(GlBlendState state)
    {
        this.enabled = state.enabled;
        this.srcFactor = state.srcFactor;
        this.dstFactor = state.dstFactor;
        this.srcFactorAlpha = state.srcFactorAlpha;
        this.dstFactorAlpha = state.dstFactorAlpha;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setEnabled()
    {
        this.enabled = true;
    }

    public void setDisabled()
    {
        this.enabled = false;
    }

    public void setFactors(int srcFactor, int dstFactor)
    {
        this.srcFactor = srcFactor;
        this.dstFactor = dstFactor;
        this.srcFactorAlpha = srcFactor;
        this.dstFactorAlpha = dstFactor;
    }

    public void setFactors(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha)
    {
        this.srcFactor = srcFactor;
        this.dstFactor = dstFactor;
        this.srcFactorAlpha = srcFactorAlpha;
        this.dstFactorAlpha = dstFactorAlpha;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public int getSrcFactor()
    {
        return this.srcFactor;
    }

    public int getDstFactor()
    {
        return this.dstFactor;
    }

    public int getSrcFactorAlpha()
    {
        return this.srcFactorAlpha;
    }

    public int getDstFactorAlpha()
    {
        return this.dstFactorAlpha;
    }

    public boolean isSeparate()
    {
        return this.srcFactor != this.srcFactorAlpha || this.dstFactor != this.dstFactorAlpha;
    }

    public String toString()
    {
        return "enabled: " + this.enabled + ", src: " + this.srcFactor + ", dst: " + this.dstFactor + ", srcAlpha: " + this.srcFactorAlpha + ", dstAlpha: " + this.dstFactorAlpha;
    }
}
