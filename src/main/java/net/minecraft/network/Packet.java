package net.minecraft.network;

import java.io.IOException;

public interface Packet<T extends INetHandler>
{
    void readPacketData(PacketBuffer buf) throws IOException;

    void writePacketData(PacketBuffer buf) throws IOException;

    void processPacket(T handler);
}
