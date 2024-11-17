package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2EPacketCloseWindow implements Packet<INetHandlerPlayClient>
{
    private int windowId;

    public S2EPacketCloseWindow()
    {
    }

    public S2EPacketCloseWindow(int windowIdIn)
    {
        this.windowId = windowIdIn;
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleCloseWindow(this);
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.windowId = buf.readUnsignedByte();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.windowId);
    }
}
