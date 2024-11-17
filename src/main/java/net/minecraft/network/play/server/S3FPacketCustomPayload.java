package net.minecraft.network.play.server;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3FPacketCustomPayload implements Packet<INetHandlerPlayClient>
{
    private String channel;
    private PacketBuffer data;

    public S3FPacketCustomPayload()
    {
    }

    public S3FPacketCustomPayload(String channelName, PacketBuffer dataIn)
    {
        this.channel = channelName;
        this.data = dataIn;

        if (dataIn.writerIndex() > 1048576)
        {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.channel = buf.readStringFromBuffer(20);
        int i = buf.readableBytes();

        if (i >= 0 && i <= 1048576)
        {
            this.data = new PacketBuffer(buf.readBytes(i));
        }
        else
        {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.channel);
        buf.writeBytes(this.data);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleCustomPayload(this);
    }

    public String getChannelName()
    {
        return this.channel;
    }

    public PacketBuffer getBufferData()
    {
        return this.data;
    }
}
