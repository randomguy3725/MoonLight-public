package net.optifine.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.util.LinkedList;

public class VboRegion
{
    private EnumWorldBlockLayer layer = null;
    private int glBufferId = OpenGlHelper.glGenBuffers();
    private int capacity = 4096;
    private int positionTop = 0;
    private int sizeUsed;
    private final LinkedList<VboRange> rangeList = new LinkedList();
    private VboRange compactRangeLast = null;
    private IntBuffer bufferIndexVertex;
    private IntBuffer bufferCountVertex;
    private int drawMode;
    private final int vertexBytes;

    public VboRegion(EnumWorldBlockLayer layer)
    {
        this.bufferIndexVertex = Config.createDirectIntBuffer(this.capacity);
        this.bufferCountVertex = Config.createDirectIntBuffer(this.capacity);
        this.drawMode = 7;
        this.vertexBytes = DefaultVertexFormats.BLOCK.getNextOffset();
        this.layer = layer;
        this.bindBuffer();
        long i = this.toBytes(this.capacity);
        OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, i, OpenGlHelper.GL_STATIC_DRAW);
        this.unbindBuffer();
    }

    public void bufferData(ByteBuffer data, VboRange range)
    {
        int i = range.getPosition();
        int j = range.getSize();
        int k = this.toVertex(data.limit());

        if (k <= 0)
        {
            if (i >= 0)
            {
                range.setPosition(-1);
                range.setSize(0);
                this.rangeList.remove(range.getNode());
                this.sizeUsed -= j;
            }
        }
        else
        {
            if (k > j)
            {
                range.setPosition(this.positionTop);
                range.setSize(k);
                this.positionTop += k;

                if (i >= 0)
                {
                    this.rangeList.remove(range.getNode());
                }

                this.rangeList.addLast(range.getNode());
            }

            range.setSize(k);
            this.sizeUsed += k - j;
            this.checkVboSize(range.getPositionNext());
            long l = this.toBytes(range.getPosition());
            this.bindBuffer();
            OpenGlHelper.glBufferSubData(OpenGlHelper.GL_ARRAY_BUFFER, l, data);
            this.unbindBuffer();

            if (this.positionTop > this.sizeUsed * 11 / 10)
            {
                this.compactRanges(1);
            }
        }
    }

    private void compactRanges(int countMax)
    {
        if (!this.rangeList.isEmpty())
        {
            VboRange vborange = this.compactRangeLast;

            if (vborange == null || !this.rangeList.contains(vborange.getNode()))
            {
                vborange = this.rangeList.getFirst().getItem();
            }

            int i = vborange.getPosition();
            VboRange vborange1 = vborange.getPrev();

            if (vborange1 == null)
            {
                i = 0;
            }
            else
            {
                i = vborange1.getPositionNext();
            }

            int j = 0;

            while (vborange != null && j < countMax)
            {
                ++j;

                if (vborange.getPosition() == i)
                {
                    i += vborange.getSize();
                    vborange = vborange.getNext();
                }
                else
                {
                    int k = vborange.getPosition() - i;

                    if (vborange.getSize() <= k)
                    {
                        this.copyVboData(vborange.getPosition(), i, vborange.getSize());
                        vborange.setPosition(i);
                        i += vborange.getSize();
                        vborange = vborange.getNext();
                    }
                    else
                    {
                        this.checkVboSize(this.positionTop + vborange.getSize());
                        this.copyVboData(vborange.getPosition(), this.positionTop, vborange.getSize());
                        vborange.setPosition(this.positionTop);
                        this.positionTop += vborange.getSize();
                        VboRange vborange2 = vborange.getNext();
                        this.rangeList.remove(vborange.getNode());
                        this.rangeList.addLast(vborange.getNode());
                        vborange = vborange2;
                    }
                }
            }

            if (vborange == null)
            {
                this.positionTop = this.rangeList.getLast().getItem().getPositionNext();
            }

            this.compactRangeLast = vborange;
        }
    }

    private void checkRanges()
    {
        int i = 0;
        int j = 0;

        for (VboRange vborange = this.rangeList.getFirst().getItem(); vborange != null; vborange = vborange.getNext())
        {
            ++i;
            j += vborange.getSize();

            if (vborange.getPosition() < 0 || vborange.getSize() <= 0 || vborange.getPositionNext() > this.positionTop)
            {
                throw new RuntimeException("Invalid range: " + vborange);
            }

            VboRange vborange1 = vborange.getPrev();

            if (vborange1 != null && vborange.getPosition() < vborange1.getPositionNext())
            {
                throw new RuntimeException("Invalid range: " + vborange);
            }

            VboRange vborange2 = vborange.getNext();

            if (vborange2 != null && vborange.getPositionNext() > vborange2.getPosition())
            {
                throw new RuntimeException("Invalid range: " + vborange);
            }
        }

        if (i != this.rangeList.getSize())
        {
            throw new RuntimeException("Invalid count: " + i + " <> " + this.rangeList.getSize());
        }
        else if (j != this.sizeUsed)
        {
            throw new RuntimeException("Invalid size: " + j + " <> " + this.sizeUsed);
        }
    }

    private void checkVboSize(int sizeMin)
    {
        if (this.capacity < sizeMin)
        {
            this.expandVbo(sizeMin);
        }
    }

    private void copyVboData(int posFrom, int posTo, int size)
    {
        long i = this.toBytes(posFrom);
        long j = this.toBytes(posTo);
        long k = this.toBytes(size);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, this.glBufferId);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, this.glBufferId);
        OpenGlHelper.glCopyBufferSubData(OpenGlHelper.GL_COPY_READ_BUFFER, OpenGlHelper.GL_COPY_WRITE_BUFFER, i, j, k);
        Config.checkGlError("Copy VBO range");
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, 0);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, 0);
    }

    private void expandVbo(int sizeMin)
    {
        int i;

        for (i = this.capacity * 6 / 4; i < sizeMin; i = i * 6 / 4)
        {
        }

        long j = this.toBytes(this.capacity);
        long k = this.toBytes(i);
        int l = OpenGlHelper.glGenBuffers();
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, l);
        OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, k, OpenGlHelper.GL_STATIC_DRAW);
        Config.checkGlError("Expand VBO");
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, this.glBufferId);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, l);
        OpenGlHelper.glCopyBufferSubData(OpenGlHelper.GL_COPY_READ_BUFFER, OpenGlHelper.GL_COPY_WRITE_BUFFER, 0L, 0L, j);
        Config.checkGlError("Copy VBO: " + k);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, 0);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, 0);
        OpenGlHelper.glDeleteBuffers(this.glBufferId);
        this.bufferIndexVertex = Config.createDirectIntBuffer(i);
        this.bufferCountVertex = Config.createDirectIntBuffer(i);
        this.glBufferId = l;
        this.capacity = i;
    }

    public void bindBuffer()
    {
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, this.glBufferId);
    }

    public void drawArrays(int drawMode, VboRange range)
    {
        if (this.drawMode != drawMode)
        {
            if (this.bufferIndexVertex.position() > 0)
            {
                throw new IllegalArgumentException("Mixed region draw modes: " + this.drawMode + " != " + drawMode);
            }

            this.drawMode = drawMode;
        }

        this.bufferIndexVertex.put(range.getPosition());
        this.bufferCountVertex.put(range.getSize());
    }

    public void finishDraw(VboRenderList vboRenderList)
    {
        this.bindBuffer();
        vboRenderList.setupArrayPointers();
        this.bufferIndexVertex.flip();
        this.bufferCountVertex.flip();
        GlStateManager.glMultiDrawArrays(this.drawMode, this.bufferIndexVertex, this.bufferCountVertex);
        this.bufferIndexVertex.limit(this.bufferIndexVertex.capacity());
        this.bufferCountVertex.limit(this.bufferCountVertex.capacity());

        if (this.positionTop > this.sizeUsed * 11 / 10)
        {
            this.compactRanges(1);
        }
    }

    public void unbindBuffer()
    {
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
    }

    public void deleteGlBuffers()
    {
        if (this.glBufferId >= 0)
        {
            OpenGlHelper.glDeleteBuffers(this.glBufferId);
            this.glBufferId = -1;
        }
    }

    private long toBytes(int vertex)
    {
        return (long)vertex * (long)this.vertexBytes;
    }

    private int toVertex(long bytes)
    {
        return (int)(bytes / (long)this.vertexBytes);
    }

    public int getPositionTop()
    {
        return this.positionTop;
    }
}
