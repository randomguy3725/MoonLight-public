package net.optifine.util;

public class IntArray
{
    private int[] array = null;
    private int position = 0;
    private int limit = 0;

    public IntArray(int size)
    {
        this.array = new int[size];
    }

    public void put(int x)
    {
        this.array[this.position] = x;
        ++this.position;

        if (this.limit < this.position)
        {
            this.limit = this.position;
        }
    }

    public void put(int pos, int x)
    {
        this.array[pos] = x;

        if (this.limit < pos)
        {
            this.limit = pos;
        }
    }

    public void position(int pos)
    {
        this.position = pos;
    }

    public void put(int[] ints)
    {
        int i = ints.length;

        for (int j = 0; j < i; ++j)
        {
            this.array[this.position] = ints[j];
            ++this.position;
        }

        if (this.limit < this.position)
        {
            this.limit = this.position;
        }
    }

    public int get(int pos)
    {
        return this.array[pos];
    }

    public int[] getArray()
    {
        return this.array;
    }

    public void clear()
    {
        this.position = 0;
        this.limit = 0;
    }

    public int getLimit()
    {
        return this.limit;
    }

    public int getPosition()
    {
        return this.position;
    }
}
