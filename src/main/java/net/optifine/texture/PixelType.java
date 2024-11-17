package net.optifine.texture;

public enum PixelType
{
    BYTE(5120),
    SHORT(5122),
    INT(5124),
    HALF_FLOAT(5131),
    FLOAT(5126),
    UNSIGNED_BYTE(5121),
    UNSIGNED_BYTE_3_3_2(32818),
    UNSIGNED_BYTE_2_3_3_REV(33634),
    UNSIGNED_SHORT(5123),
    UNSIGNED_SHORT_5_6_5(33635),
    UNSIGNED_SHORT_5_6_5_REV(33636),
    UNSIGNED_SHORT_4_4_4_4(32819),
    UNSIGNED_SHORT_4_4_4_4_REV(33637),
    UNSIGNED_SHORT_5_5_5_1(32820),
    UNSIGNED_SHORT_1_5_5_5_REV(33638),
    UNSIGNED_INT(5125),
    UNSIGNED_INT_8_8_8_8(32821),
    UNSIGNED_INT_8_8_8_8_REV(33639),
    UNSIGNED_INT_10_10_10_2(32822),
    UNSIGNED_INT_2_10_10_10_REV(33640);

    private final int id;

    PixelType(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }
}
