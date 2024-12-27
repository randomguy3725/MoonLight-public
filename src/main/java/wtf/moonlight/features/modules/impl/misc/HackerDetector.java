/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.features.modules.impl.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.combat.AntiBot;
import wtf.moonlight.features.modules.impl.misc.hackerdetector.Check;
import wtf.moonlight.features.modules.impl.misc.hackerdetector.impl.*;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.MultiBoolValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "HackerDetector", category = ModuleCategory.Misc)
public class HackerDetector extends Module {
    public final MultiBoolValue options = new MultiBoolValue("Detects", Arrays.asList(
            new BoolValue("Angle", false),
            new BoolValue("Auto Block", false),
            new BoolValue("Legit Scaffold", false),
            new BoolValue("Invalid motion", false),
            new BoolValue("No Fall", false),
            new BoolValue("No Slow", false),
            new BoolValue("Scaffold", false),
            new BoolValue("Velocity", false),
            new BoolValue("Omni Sprint", false)
    ), this);

    public final BoolValue selfCheck = new BoolValue("Self", false, this);
    public final List<EntityPlayer> hackers = new ArrayList<>();
    private final ArrayList<Check> checks = new ArrayList<>();

    public HackerDetector() {
        addChecks(
                new AngleCheck(),
                new AutoBlockCheck(),
                new LegitScaffoldCheck(),
                new MotionCheck(),
                new NoFallCheck(),
                new NoSlowCheck(),
                new ScaffoldCheck(),
                new VelocityCheck(),
                new OmniSprintCheck()

        );
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            for (Check check : checks) {
                if ((selfCheck.get() || player != mc.thePlayer) && !player.isDead && !Moonlight.INSTANCE.getFriendManager().isFriend(player)) {
                    if(isEnabled(AntiBot.class) && getModule(AntiBot.class).bots.contains(player))
                        continue;
                    if (options.isEnabled(check.getName())) {
                        check.onUpdate(player);
                    }
                }
            }
        }
    }

    @EventTarget
    public final void onPacketReceive(PacketEvent event) {
        if (event.getState() != PacketEvent.State.INCOMING) return;
        if (event.getPacket() instanceof S14PacketEntity || event.getPacket() instanceof S18PacketEntityTeleport) {
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                for (Check check : checks) {
                    if ((selfCheck.get() || player != mc.thePlayer) && !player.isDead && !Moonlight.INSTANCE.getFriendManager().isFriend(player)) {
                        if(isEnabled(AntiBot.class) && getModule(AntiBot.class).isBot(player))
                            continue;
                        if (options.isEnabled(check.getName())) {
                            check.onPacketReceive(event, player);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        hackers.clear();
    }

    public void addChecks(Check... checks) {
        this.checks.addAll(Arrays.asList(checks));
    }

    public boolean isHacker(EntityPlayer ent) {
        for (EntityPlayer hacker : hackers) {
            if (!ent.getName().equals(hacker.getName())) continue;
            return true;
        }
        return false;
    }

}