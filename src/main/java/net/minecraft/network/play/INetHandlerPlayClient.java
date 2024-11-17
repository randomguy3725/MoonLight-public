package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;

public interface INetHandlerPlayClient extends INetHandler
{
    void handleSpawnObject(S0EPacketSpawnObject packetIn);

    void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn);

    void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn);

    void handleSpawnMob(S0FPacketSpawnMob packetIn);

    void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn);

    void handleSpawnPainting(S10PacketSpawnPainting packetIn);

    void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn);

    void handleAnimation(S0BPacketAnimation packetIn);

    void handleStatistics(S37PacketStatistics packetIn);

    void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn);

    void handleSignEditorOpen(S36PacketSignEditorOpen packetIn);

    void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn);

    void handleBlockAction(S24PacketBlockAction packetIn);

    void handleBlockChange(S23PacketBlockChange packetIn);

    void handleChat(S02PacketChat packetIn);

    void handleTabComplete(S3APacketTabComplete packetIn);

    void handleMultiBlockChange(S22PacketMultiBlockChange packetIn);

    void handleMaps(S34PacketMaps packetIn);

    void handleConfirmTransaction(S32PacketConfirmTransaction packetIn);

    void handleCloseWindow(S2EPacketCloseWindow packetIn);

    void handleWindowItems(S30PacketWindowItems packetIn);

    void handleOpenWindow(S2DPacketOpenWindow packetIn);

    void handleWindowProperty(S31PacketWindowProperty packetIn);

    void handleSetSlot(S2FPacketSetSlot packetIn);

    void handleCustomPayload(S3FPacketCustomPayload packetIn);

    void handleDisconnect(S40PacketDisconnect packetIn);

    void handleUseBed(S0APacketUseBed packetIn);

    void handleEntityStatus(S19PacketEntityStatus packetIn);

    void handleEntityAttach(S1BPacketEntityAttach packetIn);

    void handleExplosion(S27PacketExplosion packetIn);

    void handleChangeGameState(S2BPacketChangeGameState packetIn);

    void handleKeepAlive(S00PacketKeepAlive packetIn);

    void handleChunkData(S21PacketChunkData packetIn);

    void handleMapChunkBulk(S26PacketMapChunkBulk packetIn);

    void handleEffect(S28PacketEffect packetIn);

    void handleJoinGame(S01PacketJoinGame packetIn);

    void handleEntityMovement(S14PacketEntity packetIn);

    void handlePlayerPosLook(S08PacketPlayerPosLook packetIn);

    void handleParticles(S2APacketParticles packetIn);

    void handlePlayerAbilities(S39PacketPlayerAbilities packetIn);

    void handlePlayerListItem(S38PacketPlayerListItem packetIn);

    void handleDestroyEntities(S13PacketDestroyEntities packetIn);

    void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn);

    void handleRespawn(S07PacketRespawn packetIn);

    void handleEntityHeadLook(S19PacketEntityHeadLook packetIn);

    void handleHeldItemChange(S09PacketHeldItemChange packetIn);

    void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn);

    void handleEntityMetadata(S1CPacketEntityMetadata packetIn);

    void handleEntityVelocity(S12PacketEntityVelocity packetIn);

    void handleEntityEquipment(S04PacketEntityEquipment packetIn);

    void handleSetExperience(S1FPacketSetExperience packetIn);

    void handleUpdateHealth(S06PacketUpdateHealth packetIn);

    void handleTeams(S3EPacketTeams packetIn);

    void handleUpdateScore(S3CPacketUpdateScore packetIn);

    void handleSpawnPosition(S05PacketSpawnPosition packetIn);

    void handleTimeUpdate(S03PacketTimeUpdate packetIn);

    void handleUpdateSign(S33PacketUpdateSign packetIn);

    void handleSoundEffect(S29PacketSoundEffect packetIn);

    void handleCollectItem(S0DPacketCollectItem packetIn);

    void handleEntityTeleport(S18PacketEntityTeleport packetIn);

    void handleEntityProperties(S20PacketEntityProperties packetIn);

    void handleEntityEffect(S1DPacketEntityEffect packetIn);

    void handleCombatEvent(S42PacketCombatEvent packetIn);

    void handleServerDifficulty(S41PacketServerDifficulty packetIn);

    void handleCamera(S43PacketCamera packetIn);

    void handleWorldBorder(S44PacketWorldBorder packetIn);

    void handleTitle(S45PacketTitle packetIn);

    void handleSetCompressionLevel(S46PacketSetCompressionLevel packetIn);

    void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn);

    void handleResourcePack(S48PacketResourcePackSend packetIn);

    void handleEntityNBT(S49PacketUpdateEntityNBT packetIn);
}
