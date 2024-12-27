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

import lombok.SneakyThrows;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.misc.DebugUtils;

import javax.script.ScriptException;
import java.util.Arrays;

public class NetworkAPI implements InstanceAccess {

    public final static Class<Packet<INetHandlerPlayServer>>[] serverbound = new Class[]{
            C00PacketKeepAlive.class,
            C01PacketChatMessage.class,
            C02PacketUseEntity.class,
            C03PacketPlayer.class,
            C03PacketPlayer.C04PacketPlayerPosition.class,
            C03PacketPlayer.C05PacketPlayerLook.class,
            C03PacketPlayer.C06PacketPlayerPosLook.class,
            C07PacketPlayerDigging.class,
            C08PacketPlayerBlockPlacement.class,
            C09PacketHeldItemChange.class,
            C0APacketAnimation.class,
            C0BPacketEntityAction.class,
            C0CPacketInput.class,
            C0DPacketCloseWindow.class,
            C0EPacketClickWindow.class,
            C0FPacketConfirmTransaction.class,
            C10PacketCreativeInventoryAction.class,
            C11PacketEnchantItem.class,
            C12PacketUpdateSign.class,
            C13PacketPlayerAbilities.class,
            C14PacketTabComplete.class,
            C15PacketClientSettings.class,
            C16PacketClientStatus.class,
            C17PacketCustomPayload.class,
            C18PacketSpectate.class,
            C19PacketResourcePackStatus.class,
    };
    public final static Class<Packet<INetHandlerPlayClient>>[] clientbound = new Class[]{
            S00PacketKeepAlive.class,
            S01PacketJoinGame.class,
            S02PacketChat.class,
            S03PacketTimeUpdate.class,
            S04PacketEntityEquipment.class,
            S05PacketSpawnPosition.class,
            S06PacketUpdateHealth.class,
            S07PacketRespawn.class,
            S08PacketPlayerPosLook.class,
            S09PacketHeldItemChange.class,
            S0APacketUseBed.class,
            S0BPacketAnimation.class,
            S0CPacketSpawnPlayer.class,
            S0DPacketCollectItem.class,
            S0EPacketSpawnObject.class,
            S0FPacketSpawnMob.class,
            S10PacketSpawnPainting.class,
            S11PacketSpawnExperienceOrb.class,
            S12PacketEntityVelocity.class,
            S13PacketDestroyEntities.class,
            S14PacketEntity.class,
            S18PacketEntityTeleport.class,
            S19PacketEntityHeadLook.class,
            S19PacketEntityStatus.class,
            S1BPacketEntityAttach.class,
            S1CPacketEntityMetadata.class,
            S1DPacketEntityEffect.class,
            S1EPacketRemoveEntityEffect.class,
            S1FPacketSetExperience.class,
            S20PacketEntityProperties.class,
            S21PacketChunkData.class,
            S22PacketMultiBlockChange.class,
            S23PacketBlockChange.class,
            S24PacketBlockAction.class,
            S25PacketBlockBreakAnim.class,
            S26PacketMapChunkBulk.class,
            S27PacketExplosion.class,
            S28PacketEffect.class,
            S29PacketSoundEffect.class,
            S2APacketParticles.class,
            S2BPacketChangeGameState.class,
            S2CPacketSpawnGlobalEntity.class,
            S2DPacketOpenWindow.class,
            S2EPacketCloseWindow.class,
            S2FPacketSetSlot.class,
            S30PacketWindowItems.class,
            S31PacketWindowProperty.class,
            S32PacketConfirmTransaction.class,
            S33PacketUpdateSign.class,
            S34PacketMaps.class,
            S35PacketUpdateTileEntity.class,
            S36PacketSignEditorOpen.class,
            S37PacketStatistics.class,
            S38PacketPlayerListItem.class,
            S39PacketPlayerAbilities.class,
            S3APacketTabComplete.class,
            S3BPacketScoreboardObjective.class,
            S3CPacketUpdateScore.class,
            S3DPacketDisplayScoreboard.class,
            S3EPacketTeams.class,
            S3FPacketCustomPayload.class,
            S40PacketDisconnect.class,
            S41PacketServerDifficulty.class,
            S42PacketCombatEvent.class,
            S43PacketCamera.class,
            S44PacketWorldBorder.class,
            S45PacketTitle.class,
            S46PacketSetCompressionLevel.class,
            S47PacketPlayerListHeaderFooter.class,
            S48PacketResourcePackSend.class,
            S49PacketUpdateEntityNBT.class
    };

    @SneakyThrows
    private static Packet<?> instantiatePacket(final EnumPacketDirection direction, final int id, final Object... params) {
        Packet<?> packet = null;
        try {
            if (direction == EnumPacketDirection.CLIENTBOUND) {
                packet = (Packet<?>) Arrays.stream(clientbound[id].getConstructors()).filter(x -> x.getParameterCount() == params.length).findFirst().get().newInstance(params);
            } else if (direction == EnumPacketDirection.SERVERBOUND) {
                packet = (Packet<?>) Arrays.stream(serverbound[id].getConstructors()).filter(x -> x.getParameterCount() == params.length).findFirst().get().newInstance(params);
            }
        } catch (Exception ex) {
            DebugUtils.sendMessage("Failed to instantiate packet!");
            throw new ScriptException(ex);
        }
        return packet;
    }
}
