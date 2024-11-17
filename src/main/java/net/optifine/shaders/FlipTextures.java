package net.optifine.shaders;

import java.nio.IntBuffer;
import java.util.Arrays;

public class FlipTextures
{
    private final IntBuffer textures;
    private final int indexFlipped;
    private final boolean[] flips;
    private final boolean[] changed;

    public FlipTextures(IntBuffer textures, int indexFlipped)
    {
        this.textures = textures;
        this.indexFlipped = indexFlipped;
        this.flips = new boolean[textures.capacity()];
        this.changed = new boolean[textures.capacity()];
    }

    public int getA(int index)
    {
        return this.get(index, this.flips[index]);
    }

    public int getB(int index)
    {
        return this.get(index, !this.flips[index]);
    }

    private int get(int index, boolean flipped)
    {
        int i = flipped ? this.indexFlipped : 0;
        return this.textures.get(i + index);
    }

    public void flip(int index)
    {
        this.flips[index] = !this.flips[index];
        this.changed[index] = true;
    }

    public boolean isChanged(int index)
    {
        return this.changed[index];
    }

    public void reset()
    {
        Arrays.fill(this.flips, false);
        Arrays.fill(this.changed, false);
    }
}
