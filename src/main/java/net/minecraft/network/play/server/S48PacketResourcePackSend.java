package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S48PacketResourcePackSend implements Packet<INetHandlerPlayClient>
{
    private String url;
    private String hash;

    public S48PacketResourcePackSend()
    {
    }

    public S48PacketResourcePackSend(String url, String hash)
    {
        this.url = url;
        this.hash = hash;

        if (hash.length() > 40)
        {
            throw new IllegalArgumentException("Hash is too long (max 40, was " + hash.length() + ")");
        }
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.url = buf.readStringFromBuffer(32767);
        this.hash = buf.readStringFromBuffer(40);
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.url);
        buf.writeString(this.hash);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleResourcePack(this);
    }

    public String getURL()
    {
        return this.url;
    }

    public String getHash()
    {
        return this.hash;
    }
}
