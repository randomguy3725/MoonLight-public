package net.minecraft.network.play.server;

import java.io.IOException;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S32PacketConfirmTransaction implements Packet<INetHandlerPlayClient>
{
    private int windowId;
    private short actionNumber;
    private boolean accepted;

    public S32PacketConfirmTransaction()
    {
    }

    public S32PacketConfirmTransaction(int windowIdIn, short actionNumberIn, boolean p_i45182_3_)
    {
        this.windowId = windowIdIn;
        this.actionNumber = actionNumberIn;
        this.accepted = p_i45182_3_;
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleConfirmTransaction(this);
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17)) {
            this.windowId = buf.readInt();
        } else {
            this.windowId = buf.readUnsignedByte();
            this.actionNumber = buf.readShort();
            this.accepted = buf.readBoolean();
        }
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.windowId);
        buf.writeShort(this.actionNumber);
        buf.writeBoolean(this.accepted);
    }

    public int getWindowId()
    {
        return this.windowId;
    }

    public short getActionNumber()
    {
        return this.actionNumber;
    }

    public boolean func_148888_e()
    {
        return this.accepted;
    }
}
