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
