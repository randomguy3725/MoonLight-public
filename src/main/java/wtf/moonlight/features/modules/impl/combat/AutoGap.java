/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight-public
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.features.modules.impl.combat;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.MoveInputEvent;
import wtf.moonlight.events.impl.player.MoveMathEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.ContinualAnimation;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.concurrent.LinkedBlockingQueue;

@ModuleInfo(name = "AutoGap", category = ModuleCategory.Combat)
public class AutoGap extends Module {

    private final SliderValue delay = new SliderValue("Delay", 1000, 0, 10000, 100,this);
    private final SliderValue health = new SliderValue("Health", 15, 0, 20, 0.5f,this);
    private final BoolValue noMove = new BoolValue("Stop Move When Eating", false,this);
    public final BoolValue alwaysAttack = new BoolValue("Always Attack", false,this);
    private final BoolValue autoClose = new BoolValue("Close When No Golden Apple", true,this);
    private final BoolValue lagValue = new BoolValue("Lag When In Air", false,this);
    private final TimerUtils timer = new TimerUtils();
    public boolean eating = false;
    private int movingPackets = 0;
    private int slot = 0;
    private final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();
    private boolean needSkip = false;
    private final ContinualAnimation animation = new ContinualAnimation();

    @Override
    public void onDisable() {
        eating = false;
        release();
    }

    @Override
    public void onEnable(){
        packets.clear();
        slot = -1;
        needSkip = false;
        movingPackets = 0;
        eating = false;
    }

    @EventTarget
    public void onPostMotion(MotionEvent event) {
        if (event.isPost()) {
            if (eating) {
                movingPackets++;
                packets.add(new C01PacketChatMessage("release"));
            }
        }
    }

    @EventTarget
    public void onPreMotion(MotionEvent event){

        if (event.isPre()) {
            if (mc.thePlayer == null || !mc.thePlayer.isEntityAlive()) {
                eating = false;
                packets.clear();

                return;
            }

            if (!mc.playerController.getCurrentGameType().isSurvivalOrAdventure() || !timer.hasTimeElapsed(delay.get())) {
                eating = false;
                release();

                return;
            }

            slot = getGApple();

            if (slot == -1 || mc.thePlayer.getHealth() >= health.get()) {
                if (eating) {
                    eating = false;
                    release();
                }
            } else {
                eating = true;
                if (movingPackets >= 32) {
                    if (slot != mc.thePlayer.inventory.currentItem) sendPacketNoEvent(new C09PacketHeldItemChange(slot));
                    sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(slot)));
                    mc.thePlayer.itemInUseCount -= 32;
                    release();
                    if (slot != mc.thePlayer.inventory.currentItem) sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    timer.reset();

                } else if (mc.thePlayer.ticksExisted % 3 == 0) {
                    while (!packets.isEmpty()) {
                        final Packet<?> packet = packets.poll();

                        if (packet instanceof C01PacketChatMessage) {
                            break;
                        }

                        if (packet instanceof C03PacketPlayer) {
                            movingPackets--;
                        }

                        sendPacketNoEvent(packet);

                        if (packet instanceof C08PacketPlayerBlockPlacement) {
                            if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47) {
                                PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                                useItem.write(Type.VAR_INT, 1);
                                PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event){
        if (mc.thePlayer == null || !mc.playerController.getCurrentGameType().isSurvivalOrAdventure()) return;

        final Packet<?> packet = event.getPacket();
        if(event.getState() == PacketEvent.State.OUTGOING) {

            if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart ||
                    packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing ||
                    packet instanceof C01PacketEncryptionResponse || packet instanceof C01PacketChatMessage) {
                return;
            }

            if (!(packet instanceof C09PacketHeldItemChange) &&
                    !(packet instanceof C0EPacketClickWindow) &&
                    !(packet instanceof C16PacketClientStatus) &&
                    !(packet instanceof C0DPacketCloseWindow)
            ) {
                if (eating) {
                    event.setCancelled(true);

                    packets.add(packet);
                }
            }
        } else {
            if (packet instanceof S12PacketEntityVelocity wrapped) {

                if (wrapped.getEntityID() == mc.thePlayer.getEntityId())
                    needSkip = true;
            }
        }
    }


    @EventTarget
    public void onMoveMath(MoveMathEvent event) {
        if (eating && lagValue.get() && mc.thePlayer.positionUpdateTicks < 20 && !needSkip) event.setCancelled(true);
        else if (needSkip) needSkip = false;
    }

    @EventTarget
    public void onMoveInput(MoveInputEvent event){
        if (eating && noMove.get()) {
            event.setForward(0);
            event.setStrafe(0);
        }
    }

    @EventTarget
    public void onWorld(WorldEvent event){
        toggle();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (eating) {
            final ScaledResolution resolution = event.getScaledResolution();
            final int x = resolution.getScaledWidth() / 2;
            final int y = resolution.getScaledHeight() - 75;
            final float thickness = 5F;

            float percentage = Math.min(movingPackets, 32f) / 32f;

            final int width = 100;
            final int half = width / 2;
            animation.animate((width - 2) * percentage, 40);

            RoundedUtils.drawRound(x - half - 1, y - 1 - 12, width + 1, (int) (thickness + 1) + 12 + 3, 2, new Color(getModule(Interface.class).bgColor(),true));
            RoundedUtils.drawRound(x - half - 1, y - 1, width + 1, (int) (thickness + 1), 2, new Color(getModule(Interface.class).bgColor(),true));

            RoundedUtils.drawGradientHorizontal(x - half, y - 1, animation.getOutput(), thickness, 2, new Color(getModule(Interface.class).color(0)), new Color(getModule(Interface.class).color(90)));

            Fonts.interRegular.get(15).drawCenteredString("Time", x, y - 1 - 11 + 3, -1);

            Fonts.interRegular.get(12).drawCenteredString(new DecimalFormat("0.0").format(percentage * 100) + "%", x, y + 2, -1);
        }
    }
    private void release() {
        if (mc.getNetHandler() == null) return;

        while (!packets.isEmpty()) {
            final Packet<?> packet = packets.poll();

            if (packet instanceof C01PacketChatMessage || packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C07PacketPlayerDigging)
                continue;

            sendPacketNoEvent(packet);
        }

        movingPackets = 0;
    }
    private int getGApple() {
        for (int i = 0;i < 9;i++) {
            final ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

            if (stack == null)
                continue;

            if (stack.getItem() instanceof ItemAppleGold) {
                return i;
            }
        }

        if (autoClose.get())
            this.toggle();

        return -1;
    }
}
