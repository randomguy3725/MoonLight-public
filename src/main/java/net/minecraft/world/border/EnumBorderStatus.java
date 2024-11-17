package net.minecraft.world.border;

public enum EnumBorderStatus
{
    GROWING(4259712),
    SHRINKING(16724016),
    STATIONARY(2138367);

    private final int id;

    EnumBorderStatus(int id)
    {
        this.id = id;
    }

    public int getID()
    {
        return this.id;
    }
}
