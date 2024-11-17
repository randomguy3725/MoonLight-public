package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S43PacketCamera implements Packet<INetHandlerPlayClient>
{
    public int entityId;

    public S43PacketCamera()
    {
    }

    public S43PacketCamera(Entity entityIn)
    {
        this.entityId = entityIn.getEntityId();
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarIntFromBuffer();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityId);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleCamera(this);
    }

    public Entity getEntity(World worldIn)
    {
        return worldIn.getEntityByID(this.entityId);
    }
}
