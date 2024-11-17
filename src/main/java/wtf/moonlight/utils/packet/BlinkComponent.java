package wtf.moonlight.utils.packet;

import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import org.apache.commons.lang3.time.StopWatch;
import wtf.moonlight.events.annotations.EventPriority;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.math.TimerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class BlinkComponent implements InstanceAccess {

    public static final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    public static boolean blinking;
    public static ArrayList<Class<?>> exemptedPackets = new ArrayList<>();
    public static TimerUtils exemptionWatch = new TimerUtils();

    public static void setExempt(Class<?>... packets) {
        exemptedPackets = new ArrayList<>(Arrays.asList(packets));
        exemptionWatch.reset();
    }

    @EventTarget
    @EventPriority(-1)
    public void onPacketSend(PacketEvent event){
        if (mc.thePlayer == null) {
            packets.clear();
            exemptedPackets.clear();
            return;
        }
        
        if(event.getState() == PacketEvent.State.OUTGOING) {

            if (mc.thePlayer.isDead || mc.isSingleplayer() || !mc.getNetHandler().doneLoadingTerrain) {
                packets.forEach(PacketUtils::sendPacketNoEvent);
                packets.clear();
                blinking = false;
                exemptedPackets.clear();
                return;
            }

            final Packet<?> packet = event.getPacket();

            if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart ||
                    packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing ||
                    packet instanceof C01PacketEncryptionResponse || packet instanceof C00PacketKeepAlive) {
                return;
            }

            if (blinking) {
                if (!event.isCancelled() && exemptedPackets.stream().noneMatch(packetClass ->
                        packetClass == packet.getClass())) {
                    packets.add(packet);
                    event.setCancelled(true);
                }
            }
        }
    }

    public static void dispatch(boolean clear) {
        blinking = false;
        if(!packets.isEmpty()) {
            packets.forEach(PacketUtils::sendPacketNoEvent);
            if(clear) {
                packets.clear();
                exemptedPackets.clear();
            }
        }
    }

    public static void dispatch() {
        dispatch(true);
    }

    @EventTarget
    @EventPriority(-1)
    public void onWorld(WorldEvent event) {
        packets.clear();
        BlinkComponent.blinking = false;
    }
}
