package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S25PacketBlockBreakAnim implements Packet<INetHandlerPlayClient>
{
    private int breakerId;
    private BlockPos position;
    private int progress;

    public S25PacketBlockBreakAnim()
    {
    }

    public S25PacketBlockBreakAnim(int breakerId, BlockPos pos, int progress)
    {
        this.breakerId = breakerId;
        this.position = pos;
        this.progress = progress;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.breakerId = buf.readVarIntFromBuffer();
        this.position = buf.readBlockPos();
        this.progress = buf.readUnsignedByte();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.breakerId);
        buf.writeBlockPos(this.position);
        buf.writeByte(this.progress);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleBlockBreakAnim(this);
    }

    public int getBreakerId()
    {
        return this.breakerId;
    }

    public BlockPos getPosition()
    {
        return this.position;
    }

    public int getProgress()
    {
        return this.progress;
    }
}
