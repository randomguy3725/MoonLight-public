package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3APacketTabComplete implements Packet<INetHandlerPlayClient>
{
    private String[] matches;

    public S3APacketTabComplete()
    {
    }

    public S3APacketTabComplete(String[] matchesIn)
    {
        this.matches = matchesIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.matches = new String[buf.readVarIntFromBuffer()];

        for (int i = 0; i < this.matches.length; ++i)
        {
            this.matches[i] = buf.readStringFromBuffer(32767);
        }
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.matches.length);

        for (String s : this.matches)
        {
            buf.writeString(s);
        }
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleTabComplete(this);
    }

    public String[] func_149630_c()
    {
        return this.matches;
    }
}
