package net.minecraft.client.renderer.block.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexProducer;
import net.optifine.model.QuadBounds;
import net.optifine.reflect.Reflector;

public class BakedQuad implements IVertexProducer
{
    protected int[] vertexData;
    protected final int tintIndex;
    protected EnumFacing face;
    protected TextureAtlasSprite sprite;
    private int[] vertexDataSingle = null;
    private QuadBounds quadBounds;
    private boolean quadEmissiveChecked;
    private BakedQuad quadEmissive;

    public BakedQuad(int[] p_i3_1_, int p_i3_2_, EnumFacing p_i3_3_, TextureAtlasSprite p_i3_4_)
    {
        this.vertexData = p_i3_1_;
        this.tintIndex = p_i3_2_;
        this.face = p_i3_3_;
        this.sprite = p_i3_4_;
        this.fixVertexData();
    }

    public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn)
    {
        this.vertexData = vertexDataIn;
        this.tintIndex = tintIndexIn;
        this.face = faceIn;
        this.fixVertexData();
    }

    public TextureAtlasSprite getSprite()
    {
        if (this.sprite == null)
        {
            this.sprite = getSpriteByUv(this.getVertexData());
        }

        return this.sprite;
    }

    public int[] getVertexData()
    {
        this.fixVertexData();
        return this.vertexData;
    }

    public boolean hasTintIndex()
    {
        return this.tintIndex != -1;
    }

    public int getTintIndex()
    {
        return this.tintIndex;
    }

    public EnumFacing getFace()
    {
        if (this.face == null)
        {
            this.face = FaceBakery.getFacingFromVertexData(this.getVertexData());
        }

        return this.face;
    }

    public int[] getVertexDataSingle()
    {
        if (this.vertexDataSingle == null)
        {
            this.vertexDataSingle = makeVertexDataSingle(this.getVertexData(), this.getSprite());
        }

        return this.vertexDataSingle;
    }

    private static int[] makeVertexDataSingle(int[] p_makeVertexDataSingle_0_, TextureAtlasSprite p_makeVertexDataSingle_1_)
    {
        int[] aint = p_makeVertexDataSingle_0_.clone();
        int i = aint.length / 4;

        for (int j = 0; j < 4; ++j)
        {
            int k = j * i;
            float f = Float.intBitsToFloat(aint[k + 4]);
            float f1 = Float.intBitsToFloat(aint[k + 4 + 1]);
            float f2 = p_makeVertexDataSingle_1_.toSingleU(f);
            float f3 = p_makeVertexDataSingle_1_.toSingleV(f1);
            aint[k + 4] = Float.floatToRawIntBits(f2);
            aint[k + 4 + 1] = Float.floatToRawIntBits(f3);
        }

        return aint;
    }

    public void pipe(IVertexConsumer p_pipe_1_)
    {
        Reflector.callVoid(Reflector.LightUtil_putBakedQuad, p_pipe_1_, this);
    }

    private static TextureAtlasSprite getSpriteByUv(int[] p_getSpriteByUv_0_)
    {
        float f = 1.0F;
        float f1 = 1.0F;
        float f2 = 0.0F;
        float f3 = 0.0F;
        int i = p_getSpriteByUv_0_.length / 4;

        for (int j = 0; j < 4; ++j)
        {
            int k = j * i;
            float f4 = Float.intBitsToFloat(p_getSpriteByUv_0_[k + 4]);
            float f5 = Float.intBitsToFloat(p_getSpriteByUv_0_[k + 4 + 1]);
            f = Math.min(f, f4);
            f1 = Math.min(f1, f5);
            f2 = Math.max(f2, f4);
            f3 = Math.max(f3, f5);
        }

        float f6 = (f + f2) / 2.0F;
        float f7 = (f1 + f3) / 2.0F;
        TextureAtlasSprite textureatlassprite = Minecraft.getMinecraft().getTextureMapBlocks().getIconByUV(f6, f7);
        return textureatlassprite;
    }

    protected void fixVertexData()
    {
        if (Config.isShaders())
        {
            if (this.vertexData.length == 28)
            {
                this.vertexData = expandVertexData(this.vertexData);
            }
        }
        else if (this.vertexData.length == 56)
        {
            this.vertexData = compactVertexData(this.vertexData);
        }
    }

    private static int[] expandVertexData(int[] p_expandVertexData_0_)
    {
        int i = p_expandVertexData_0_.length / 4;
        int j = i * 2;
        int[] aint = new int[j * 4];

        for (int k = 0; k < 4; ++k)
        {
            System.arraycopy(p_expandVertexData_0_, k * i, aint, k * j, i);
        }

        return aint;
    }

    private static int[] compactVertexData(int[] p_compactVertexData_0_)
    {
        int i = p_compactVertexData_0_.length / 4;
        int j = i / 2;
        int[] aint = new int[j * 4];

        for (int k = 0; k < 4; ++k)
        {
            System.arraycopy(p_compactVertexData_0_, k * i, aint, k * j, j);
        }

        return aint;
    }

    public QuadBounds getQuadBounds()
    {
        if (this.quadBounds == null)
        {
            this.quadBounds = new QuadBounds(this.getVertexData());
        }

        return this.quadBounds;
    }

    public float getMidX()
    {
        QuadBounds quadbounds = this.getQuadBounds();
        return (quadbounds.getMaxX() + quadbounds.getMinX()) / 2.0F;
    }

    public double getMidY()
    {
        QuadBounds quadbounds = this.getQuadBounds();
        return (quadbounds.getMaxY() + quadbounds.getMinY()) / 2.0F;
    }

    public double getMidZ()
    {
        QuadBounds quadbounds = this.getQuadBounds();
        return (quadbounds.getMaxZ() + quadbounds.getMinZ()) / 2.0F;
    }

    public boolean isFaceQuad()
    {
        QuadBounds quadbounds = this.getQuadBounds();
        return quadbounds.isFaceQuad(this.face);
    }

    public boolean isFullQuad()
    {
        QuadBounds quadbounds = this.getQuadBounds();
        return quadbounds.isFullQuad(this.face);
    }

    public boolean isFullFaceQuad()
    {
        return this.isFullQuad() && this.isFaceQuad();
    }

    public BakedQuad getQuadEmissive()
    {
        if (this.quadEmissiveChecked)
        {
            return this.quadEmissive;
        }
        else
        {
            if (this.quadEmissive == null && this.sprite != null && this.sprite.spriteEmissive != null)
            {
                this.quadEmissive = new BreakingFour(this, this.sprite.spriteEmissive);
            }

            this.quadEmissiveChecked = true;
            return this.quadEmissive;
        }
    }

    public String toString()
    {
        return "vertex: " + this.vertexData.length / 7 + ", tint: " + this.tintIndex + ", facing: " + this.face + ", sprite: " + this.sprite;
    }
}
