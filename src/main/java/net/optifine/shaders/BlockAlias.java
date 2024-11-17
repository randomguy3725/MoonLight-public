package net.optifine.shaders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.src.Config;
import net.optifine.config.MatchBlock;

public class BlockAlias
{
    private final int blockAliasId;
    private final MatchBlock[] matchBlocks;

    public BlockAlias(int blockAliasId, MatchBlock[] matchBlocks)
    {
        this.blockAliasId = blockAliasId;
        this.matchBlocks = matchBlocks;
    }

    public int getBlockAliasId()
    {
        return this.blockAliasId;
    }

    public boolean matches(int id, int metadata)
    {
        for (int i = 0; i < this.matchBlocks.length; ++i)
        {
            MatchBlock matchblock = this.matchBlocks[i];

            if (matchblock.matches(id, metadata))
            {
                return true;
            }
        }

        return false;
    }

    public int[] getMatchBlockIds()
    {
        Set<Integer> set = new HashSet();

        for (int i = 0; i < this.matchBlocks.length; ++i)
        {
            MatchBlock matchblock = this.matchBlocks[i];
            int j = matchblock.getBlockId();
            set.add(Integer.valueOf(j));
        }

        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        int[] aint = Config.toPrimitive(ainteger);
        return aint;
    }

    public MatchBlock[] getMatchBlocks(int matchBlockId)
    {
        List<MatchBlock> list = new ArrayList();

        for (int i = 0; i < this.matchBlocks.length; ++i)
        {
            MatchBlock matchblock = this.matchBlocks[i];

            if (matchblock.getBlockId() == matchBlockId)
            {
                list.add(matchblock);
            }
        }

        MatchBlock[] amatchblock = list.toArray(new MatchBlock[list.size()]);
        return amatchblock;
    }

    public String toString()
    {
        return "block." + this.blockAliasId + "=" + Config.arrayToString(this.matchBlocks);
    }
}
