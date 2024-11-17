package net.minecraft.network.rcon;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RConConsoleSource implements ICommandSender
{
    private static final RConConsoleSource instance = new RConConsoleSource();
    private final StringBuffer buffer = new StringBuffer();

    public String getName()
    {
        return "Rcon";
    }

    public IChatComponent getDisplayName()
    {
        return new ChatComponentText(this.getName());
    }

    public void addChatMessage(IChatComponent component)
    {
        this.buffer.append(component.getUnformattedText());
    }

    public boolean canCommandSenderUseCommand(int permLevel, String commandName)
    {
        return true;
    }

    public BlockPos getPosition()
    {
        return new BlockPos(0, 0, 0);
    }

    public Vec3 getPositionVector()
    {
        return new Vec3(0.0D, 0.0D, 0.0D);
    }

    public World getEntityWorld()
    {
        return MinecraftServer.getServer().getEntityWorld();
    }

    public Entity getCommandSenderEntity()
    {
        return null;
    }

    public boolean sendCommandFeedback()
    {
        return true;
    }

    public void setCommandStat(CommandResultStats.Type type, int amount)
    {
    }
}
