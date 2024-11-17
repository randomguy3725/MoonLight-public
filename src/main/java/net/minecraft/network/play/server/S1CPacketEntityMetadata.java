package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1CPacketEntityMetadata implements Packet<INetHandlerPlayClient>
{
    private int entityId;
    private List<DataWatcher.WatchableObject> field_149378_b;

    public S1CPacketEntityMetadata()
    {
    }

    public S1CPacketEntityMetadata(int entityIdIn, DataWatcher p_i45217_2_, boolean p_i45217_3_)
    {
        this.entityId = entityIdIn;

        if (p_i45217_3_)
        {
            this.field_149378_b = p_i45217_2_.getAllWatched();
        }
        else
        {
            this.field_149378_b = p_i45217_2_.getChanged();
        }
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarIntFromBuffer();
        this.field_149378_b = DataWatcher.readWatchedListFromPacketBuffer(buf);
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityId);
        DataWatcher.writeWatchedListToPacketBuffer(this.field_149378_b, buf);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleEntityMetadata(this);
    }

    public List<DataWatcher.WatchableObject> func_149376_c()
    {
        return this.field_149378_b;
    }

    public int getEntityId()
    {
        return this.entityId;
    }
}
