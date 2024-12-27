/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.utils.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class DebugUtils {
    public static void sendMessage(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Moonlight" + EnumChatFormatting.WHITE + EnumChatFormatting.GRAY + ">> " + EnumChatFormatting.WHITE + message));
    }
}
