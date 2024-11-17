package wtf.moonlight.features.modules.impl.misc.hackerdetector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.features.modules.impl.misc.HackerDetector;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.misc.DebugUtils;

public abstract class Check implements InstanceAccess {
    public abstract String getName();

    public abstract void onPacketReceive(PacketEvent event, EntityPlayer player);

    public abstract void onUpdate(EntityPlayer player);

    public void onMotion(EntityPlayer player, double x, double y, double z) {
    }

    public void flag(EntityPlayer player, String verbose) {
        DebugUtils.sendMessage(player.getName() + EnumChatFormatting.WHITE + " detected for " + EnumChatFormatting.GRAY + getName() + EnumChatFormatting.WHITE + ", " + EnumChatFormatting.WHITE + verbose);
        INSTANCE.getModuleManager().getModule(HackerDetector.class).hackers.add(player);
    }
}