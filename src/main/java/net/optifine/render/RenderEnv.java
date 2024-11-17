package net.optifine.render;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.BlockPosM;
import net.optifine.model.ListQuadsOverlay;

public class RenderEnv
{
    private IBlockState blockState;
    private BlockPos blockPos;
    private int blockId = -1;
    private int metadata = -1;
    private int breakingAnimation = -1;
    private int smartLeaves = -1;
    private final float[] quadBounds = new float[EnumFacing.VALUES.length * 2];
    private final BitSet boundsFlags = new BitSet(3);
    private final BlockModelRenderer.AmbientOcclusionFace aoFace = new BlockModelRenderer.AmbientOcclusionFace();
    private BlockPosM colorizerBlockPosM = null;
    private boolean[] borderFlags = null;
    private boolean[] borderFlags2 = null;
    private boolean[] borderFlags3 = null;
    private EnumFacing[] borderDirections = null;
    private final List<BakedQuad> listQuadsCustomizer = new ArrayList();
    private final List<BakedQuad> listQuadsCtmMultipass = new ArrayList();
    private final BakedQuad[] arrayQuadsCtm1 = new BakedQuad[1];
    private final BakedQuad[] arrayQuadsCtm2 = new BakedQuad[2];
    private final BakedQuad[] arrayQuadsCtm3 = new BakedQuad[3];
    private final BakedQuad[] arrayQuadsCtm4 = new BakedQuad[4];
    private RegionRenderCacheBuilder regionRenderCacheBuilder = null;
    private final ListQuadsOverlay[] listsQuadsOverlay = new ListQuadsOverlay[EnumWorldBlockLayer.values().length];
    private boolean overlaysRendered = false;
    private static final int UNKNOWN = -1;
    private static final int FALSE = 0;
    private static final int TRUE = 1;

    public RenderEnv(IBlockState blockState, BlockPos blockPos)
    {
        this.blockState = blockState;
        this.blockPos = blockPos;
    }

    public void reset(IBlockState blockStateIn, BlockPos blockPosIn)
    {
        if (this.blockState != blockStateIn || this.blockPos != blockPosIn)
        {
            this.blockState = blockStateIn;
            this.blockPos = blockPosIn;
            this.blockId = -1;
            this.metadata = -1;
            this.breakingAnimation = -1;
            this.smartLeaves = -1;
            this.boundsFlags.clear();
        }
    }

    public int getBlockId()
    {
        if (this.blockId < 0)
        {
            if (this.blockState instanceof BlockStateBase blockstatebase)
            {
                this.blockId = blockstatebase.getBlockId();
            }
            else
            {
                this.blockId = Block.getIdFromBlock(this.blockState.getBlock());
            }
        }

        return this.blockId;
    }

    public int getMetadata()
    {
        if (this.metadata < 0)
        {
            if (this.blockState instanceof BlockStateBase blockstatebase)
            {
                this.metadata = blockstatebase.getMetadata();
            }
            else
            {
                this.metadata = this.blockState.getBlock().getMetaFromState(this.blockState);
            }
        }

        return this.metadata;
    }

    public float[] getQuadBounds()
    {
        return this.quadBounds;
    }

    public BitSet getBoundsFlags()
    {
        return this.boundsFlags;
    }

    public BlockModelRenderer.AmbientOcclusionFace getAoFace()
    {
        return this.aoFace;
    }

    public boolean isBreakingAnimation(List listQuads)
    {
        if (this.breakingAnimation == -1 && listQuads.size() > 0)
        {
            if (listQuads.get(0) instanceof BreakingFour)
            {
                this.breakingAnimation = 1;
            }
            else
            {
                this.breakingAnimation = 0;
            }
        }

        return this.breakingAnimation == 1;
    }

    public boolean isBreakingAnimation(BakedQuad quad)
    {
        if (this.breakingAnimation < 0)
        {
            if (quad instanceof BreakingFour)
            {
                this.breakingAnimation = 1;
            }
            else
            {
                this.breakingAnimation = 0;
            }
        }

        return this.breakingAnimation == 1;
    }

    public boolean isBreakingAnimation()
    {
        return this.breakingAnimation == 1;
    }

    public IBlockState getBlockState()
    {
        return this.blockState;
    }

    public BlockPosM getColorizerBlockPosM()
    {
        if (this.colorizerBlockPosM == null)
        {
            this.colorizerBlockPosM = new BlockPosM(0, 0, 0);
        }

        return this.colorizerBlockPosM;
    }

    public boolean[] getBorderFlags()
    {
        if (this.borderFlags == null)
        {
            this.borderFlags = new boolean[4];
        }

        return this.borderFlags;
    }

    public boolean[] getBorderFlags2()
    {
        if (this.borderFlags2 == null)
        {
            this.borderFlags2 = new boolean[4];
        }

        return this.borderFlags2;
    }

    public boolean[] getBorderFlags3()
    {
        if (this.borderFlags3 == null)
        {
            this.borderFlags3 = new boolean[4];
        }

        return this.borderFlags3;
    }

    public EnumFacing[] getBorderDirections()
    {
        if (this.borderDirections == null)
        {
            this.borderDirections = new EnumFacing[4];
        }

        return this.borderDirections;
    }

    public EnumFacing[] getBorderDirections(EnumFacing dir0, EnumFacing dir1, EnumFacing dir2, EnumFacing dir3)
    {
        EnumFacing[] aenumfacing = this.getBorderDirections();
        aenumfacing[0] = dir0;
        aenumfacing[1] = dir1;
        aenumfacing[2] = dir2;
        aenumfacing[3] = dir3;
        return aenumfacing;
    }

    public boolean isSmartLeaves()
    {
        if (this.smartLeaves == -1)
        {
            if (Config.isTreesSmart() && this.blockState.getBlock() instanceof BlockLeaves)
            {
                this.smartLeaves = 1;
            }
            else
            {
                this.smartLeaves = 0;
            }
        }

        return this.smartLeaves == 1;
    }

    public List<BakedQuad> getListQuadsCustomizer()
    {
        return this.listQuadsCustomizer;
    }

    public BakedQuad[] getArrayQuadsCtm(BakedQuad quad)
    {
        this.arrayQuadsCtm1[0] = quad;
        return this.arrayQuadsCtm1;
    }

    public BakedQuad[] getArrayQuadsCtm(BakedQuad quad0, BakedQuad quad1)
    {
        this.arrayQuadsCtm2[0] = quad0;
        this.arrayQuadsCtm2[1] = quad1;
        return this.arrayQuadsCtm2;
    }

    public BakedQuad[] getArrayQuadsCtm(BakedQuad quad0, BakedQuad quad1, BakedQuad quad2)
    {
        this.arrayQuadsCtm3[0] = quad0;
        this.arrayQuadsCtm3[1] = quad1;
        this.arrayQuadsCtm3[2] = quad2;
        return this.arrayQuadsCtm3;
    }

    public BakedQuad[] getArrayQuadsCtm(BakedQuad quad0, BakedQuad quad1, BakedQuad quad2, BakedQuad quad3)
    {
        this.arrayQuadsCtm4[0] = quad0;
        this.arrayQuadsCtm4[1] = quad1;
        this.arrayQuadsCtm4[2] = quad2;
        this.arrayQuadsCtm4[3] = quad3;
        return this.arrayQuadsCtm4;
    }

    public List<BakedQuad> getListQuadsCtmMultipass(BakedQuad[] quads)
    {
        this.listQuadsCtmMultipass.clear();

        if (quads != null)
        {
            Collections.addAll(this.listQuadsCtmMultipass, quads);
        }

        return this.listQuadsCtmMultipass;
    }

    public RegionRenderCacheBuilder getRegionRenderCacheBuilder()
    {
        return this.regionRenderCacheBuilder;
    }

    public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder regionRenderCacheBuilder)
    {
        this.regionRenderCacheBuilder = regionRenderCacheBuilder;
    }

    public ListQuadsOverlay getListQuadsOverlay(EnumWorldBlockLayer layer)
    {
        ListQuadsOverlay listquadsoverlay = this.listsQuadsOverlay[layer.ordinal()];

        if (listquadsoverlay == null)
        {
            listquadsoverlay = new ListQuadsOverlay();
            this.listsQuadsOverlay[layer.ordinal()] = listquadsoverlay;
        }

        return listquadsoverlay;
    }

    public boolean isOverlaysRendered()
    {
        return this.overlaysRendered;
    }

    public void setOverlaysRendered(boolean overlaysRendered)
    {
        this.overlaysRendered = overlaysRendered;
    }
}
