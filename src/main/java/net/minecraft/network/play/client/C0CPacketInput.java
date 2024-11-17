package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0CPacketInput implements Packet<INetHandlerPlayServer>
{
    private float strafeSpeed;
    private float forwardSpeed;
    private boolean jumping;
    private boolean sneaking;

    public C0CPacketInput()
    {
    }

    public C0CPacketInput(float strafeSpeed, float forwardSpeed, boolean jumping, boolean sneaking)
    {
        this.strafeSpeed = strafeSpeed;
        this.forwardSpeed = forwardSpeed;
        this.jumping = jumping;
        this.sneaking = sneaking;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.strafeSpeed = buf.readFloat();
        this.forwardSpeed = buf.readFloat();
        byte b0 = buf.readByte();
        this.jumping = (b0 & 1) > 0;
        this.sneaking = (b0 & 2) > 0;
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeFloat(this.strafeSpeed);
        buf.writeFloat(this.forwardSpeed);
        byte b0 = 0;

        if (this.jumping)
        {
            b0 = (byte)(b0 | 1);
        }

        if (this.sneaking)
        {
            b0 = (byte)(b0 | 2);
        }

        buf.writeByte(b0);
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processInput(this);
    }

    public float getStrafeSpeed()
    {
        return this.strafeSpeed;
    }

    public float getForwardSpeed()
    {
        return this.forwardSpeed;
    }

    public boolean isJumping()
    {
        return this.jumping;
    }

    public boolean isSneaking()
    {
        return this.sneaking;
    }
}
