package net.optifine.texture;

public enum PixelFormat
{
    RED(6403),
    RG(33319),
    RGB(6407),
    BGR(32992),
    RGBA(6408),
    BGRA(32993),
    RED_INTEGER(36244),
    RG_INTEGER(33320),
    RGB_INTEGER(36248),
    BGR_INTEGER(36250),
    RGBA_INTEGER(36249),
    BGRA_INTEGER(36251);

    private final int id;

    PixelFormat(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }
}
