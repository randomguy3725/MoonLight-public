package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C00PacketKeepAlive implements Packet<INetHandlerPlayServer>
{
    private int key;

    public C00PacketKeepAlive()
    {
    }

    public C00PacketKeepAlive(int key)
    {
        this.key = key;
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processKeepAlive(this);
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.key = buf.readVarIntFromBuffer();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.key);
    }

    public int getKey()
    {
        return this.key;
    }
}
