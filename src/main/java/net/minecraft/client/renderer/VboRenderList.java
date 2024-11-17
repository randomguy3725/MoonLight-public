package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.render.VboRegion;
import net.optifine.shaders.ShadersRender;
import org.lwjgl.opengl.GL11;

public class VboRenderList extends ChunkRenderContainer
{
    private double viewEntityX;
    private double viewEntityY;
    private double viewEntityZ;

    public void renderChunkLayer(EnumWorldBlockLayer layer)
    {
        if (this.initialized)
        {
            if (!Config.isRenderRegions())
            {
                for (RenderChunk renderchunk1 : this.renderChunks)
                {
                    VertexBuffer vertexbuffer1 = renderchunk1.getVertexBufferByLayer(layer.ordinal());
                    GlStateManager.pushMatrix();
                    this.preRenderChunk(renderchunk1);
                    renderchunk1.multModelviewMatrix();
                    vertexbuffer1.bindBuffer();
                    this.setupArrayPointers();
                    vertexbuffer1.drawArrays(7);
                    GlStateManager.popMatrix();
                }
            }
            else
            {
                int i = Integer.MIN_VALUE;
                int j = Integer.MIN_VALUE;
                VboRegion vboregion = null;

                for (RenderChunk renderchunk : this.renderChunks)
                {
                    VertexBuffer vertexbuffer = renderchunk.getVertexBufferByLayer(layer.ordinal());
                    VboRegion vboregion1 = vertexbuffer.getVboRegion();

                    if (vboregion1 != vboregion || i != renderchunk.regionX || j != renderchunk.regionZ)
                    {
                        if (vboregion != null)
                        {
                            this.drawRegion(i, j, vboregion);
                        }

                        i = renderchunk.regionX;
                        j = renderchunk.regionZ;
                        vboregion = vboregion1;
                    }

                    vertexbuffer.drawArrays(7);
                }

                if (vboregion != null)
                {
                    this.drawRegion(i, j, vboregion);
                }
            }

            OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
            GlStateManager.resetColor();
            this.renderChunks.clear();
        }
    }

    public void setupArrayPointers()
    {
        if (Config.isShaders())
        {
            ShadersRender.setupArrayPointersVbo();
        }
        else
        {
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 28, 0L);
            GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 28, 12L);
            GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 28, 16L);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glTexCoordPointer(2, GL11.GL_SHORT, 28, 24L);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        }
    }

    public void initialize(double viewEntityXIn, double viewEntityYIn, double viewEntityZIn)
    {
        this.viewEntityX = viewEntityXIn;
        this.viewEntityY = viewEntityYIn;
        this.viewEntityZ = viewEntityZIn;
        super.initialize(viewEntityXIn, viewEntityYIn, viewEntityZIn);
    }

    private void drawRegion(int p_drawRegion_1_, int p_drawRegion_2_, VboRegion p_drawRegion_3_)
    {
        GlStateManager.pushMatrix();
        this.preRenderRegion(p_drawRegion_1_, 0, p_drawRegion_2_);
        p_drawRegion_3_.finishDraw(this);
        GlStateManager.popMatrix();
    }

    public void preRenderRegion(int p_preRenderRegion_1_, int p_preRenderRegion_2_, int p_preRenderRegion_3_)
    {
        GlStateManager.translate((float)((double)p_preRenderRegion_1_ - this.viewEntityX), (float)((double)p_preRenderRegion_2_ - this.viewEntityY), (float)((double)p_preRenderRegion_3_ - this.viewEntityZ));
    }
}
