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
package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.visual.finalkills.FkCounter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import static wtf.moonlight.features.modules.impl.visual.finalkills.FkCounter.MW_GAME_START_MESSAGE;

@ModuleInfo(name = "FinalKills", category = ModuleCategory.Visual)
public class FinalKills extends Module {
    public static FkCounter killCounter = new FkCounter();
    public void finals() {
        ArrayList<String> messages = new ArrayList<String>();
        if (mc.ingameGUI.getChatGUI().getChatOpen()) {
            messages.add(EnumChatFormatting.RED + "RED" + EnumChatFormatting.WHITE + ": " + killCounter.getPlayers(0).entrySet().stream().map((entry) -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
            messages.add(EnumChatFormatting.GREEN + "GREEN" + EnumChatFormatting.WHITE + ": " + killCounter.getPlayers(1).entrySet().stream().map((entry) -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
            messages.add(EnumChatFormatting.YELLOW + "YELLOW" + EnumChatFormatting.WHITE + ": " + killCounter.getPlayers(2).entrySet().stream().map((entry) -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
            messages.add(EnumChatFormatting.BLUE + "BLUE" + EnumChatFormatting.WHITE + ": " + killCounter.getPlayers(3).entrySet().stream().map((entry) -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", ")));
        } else {
            messages.add(EnumChatFormatting.RED + "RED" + EnumChatFormatting.WHITE + ": " + killCounter.getKills(0));
            messages.add(EnumChatFormatting.GREEN + "GREEN" + EnumChatFormatting.WHITE + ": " + killCounter.getKills(1));
            messages.add(EnumChatFormatting.YELLOW + "YELLOW" + EnumChatFormatting.WHITE + ": " + killCounter.getKills(2));
            messages.add(EnumChatFormatting.BLUE + "BLUE" + EnumChatFormatting.WHITE + ": " + killCounter.getKills(3));
        }

        int y = 15;// + 80;

        for (Iterator var4 = messages.iterator(); var4.hasNext(); y = (int) ((float) y + 9.0F)) {
            String text = (String) var4.next();
            mc.fontRendererObj.drawOutlinedString(text, 4.0F, (float) y + 50f,0.5f, -1, Color.BLACK.getRGB());
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event){
        finals();
    }

    @EventTarget
    public void onPacket(PacketEvent event){
        if (event.getState() == PacketEvent.State.INCOMING) {
            Packet packet = event.getPacket();
            if (packet instanceof S02PacketChat) {
                if (((S02PacketChat) packet).getChatComponent().getUnformattedText().equals(MW_GAME_START_MESSAGE)) {
                    killCounter = new FkCounter();
                }

                if (killCounter != null) {
                    killCounter.onChatMessage(((S02PacketChat) packet).getChatComponent());
                }
            }
        }
    }
}
