package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class WorldManager implements IWorldAccess
{
    private final MinecraftServer mcServer;
    private final WorldServer theWorldServer;

    public WorldManager(MinecraftServer mcServerIn, WorldServer worldServerIn)
    {
        this.mcServer = mcServerIn;
        this.theWorldServer = worldServerIn;
    }

    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters)
    {
    }

    public void onEntityAdded(Entity entityIn)
    {
        this.theWorldServer.getEntityTracker().trackEntity(entityIn);
    }

    public void onEntityRemoved(Entity entityIn)
    {
        this.theWorldServer.getEntityTracker().untrackEntity(entityIn);
        this.theWorldServer.getScoreboard().func_181140_a(entityIn);
    }

    public void playSound(String soundName, double x, double y, double z, float volume, float pitch)
    {
        this.mcServer.getConfigurationManager().sendToAllNear(x, y, z, volume > 1.0F ? (double)(16.0F * volume) : 16.0D, this.theWorldServer.provider.getDimensionId(), new S29PacketSoundEffect(soundName, x, y, z, volume, pitch));
    }

    public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch)
    {
        this.mcServer.getConfigurationManager().sendToAllNearExcept(except, x, y, z, volume > 1.0F ? (double)(16.0F * volume) : 16.0D, this.theWorldServer.provider.getDimensionId(), new S29PacketSoundEffect(soundName, x, y, z, volume, pitch));
    }

    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
    }

    public void markBlockForUpdate(BlockPos pos)
    {
        this.theWorldServer.getPlayerManager().markBlockForUpdate(pos);
    }

    public void notifyLightSet(BlockPos pos)
    {
    }

    public void playRecord(String recordName, BlockPos blockPosIn)
    {
    }

    public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int data)
    {
        this.mcServer.getConfigurationManager().sendToAllNearExcept(player, blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ(), 64.0D, this.theWorldServer.provider.getDimensionId(), new S28PacketEffect(sfxType, blockPosIn, data, false));
    }

    public void broadcastSound(int soundID, BlockPos pos, int data)
    {
        this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S28PacketEffect(soundID, pos, data, true));
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        for (EntityPlayerMP entityplayermp : this.mcServer.getConfigurationManager().getPlayerList())
        {
            if (entityplayermp != null && entityplayermp.worldObj == this.theWorldServer && entityplayermp.getEntityId() != breakerId)
            {
                double d0 = (double)pos.getX() - entityplayermp.posX;
                double d1 = (double)pos.getY() - entityplayermp.posY;
                double d2 = (double)pos.getZ() - entityplayermp.posZ;

                if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D)
                {
                    entityplayermp.playerNetServerHandler.sendPacket(new S25PacketBlockBreakAnim(breakerId, pos, progress));
                }
            }
        }
    }
}
