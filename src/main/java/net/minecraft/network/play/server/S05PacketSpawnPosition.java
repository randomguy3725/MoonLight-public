package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S05PacketSpawnPosition implements Packet<INetHandlerPlayClient>
{
    private BlockPos spawnBlockPos;

    public S05PacketSpawnPosition()
    {
    }

    public S05PacketSpawnPosition(BlockPos spawnBlockPosIn)
    {
        this.spawnBlockPos = spawnBlockPosIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.spawnBlockPos = buf.readBlockPos();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.spawnBlockPos);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleSpawnPosition(this);
    }

    public BlockPos getSpawnPos()
    {
        return this.spawnBlockPos;
    }
}
