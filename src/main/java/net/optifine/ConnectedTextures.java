package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.Matches;
import net.optifine.model.BlockModelUtils;
import net.optifine.model.ListQuadsOverlay;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.TileEntityUtils;

public class ConnectedTextures
{
    private static Map[] spriteQuadMaps = null;
    private static Map[] spriteQuadFullMaps = null;
    private static Map[][] spriteQuadCompactMaps = null;
    private static ConnectedProperties[][] blockProperties = null;
    private static ConnectedProperties[][] tileProperties = null;
    private static boolean multipass = false;
    protected static final int UNKNOWN = -1;
    protected static final int Y_NEG_DOWN = 0;
    protected static final int Y_POS_UP = 1;
    protected static final int Z_NEG_NORTH = 2;
    protected static final int Z_POS_SOUTH = 3;
    protected static final int X_NEG_WEST = 4;
    protected static final int X_POS_EAST = 5;
    private static final int Y_AXIS = 0;
    private static final int Z_AXIS = 1;
    private static final int X_AXIS = 2;
    public static final IBlockState AIR_DEFAULT_STATE = Blocks.air.getDefaultState();
    private static TextureAtlasSprite emptySprite = null;
    private static final BlockDir[] SIDES_Y_NEG_DOWN = new BlockDir[] {BlockDir.WEST, BlockDir.EAST, BlockDir.NORTH, BlockDir.SOUTH};
    private static final BlockDir[] SIDES_Y_POS_UP = new BlockDir[] {BlockDir.WEST, BlockDir.EAST, BlockDir.SOUTH, BlockDir.NORTH};
    private static final BlockDir[] SIDES_Z_NEG_NORTH = new BlockDir[] {BlockDir.EAST, BlockDir.WEST, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_Z_POS_SOUTH = new BlockDir[] {BlockDir.WEST, BlockDir.EAST, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_X_NEG_WEST = new BlockDir[] {BlockDir.NORTH, BlockDir.SOUTH, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_X_POS_EAST = new BlockDir[] {BlockDir.SOUTH, BlockDir.NORTH, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_Z_NEG_NORTH_Z_AXIS = new BlockDir[] {BlockDir.WEST, BlockDir.EAST, BlockDir.UP, BlockDir.DOWN};
    private static final BlockDir[] SIDES_X_POS_EAST_X_AXIS = new BlockDir[] {BlockDir.NORTH, BlockDir.SOUTH, BlockDir.UP, BlockDir.DOWN};
    private static final BlockDir[] EDGES_Y_NEG_DOWN = new BlockDir[] {BlockDir.NORTH_EAST, BlockDir.NORTH_WEST, BlockDir.SOUTH_EAST, BlockDir.SOUTH_WEST};
    private static final BlockDir[] EDGES_Y_POS_UP = new BlockDir[] {BlockDir.SOUTH_EAST, BlockDir.SOUTH_WEST, BlockDir.NORTH_EAST, BlockDir.NORTH_WEST};
    private static final BlockDir[] EDGES_Z_NEG_NORTH = new BlockDir[] {BlockDir.DOWN_WEST, BlockDir.DOWN_EAST, BlockDir.UP_WEST, BlockDir.UP_EAST};
    private static final BlockDir[] EDGES_Z_POS_SOUTH = new BlockDir[] {BlockDir.DOWN_EAST, BlockDir.DOWN_WEST, BlockDir.UP_EAST, BlockDir.UP_WEST};
    private static final BlockDir[] EDGES_X_NEG_WEST = new BlockDir[] {BlockDir.DOWN_SOUTH, BlockDir.DOWN_NORTH, BlockDir.UP_SOUTH, BlockDir.UP_NORTH};
    private static final BlockDir[] EDGES_X_POS_EAST = new BlockDir[] {BlockDir.DOWN_NORTH, BlockDir.DOWN_SOUTH, BlockDir.UP_NORTH, BlockDir.UP_SOUTH};
    private static final BlockDir[] EDGES_Z_NEG_NORTH_Z_AXIS = new BlockDir[] {BlockDir.UP_EAST, BlockDir.UP_WEST, BlockDir.DOWN_EAST, BlockDir.DOWN_WEST};
    private static final BlockDir[] EDGES_X_POS_EAST_X_AXIS = new BlockDir[] {BlockDir.UP_SOUTH, BlockDir.UP_NORTH, BlockDir.DOWN_SOUTH, BlockDir.DOWN_NORTH};
    public static final TextureAtlasSprite SPRITE_DEFAULT = new TextureAtlasSprite("<default>");

    public static BakedQuad[] getConnectedTexture(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, BakedQuad quad, RenderEnv renderEnv)
    {
        TextureAtlasSprite textureatlassprite = quad.getSprite();

        if (textureatlassprite == null)
        {
            return renderEnv.getArrayQuadsCtm(quad);
        }
        else
        {
            Block block = blockState.getBlock();

            if (skipConnectedTexture(blockAccess, blockState, blockPos, quad, renderEnv))
            {
                quad = getQuad(emptySprite, quad);
                return renderEnv.getArrayQuadsCtm(quad);
            }
            else
            {
                EnumFacing enumfacing = quad.getFace();
                BakedQuad[] abakedquad = getConnectedTextureMultiPass(blockAccess, blockState, blockPos, enumfacing, quad, renderEnv);
                return abakedquad;
            }
        }
    }

    private static boolean skipConnectedTexture(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, BakedQuad quad, RenderEnv renderEnv)
    {
        Block block = blockState.getBlock();

        if (block instanceof BlockPane)
        {
            TextureAtlasSprite textureatlassprite = quad.getSprite();

            if (textureatlassprite.getIconName().startsWith("minecraft:blocks/glass_pane_top"))
            {
                IBlockState iblockstate1 = blockAccess.getBlockState(blockPos.offset(quad.getFace()));
                return iblockstate1 == blockState;
            }
        }

        if (block instanceof BlockPane)
        {
            EnumFacing enumfacing = quad.getFace();

            if (enumfacing != EnumFacing.UP && enumfacing != EnumFacing.DOWN)
            {
                return false;
            }

            if (!quad.isFaceQuad())
            {
                return false;
            }

            BlockPos blockpos = blockPos.offset(quad.getFace());
            IBlockState iblockstate = blockAccess.getBlockState(blockpos);

            if (iblockstate.getBlock() != block)
            {
                return false;
            }

            if (block == Blocks.stained_glass_pane && iblockstate.getValue(BlockStainedGlassPane.COLOR) != blockState.getValue(BlockStainedGlassPane.COLOR))
            {
                return false;
            }

            iblockstate = iblockstate.getBlock().getActualState(iblockstate, blockAccess, blockpos);
            double d0 = quad.getMidX();

            if (d0 < 0.4D)
            {
                return iblockstate.getValue(BlockPane.WEST).booleanValue();
            }
            else if (d0 > 0.6D)
            {
                return iblockstate.getValue(BlockPane.EAST).booleanValue();
            }
            else
            {
                double d1 = quad.getMidZ();

                if (d1 < 0.4D)
                {
                    return iblockstate.getValue(BlockPane.NORTH).booleanValue();
                }
                else
                {
                    if (d1 <= 0.6D)
                    {
                        return true;
                    }

                    return iblockstate.getValue(BlockPane.SOUTH).booleanValue();
                }
            }
        }

        return false;
    }

    protected static BakedQuad[] getQuads(TextureAtlasSprite sprite, BakedQuad quadIn, RenderEnv renderEnv)
    {
        if (sprite == null)
        {
            return null;
        }
        else if (sprite == SPRITE_DEFAULT)
        {
            return renderEnv.getArrayQuadsCtm(quadIn);
        }
        else
        {
            BakedQuad bakedquad = getQuad(sprite, quadIn);
            BakedQuad[] abakedquad = renderEnv.getArrayQuadsCtm(bakedquad);
            return abakedquad;
        }
    }

    private static synchronized BakedQuad getQuad(TextureAtlasSprite sprite, BakedQuad quadIn)
    {
        if (spriteQuadMaps == null)
        {
            return quadIn;
        }
        else
        {
            int i = sprite.getIndexInMap();

            if (i >= 0 && i < spriteQuadMaps.length)
            {
                Map map = spriteQuadMaps[i];

                if (map == null)
                {
                    map = new IdentityHashMap(1);
                    spriteQuadMaps[i] = map;
                }

                BakedQuad bakedquad = (BakedQuad)map.get(quadIn);

                if (bakedquad == null)
                {
                    bakedquad = makeSpriteQuad(quadIn, sprite);
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

    private static synchronized BakedQuad getQuadFull(TextureAtlasSprite sprite, BakedQuad quadIn, int tintIndex)
    {
        if (spriteQuadFullMaps == null)
        {
            return null;
        }
        else if (sprite == null)
        {
            return null;
        }
        else
        {
            int i = sprite.getIndexInMap();

            if (i >= 0 && i < spriteQuadFullMaps.length)
            {
                Map map = spriteQuadFullMaps[i];

                if (map == null)
                {
                    map = new EnumMap(EnumFacing.class);
                    spriteQuadFullMaps[i] = map;
                }

                EnumFacing enumfacing = quadIn.getFace();
                BakedQuad bakedquad = (BakedQuad)map.get(enumfacing);

                if (bakedquad == null)
                {
                    bakedquad = BlockModelUtils.makeBakedQuad(enumfacing, sprite, tintIndex);
                    map.put(enumfacing, bakedquad);
                }

                return bakedquad;
            }
            else
            {
                return null;
            }
        }
    }

    private static BakedQuad makeSpriteQuad(BakedQuad quad, TextureAtlasSprite sprite)
    {
        int[] aint = quad.getVertexData().clone();
        TextureAtlasSprite textureatlassprite = quad.getSprite();

        for (int i = 0; i < 4; ++i)
        {
            fixVertex(aint, i, textureatlassprite, sprite);
        }

        BakedQuad bakedquad = new BakedQuad(aint, quad.getTintIndex(), quad.getFace(), sprite);
        return bakedquad;
    }

    private static void fixVertex(int[] data, int vertex, TextureAtlasSprite spriteFrom, TextureAtlasSprite spriteTo)
    {
        int i = data.length / 4;
        int j = i * vertex;
        float f = Float.intBitsToFloat(data[j + 4]);
        float f1 = Float.intBitsToFloat(data[j + 4 + 1]);
        double d0 = spriteFrom.getSpriteU16(f);
        double d1 = spriteFrom.getSpriteV16(f1);
        data[j + 4] = Float.floatToRawIntBits(spriteTo.getInterpolatedU(d0));
        data[j + 4 + 1] = Float.floatToRawIntBits(spriteTo.getInterpolatedV(d1));
    }

    private static BakedQuad[] getConnectedTextureMultiPass(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing side, BakedQuad quad, RenderEnv renderEnv)
    {
        BakedQuad[] abakedquad = getConnectedTextureSingle(blockAccess, blockState, blockPos, side, quad, true, 0, renderEnv);

        if (!multipass)
        {
            return abakedquad;
        }
        else if (abakedquad.length == 1 && abakedquad[0] == quad)
        {
            return abakedquad;
        }
        else
        {
            List<BakedQuad> list = renderEnv.getListQuadsCtmMultipass(abakedquad);

            for (int i = 0; i < list.size(); ++i)
            {
                BakedQuad bakedquad = list.get(i);
                BakedQuad bakedquad1 = bakedquad;

                for (int j = 0; j < 3; ++j)
                {
                    BakedQuad[] abakedquad1 = getConnectedTextureSingle(blockAccess, blockState, blockPos, side, bakedquad1, false, j + 1, renderEnv);

                    if (abakedquad1.length != 1 || abakedquad1[0] == bakedquad1)
                    {
                        break;
                    }

                    bakedquad1 = abakedquad1[0];
                }

                list.set(i, bakedquad1);
            }

            for (int k = 0; k < abakedquad.length; ++k)
            {
                abakedquad[k] = list.get(k);
            }

            return abakedquad;
        }
    }

    public static BakedQuad[] getConnectedTextureSingle(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, BakedQuad quad, boolean checkBlocks, int pass, RenderEnv renderEnv)
    {
        Block block = blockState.getBlock();

        if (!(blockState instanceof BlockStateBase blockstatebase))
        {
            return renderEnv.getArrayQuadsCtm(quad);
        }
        else
        {
            TextureAtlasSprite textureatlassprite = quad.getSprite();

            if (tileProperties != null)
            {
                int i = textureatlassprite.getIndexInMap();

                if (i >= 0 && i < tileProperties.length)
                {
                    ConnectedProperties[] aconnectedproperties = tileProperties[i];

                    if (aconnectedproperties != null)
                    {
                        int j = getSide(facing);

                        for (int k = 0; k < aconnectedproperties.length; ++k)
                        {
                            ConnectedProperties connectedproperties = aconnectedproperties[k];

                            if (connectedproperties != null && connectedproperties.matchesBlockId(blockstatebase.getBlockId()))
                            {
                                BakedQuad[] abakedquad = getConnectedTexture(connectedproperties, blockAccess, blockstatebase, blockPos, j, quad, pass, renderEnv);

                                if (abakedquad != null)
                                {
                                    return abakedquad;
                                }
                            }
                        }
                    }
                }
            }

            if (blockProperties != null && checkBlocks)
            {
                int l = renderEnv.getBlockId();

                if (l >= 0 && l < blockProperties.length)
                {
                    ConnectedProperties[] aconnectedproperties1 = blockProperties[l];

                    if (aconnectedproperties1 != null)
                    {
                        int i1 = getSide(facing);

                        for (int j1 = 0; j1 < aconnectedproperties1.length; ++j1)
                        {
                            ConnectedProperties connectedproperties1 = aconnectedproperties1[j1];

                            if (connectedproperties1 != null && connectedproperties1.matchesIcon(textureatlassprite))
                            {
                                BakedQuad[] abakedquad1 = getConnectedTexture(connectedproperties1, blockAccess, blockstatebase, blockPos, i1, quad, pass, renderEnv);

                                if (abakedquad1 != null)
                                {
                                    return abakedquad1;
                                }
                            }
                        }
                    }
                }
            }

            return renderEnv.getArrayQuadsCtm(quad);
        }
    }

    public static int getSide(EnumFacing facing)
    {
        if (facing == null)
        {
            return -1;
        }
        else
        {
            switch (facing)
            {
                case DOWN:
                    return 0;

                case UP:
                    return 1;

                case EAST:
                    return 5;

                case WEST:
                    return 4;

                case NORTH:
                    return 2;

                case SOUTH:
                    return 3;

                default:
                    return -1;
            }
        }
    }

    private static EnumFacing getFacing(int side)
    {
        switch (side)
        {
            case 0:
                return EnumFacing.DOWN;

            case 1:
                return EnumFacing.UP;

            case 2:
                return EnumFacing.NORTH;

            case 3:
                return EnumFacing.SOUTH;

            case 4:
                return EnumFacing.WEST;

            case 5:
                return EnumFacing.EAST;

            default:
                return EnumFacing.UP;
        }
    }

    private static BakedQuad[] getConnectedTexture(ConnectedProperties cp, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side, BakedQuad quad, int pass, RenderEnv renderEnv)
    {
        int i = 0;
        int j = blockState.getMetadata();
        int k = j;
        Block block = blockState.getBlock();

        if (block instanceof BlockRotatedPillar)
        {
            i = getWoodAxis(side, j);

            if (cp.getMetadataMax() <= 3)
            {
                k = j & 3;
            }
        }

        if (block instanceof BlockQuartz)
        {
            i = getQuartzAxis(side, j);

            if (cp.getMetadataMax() <= 2 && k > 2)
            {
                k = 2;
            }
        }

        if (!cp.matchesBlock(blockState.getBlockId(), k))
        {
            return null;
        }
        else
        {
            if (side >= 0 && cp.faces != 63)
            {
                int l = side;

                if (i != 0)
                {
                    l = fixSideByAxis(side, i);
                }

                if ((1 << l & cp.faces) == 0)
                {
                    return null;
                }
            }

            int i1 = blockPos.getY();

            if (cp.heights != null && !cp.heights.isInRange(i1))
            {
                return null;
            }
            else
            {
                if (cp.biomes != null)
                {
                    BiomeGenBase biomegenbase = blockAccess.getBiomeGenForCoords(blockPos);

                    if (!cp.matchesBiome(biomegenbase))
                    {
                        return null;
                    }
                }

                if (cp.nbtName != null)
                {
                    String s = TileEntityUtils.getTileEntityName(blockAccess, blockPos);

                    if (!cp.nbtName.matchesValue(s))
                    {
                        return null;
                    }
                }

                TextureAtlasSprite textureatlassprite = quad.getSprite();

                switch (cp.method)
                {
                    case 1:
                        return getQuads(getConnectedTextureCtm(cp, blockAccess, blockState, blockPos, i, side, textureatlassprite, j, renderEnv), quad, renderEnv);

                    case 2:
                        return getQuads(getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, i, side, textureatlassprite, j), quad, renderEnv);

                    case 3:
                        return getQuads(getConnectedTextureTop(cp, blockAccess, blockState, blockPos, i, side, textureatlassprite, j), quad, renderEnv);

                    case 4:
                        return getQuads(getConnectedTextureRandom(cp, blockAccess, blockState, blockPos, side), quad, renderEnv);

                    case 5:
                        return getQuads(getConnectedTextureRepeat(cp, blockPos, side), quad, renderEnv);

                    case 6:
                        return getQuads(getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, i, side, textureatlassprite, j), quad, renderEnv);

                    case 7:
                        return getQuads(getConnectedTextureFixed(cp), quad, renderEnv);

                    case 8:
                        return getQuads(getConnectedTextureHorizontalVertical(cp, blockAccess, blockState, blockPos, i, side, textureatlassprite, j), quad, renderEnv);

                    case 9:
                        return getQuads(getConnectedTextureVerticalHorizontal(cp, blockAccess, blockState, blockPos, i, side, textureatlassprite, j), quad, renderEnv);

                    case 10:
                        if (pass == 0)
                        {
                            return getConnectedTextureCtmCompact(cp, blockAccess, blockState, blockPos, i, side, quad, j, renderEnv);
                        }

                    default:
                        return null;

                    case 11:
                        return getConnectedTextureOverlay(cp, blockAccess, blockState, blockPos, i, side, quad, j, renderEnv);

                    case 12:
                        return getConnectedTextureOverlayFixed(cp, quad, renderEnv);

                    case 13:
                        return getConnectedTextureOverlayRandom(cp, blockAccess, blockState, blockPos, side, quad, renderEnv);

                    case 14:
                        return getConnectedTextureOverlayRepeat(cp, blockPos, side, quad, renderEnv);

                    case 15:
                        return getConnectedTextureOverlayCtm(cp, blockAccess, blockState, blockPos, i, side, quad, j, renderEnv);
                }
            }
        }
    }

    private static int fixSideByAxis(int side, int vertAxis)
    {
        switch (vertAxis)
        {
            case 0:
                return side;

            case 1:
                switch (side)
                {
                    case 0:
                        return 2;

                    case 1:
                        return 3;

                    case 2:
                        return 1;

                    case 3:
                        return 0;

                    default:
                        return side;
                }

            case 2:
                switch (side)
                {
                    case 0:
                        return 4;

                    case 1:
                        return 5;

                    case 2:
                    case 3:
                    default:
                        return side;

                    case 4:
                        return 1;

                    case 5:
                        return 0;
                }

            default:
                return side;
        }
    }

    private static int getWoodAxis(int side, int metadata)
    {
        int i = (metadata & 12) >> 2;

        switch (i)
        {
            case 1:
                return 2;

            case 2:
                return 1;

            default:
                return 0;
        }
    }

    private static int getQuartzAxis(int side, int metadata)
    {
        switch (metadata)
        {
            case 3:
                return 2;

            case 4:
                return 1;

            default:
                return 0;
        }
    }

    private static TextureAtlasSprite getConnectedTextureRandom(ConnectedProperties cp, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side)
    {
        if (cp.tileIcons.length == 1)
        {
            return cp.tileIcons[0];
        }
        else
        {
            int i = side / cp.symmetry * cp.symmetry;

            if (cp.linked)
            {
                BlockPos blockpos = blockPos.down();

                for (IBlockState iblockstate = blockAccess.getBlockState(blockpos); iblockstate.getBlock() == blockState.getBlock(); iblockstate = blockAccess.getBlockState(blockpos))
                {
                    blockPos = blockpos;
                    blockpos = blockpos.down();

                    if (blockpos.getY() < 0)
                    {
                        break;
                    }
                }
            }

            int l = Config.getRandom(blockPos, i) & Integer.MAX_VALUE;

            for (int i1 = 0; i1 < cp.randomLoops; ++i1)
            {
                l = Config.intHash(l);
            }

            int j1 = 0;

            if (cp.weights == null)
            {
                j1 = l % cp.tileIcons.length;
            }
            else
            {
                int j = l % cp.sumAllWeights;
                int[] aint = cp.sumWeights;

                for (int k = 0; k < aint.length; ++k)
                {
                    if (j < aint[k])
                    {
                        j1 = k;
                        break;
                    }
                }
            }

            return cp.tileIcons[j1];
        }
    }

    private static TextureAtlasSprite getConnectedTextureFixed(ConnectedProperties cp)
    {
        return cp.tileIcons[0];
    }

    private static TextureAtlasSprite getConnectedTextureRepeat(ConnectedProperties cp, BlockPos blockPos, int side)
    {
        if (cp.tileIcons.length == 1)
        {
            return cp.tileIcons[0];
        }
        else
        {
            int i = blockPos.getX();
            int j = blockPos.getY();
            int k = blockPos.getZ();
            int l = 0;
            int i1 = 0;

            switch (side)
            {
                case 0:
                    l = i;
                    i1 = -k - 1;
                    break;

                case 1:
                    l = i;
                    i1 = k;
                    break;

                case 2:
                    l = -i - 1;
                    i1 = -j;
                    break;

                case 3:
                    l = i;
                    i1 = -j;
                    break;

                case 4:
                    l = k;
                    i1 = -j;
                    break;

                case 5:
                    l = -k - 1;
                    i1 = -j;
            }

            l = l % cp.width;
            i1 = i1 % cp.height;

            if (l < 0)
            {
                l += cp.width;
            }

            if (i1 < 0)
            {
                i1 += cp.height;
            }

            int j1 = i1 * cp.width + l;
            return cp.tileIcons[j1];
        }
    }

    private static TextureAtlasSprite getConnectedTextureCtm(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata, RenderEnv renderEnv)
    {
        int i = getConnectedTextureCtmIndex(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata, renderEnv);
        return cp.tileIcons[i];
    }

    private static synchronized BakedQuad[] getConnectedTextureCtmCompact(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, BakedQuad quad, int metadata, RenderEnv renderEnv)
    {
        TextureAtlasSprite textureatlassprite = quad.getSprite();
        int i = getConnectedTextureCtmIndex(cp, blockAccess, blockState, blockPos, vertAxis, side, textureatlassprite, metadata, renderEnv);
        return ConnectedTexturesCompact.getConnectedTextureCtmCompact(i, cp, side, quad, renderEnv);
    }

    private static BakedQuad[] getConnectedTextureOverlay(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, BakedQuad quad, int metadata, RenderEnv renderEnv)
    {
        if (!quad.isFullQuad())
        {
            return null;
        }
        else
        {
            TextureAtlasSprite textureatlassprite = quad.getSprite();
            BlockDir[] ablockdir = getSideDirections(side, vertAxis);
            boolean[] aboolean = renderEnv.getBorderFlags();

            for (int i = 0; i < 4; ++i)
            {
                aboolean[i] = isNeighbourOverlay(cp, blockAccess, blockState, ablockdir[i].offset(blockPos), side, textureatlassprite, metadata);
            }

            ListQuadsOverlay listquadsoverlay = renderEnv.getListQuadsOverlay(cp.layer);
            Object dirEdges;

            try
            {
                if (!aboolean[0] || !aboolean[1] || !aboolean[2] || !aboolean[3])
                {
                    if (aboolean[0] && aboolean[1] && aboolean[2])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[5], quad, cp.tintIndex), cp.tintBlockState);
                        dirEdges = null;
                        return (BakedQuad[])dirEdges;
                    }

                    if (aboolean[0] && aboolean[2] && aboolean[3])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[6], quad, cp.tintIndex), cp.tintBlockState);
                        dirEdges = null;
                        return (BakedQuad[])dirEdges;
                    }

                    if (aboolean[1] && aboolean[2] && aboolean[3])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[12], quad, cp.tintIndex), cp.tintBlockState);
                        dirEdges = null;
                        return (BakedQuad[])dirEdges;
                    }

                    if (aboolean[0] && aboolean[1] && aboolean[3])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[13], quad, cp.tintIndex), cp.tintBlockState);
                        dirEdges = null;
                        return (BakedQuad[])dirEdges;
                    }

                    BlockDir[] ablockdir1 = getEdgeDirections(side, vertAxis);
                    boolean[] aboolean1 = renderEnv.getBorderFlags2();

                    for (int j = 0; j < 4; ++j)
                    {
                        aboolean1[j] = isNeighbourOverlay(cp, blockAccess, blockState, ablockdir1[j].offset(blockPos), side, textureatlassprite, metadata);
                    }

                    if (aboolean[1] && aboolean[2])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[3], quad, cp.tintIndex), cp.tintBlockState);

                        if (aboolean1[3])
                        {
                            listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[16], quad, cp.tintIndex), cp.tintBlockState);
                        }

                        Object object4 = null;
                        return (BakedQuad[])object4;
                    }

                    if (aboolean[0] && aboolean[2])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[4], quad, cp.tintIndex), cp.tintBlockState);

                        if (aboolean1[2])
                        {
                            listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[14], quad, cp.tintIndex), cp.tintBlockState);
                        }

                        Object object3 = null;
                        return (BakedQuad[])object3;
                    }

                    if (aboolean[1] && aboolean[3])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[10], quad, cp.tintIndex), cp.tintBlockState);

                        if (aboolean1[1])
                        {
                            listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[2], quad, cp.tintIndex), cp.tintBlockState);
                        }

                        Object object2 = null;
                        return (BakedQuad[])object2;
                    }

                    if (aboolean[0] && aboolean[3])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[11], quad, cp.tintIndex), cp.tintBlockState);

                        if (aboolean1[0])
                        {
                            listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[0], quad, cp.tintIndex), cp.tintBlockState);
                        }

                        Object object1 = null;
                        return (BakedQuad[])object1;
                    }

                    boolean[] aboolean2 = renderEnv.getBorderFlags3();

                    for (int k = 0; k < 4; ++k)
                    {
                        aboolean2[k] = isNeighbourMatching(cp, blockAccess, blockState, ablockdir[k].offset(blockPos), side, textureatlassprite, metadata);
                    }

                    if (aboolean[0])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[9], quad, cp.tintIndex), cp.tintBlockState);
                    }

                    if (aboolean[1])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[7], quad, cp.tintIndex), cp.tintBlockState);
                    }

                    if (aboolean[2])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[1], quad, cp.tintIndex), cp.tintBlockState);
                    }

                    if (aboolean[3])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[15], quad, cp.tintIndex), cp.tintBlockState);
                    }

                    if (aboolean1[0] && (aboolean2[1] || aboolean2[2]) && !aboolean[1] && !aboolean[2])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[0], quad, cp.tintIndex), cp.tintBlockState);
                    }

                    if (aboolean1[1] && (aboolean2[0] || aboolean2[2]) && !aboolean[0] && !aboolean[2])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[2], quad, cp.tintIndex), cp.tintBlockState);
                    }

                    if (aboolean1[2] && (aboolean2[1] || aboolean2[3]) && !aboolean[1] && !aboolean[3])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[14], quad, cp.tintIndex), cp.tintBlockState);
                    }

                    if (aboolean1[3] && (aboolean2[0] || aboolean2[3]) && !aboolean[0] && !aboolean[3])
                    {
                        listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[16], quad, cp.tintIndex), cp.tintBlockState);
                    }

                    Object object5 = null;
                    return (BakedQuad[])object5;
                }

                listquadsoverlay.addQuad(getQuadFull(cp.tileIcons[8], quad, cp.tintIndex), cp.tintBlockState);
                dirEdges = null;
            }
            finally
            {
                if (listquadsoverlay.size() > 0)
                {
                    renderEnv.setOverlaysRendered(true);
                }
            }

            return (BakedQuad[])dirEdges;
        }
    }

    private static BakedQuad[] getConnectedTextureOverlayFixed(ConnectedProperties cp, BakedQuad quad, RenderEnv renderEnv)
    {
        if (!quad.isFullQuad())
        {
            return null;
        }
        else
        {
            ListQuadsOverlay listquadsoverlay = renderEnv.getListQuadsOverlay(cp.layer);
            Object object;

            try
            {
                TextureAtlasSprite textureatlassprite = getConnectedTextureFixed(cp);

                if (textureatlassprite != null)
                {
                    listquadsoverlay.addQuad(getQuadFull(textureatlassprite, quad, cp.tintIndex), cp.tintBlockState);
                }

                object = null;
            }
            finally
            {
                if (listquadsoverlay.size() > 0)
                {
                    renderEnv.setOverlaysRendered(true);
                }
            }

            return (BakedQuad[])object;
        }
    }

    private static BakedQuad[] getConnectedTextureOverlayRandom(ConnectedProperties cp, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side, BakedQuad quad, RenderEnv renderEnv)
    {
        if (!quad.isFullQuad())
        {
            return null;
        }
        else
        {
            ListQuadsOverlay listquadsoverlay = renderEnv.getListQuadsOverlay(cp.layer);
            Object object;

            try
            {
                TextureAtlasSprite textureatlassprite = getConnectedTextureRandom(cp, blockAccess, blockState, blockPos, side);

                if (textureatlassprite != null)
                {
                    listquadsoverlay.addQuad(getQuadFull(textureatlassprite, quad, cp.tintIndex), cp.tintBlockState);
                }

                object = null;
            }
            finally
            {
                if (listquadsoverlay.size() > 0)
                {
                    renderEnv.setOverlaysRendered(true);
                }
            }

            return (BakedQuad[])object;
        }
    }

    private static BakedQuad[] getConnectedTextureOverlayRepeat(ConnectedProperties cp, BlockPos blockPos, int side, BakedQuad quad, RenderEnv renderEnv)
    {
        if (!quad.isFullQuad())
        {
            return null;
        }
        else
        {
            ListQuadsOverlay listquadsoverlay = renderEnv.getListQuadsOverlay(cp.layer);
            Object object;

            try
            {
                TextureAtlasSprite textureatlassprite = getConnectedTextureRepeat(cp, blockPos, side);

                if (textureatlassprite != null)
                {
                    listquadsoverlay.addQuad(getQuadFull(textureatlassprite, quad, cp.tintIndex), cp.tintBlockState);
                }

                object = null;
            }
            finally
            {
                if (listquadsoverlay.size() > 0)
                {
                    renderEnv.setOverlaysRendered(true);
                }
            }

            return (BakedQuad[])object;
        }
    }

    private static BakedQuad[] getConnectedTextureOverlayCtm(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, BakedQuad quad, int metadata, RenderEnv renderEnv)
    {
        if (!quad.isFullQuad())
        {
            return null;
        }
        else
        {
            ListQuadsOverlay listquadsoverlay = renderEnv.getListQuadsOverlay(cp.layer);
            Object object;

            try
            {
                TextureAtlasSprite textureatlassprite = getConnectedTextureCtm(cp, blockAccess, blockState, blockPos, vertAxis, side, quad.getSprite(), metadata, renderEnv);

                if (textureatlassprite != null)
                {
                    listquadsoverlay.addQuad(getQuadFull(textureatlassprite, quad, cp.tintIndex), cp.tintBlockState);
                }

                object = null;
            }
            finally
            {
                if (listquadsoverlay.size() > 0)
                {
                    renderEnv.setOverlaysRendered(true);
                }
            }

            return (BakedQuad[])object;
        }
    }

    private static BlockDir[] getSideDirections(int side, int vertAxis)
    {
        switch (side)
        {
            case 0:
                return SIDES_Y_NEG_DOWN;

            case 1:
                return SIDES_Y_POS_UP;

            case 2:
                if (vertAxis == 1)
                {
                    return SIDES_Z_NEG_NORTH_Z_AXIS;
                }

                return SIDES_Z_NEG_NORTH;

            case 3:
                return SIDES_Z_POS_SOUTH;

            case 4:
                return SIDES_X_NEG_WEST;

            case 5:
                if (vertAxis == 2)
                {
                    return SIDES_X_POS_EAST_X_AXIS;
                }

                return SIDES_X_POS_EAST;

            default:
                throw new IllegalArgumentException("Unknown side: " + side);
        }
    }

    private static BlockDir[] getEdgeDirections(int side, int vertAxis)
    {
        switch (side)
        {
            case 0:
                return EDGES_Y_NEG_DOWN;

            case 1:
                return EDGES_Y_POS_UP;

            case 2:
                if (vertAxis == 1)
                {
                    return EDGES_Z_NEG_NORTH_Z_AXIS;
                }

                return EDGES_Z_NEG_NORTH;

            case 3:
                return EDGES_Z_POS_SOUTH;

            case 4:
                return EDGES_X_NEG_WEST;

            case 5:
                if (vertAxis == 2)
                {
                    return EDGES_X_POS_EAST_X_AXIS;
                }

                return EDGES_X_POS_EAST;

            default:
                throw new IllegalArgumentException("Unknown side: " + side);
        }
    }

    protected static Map[][] getSpriteQuadCompactMaps()
    {
        return spriteQuadCompactMaps;
    }

    private static int getConnectedTextureCtmIndex(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata, RenderEnv renderEnv)
    {
        boolean[] aboolean = renderEnv.getBorderFlags();

        switch (side)
        {
            case 0:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);

                if (cp.innerSeams)
                {
                    BlockPos blockpos6 = blockPos.down();
                    aboolean[0] = aboolean[0] && !isNeighbour(cp, blockAccess, blockState, blockpos6.west(), side, icon, metadata);
                    aboolean[1] = aboolean[1] && !isNeighbour(cp, blockAccess, blockState, blockpos6.east(), side, icon, metadata);
                    aboolean[2] = aboolean[2] && !isNeighbour(cp, blockAccess, blockState, blockpos6.north(), side, icon, metadata);
                    aboolean[3] = aboolean[3] && !isNeighbour(cp, blockAccess, blockState, blockpos6.south(), side, icon, metadata);
                }

                break;

            case 1:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);

                if (cp.innerSeams)
                {
                    BlockPos blockpos5 = blockPos.up();
                    aboolean[0] = aboolean[0] && !isNeighbour(cp, blockAccess, blockState, blockpos5.west(), side, icon, metadata);
                    aboolean[1] = aboolean[1] && !isNeighbour(cp, blockAccess, blockState, blockpos5.east(), side, icon, metadata);
                    aboolean[2] = aboolean[2] && !isNeighbour(cp, blockAccess, blockState, blockpos5.south(), side, icon, metadata);
                    aboolean[3] = aboolean[3] && !isNeighbour(cp, blockAccess, blockState, blockpos5.north(), side, icon, metadata);
                }

                break;

            case 2:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);

                if (cp.innerSeams)
                {
                    BlockPos blockpos4 = blockPos.north();
                    aboolean[0] = aboolean[0] && !isNeighbour(cp, blockAccess, blockState, blockpos4.east(), side, icon, metadata);
                    aboolean[1] = aboolean[1] && !isNeighbour(cp, blockAccess, blockState, blockpos4.west(), side, icon, metadata);
                    aboolean[2] = aboolean[2] && !isNeighbour(cp, blockAccess, blockState, blockpos4.down(), side, icon, metadata);
                    aboolean[3] = aboolean[3] && !isNeighbour(cp, blockAccess, blockState, blockpos4.up(), side, icon, metadata);
                }

                if (vertAxis == 1)
                {
                    switchValues(0, 1, aboolean);
                    switchValues(2, 3, aboolean);
                }

                break;

            case 3:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);

                if (cp.innerSeams)
                {
                    BlockPos blockpos3 = blockPos.south();
                    aboolean[0] = aboolean[0] && !isNeighbour(cp, blockAccess, blockState, blockpos3.west(), side, icon, metadata);
                    aboolean[1] = aboolean[1] && !isNeighbour(cp, blockAccess, blockState, blockpos3.east(), side, icon, metadata);
                    aboolean[2] = aboolean[2] && !isNeighbour(cp, blockAccess, blockState, blockpos3.down(), side, icon, metadata);
                    aboolean[3] = aboolean[3] && !isNeighbour(cp, blockAccess, blockState, blockpos3.up(), side, icon, metadata);
                }

                break;

            case 4:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);

                if (cp.innerSeams)
                {
                    BlockPos blockpos2 = blockPos.west();
                    aboolean[0] = aboolean[0] && !isNeighbour(cp, blockAccess, blockState, blockpos2.north(), side, icon, metadata);
                    aboolean[1] = aboolean[1] && !isNeighbour(cp, blockAccess, blockState, blockpos2.south(), side, icon, metadata);
                    aboolean[2] = aboolean[2] && !isNeighbour(cp, blockAccess, blockState, blockpos2.down(), side, icon, metadata);
                    aboolean[3] = aboolean[3] && !isNeighbour(cp, blockAccess, blockState, blockpos2.up(), side, icon, metadata);
                }

                break;

            case 5:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);

                if (cp.innerSeams)
                {
                    BlockPos blockpos = blockPos.east();
                    aboolean[0] = aboolean[0] && !isNeighbour(cp, blockAccess, blockState, blockpos.south(), side, icon, metadata);
                    aboolean[1] = aboolean[1] && !isNeighbour(cp, blockAccess, blockState, blockpos.north(), side, icon, metadata);
                    aboolean[2] = aboolean[2] && !isNeighbour(cp, blockAccess, blockState, blockpos.down(), side, icon, metadata);
                    aboolean[3] = aboolean[3] && !isNeighbour(cp, blockAccess, blockState, blockpos.up(), side, icon, metadata);
                }

                if (vertAxis == 2)
                {
                    switchValues(0, 1, aboolean);
                    switchValues(2, 3, aboolean);
                }
        }

        int i = 0;

        if (aboolean[0] & !aboolean[1] & !aboolean[2] & !aboolean[3])
        {
            i = 3;
        }
        else if (!aboolean[0] & aboolean[1] & !aboolean[2] & !aboolean[3])
        {
            i = 1;
        }
        else if (!aboolean[0] & !aboolean[1] & aboolean[2] & !aboolean[3])
        {
            i = 12;
        }
        else if (!aboolean[0] & !aboolean[1] & !aboolean[2] & aboolean[3])
        {
            i = 36;
        }
        else if (aboolean[0] & aboolean[1] & !aboolean[2] & !aboolean[3])
        {
            i = 2;
        }
        else if (!aboolean[0] & !aboolean[1] & aboolean[2] & aboolean[3])
        {
            i = 24;
        }
        else if (aboolean[0] & !aboolean[1] & aboolean[2] & !aboolean[3])
        {
            i = 15;
        }
        else if (aboolean[0] & !aboolean[1] & !aboolean[2] & aboolean[3])
        {
            i = 39;
        }
        else if (!aboolean[0] & aboolean[1] & aboolean[2] & !aboolean[3])
        {
            i = 13;
        }
        else if (!aboolean[0] & aboolean[1] & !aboolean[2] & aboolean[3])
        {
            i = 37;
        }
        else if (!aboolean[0] & aboolean[1] & aboolean[2] & aboolean[3])
        {
            i = 25;
        }
        else if (aboolean[0] & !aboolean[1] & aboolean[2] & aboolean[3])
        {
            i = 27;
        }
        else if (aboolean[0] & aboolean[1] & !aboolean[2] & aboolean[3])
        {
            i = 38;
        }
        else if (aboolean[0] & aboolean[1] & aboolean[2] & !aboolean[3])
        {
            i = 14;
        }
        else if (aboolean[0] & aboolean[1] & aboolean[2] & aboolean[3])
        {
            i = 26;
        }

        if (i == 0)
        {
            return i;
        }
        else if (!Config.isConnectedTexturesFancy())
        {
            return i;
        }
        else
        {
            switch (side)
            {
                case 0:
                    aboolean[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().north(), side, icon, metadata);
                    aboolean[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().north(), side, icon, metadata);
                    aboolean[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().south(), side, icon, metadata);
                    aboolean[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().south(), side, icon, metadata);

                    if (cp.innerSeams)
                    {
                        BlockPos blockpos11 = blockPos.down();
                        aboolean[0] = aboolean[0] || isNeighbour(cp, blockAccess, blockState, blockpos11.east().north(), side, icon, metadata);
                        aboolean[1] = aboolean[1] || isNeighbour(cp, blockAccess, blockState, blockpos11.west().north(), side, icon, metadata);
                        aboolean[2] = aboolean[2] || isNeighbour(cp, blockAccess, blockState, blockpos11.east().south(), side, icon, metadata);
                        aboolean[3] = aboolean[3] || isNeighbour(cp, blockAccess, blockState, blockpos11.west().south(), side, icon, metadata);
                    }

                    break;

                case 1:
                    aboolean[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().south(), side, icon, metadata);
                    aboolean[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().south(), side, icon, metadata);
                    aboolean[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().north(), side, icon, metadata);
                    aboolean[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().north(), side, icon, metadata);

                    if (cp.innerSeams)
                    {
                        BlockPos blockpos10 = blockPos.up();
                        aboolean[0] = aboolean[0] || isNeighbour(cp, blockAccess, blockState, blockpos10.east().south(), side, icon, metadata);
                        aboolean[1] = aboolean[1] || isNeighbour(cp, blockAccess, blockState, blockpos10.west().south(), side, icon, metadata);
                        aboolean[2] = aboolean[2] || isNeighbour(cp, blockAccess, blockState, blockpos10.east().north(), side, icon, metadata);
                        aboolean[3] = aboolean[3] || isNeighbour(cp, blockAccess, blockState, blockpos10.west().north(), side, icon, metadata);
                    }

                    break;

                case 2:
                    aboolean[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().down(), side, icon, metadata);
                    aboolean[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().down(), side, icon, metadata);
                    aboolean[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().up(), side, icon, metadata);
                    aboolean[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().up(), side, icon, metadata);

                    if (cp.innerSeams)
                    {
                        BlockPos blockpos9 = blockPos.north();
                        aboolean[0] = aboolean[0] || isNeighbour(cp, blockAccess, blockState, blockpos9.west().down(), side, icon, metadata);
                        aboolean[1] = aboolean[1] || isNeighbour(cp, blockAccess, blockState, blockpos9.east().down(), side, icon, metadata);
                        aboolean[2] = aboolean[2] || isNeighbour(cp, blockAccess, blockState, blockpos9.west().up(), side, icon, metadata);
                        aboolean[3] = aboolean[3] || isNeighbour(cp, blockAccess, blockState, blockpos9.east().up(), side, icon, metadata);
                    }

                    if (vertAxis == 1)
                    {
                        switchValues(0, 3, aboolean);
                        switchValues(1, 2, aboolean);
                    }

                    break;

                case 3:
                    aboolean[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().down(), side, icon, metadata);
                    aboolean[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().down(), side, icon, metadata);
                    aboolean[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().up(), side, icon, metadata);
                    aboolean[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().up(), side, icon, metadata);

                    if (cp.innerSeams)
                    {
                        BlockPos blockpos8 = blockPos.south();
                        aboolean[0] = aboolean[0] || isNeighbour(cp, blockAccess, blockState, blockpos8.east().down(), side, icon, metadata);
                        aboolean[1] = aboolean[1] || isNeighbour(cp, blockAccess, blockState, blockpos8.west().down(), side, icon, metadata);
                        aboolean[2] = aboolean[2] || isNeighbour(cp, blockAccess, blockState, blockpos8.east().up(), side, icon, metadata);
                        aboolean[3] = aboolean[3] || isNeighbour(cp, blockAccess, blockState, blockpos8.west().up(), side, icon, metadata);
                    }

                    break;

                case 4:
                    aboolean[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().south(), side, icon, metadata);
                    aboolean[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().north(), side, icon, metadata);
                    aboolean[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().south(), side, icon, metadata);
                    aboolean[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().north(), side, icon, metadata);

                    if (cp.innerSeams)
                    {
                        BlockPos blockpos7 = blockPos.west();
                        aboolean[0] = aboolean[0] || isNeighbour(cp, blockAccess, blockState, blockpos7.down().south(), side, icon, metadata);
                        aboolean[1] = aboolean[1] || isNeighbour(cp, blockAccess, blockState, blockpos7.down().north(), side, icon, metadata);
                        aboolean[2] = aboolean[2] || isNeighbour(cp, blockAccess, blockState, blockpos7.up().south(), side, icon, metadata);
                        aboolean[3] = aboolean[3] || isNeighbour(cp, blockAccess, blockState, blockpos7.up().north(), side, icon, metadata);
                    }

                    break;

                case 5:
                    aboolean[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().north(), side, icon, metadata);
                    aboolean[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().south(), side, icon, metadata);
                    aboolean[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().north(), side, icon, metadata);
                    aboolean[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().south(), side, icon, metadata);

                    if (cp.innerSeams)
                    {
                        BlockPos blockpos1 = blockPos.east();
                        aboolean[0] = aboolean[0] || isNeighbour(cp, blockAccess, blockState, blockpos1.down().north(), side, icon, metadata);
                        aboolean[1] = aboolean[1] || isNeighbour(cp, blockAccess, blockState, blockpos1.down().south(), side, icon, metadata);
                        aboolean[2] = aboolean[2] || isNeighbour(cp, blockAccess, blockState, blockpos1.up().north(), side, icon, metadata);
                        aboolean[3] = aboolean[3] || isNeighbour(cp, blockAccess, blockState, blockpos1.up().south(), side, icon, metadata);
                    }

                    if (vertAxis == 2)
                    {
                        switchValues(0, 3, aboolean);
                        switchValues(1, 2, aboolean);
                    }
            }

            if (i == 13 && aboolean[0])
            {
                i = 4;
            }
            else if (i == 15 && aboolean[1])
            {
                i = 5;
            }
            else if (i == 37 && aboolean[2])
            {
                i = 16;
            }
            else if (i == 39 && aboolean[3])
            {
                i = 17;
            }
            else if (i == 14 && aboolean[0] && aboolean[1])
            {
                i = 7;
            }
            else if (i == 25 && aboolean[0] && aboolean[2])
            {
                i = 6;
            }
            else if (i == 27 && aboolean[3] && aboolean[1])
            {
                i = 19;
            }
            else if (i == 38 && aboolean[3] && aboolean[2])
            {
                i = 18;
            }
            else if (i == 14 && !aboolean[0] && aboolean[1])
            {
                i = 31;
            }
            else if (i == 25 && aboolean[0] && !aboolean[2])
            {
                i = 30;
            }
            else if (i == 27 && !aboolean[3] && aboolean[1])
            {
                i = 41;
            }
            else if (i == 38 && aboolean[3] && !aboolean[2])
            {
                i = 40;
            }
            else if (i == 14 && aboolean[0] && !aboolean[1])
            {
                i = 29;
            }
            else if (i == 25 && !aboolean[0] && aboolean[2])
            {
                i = 28;
            }
            else if (i == 27 && aboolean[3] && !aboolean[1])
            {
                i = 43;
            }
            else if (i == 38 && !aboolean[3] && aboolean[2])
            {
                i = 42;
            }
            else if (i == 26 && aboolean[0] && aboolean[1] && aboolean[2] && aboolean[3])
            {
                i = 46;
            }
            else if (i == 26 && !aboolean[0] && aboolean[1] && aboolean[2] && aboolean[3])
            {
                i = 9;
            }
            else if (i == 26 && aboolean[0] && !aboolean[1] && aboolean[2] && aboolean[3])
            {
                i = 21;
            }
            else if (i == 26 && aboolean[0] && aboolean[1] && !aboolean[2] && aboolean[3])
            {
                i = 8;
            }
            else if (i == 26 && aboolean[0] && aboolean[1] && aboolean[2] && !aboolean[3])
            {
                i = 20;
            }
            else if (i == 26 && aboolean[0] && aboolean[1] && !aboolean[2] && !aboolean[3])
            {
                i = 11;
            }
            else if (i == 26 && !aboolean[0] && !aboolean[1] && aboolean[2] && aboolean[3])
            {
                i = 22;
            }
            else if (i == 26 && !aboolean[0] && aboolean[1] && !aboolean[2] && aboolean[3])
            {
                i = 23;
            }
            else if (i == 26 && aboolean[0] && !aboolean[1] && aboolean[2] && !aboolean[3])
            {
                i = 10;
            }
            else if (i == 26 && aboolean[0] && !aboolean[1] && !aboolean[2] && aboolean[3])
            {
                i = 34;
            }
            else if (i == 26 && !aboolean[0] && aboolean[1] && aboolean[2] && !aboolean[3])
            {
                i = 35;
            }
            else if (i == 26 && aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3])
            {
                i = 32;
            }
            else if (i == 26 && !aboolean[0] && aboolean[1] && !aboolean[2] && !aboolean[3])
            {
                i = 33;
            }
            else if (i == 26 && !aboolean[0] && !aboolean[1] && aboolean[2] && !aboolean[3])
            {
                i = 44;
            }
            else if (i == 26 && !aboolean[0] && !aboolean[1] && !aboolean[2] && aboolean[3])
            {
                i = 45;
            }

            return i;
        }
    }

    private static void switchValues(int ix1, int ix2, boolean[] arr)
    {
        boolean flag = arr[ix1];
        arr[ix1] = arr[ix2];
        arr[ix2] = flag;
    }

    private static boolean isNeighbourOverlay(ConnectedProperties cp, IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, int metadata)
    {
        IBlockState iblockstate = iblockaccess.getBlockState(blockPos);

        if (!isFullCubeModel(iblockstate))
        {
            return false;
        }
        else
        {
            if (cp.connectBlocks != null)
            {
                BlockStateBase blockstatebase = (BlockStateBase)iblockstate;

                if (!Matches.block(blockstatebase.getBlockId(), blockstatebase.getMetadata(), cp.connectBlocks))
                {
                    return false;
                }
            }

            if (cp.connectTileIcons != null)
            {
                TextureAtlasSprite textureatlassprite = getNeighbourIcon(iblockaccess, blockState, blockPos, iblockstate, side);

                if (!Config.isSameOne(textureatlassprite, cp.connectTileIcons))
                {
                    return false;
                }
            }

            IBlockState iblockstate1 = iblockaccess.getBlockState(blockPos.offset(getFacing(side)));
            return !iblockstate1.getBlock().isOpaqueCube() && ((side != 1 || iblockstate1.getBlock() != Blocks.snow_layer) && !isNeighbour(cp, iblockaccess, blockState, blockPos, iblockstate, side, icon, metadata));
        }
    }

    private static boolean isFullCubeModel(IBlockState state)
    {
        if (state.getBlock().isFullCube())
        {
            return true;
        }
        else
        {
            Block block = state.getBlock();
            return block instanceof BlockGlass || block instanceof BlockStainedGlass;
        }
    }

    private static boolean isNeighbourMatching(ConnectedProperties cp, IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, int metadata)
    {
        IBlockState iblockstate = iblockaccess.getBlockState(blockPos);

        if (iblockstate == AIR_DEFAULT_STATE)
        {
            return false;
        }
        else
        {
            if (cp.matchBlocks != null && iblockstate instanceof BlockStateBase blockstatebase)
            {

                if (!cp.matchesBlock(blockstatebase.getBlockId(), blockstatebase.getMetadata()))
                {
                    return false;
                }
            }

            if (cp.matchTileIcons != null)
            {
                TextureAtlasSprite textureatlassprite = getNeighbourIcon(iblockaccess, blockState, blockPos, iblockstate, side);

                if (textureatlassprite != icon)
                {
                    return false;
                }
            }

            IBlockState iblockstate1 = iblockaccess.getBlockState(blockPos.offset(getFacing(side)));
            return !iblockstate1.getBlock().isOpaqueCube() && (side != 1 || iblockstate1.getBlock() != Blocks.snow_layer);
        }
    }

    private static boolean isNeighbour(ConnectedProperties cp, IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, int metadata)
    {
        IBlockState iblockstate = iblockaccess.getBlockState(blockPos);
        return isNeighbour(cp, iblockaccess, blockState, blockPos, iblockstate, side, icon, metadata);
    }

    private static boolean isNeighbour(ConnectedProperties cp, IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, IBlockState neighbourState, int side, TextureAtlasSprite icon, int metadata)
    {
        if (blockState == neighbourState)
        {
            return true;
        }
        else if (cp.connect == 2)
        {
            if (neighbourState == null)
            {
                return false;
            }
            else if (neighbourState == AIR_DEFAULT_STATE)
            {
                return false;
            }
            else
            {
                TextureAtlasSprite textureatlassprite = getNeighbourIcon(iblockaccess, blockState, blockPos, neighbourState, side);
                return textureatlassprite == icon;
            }
        }
        else if (cp.connect == 3)
        {
            return neighbourState != null && (neighbourState != AIR_DEFAULT_STATE && neighbourState.getBlock().getMaterial() == blockState.getBlock().getMaterial());
        }
        else if (!(neighbourState instanceof BlockStateBase blockstatebase))
        {
            return false;
        }
        else
        {
            Block block = blockstatebase.getBlock();
            int i = blockstatebase.getMetadata();
            return block == blockState.getBlock() && i == metadata;
        }
    }

    private static TextureAtlasSprite getNeighbourIcon(IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, IBlockState neighbourState, int side)
    {
        neighbourState = neighbourState.getBlock().getActualState(neighbourState, iblockaccess, blockPos);
        IBakedModel ibakedmodel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(neighbourState);

        if (ibakedmodel == null)
        {
            return null;
        }
        else
        {
            if (Reflector.ForgeBlock_getExtendedState.exists())
            {
                neighbourState = (IBlockState)Reflector.call(neighbourState.getBlock(), Reflector.ForgeBlock_getExtendedState, new Object[] {neighbourState, iblockaccess, blockPos});
            }

            EnumFacing enumfacing = getFacing(side);
            List list = ibakedmodel.getFaceQuads(enumfacing);

            if (list == null)
            {
                return null;
            }
            else
            {
                if (Config.isBetterGrass())
                {
                    list = BetterGrass.getFaceQuads(iblockaccess, neighbourState, blockPos, enumfacing, list);
                }

                if (list.size() > 0)
                {
                    BakedQuad bakedquad1 = (BakedQuad)list.get(0);
                    return bakedquad1.getSprite();
                }
                else
                {
                    List list1 = ibakedmodel.getGeneralQuads();

                    if (list1 == null)
                    {
                        return null;
                    }
                    else
                    {
                        for (int i = 0; i < list1.size(); ++i)
                        {
                            BakedQuad bakedquad = (BakedQuad)list1.get(i);

                            if (bakedquad.getFace() == enumfacing)
                            {
                                return bakedquad.getSprite();
                            }
                        }

                        return null;
                    }
                }
            }
        }
    }

    private static TextureAtlasSprite getConnectedTextureHorizontal(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata)
    {
        boolean flag;
        boolean flag1;
        flag = false;
        flag1 = false;
        label0:

        switch (vertAxis)
        {
            case 0:
                switch (side)
                {
                    case 0:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break label0;

                    case 1:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break label0;

                    case 2:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        break label0;

                    case 3:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break label0;

                    case 4:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        break label0;

                    case 5:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);

                    default:
                        break label0;
                }

            case 1:
                switch (side)
                {
                    case 0:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        break label0;

                    case 1:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break label0;

                    case 2:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break label0;

                    case 3:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break label0;

                    case 4:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        break label0;

                    case 5:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);

                    default:
                        break label0;
                }

            case 2:
                switch (side)
                {
                    case 0:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        break;

                    case 1:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        break;

                    case 2:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        break;

                    case 3:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                        break;

                    case 4:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        break;

                    case 5:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                }
        }

        int i = 3;

        if (flag)
        {
            if (flag1)
            {
                i = 1;
            }
            else
            {
                i = 2;
            }
        }
        else if (flag1)
        {
            i = 0;
        }
        else
        {
            i = 3;
        }

        return cp.tileIcons[i];
    }

    private static TextureAtlasSprite getConnectedTextureVertical(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata)
    {
        boolean flag = false;
        boolean flag1 = false;

        switch (vertAxis)
        {
            case 0:
                if (side == 1)
                {
                    flag = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                    flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                }
                else if (side == 0)
                {
                    flag = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                    flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                }
                else
                {
                    flag = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                    flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                }

                break;

            case 1:
                if (side == 3)
                {
                    flag = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                    flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                }
                else if (side == 2)
                {
                    flag = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                    flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                }
                else
                {
                    flag = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                    flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                }

                break;

            case 2:
                if (side == 5)
                {
                    flag = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                    flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                }
                else if (side == 4)
                {
                    flag = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                    flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                }
                else
                {
                    flag = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                    flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                }
        }

        int i = 3;

        if (flag)
        {
            if (flag1)
            {
                i = 1;
            }
            else
            {
                i = 2;
            }
        }
        else if (flag1)
        {
            i = 0;
        }
        else
        {
            i = 3;
        }

        return cp.tileIcons[i];
    }

    private static TextureAtlasSprite getConnectedTextureHorizontalVertical(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata)
    {
        TextureAtlasSprite[] atextureatlassprite = cp.tileIcons;
        TextureAtlasSprite textureatlassprite = getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);

        if (textureatlassprite != null && textureatlassprite != icon && textureatlassprite != atextureatlassprite[3])
        {
            return textureatlassprite;
        }
        else
        {
            TextureAtlasSprite textureatlassprite1 = getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
            return textureatlassprite1 == atextureatlassprite[0] ? atextureatlassprite[4] : (textureatlassprite1 == atextureatlassprite[1] ? atextureatlassprite[5] : (textureatlassprite1 == atextureatlassprite[2] ? atextureatlassprite[6] : textureatlassprite1));
        }
    }

    private static TextureAtlasSprite getConnectedTextureVerticalHorizontal(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata)
    {
        TextureAtlasSprite[] atextureatlassprite = cp.tileIcons;
        TextureAtlasSprite textureatlassprite = getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);

        if (textureatlassprite != null && textureatlassprite != icon && textureatlassprite != atextureatlassprite[3])
        {
            return textureatlassprite;
        }
        else
        {
            TextureAtlasSprite textureatlassprite1 = getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
            return textureatlassprite1 == atextureatlassprite[0] ? atextureatlassprite[4] : (textureatlassprite1 == atextureatlassprite[1] ? atextureatlassprite[5] : (textureatlassprite1 == atextureatlassprite[2] ? atextureatlassprite[6] : textureatlassprite1));
        }
    }

    private static TextureAtlasSprite getConnectedTextureTop(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata)
    {
        boolean flag = false;

        switch (vertAxis)
        {
            case 0:
                if (side == 1 || side == 0)
                {
                    return null;
                }

                flag = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                break;

            case 1:
                if (side == 3 || side == 2)
                {
                    return null;
                }

                flag = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                break;

            case 2:
                if (side == 5 || side == 4)
                {
                    return null;
                }

                flag = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
        }

        if (flag)
        {
            return cp.tileIcons[0];
        }
        else
        {
            return null;
        }
    }

    public static void updateIcons(TextureMap textureMap)
    {
        blockProperties = null;
        tileProperties = null;
        spriteQuadMaps = null;
        spriteQuadCompactMaps = null;

        if (Config.isConnectedTextures())
        {
            IResourcePack[] airesourcepack = Config.getResourcePacks();

            for (int i = airesourcepack.length - 1; i >= 0; --i)
            {
                IResourcePack iresourcepack = airesourcepack[i];
                updateIcons(textureMap, iresourcepack);
            }

            updateIcons(textureMap, Config.getDefaultResourcePack());
            ResourceLocation resourcelocation = new ResourceLocation("mcpatcher/ctm/default/empty");
            emptySprite = textureMap.registerSprite(resourcelocation);
            spriteQuadMaps = new Map[textureMap.getCountRegisteredSprites() + 1];
            spriteQuadFullMaps = new Map[textureMap.getCountRegisteredSprites() + 1];
            spriteQuadCompactMaps = new Map[textureMap.getCountRegisteredSprites() + 1][];

            if (blockProperties.length <= 0)
            {
                blockProperties = null;
            }

            if (tileProperties.length <= 0)
            {
                tileProperties = null;
            }
        }
    }

    private static void updateIconEmpty(TextureMap textureMap)
    {
    }

    public static void updateIcons(TextureMap textureMap, IResourcePack rp)
    {
        String[] astring = ResUtils.collectFiles(rp, "mcpatcher/ctm/", ".properties", getDefaultCtmPaths());
        Arrays.sort(astring);
        List list = makePropertyList(tileProperties);
        List list1 = makePropertyList(blockProperties);

        for (int i = 0; i < astring.length; ++i)
        {
            String s = astring[i];
            Config.dbg("ConnectedTextures: " + s);

            try
            {
                ResourceLocation resourcelocation = new ResourceLocation(s);
                InputStream inputstream = rp.getInputStream(resourcelocation);

                if (inputstream == null)
                {
                    Config.warn("ConnectedTextures file not found: " + s);
                }
                else
                {
                    Properties properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    inputstream.close();
                    ConnectedProperties connectedproperties = new ConnectedProperties(properties, s);

                    if (connectedproperties.isValid(s))
                    {
                        connectedproperties.updateIcons(textureMap);
                        addToTileList(connectedproperties, list);
                        addToBlockList(connectedproperties, list1);
                    }
                }
            }
            catch (FileNotFoundException var11)
            {
                Config.warn("ConnectedTextures file not found: " + s);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }

        blockProperties = propertyListToArray(list1);
        tileProperties = propertyListToArray(list);
        multipass = detectMultipass();
        Config.dbg("Multipass connected textures: " + multipass);
    }

    private static List makePropertyList(ConnectedProperties[][] propsArr)
    {
        List list = new ArrayList();

        if (propsArr != null)
        {
            for (int i = 0; i < propsArr.length; ++i)
            {
                ConnectedProperties[] aconnectedproperties = propsArr[i];
                List list1 = null;

                if (aconnectedproperties != null)
                {
                    list1 = new ArrayList(Arrays.asList(aconnectedproperties));
                }

                list.add(list1);
            }
        }

        return list;
    }

    private static boolean detectMultipass()
    {
        List list = new ArrayList();

        for (int i = 0; i < tileProperties.length; ++i)
        {
            ConnectedProperties[] aconnectedproperties = tileProperties[i];

            if (aconnectedproperties != null)
            {
                list.addAll(Arrays.asList(aconnectedproperties));
            }
        }

        for (int k = 0; k < blockProperties.length; ++k)
        {
            ConnectedProperties[] aconnectedproperties2 = blockProperties[k];

            if (aconnectedproperties2 != null)
            {
                list.addAll(Arrays.asList(aconnectedproperties2));
            }
        }

        ConnectedProperties[] aconnectedproperties1 = (ConnectedProperties[]) list.toArray(new ConnectedProperties[list.size()]);
        Set set1 = new HashSet();
        Set set = new HashSet();

        for (int j = 0; j < aconnectedproperties1.length; ++j)
        {
            ConnectedProperties connectedproperties = aconnectedproperties1[j];

            if (connectedproperties.matchTileIcons != null)
            {
                set1.addAll(Arrays.asList(connectedproperties.matchTileIcons));
            }

            if (connectedproperties.tileIcons != null)
            {
                set.addAll(Arrays.asList(connectedproperties.tileIcons));
            }
        }

        set1.retainAll(set);
        return !set1.isEmpty();
    }

    private static ConnectedProperties[][] propertyListToArray(List list)
    {
        ConnectedProperties[][] aconnectedproperties = new ConnectedProperties[list.size()][];

        for (int i = 0; i < list.size(); ++i)
        {
            List sublist = (List)list.get(i);

            if (sublist != null)
            {
                ConnectedProperties[] aconnectedproperties1 = (ConnectedProperties[]) sublist.toArray(new ConnectedProperties[sublist.size()]);
                aconnectedproperties[i] = aconnectedproperties1;
            }
        }

        return aconnectedproperties;
    }

    private static void addToTileList(ConnectedProperties cp, List tileList)
    {
        if (cp.matchTileIcons != null)
        {
            for (int i = 0; i < cp.matchTileIcons.length; ++i)
            {
                TextureAtlasSprite textureatlassprite = cp.matchTileIcons[i];

                if (!(textureatlassprite instanceof TextureAtlasSprite))
                {
                    Config.warn("TextureAtlasSprite is not TextureAtlasSprite: " + textureatlassprite + ", name: " + textureatlassprite.getIconName());
                }
                else
                {
                    int j = textureatlassprite.getIndexInMap();

                    if (j < 0)
                    {
                        Config.warn("Invalid tile ID: " + j + ", icon: " + textureatlassprite.getIconName());
                    }
                    else
                    {
                        addToList(cp, tileList, j);
                    }
                }
            }
        }
    }

    private static void addToBlockList(ConnectedProperties cp, List blockList)
    {
        if (cp.matchBlocks != null)
        {
            for (int i = 0; i < cp.matchBlocks.length; ++i)
            {
                int j = cp.matchBlocks[i].getBlockId();

                if (j < 0)
                {
                    Config.warn("Invalid block ID: " + j);
                }
                else
                {
                    addToList(cp, blockList, j);
                }
            }
        }
    }

    private static void addToList(ConnectedProperties cp, List lists, int id)
    {
        while (id >= lists.size())
        {
            lists.add(null);
        }

        List list = (List)lists.get(id);

        if (list == null)
        {
            list = new ArrayList();
            lists.set(id, list);
        }

        list.add(cp);
    }

    private static String[] getDefaultCtmPaths()
    {
        List list = new ArrayList();
        String s = "mcpatcher/ctm/default/";

        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass.png")))
        {
            list.add(s + "glass.properties");
            list.add(s + "glasspane.properties");
        }

        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/bookshelf.png")))
        {
            list.add(s + "bookshelf.properties");
        }

        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/sandstone_normal.png")))
        {
            list.add(s + "sandstone.properties");
        }

        String[] astring = new String[] {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"};

        for (int i = 0; i < astring.length; ++i)
        {
            String s1 = astring[i];

            if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass_" + s1 + ".png")))
            {
                list.add(s + i + "_glass_" + s1 + "/glass_" + s1 + ".properties");
                list.add(s + i + "_glass_" + s1 + "/glass_pane_" + s1 + ".properties");
            }
        }

        String[] astring1 = (String[]) list.toArray(new String[list.size()]);
        return astring1;
    }
}
