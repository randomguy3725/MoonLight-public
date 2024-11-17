package net.optifine;

public class TextureAnimationFrame
{
    public int index;
    public int duration;
    public int counter;

    public TextureAnimationFrame(int index, int duration)
    {
        this.index = index;
        this.duration = duration;
        this.counter = 0;
    }
}
