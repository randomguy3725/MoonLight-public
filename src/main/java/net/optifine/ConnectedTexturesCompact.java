package net.optifine;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.optifine.render.RenderEnv;

public class ConnectedTexturesCompact
{
    private static final int COMPACT_NONE = 0;
    private static final int COMPACT_ALL = 1;
    private static final int COMPACT_V = 2;
    private static final int COMPACT_H = 3;
    private static final int COMPACT_HV = 4;

    public static BakedQuad[] getConnectedTextureCtmCompact(int ctmIndex, ConnectedProperties cp, int side, BakedQuad quad, RenderEnv renderEnv)
    {
        if (cp.ctmTileIndexes != null && ctmIndex >= 0 && ctmIndex < cp.ctmTileIndexes.length)
        {
            int i = cp.ctmTileIndexes[ctmIndex];

            if (i >= 0 && i <= cp.tileIcons.length)
            {
                return getQuadsCompact(i, cp.tileIcons, quad, renderEnv);
            }
        }

        switch (ctmIndex)
        {
            case 1:
                return getQuadsCompactH(0, 3, cp.tileIcons, side, quad, renderEnv);

            case 2:
                return getQuadsCompact(3, cp.tileIcons, quad, renderEnv);

            case 3:
                return getQuadsCompactH(3, 0, cp.tileIcons, side, quad, renderEnv);

            case 4:
                return getQuadsCompact4(0, 3, 2, 4, cp.tileIcons, side, quad, renderEnv);

            case 5:
                return getQuadsCompact4(3, 0, 4, 2, cp.tileIcons, side, quad, renderEnv);

            case 6:
                return getQuadsCompact4(2, 4, 2, 4, cp.tileIcons, side, quad, renderEnv);

            case 7:
                return getQuadsCompact4(3, 3, 4, 4, cp.tileIcons, side, quad, renderEnv);

            case 8:
                return getQuadsCompact4(4, 1, 4, 4, cp.tileIcons, side, quad, renderEnv);

            case 9:
                return getQuadsCompact4(4, 4, 4, 1, cp.tileIcons, side, quad, renderEnv);

            case 10:
                return getQuadsCompact4(1, 4, 1, 4, cp.tileIcons, side, quad, renderEnv);

            case 11:
                return getQuadsCompact4(1, 1, 4, 4, cp.tileIcons, side, quad, renderEnv);

            case 12:
                return getQuadsCompactV(0, 2, cp.tileIcons, side, quad, renderEnv);

            case 13:
                return getQuadsCompact4(0, 3, 2, 1, cp.tileIcons, side, quad, renderEnv);

            case 14:
                return getQuadsCompactV(3, 1, cp.tileIcons, side, quad, renderEnv);

            case 15:
                return getQuadsCompact4(3, 0, 1, 2, cp.tileIcons, side, quad, renderEnv);

            case 16:
                return getQuadsCompact4(2, 4, 0, 3, cp.tileIcons, side, quad, renderEnv);

            case 17:
                return getQuadsCompact4(4, 2, 3, 0, cp.tileIcons, side, quad, renderEnv);

            case 18:
                return getQuadsCompact4(4, 4, 3, 3, cp.tileIcons, side, quad, renderEnv);

            case 19:
                return getQuadsCompact4(4, 2, 4, 2, cp.tileIcons, side, quad, renderEnv);

            case 20:
                return getQuadsCompact4(1, 4, 4, 4, cp.tileIcons, side, quad, renderEnv);

            case 21:
                return getQuadsCompact4(4, 4, 1, 4, cp.tileIcons, side, quad, renderEnv);

            case 22:
                return getQuadsCompact4(4, 4, 1, 1, cp.tileIcons, side, quad, renderEnv);

            case 23:
                return getQuadsCompact4(4, 1, 4, 1, cp.tileIcons, side, quad, renderEnv);

            case 24:
                return getQuadsCompact(2, cp.tileIcons, quad, renderEnv);

            case 25:
                return getQuadsCompactH(2, 1, cp.tileIcons, side, quad, renderEnv);

            case 26:
                return getQuadsCompact(1, cp.tileIcons, quad, renderEnv);

            case 27:
                return getQuadsCompactH(1, 2, cp.tileIcons, side, quad, renderEnv);

            case 28:
                return getQuadsCompact4(2, 4, 2, 1, cp.tileIcons, side, quad, renderEnv);

            case 29:
                return getQuadsCompact4(3, 3, 1, 4, cp.tileIcons, side, quad, renderEnv);

            case 30:
                return getQuadsCompact4(2, 1, 2, 4, cp.tileIcons, side, quad, renderEnv);

            case 31:
                return getQuadsCompact4(3, 3, 4, 1, cp.tileIcons, side, quad, renderEnv);

            case 32:
                return getQuadsCompact4(1, 1, 1, 4, cp.tileIcons, side, quad, renderEnv);

            case 33:
                return getQuadsCompact4(1, 1, 4, 1, cp.tileIcons, side, quad, renderEnv);

            case 34:
                return getQuadsCompact4(4, 1, 1, 4, cp.tileIcons, side, quad, renderEnv);

            case 35:
                return getQuadsCompact4(1, 4, 4, 1, cp.tileIcons, side, quad, renderEnv);

            case 36:
                return getQuadsCompactV(2, 0, cp.tileIcons, side, quad, renderEnv);

            case 37:
                return getQuadsCompact4(2, 1, 0, 3, cp.tileIcons, side, quad, renderEnv);

            case 38:
                return getQuadsCompactV(1, 3, cp.tileIcons, side, quad, renderEnv);

            case 39:
                return getQuadsCompact4(1, 2, 3, 0, cp.tileIcons, side, quad, renderEnv);

            case 40:
                return getQuadsCompact4(4, 1, 3, 3, cp.tileIcons, side, quad, renderEnv);

            case 41:
                return getQuadsCompact4(1, 2, 4, 2, cp.tileIcons, side, quad, renderEnv);

            case 42:
                return getQuadsCompact4(1, 4, 3, 3, cp.tileIcons, side, quad, renderEnv);

            case 43:
                return getQuadsCompact4(4, 2, 1, 2, cp.tileIcons, side, quad, renderEnv);

            case 44:
                return getQuadsCompact4(1, 4, 1, 1, cp.tileIcons, side, quad, renderEnv);

            case 45:
                return getQuadsCompact4(4, 1, 1, 1, cp.tileIcons, side, quad, renderEnv);

            case 46:
                return getQuadsCompact(4, cp.tileIcons, quad, renderEnv);

            default:
                return getQuadsCompact(0, cp.tileIcons, quad, renderEnv);
        }
    }

    private static BakedQuad[] getQuadsCompactH(int indexLeft, int indexRight, TextureAtlasSprite[] sprites, int side, BakedQuad quad, RenderEnv renderEnv)
    {
        return getQuadsCompact(ConnectedTexturesCompact.Dir.LEFT, indexLeft, ConnectedTexturesCompact.Dir.RIGHT, indexRight, sprites, side, quad, renderEnv);
    }

    private static BakedQuad[] getQuadsCompactV(int indexUp, int indexDown, TextureAtlasSprite[] sprites, int side, BakedQuad quad, RenderEnv renderEnv)
    {
        return getQuadsCompact(ConnectedTexturesCompact.Dir.UP, indexUp, ConnectedTexturesCompact.Dir.DOWN, indexDown, sprites, side, quad, renderEnv);
    }

    private static BakedQuad[] getQuadsCompact4(int upLeft, int upRight, int downLeft, int downRight, TextureAtlasSprite[] sprites, int side, BakedQuad quad, RenderEnv renderEnv)
    {
        return upLeft == upRight ? (downLeft == downRight ? getQuadsCompact(ConnectedTexturesCompact.Dir.UP, upLeft, ConnectedTexturesCompact.Dir.DOWN, downLeft, sprites, side, quad, renderEnv) : getQuadsCompact(ConnectedTexturesCompact.Dir.UP, upLeft, ConnectedTexturesCompact.Dir.DOWN_LEFT, downLeft, ConnectedTexturesCompact.Dir.DOWN_RIGHT, downRight, sprites, side, quad, renderEnv)) : (downLeft == downRight ? getQuadsCompact(ConnectedTexturesCompact.Dir.UP_LEFT, upLeft, ConnectedTexturesCompact.Dir.UP_RIGHT, upRight, ConnectedTexturesCompact.Dir.DOWN, downLeft, sprites, side, quad, renderEnv) : (upLeft == downLeft ? (upRight == downRight ? getQuadsCompact(ConnectedTexturesCompact.Dir.LEFT, upLeft, ConnectedTexturesCompact.Dir.RIGHT, upRight, sprites, side, quad, renderEnv) : getQuadsCompact(ConnectedTexturesCompact.Dir.LEFT, upLeft, ConnectedTexturesCompact.Dir.UP_RIGHT, upRight, ConnectedTexturesCompact.Dir.DOWN_RIGHT, downRight, sprites, side, quad, renderEnv)) : (upRight == downRight ? getQuadsCompact(ConnectedTexturesCompact.Dir.UP_LEFT, upLeft, ConnectedTexturesCompact.Dir.DOWN_LEFT, downLeft, ConnectedTexturesCompact.Dir.RIGHT, upRight, sprites, side, quad, renderEnv) : getQuadsCompact(ConnectedTexturesCompact.Dir.UP_LEFT, upLeft, ConnectedTexturesCompact.Dir.UP_RIGHT, upRight, ConnectedTexturesCompact.Dir.DOWN_LEFT, downLeft, ConnectedTexturesCompact.Dir.DOWN_RIGHT, downRight, sprites, side, quad, renderEnv))));
    }

    private static BakedQuad[] getQuadsCompact(int index, TextureAtlasSprite[] sprites, BakedQuad quad, RenderEnv renderEnv)
    {
        TextureAtlasSprite textureatlassprite = sprites[index];
        return ConnectedTextures.getQuads(textureatlassprite, quad, renderEnv);
    }

    private static BakedQuad[] getQuadsCompact(ConnectedTexturesCompact.Dir dir1, int index1, ConnectedTexturesCompact.Dir dir2, int index2, TextureAtlasSprite[] sprites, int side, BakedQuad quad, RenderEnv renderEnv)
    {
        BakedQuad bakedquad = getQuadCompact(sprites[index1], dir1, side, quad, renderEnv);
        BakedQuad bakedquad1 = getQuadCompact(sprites[index2], dir2, side, quad, renderEnv);
        return renderEnv.getArrayQuadsCtm(bakedquad, bakedquad1);
    }

    private static BakedQuad[] getQuadsCompact(ConnectedTexturesCompact.Dir dir1, int index1, ConnectedTexturesCompact.Dir dir2, int index2, ConnectedTexturesCompact.Dir dir3, int index3, TextureAtlasSprite[] sprites, int side, BakedQuad quad, RenderEnv renderEnv)
    {
        BakedQuad bakedquad = getQuadCompact(sprites[index1], dir1, side, quad, renderEnv);
        BakedQuad bakedquad1 = getQuadCompact(sprites[index2], dir2, side, quad, renderEnv);
        BakedQuad bakedquad2 = getQuadCompact(sprites[index3], dir3, side, quad, renderEnv);
        return renderEnv.getArrayQuadsCtm(bakedquad, bakedquad1, bakedquad2);
    }

    private static BakedQuad[] getQuadsCompact(ConnectedTexturesCompact.Dir dir1, int index1, ConnectedTexturesCompact.Dir dir2, int index2, ConnectedTexturesCompact.Dir dir3, int index3, ConnectedTexturesCompact.Dir dir4, int index4, TextureAtlasSprite[] sprites, int side, BakedQuad quad, RenderEnv renderEnv)
    {
        BakedQuad bakedquad = getQuadCompact(sprites[index1], dir1, side, quad, renderEnv);
        BakedQuad bakedquad1 = getQuadCompact(sprites[index2], dir2, side, quad, renderEnv);
        BakedQuad bakedquad2 = getQuadCompact(sprites[index3], dir3, side, quad, renderEnv);
        BakedQuad bakedquad3 = getQuadCompact(sprites[index4], dir4, side, quad, renderEnv);
        return renderEnv.getArrayQuadsCtm(bakedquad, bakedquad1, bakedquad2, bakedquad3);
    }

    private static BakedQuad getQuadCompact(TextureAtlasSprite sprite, ConnectedTexturesCompact.Dir dir, int side, BakedQuad quad, RenderEnv renderEnv)
    {
        switch (dir)
        {
            case UP:
                return getQuadCompact(sprite, dir, 0, 0, 16, 8, side, quad, renderEnv);

            case UP_RIGHT:
                return getQuadCompact(sprite, dir, 8, 0, 16, 8, side, quad, renderEnv);

            case RIGHT:
                return getQuadCompact(sprite, dir, 8, 0, 16, 16, side, quad, renderEnv);

            case DOWN_RIGHT:
                return getQuadCompact(sprite, dir, 8, 8, 16, 16, side, quad, renderEnv);

            case DOWN:
                return getQuadCompact(sprite, dir, 0, 8, 16, 16, side, quad, renderEnv);

            case DOWN_LEFT:
                return getQuadCompact(sprite, dir, 0, 8, 8, 16, side, quad, renderEnv);

            case LEFT:
                return getQuadCompact(sprite, dir, 0, 0, 8, 16, side, quad, renderEnv);

            case UP_LEFT:
                return getQuadCompact(sprite, dir, 0, 0, 8, 8, side, quad, renderEnv);

            default:
                return quad;
        }
    }

    private static BakedQuad getQuadCompact(TextureAtlasSprite sprite, ConnectedTexturesCompact.Dir dir, int x1, int y1, int x2, int y2, int side, BakedQuad quadIn, RenderEnv renderEnv)
    {
        Map[][] amap = ConnectedTextures.getSpriteQuadCompactMaps();

        if (amap == null)
        {
            return quadIn;
        }
        else
        {
            int i = sprite.getIndexInMap();

            if (i >= 0 && i < amap.length)
            {
                Map[] amap1 = amap[i];

                if (amap1 == null)
                {
                    amap1 = new Map[ConnectedTexturesCompact.Dir.VALUES.length];
                    amap[i] = amap1;
                }

                Map<BakedQuad, BakedQuad> map = amap1[dir.ordinal()];

                if (map == null)
                {
                    map = new IdentityHashMap(1);
                    amap1[dir.ordinal()] = map;
                }

                BakedQuad bakedquad = map.get(quadIn);

                if (bakedquad == null)
                {
                    bakedquad = makeSpriteQuadCompact(quadIn, sprite, side, x1, y1, x2, y2);
                    map.put(quadIn, bakedquad);
                }

                return bakedquad;
            }
            else
            {
                return quadIn;
            }
        }
    }

    private static BakedQuad makeSpriteQuadCompact(BakedQuad quad, TextureAtlasSprite sprite, int side, int x1, int y1, int x2, int y2)
    {
        int[] aint = quad.getVertexData().clone();
        TextureAtlasSprite textureatlassprite = quad.getSprite();

        for (int i = 0; i < 4; ++i)
        {
            fixVertexCompact(aint, i, textureatlassprite, sprite, side, x1, y1, x2, y2);
        }

        BakedQuad bakedquad = new BakedQuad(aint, quad.getTintIndex(), quad.getFace(), sprite);
        return bakedquad;
    }

    private static void fixVertexCompact(int[] data, int vertex, TextureAtlasSprite spriteFrom, TextureAtlasSprite spriteTo, int side, int x1, int y1, int x2, int y2)
    {
        int i = data.length / 4;
        int j = i * vertex;
        float f = Float.intBitsToFloat(data[j + 4]);
        float f1 = Float.intBitsToFloat(data[j + 4 + 1]);
        double d0 = spriteFrom.getSpriteU16(f);
        double d1 = spriteFrom.getSpriteV16(f1);
        float f2 = Float.intBitsToFloat(data[j]);
        float f3 = Float.intBitsToFloat(data[j + 1]);
        float f4 = Float.intBitsToFloat(data[j + 2]);
        float f5;
        float f6;

        switch (side)
        {
            case 0:
                f5 = f2;
                f6 = 1.0F - f4;
                break;

            case 1:
                f5 = f2;
                f6 = f4;
                break;

            case 2:
                f5 = 1.0F - f2;
                f6 = 1.0F - f3;
                break;

            case 3:
                f5 = f2;
                f6 = 1.0F - f3;
                break;

            case 4:
                f5 = f4;
                f6 = 1.0F - f3;
                break;

            case 5:
                f5 = 1.0F - f4;
                f6 = 1.0F - f3;
                break;

            default:
                return;
        }

        float f7 = 15.968F;
        float f8 = 15.968F;

        if (d0 < (double)x1)
        {
            f5 = (float)((double)f5 + ((double)x1 - d0) / (double)f7);
            d0 = x1;
        }

        if (d0 > (double)x2)
        {
            f5 = (float)((double)f5 - (d0 - (double)x2) / (double)f7);
            d0 = x2;
        }

        if (d1 < (double)y1)
        {
            f6 = (float)((double)f6 + ((double)y1 - d1) / (double)f8);
            d1 = y1;
        }

        if (d1 > (double)y2)
        {
            f6 = (float)((double)f6 - (d1 - (double)y2) / (double)f8);
            d1 = y2;
        }

        switch (side)
        {
            case 0:
                f2 = f5;
                f4 = 1.0F - f6;
                break;

            case 1:
                f2 = f5;
                f4 = f6;
                break;

            case 2:
                f2 = 1.0F - f5;
                f3 = 1.0F - f6;
                break;

            case 3:
                f2 = f5;
                f3 = 1.0F - f6;
                break;

            case 4:
                f4 = f5;
                f3 = 1.0F - f6;
                break;

            case 5:
                f4 = 1.0F - f5;
                f3 = 1.0F - f6;
                break;

            default:
                return;
        }

        data[j + 4] = Float.floatToRawIntBits(spriteTo.getInterpolatedU(d0));
        data[j + 4 + 1] = Float.floatToRawIntBits(spriteTo.getInterpolatedV(d1));
        data[j] = Float.floatToRawIntBits(f2);
        data[j + 1] = Float.floatToRawIntBits(f3);
        data[j + 2] = Float.floatToRawIntBits(f4);
    }

    private enum Dir
    {
        UP,
        UP_RIGHT,
        RIGHT,
        DOWN_RIGHT,
        DOWN,
        DOWN_LEFT,
        LEFT,
        UP_LEFT;

        public static final ConnectedTexturesCompact.Dir[] VALUES = values();
    }
}
