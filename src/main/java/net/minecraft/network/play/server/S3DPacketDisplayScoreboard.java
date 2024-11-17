package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

public class S3DPacketDisplayScoreboard implements Packet<INetHandlerPlayClient>
{
    private int position;
    private String scoreName;

    public S3DPacketDisplayScoreboard()
    {
    }

    public S3DPacketDisplayScoreboard(int positionIn, ScoreObjective scoreIn)
    {
        this.position = positionIn;

        if (scoreIn == null)
        {
            this.scoreName = "";
        }
        else
        {
            this.scoreName = scoreIn.getName();
        }
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.position = buf.readByte();
        this.scoreName = buf.readStringFromBuffer(16);
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.position);
        buf.writeString(this.scoreName);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleDisplayScoreboard(this);
    }

    public int func_149371_c()
    {
        return this.position;
    }

    public String func_149370_d()
    {
        return this.scoreName;
    }
}
