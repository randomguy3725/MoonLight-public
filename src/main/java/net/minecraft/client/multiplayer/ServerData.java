package net.minecraft.client.multiplayer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class ServerData
{
    public String serverName;
    public String serverIP;
    public String populationInfo;
    public String serverMOTD;
    public long pingToServer;
    public int version = 47;
    public String gameVersion = "1.8.9";
    public boolean field_78841_f;
    public String playerList;
    private ServerData.ServerResourceMode resourceMode = ServerData.ServerResourceMode.PROMPT;
    private String serverIcon;
    private boolean lanServer;

    public ServerData(String name, String ip, boolean isLan)
    {
        this.serverName = name;
        this.serverIP = ip;
        this.lanServer = isLan;
    }

    public NBTTagCompound getNBTCompound()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("name", this.serverName);
        nbttagcompound.setString("ip", this.serverIP);

        if (this.serverIcon != null)
        {
            nbttagcompound.setString("icon", this.serverIcon);
        }

        if (this.resourceMode == ServerData.ServerResourceMode.ENABLED)
        {
            nbttagcompound.setBoolean("acceptTextures", true);
        }
        else if (this.resourceMode == ServerData.ServerResourceMode.DISABLED)
        {
            nbttagcompound.setBoolean("acceptTextures", false);
        }

        return nbttagcompound;
    }

    public ServerData.ServerResourceMode getResourceMode()
    {
        return this.resourceMode;
    }

    public void setResourceMode(ServerData.ServerResourceMode mode)
    {
        this.resourceMode = mode;
    }

    public static ServerData getServerDataFromNBTCompound(NBTTagCompound nbtCompound)
    {
        ServerData serverdata = new ServerData(nbtCompound.getString("name"), nbtCompound.getString("ip"), false);

        if (nbtCompound.hasKey("icon", 8))
        {
            serverdata.setBase64EncodedIconData(nbtCompound.getString("icon"));
        }

        if (nbtCompound.hasKey("acceptTextures", 1))
        {
            if (nbtCompound.getBoolean("acceptTextures"))
            {
                serverdata.setResourceMode(ServerData.ServerResourceMode.ENABLED);
            }
            else
            {
                serverdata.setResourceMode(ServerData.ServerResourceMode.DISABLED);
            }
        }
        else
        {
            serverdata.setResourceMode(ServerData.ServerResourceMode.PROMPT);
        }

        return serverdata;
    }

    public String getBase64EncodedIconData()
    {
        return this.serverIcon;
    }

    public void setBase64EncodedIconData(String icon)
    {
        this.serverIcon = icon;
    }

    public boolean isOnLAN()
    {
        return this.lanServer;
    }

    public void copyFrom(ServerData serverDataIn)
    {
        this.serverIP = serverDataIn.serverIP;
        this.serverName = serverDataIn.serverName;
        this.setResourceMode(serverDataIn.getResourceMode());
        this.serverIcon = serverDataIn.serverIcon;
        this.lanServer = serverDataIn.lanServer;
    }

    public enum ServerResourceMode
    {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final IChatComponent motd;

        ServerResourceMode(String name)
        {
            this.motd = new ChatComponentTranslation("addServer.resourcePack." + name);
        }

        public IChatComponent getMotd()
        {
            return this.motd;
        }
    }
}
