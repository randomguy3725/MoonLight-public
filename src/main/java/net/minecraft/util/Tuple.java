package net.minecraft.util;

import lombok.Setter;

public class Tuple<A, B>
{
    private A a;
    private B b;

    public Tuple(A aIn, B bIn)
    {
        this.a = aIn;
        this.b = bIn;
    }

    public A getFirst()
    {
        return this.a;
    }

    public B getSecond()
    {
        return this.b;
    }

    public void setFirst(A a) {
        this.a = a;
    }

    public void setSecond(B b) {
        this.b = b;
    }
}
