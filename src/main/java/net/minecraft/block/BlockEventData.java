package net.minecraft.block;

import net.minecraft.util.BlockPos;

public class BlockEventData
{
    private final BlockPos position;
    private final Block blockType;
    private final int eventID;
    private final int eventParameter;

    public BlockEventData(BlockPos pos, Block blockType, int eventId, int p_i45756_4_)
    {
        this.position = pos;
        this.eventID = eventId;
        this.eventParameter = p_i45756_4_;
        this.blockType = blockType;
    }

    public BlockPos getPosition()
    {
        return this.position;
    }

    public int getEventID()
    {
        return this.eventID;
    }

    public int getEventParameter()
    {
        return this.eventParameter;
    }

    public Block getBlock()
    {
        return this.blockType;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (!(p_equals_1_ instanceof BlockEventData blockeventdata))
        {
            return false;
        }
        else
        {
            return this.position.equals(blockeventdata.position) && this.eventID == blockeventdata.eventID && this.eventParameter == blockeventdata.eventParameter && this.blockType == blockeventdata.blockType;
        }
    }

    public String toString()
    {
        return "TE(" + this.position + ")," + this.eventID + "," + this.eventParameter + "," + this.blockType;
    }
}
