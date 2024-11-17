package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

public class S1EPacketRemoveEntityEffect implements Packet<INetHandlerPlayClient>
{
    private int entityId;
    private int effectId;

    public S1EPacketRemoveEntityEffect()
    {
    }

    public S1EPacketRemoveEntityEffect(int entityIdIn, PotionEffect effect)
    {
        this.entityId = entityIdIn;
        this.effectId = effect.getPotionID();
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarIntFromBuffer();
        this.effectId = buf.readUnsignedByte();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityId);
        buf.writeByte(this.effectId);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleRemoveEntityEffect(this);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public int getEffectId()
    {
        return this.effectId;
    }
}
