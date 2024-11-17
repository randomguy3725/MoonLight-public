package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.IChatComponent;

public class S00PacketDisconnect implements Packet<INetHandlerLoginClient>
{
    private IChatComponent reason;

    public S00PacketDisconnect()
    {
    }

    public S00PacketDisconnect(IChatComponent reasonIn)
    {
        this.reason = reasonIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.reason = buf.readChatComponent();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeChatComponent(this.reason);
    }

    public void processPacket(INetHandlerLoginClient handler)
    {
        handler.handleDisconnect(this);
    }

    public IChatComponent func_149603_c()
    {
        return this.reason;
    }
}
