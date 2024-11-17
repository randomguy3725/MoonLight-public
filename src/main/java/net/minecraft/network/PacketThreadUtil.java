package net.minecraft.network;

import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.src.Config;
import net.minecraft.util.IThreadListener;

public class PacketThreadUtil
{
    public static int lastDimensionId = Integer.MIN_VALUE;

    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> p_180031_0_, final T p_180031_1_, IThreadListener p_180031_2_) throws ThreadQuickExitException
    {
        if (!p_180031_2_.isCallingFromMinecraftThread())
        {
            p_180031_2_.addScheduledTask(new Runnable()
            {
                public void run()
                {
                    PacketThreadUtil.clientPreProcessPacket(p_180031_0_);
                    p_180031_0_.processPacket(p_180031_1_);
                }
            });
            throw ThreadQuickExitException.INSTANCE;
        }
        else
        {
            clientPreProcessPacket(p_180031_0_);
        }
    }

    protected static void clientPreProcessPacket(Packet p_clientPreProcessPacket_0_)
    {
        if (p_clientPreProcessPacket_0_ instanceof S08PacketPlayerPosLook)
        {
            Config.getRenderGlobal().onPlayerPositionSet();
        }

        if (p_clientPreProcessPacket_0_ instanceof S07PacketRespawn s07packetrespawn)
        {
            lastDimensionId = s07packetrespawn.getDimensionID();
        }
        else if (p_clientPreProcessPacket_0_ instanceof S01PacketJoinGame s01packetjoingame)
        {
            lastDimensionId = s01packetjoingame.getDimension();
        }
        else
        {
            lastDimensionId = Integer.MIN_VALUE;
        }
    }
}
