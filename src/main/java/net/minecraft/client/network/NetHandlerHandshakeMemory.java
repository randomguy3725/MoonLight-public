package net.minecraft.client.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.IChatComponent;

public class NetHandlerHandshakeMemory implements INetHandlerHandshakeServer
{
    private final MinecraftServer mcServer;
    private final NetworkManager networkManager;

    public NetHandlerHandshakeMemory(MinecraftServer mcServerIn, NetworkManager networkManagerIn)
    {
        this.mcServer = mcServerIn;
        this.networkManager = networkManagerIn;
    }

    public void processHandshake(C00Handshake packetIn)
    {
        this.networkManager.setConnectionState(packetIn.getRequestedState());
        this.networkManager.setNetHandler(new NetHandlerLoginServer(this.mcServer, this.networkManager));
    }

    public void onDisconnect(IChatComponent reason)
    {
    }
}
