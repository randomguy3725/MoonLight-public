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
package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.block.BlockGlass;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.BlockAABBEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.packet.PingSpoofComponent;

@ModuleInfo(name = "Phase", category = ModuleCategory.Movement)
public class Phase extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"Watchdog Auto"}, "Watchdog Auto", this);
    public boolean phase;
    private final TimerUtils timerUtils = new TimerUtils();

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());
        if (mode.get().equals("Watchdog Auto")) {
            if (phase && !timerUtils.hasTimeElapsed(4000)) PingSpoofComponent.blink();
        }
    }

    @EventTarget
    public void onBlockAABB(BlockAABBEvent event) {
        if (mode.get().equals("Watchdog Auto")) {
            if (phase && PingSpoofComponent.enabled && event.getBlock() instanceof BlockGlass)
                event.setCancelled(true);
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {

        Packet<?> packet = event.getPacket();
        if (mode.is("Watchdog Auto")) {
            if (event.getState() == PacketEvent.State.INCOMING) {
                if (packet instanceof S02PacketChat s02PacketChat) {
                    String chat = s02PacketChat.getChatComponent().getUnformattedText();

                    switch (chat) {
                        case "Cages opened! FIGHT!":
                        case "§r§r§r                               §r§f§lSkyWars Duel§r":
                        case "§r§eCages opened! §r§cFIGHT!§r":
                            phase = false;
                            break;

                        case "The game starts in 3 seconds!":
                        case "§r§e§r§eThe game starts in §r§a§r§c3§r§e seconds!§r§e§r":
                        case "§r§eCages open in: §r§c3 §r§eseconds!§r":
                            phase = true;
                            timerUtils.reset();
                            break;
                    }
                }
            }
        }
    }
}
