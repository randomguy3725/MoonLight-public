package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

public class C00PacketLoginStart implements Packet<INetHandlerLoginServer>
{
    private GameProfile profile;

    public C00PacketLoginStart()
    {
    }

    public C00PacketLoginStart(GameProfile profileIn)
    {
        this.profile = profileIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.profile = new GameProfile(null, buf.readStringFromBuffer(16));
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.profile.getName());
    }

    public void processPacket(INetHandlerLoginServer handler)
    {
        handler.processLoginStart(this);
    }

    public GameProfile getProfile()
    {
        return this.profile;
    }
}
