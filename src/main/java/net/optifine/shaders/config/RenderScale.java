package net.optifine.shaders.config;

public class RenderScale
{
    private float scale = 1.0F;
    private float offsetX = 0.0F;
    private float offsetY = 0.0F;

    public RenderScale(float scale, float offsetX, float offsetY)
    {
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public float getScale()
    {
        return this.scale;
    }

    public float getOffsetX()
    {
        return this.offsetX;
    }

    public float getOffsetY()
    {
        return this.offsetY;
    }

    public String toString()
    {
        return this.scale + ", " + this.offsetX + ", " + this.offsetY;
    }
}
