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
package wtf.moonlight.features.modules.impl.misc;

import net.minecraft.network.play.server.S02PacketChat;
import org.apache.commons.lang3.StringUtils;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.features.values.impl.TextValue;

@ModuleInfo(name = "AutoAuthenticate", category = ModuleCategory.Misc)
public class AutoAuthenticate extends Module {
    public final TextValue password = new TextValue("Password","12341234",this);
    public final SliderValue delay = new SliderValue("Delay",1000,100,5000,100,this);
    private final String[] PASSWORD_PLACEHOLDERS = {"password", "pass"};
    private long runAt, startAt;
    private String runCommand;
    @EventTarget
    public void onMotion(MotionEvent event) {
        if (this.runAt < System.currentTimeMillis() && this.runCommand != null) {
            mc.thePlayer.sendChatMessage(runCommand);
            reset();
        }
    }

    @EventTarget
    public void onChatReceivedEvent(PacketEvent event) {
        if (event.getPacket() instanceof S02PacketChat s02PacketChat) {
            String msg = s02PacketChat.getChatComponent().getUnformattedText();
            String password = this.password.get();
            int passCount = count(msg);
            if (passCount > 0) {
                if (msg.contains("/register ") || msg.contains("/reg ")) {
                    setRun("/register " + StringUtils.repeat(password + " ", passCount));
                } else if (msg.contains("/login ")) {
                    setRun("/login " + StringUtils.repeat(password + " ", passCount));
                }
            }
        }
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        reset();
    }

    @Override
    public void onEnable() {
        reset();
        super.onEnable();
    }

    private int count(String data) {
        int count = 0;
        data = data.toLowerCase();
        for (String pass : PASSWORD_PLACEHOLDERS) {
            count += StringUtils.countMatches(data, pass);
        }
        return count;
    }

    private void setRun(String runCommand) {
        long currentTimeMillis = System.currentTimeMillis();
        this.startAt = currentTimeMillis;
        this.runAt = (long) (currentTimeMillis + delay.get());
        this.runCommand = runCommand.trim();
    }

    private void reset() {
        this.startAt = this.runAt = 0;
        this.runCommand = null;
    }
}
