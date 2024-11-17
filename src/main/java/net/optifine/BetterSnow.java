package net.optifine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BetterSnow
{
    private static IBakedModel modelSnowLayer = null;

    public static void update()
    {
        modelSnowLayer = Config.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(Blocks.snow_layer.getDefaultState());
    }

    public static IBakedModel getModelSnowLayer()
    {
        return modelSnowLayer;
    }

    public static IBlockState getStateSnowLayer()
    {
        return Blocks.snow_layer.getDefaultState();
    }

    public static boolean shouldRender(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos)
    {
        Block block = blockState.getBlock();
        return checkBlock(block, blockState) && hasSnowNeighbours(blockAccess, blockPos);
    }

    private static boolean hasSnowNeighbours(IBlockAccess blockAccess, BlockPos pos)
    {
        Block block = Blocks.snow_layer;
        return (blockAccess.getBlockState(pos.north()).getBlock() == block || blockAccess.getBlockState(pos.south()).getBlock() == block || blockAccess.getBlockState(pos.west()).getBlock() == block || blockAccess.getBlockState(pos.east()).getBlock() == block) && blockAccess.getBlockState(pos.down()).getBlock().isOpaqueCube();
    }

    private static boolean checkBlock(Block block, IBlockState blockState)
    {
        if (block.isFullCube())
        {
            return false;
        }
        else if (block.isOpaqueCube())
        {
            return false;
        }
        else if (block instanceof BlockSnow)
        {
            return false;
        }
        else if (!(block instanceof BlockBush) || !(block instanceof BlockDoublePlant) && !(block instanceof BlockFlower) && !(block instanceof BlockMushroom) && !(block instanceof BlockSapling) && !(block instanceof BlockTallGrass))
        {
            if (!(block instanceof BlockFence) && !(block instanceof BlockFenceGate) && !(block instanceof BlockFlowerPot) && !(block instanceof BlockPane) && !(block instanceof BlockReed) && !(block instanceof BlockWall))
            {
                if (block instanceof BlockRedstoneTorch && blockState.getValue(BlockTorch.FACING) == EnumFacing.UP)
                {
                    return true;
                }
                else
                {
                    if (block instanceof BlockLever)
                    {
                        Object object = blockState.getValue(BlockLever.FACING);

                        return object == BlockLever.EnumOrientation.UP_X || object == BlockLever.EnumOrientation.UP_Z;
                    }

                    return false;
                }
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }
}
