package wtf.moonlight.utils.packet;

import net.minecraft.network.Packet;
import wtf.moonlight.utils.InstanceAccess;

import java.util.Arrays;

public class PacketUtils implements InstanceAccess {
    public static void sendPacket(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendPacketNoEvent(Packet<?> packet) {
        mc.getNetHandler().sendPacketNoEvent(packet);
    }

    public static void queue(final Packet packet) {
        if (packet == null) {
            System.out.println("Packet is null");
            return;
        }

        if (isClientPacket(packet)) {
            mc.getNetHandler().sendPacketNoEvent(packet);
        } else {
            packet.processPacket(mc.getNetHandler().getNetworkManager().getNetHandler());
        }
    }

    public static boolean isClientPacket(final Packet<?> packet) {
        return Arrays.stream(NetworkAPI.serverbound).anyMatch(clazz -> clazz == packet.getClass());
    }
}
