package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0DPacketCloseWindow implements Packet<INetHandlerPlayServer>
{
    public int windowId;

    public C0DPacketCloseWindow()
    {
    }

    public C0DPacketCloseWindow(int windowId)
    {
        this.windowId = windowId;
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processCloseWindow(this);
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.windowId = buf.readByte();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.windowId);
    }
}
