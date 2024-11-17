package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;

public class CompiledChunk
{
    public static final CompiledChunk DUMMY = new CompiledChunk()
    {
        protected void setLayerUsed(EnumWorldBlockLayer layer)
        {
            throw new UnsupportedOperationException();
        }
        public void setLayerStarted(EnumWorldBlockLayer layer)
        {
            throw new UnsupportedOperationException();
        }
        public boolean isVisible(EnumFacing facing, EnumFacing facing2)
        {
            return false;
        }
        public void setAnimatedSprites(EnumWorldBlockLayer p_setAnimatedSprites_1_, BitSet p_setAnimatedSprites_2_)
        {
            throw new UnsupportedOperationException();
        }
    };
    private final boolean[] layersUsed = new boolean[RenderChunk.ENUM_WORLD_BLOCK_LAYERS.length];
    private final boolean[] layersStarted = new boolean[RenderChunk.ENUM_WORLD_BLOCK_LAYERS.length];
    private boolean empty = true;
    private final List<TileEntity> tileEntities = Lists.newArrayList();
    private SetVisibility setVisibility = new SetVisibility();
    private WorldRenderer.State state;
    private final BitSet[] animatedSprites = new BitSet[RenderChunk.ENUM_WORLD_BLOCK_LAYERS.length];

    public boolean isEmpty()
    {
        return this.empty;
    }

    protected void setLayerUsed(EnumWorldBlockLayer layer)
    {
        this.empty = false;
        this.layersUsed[layer.ordinal()] = true;
    }

    public boolean isLayerEmpty(EnumWorldBlockLayer layer)
    {
        return !this.layersUsed[layer.ordinal()];
    }

    public void setLayerStarted(EnumWorldBlockLayer layer)
    {
        this.layersStarted[layer.ordinal()] = true;
    }

    public boolean isLayerStarted(EnumWorldBlockLayer layer)
    {
        return this.layersStarted[layer.ordinal()];
    }

    public List<TileEntity> getTileEntities()
    {
        return this.tileEntities;
    }

    public void addTileEntity(TileEntity tileEntityIn)
    {
        this.tileEntities.add(tileEntityIn);
    }

    public boolean isVisible(EnumFacing facing, EnumFacing facing2)
    {
        return this.setVisibility.isVisible(facing, facing2);
    }

    public void setVisibility(SetVisibility visibility)
    {
        this.setVisibility = visibility;
    }

    public WorldRenderer.State getState()
    {
        return this.state;
    }

    public void setState(WorldRenderer.State stateIn)
    {
        this.state = stateIn;
    }

    public BitSet getAnimatedSprites(EnumWorldBlockLayer p_getAnimatedSprites_1_)
    {
        return this.animatedSprites[p_getAnimatedSprites_1_.ordinal()];
    }

    public void setAnimatedSprites(EnumWorldBlockLayer p_setAnimatedSprites_1_, BitSet p_setAnimatedSprites_2_)
    {
        this.animatedSprites[p_setAnimatedSprites_1_.ordinal()] = p_setAnimatedSprites_2_;
    }
}
