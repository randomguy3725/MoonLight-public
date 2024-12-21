package wtf.moonlight.utils.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class DebugUtils {
    public static void sendMessage(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Moonlight" + EnumChatFormatting.WHITE + EnumChatFormatting.GRAY + ">> " + EnumChatFormatting.WHITE + message));
    }
}
