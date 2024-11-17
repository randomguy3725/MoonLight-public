package net.optifine.shaders;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class SVertexBuilder
{
    int vertexSize;
    int offsetNormal;
    int offsetUV;
    int offsetUVCenter;
    boolean hasNormal;
    boolean hasTangent;
    boolean hasUV;
    boolean hasUVCenter;
    long[] entityData = new long[10];
    int entityDataIndex = 0;

    public SVertexBuilder()
    {
        this.entityData[this.entityDataIndex] = 0L;
    }

    public static void initVertexBuilder(WorldRenderer wrr)
    {
        wrr.sVertexBuilder = new SVertexBuilder();
    }

    public void pushEntity(long data)
    {
        ++this.entityDataIndex;
        this.entityData[this.entityDataIndex] = data;
    }

    public void popEntity()
    {
        this.entityData[this.entityDataIndex] = 0L;
        --this.entityDataIndex;
    }

    public static void pushEntity(IBlockState blockState, BlockPos blockPos, IBlockAccess blockAccess, WorldRenderer wrr)
    {
        Block block = blockState.getBlock();
        int i;
        int j;

        if (blockState instanceof BlockStateBase blockstatebase)
        {
            i = blockstatebase.getBlockId();
            j = blockstatebase.getMetadata();
        }
        else
        {
            i = Block.getIdFromBlock(block);
            j = block.getMetaFromState(blockState);
        }

        int j1 = BlockAliases.getBlockAliasId(i, j);

        if (j1 >= 0)
        {
            i = j1;
        }

        int k = block.getRenderType();
        int l = ((k & 65535) << 16) + (i & 65535);
        int i1 = j & 65535;
        wrr.sVertexBuilder.pushEntity(((long)i1 << 32) + (long)l);
    }

    public static void popEntity(WorldRenderer wrr)
    {
        wrr.sVertexBuilder.popEntity();
    }

    public static boolean popEntity(boolean value, WorldRenderer wrr)
    {
        wrr.sVertexBuilder.popEntity();
        return value;
    }

    public static void endSetVertexFormat(WorldRenderer wrr)
    {
        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;
        VertexFormat vertexformat = wrr.getVertexFormat();
        svertexbuilder.vertexSize = vertexformat.getNextOffset() / 4;
        svertexbuilder.hasNormal = vertexformat.hasNormal();
        svertexbuilder.hasTangent = svertexbuilder.hasNormal;
        svertexbuilder.hasUV = vertexformat.hasUvOffset(0);
        svertexbuilder.offsetNormal = svertexbuilder.hasNormal ? vertexformat.getNormalOffset() / 4 : 0;
        svertexbuilder.offsetUV = svertexbuilder.hasUV ? vertexformat.getUvOffsetById(0) / 4 : 0;
        svertexbuilder.offsetUVCenter = 8;
    }

    public static void beginAddVertex(WorldRenderer wrr)
    {
        if (wrr.vertexCount == 0)
        {
            endSetVertexFormat(wrr);
        }
    }

    public static void endAddVertex(WorldRenderer wrr)
    {
        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;

        if (svertexbuilder.vertexSize == 14)
        {
            if (wrr.drawMode == 7 && wrr.vertexCount % 4 == 0)
            {
                svertexbuilder.calcNormal(wrr, wrr.getBufferSize() - 4 * svertexbuilder.vertexSize);
            }

            long i = svertexbuilder.entityData[svertexbuilder.entityDataIndex];
            int j = wrr.getBufferSize() - 14 + 12;
            wrr.rawIntBuffer.put(j, (int)i);
            wrr.rawIntBuffer.put(j + 1, (int)(i >> 32));
        }
    }

    public static void beginAddVertexData(WorldRenderer wrr, int[] data)
    {
        if (wrr.vertexCount == 0)
        {
            endSetVertexFormat(wrr);
        }

        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;

        if (svertexbuilder.vertexSize == 14)
        {
            long i = svertexbuilder.entityData[svertexbuilder.entityDataIndex];

            for (int j = 12; j + 1 < data.length; j += 14)
            {
                data[j] = (int)i;
                data[j + 1] = (int)(i >> 32);
            }
        }
    }

    public static void beginAddVertexData(WorldRenderer wrr, ByteBuffer byteBuffer)
    {
        if (wrr.vertexCount == 0)
        {
            endSetVertexFormat(wrr);
        }

        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;

        if (svertexbuilder.vertexSize == 14)
        {
            long i = svertexbuilder.entityData[svertexbuilder.entityDataIndex];
            int j = byteBuffer.limit() / 4;

            for (int k = 12; k + 1 < j; k += 14)
            {
                int l = (int)i;
                int i1 = (int)(i >> 32);
                byteBuffer.putInt(k * 4, l);
                byteBuffer.putInt((k + 1) * 4, i1);
            }
        }
    }

    public static void endAddVertexData(WorldRenderer wrr)
    {
        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;

        if (svertexbuilder.vertexSize == 14 && wrr.drawMode == 7 && wrr.vertexCount % 4 == 0)
        {
            svertexbuilder.calcNormal(wrr, wrr.getBufferSize() - 4 * svertexbuilder.vertexSize);
        }
    }

    public void calcNormal(WorldRenderer wrr, int baseIndex)
    {
        FloatBuffer floatbuffer = wrr.rawFloatBuffer;
        IntBuffer intbuffer = wrr.rawIntBuffer;
        int i = wrr.getBufferSize();
        float f = floatbuffer.get(baseIndex);
        float f1 = floatbuffer.get(baseIndex + 1);
        float f2 = floatbuffer.get(baseIndex + 2);
        float f3 = floatbuffer.get(baseIndex + this.offsetUV);
        float f4 = floatbuffer.get(baseIndex + this.offsetUV + 1);
        float f5 = floatbuffer.get(baseIndex + this.vertexSize);
        float f6 = floatbuffer.get(baseIndex + this.vertexSize + 1);
        float f7 = floatbuffer.get(baseIndex + this.vertexSize + 2);
        float f8 = floatbuffer.get(baseIndex + this.vertexSize + this.offsetUV);
        float f9 = floatbuffer.get(baseIndex + this.vertexSize + this.offsetUV + 1);
        float f10 = floatbuffer.get(baseIndex + 2 * this.vertexSize);
        float f11 = floatbuffer.get(baseIndex + 2 * this.vertexSize + 1);
        float f12 = floatbuffer.get(baseIndex + 2 * this.vertexSize + 2);
        float f13 = floatbuffer.get(baseIndex + 2 * this.vertexSize + this.offsetUV);
        float f14 = floatbuffer.get(baseIndex + 2 * this.vertexSize + this.offsetUV + 1);
        float f15 = floatbuffer.get(baseIndex + 3 * this.vertexSize);
        float f16 = floatbuffer.get(baseIndex + 3 * this.vertexSize + 1);
        float f17 = floatbuffer.get(baseIndex + 3 * this.vertexSize + 2);
        float f18 = floatbuffer.get(baseIndex + 3 * this.vertexSize + this.offsetUV);
        float f19 = floatbuffer.get(baseIndex + 3 * this.vertexSize + this.offsetUV + 1);
        float f20 = f10 - f;
        float f21 = f11 - f1;
        float f22 = f12 - f2;
        float f23 = f15 - f5;
        float f24 = f16 - f6;
        float f25 = f17 - f7;
        float f30 = f21 * f25 - f24 * f22;
        float f31 = f22 * f23 - f25 * f20;
        float f32 = f20 * f24 - f23 * f21;
        float f33 = f30 * f30 + f31 * f31 + f32 * f32;
        float f34 = (double)f33 != 0.0D ? (float)(1.0D / Math.sqrt(f33)) : 1.0F;
        f30 = f30 * f34;
        f31 = f31 * f34;
        f32 = f32 * f34;
        f20 = f5 - f;
        f21 = f6 - f1;
        f22 = f7 - f2;
        float f26 = f8 - f3;
        float f27 = f9 - f4;
        f23 = f10 - f;
        f24 = f11 - f1;
        f25 = f12 - f2;
        float f28 = f13 - f3;
        float f29 = f14 - f4;
        float f35 = f26 * f29 - f28 * f27;
        float f36 = f35 != 0.0F ? 1.0F / f35 : 1.0F;
        float f37 = (f29 * f20 - f27 * f23) * f36;
        float f38 = (f29 * f21 - f27 * f24) * f36;
        float f39 = (f29 * f22 - f27 * f25) * f36;
        float f40 = (f26 * f23 - f28 * f20) * f36;
        float f41 = (f26 * f24 - f28 * f21) * f36;
        float f42 = (f26 * f25 - f28 * f22) * f36;
        f33 = f37 * f37 + f38 * f38 + f39 * f39;
        f34 = (double)f33 != 0.0D ? (float)(1.0D / Math.sqrt(f33)) : 1.0F;
        f37 = f37 * f34;
        f38 = f38 * f34;
        f39 = f39 * f34;
        f33 = f40 * f40 + f41 * f41 + f42 * f42;
        f34 = (double)f33 != 0.0D ? (float)(1.0D / Math.sqrt(f33)) : 1.0F;
        f40 = f40 * f34;
        f41 = f41 * f34;
        f42 = f42 * f34;
        float f43 = f32 * f38 - f31 * f39;
        float f44 = f30 * f39 - f32 * f37;
        float f45 = f31 * f37 - f30 * f38;
        float f46 = f40 * f43 + f41 * f44 + f42 * f45 < 0.0F ? -1.0F : 1.0F;
        int j = (int)(f30 * 127.0F) & 255;
        int k = (int)(f31 * 127.0F) & 255;
        int l = (int)(f32 * 127.0F) & 255;
        int i1 = (l << 16) + (k << 8) + j;
        intbuffer.put(baseIndex + this.offsetNormal, i1);
        intbuffer.put(baseIndex + this.vertexSize + this.offsetNormal, i1);
        intbuffer.put(baseIndex + 2 * this.vertexSize + this.offsetNormal, i1);
        intbuffer.put(baseIndex + 3 * this.vertexSize + this.offsetNormal, i1);
        int j1 = ((int)(f37 * 32767.0F) & 65535) + (((int)(f38 * 32767.0F) & 65535) << 16);
        int k1 = ((int)(f39 * 32767.0F) & 65535) + (((int)(f46 * 32767.0F) & 65535) << 16);
        intbuffer.put(baseIndex + 10, j1);
        intbuffer.put(baseIndex + 10 + 1, k1);
        intbuffer.put(baseIndex + this.vertexSize + 10, j1);
        intbuffer.put(baseIndex + this.vertexSize + 10 + 1, k1);
        intbuffer.put(baseIndex + 2 * this.vertexSize + 10, j1);
        intbuffer.put(baseIndex + 2 * this.vertexSize + 10 + 1, k1);
        intbuffer.put(baseIndex + 3 * this.vertexSize + 10, j1);
        intbuffer.put(baseIndex + 3 * this.vertexSize + 10 + 1, k1);
        float f47 = (f3 + f8 + f13 + f18) / 4.0F;
        float f48 = (f4 + f9 + f14 + f19) / 4.0F;
        floatbuffer.put(baseIndex + 8, f47);
        floatbuffer.put(baseIndex + 8 + 1, f48);
        floatbuffer.put(baseIndex + this.vertexSize + 8, f47);
        floatbuffer.put(baseIndex + this.vertexSize + 8 + 1, f48);
        floatbuffer.put(baseIndex + 2 * this.vertexSize + 8, f47);
        floatbuffer.put(baseIndex + 2 * this.vertexSize + 8 + 1, f48);
        floatbuffer.put(baseIndex + 3 * this.vertexSize + 8, f47);
        floatbuffer.put(baseIndex + 3 * this.vertexSize + 8 + 1, f48);
    }

    public static void calcNormalChunkLayer(WorldRenderer wrr)
    {
        if (wrr.getVertexFormat().hasNormal() && wrr.drawMode == 7 && wrr.vertexCount % 4 == 0)
        {
            SVertexBuilder svertexbuilder = wrr.sVertexBuilder;
            endSetVertexFormat(wrr);
            int i = wrr.vertexCount * svertexbuilder.vertexSize;

            for (int j = 0; j < i; j += svertexbuilder.vertexSize * 4)
            {
                svertexbuilder.calcNormal(wrr, j);
            }
        }
    }

    public static void drawArrays(int drawMode, int first, int count, WorldRenderer wrr)
    {
        if (count != 0)
        {
            VertexFormat vertexformat = wrr.getVertexFormat();
            int i = vertexformat.getNextOffset();

            if (i == 56)
            {
                ByteBuffer bytebuffer = wrr.getByteBuffer();
                bytebuffer.position(32);
                GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, GL11.GL_FLOAT, false, i, bytebuffer);
                bytebuffer.position(40);
                GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, GL11.GL_SHORT, false, i, bytebuffer);
                bytebuffer.position(48);
                GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, GL11.GL_SHORT, false, i, bytebuffer);
                bytebuffer.position(0);
                GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
                GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
                GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
                GlStateManager.glDrawArrays(drawMode, first, count);
                GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
                GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
                GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
            }
            else
            {
                GlStateManager.glDrawArrays(drawMode, first, count);
            }
        }
    }
}
