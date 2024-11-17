package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.MoveMathEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.ContinualAnimation;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.packet.BlinkComponent;
import wtf.moonlight.utils.player.InventoryUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.text.DecimalFormat;

@ModuleInfo(name = "AutoGap", category = ModuleCategory.Combat)
public class AutoGap extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"Dev"}, "Dev", this);
    public final SliderValue health = new SliderValue("Health", 15, 1, 20, 0.5f, this);
    public final SliderValue delay = new SliderValue("Delay", 75, 0, 300, 1, this);
    private final TimerUtils timer = new TimerUtils();
    public boolean eating = false;
    private int movingPackets = 0;
    private int slot = 0;
    private final ContinualAnimation animation = new ContinualAnimation();

    @Override
    public void onEnable() {
        movingPackets = 0;
        slot = -1;
        eating = false;
    }

    @Override
    public void onDisable(){
        eating = false;
        BlinkComponent.dispatch();
        movingPackets = 0;
    }

    @EventTarget
    public void onWorld(WorldEvent event){
        eating = false;
        movingPackets = 0;
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (mode.is("Dev")) {

            if (event.isPost() && eating) {
                movingPackets++;
            }

            if (event.isPre()) {

                if (mc.thePlayer == null || !mc.thePlayer.isEntityAlive()) {
                    eating = false;
                    BlinkComponent.dispatch();
                    movingPackets = 0;

                    return;
                }

                if (!mc.playerController.getCurrentGameType().isSurvivalOrAdventure() || !timer.hasTimeElapsed(delay.get())) {
                    eating = false;
                    BlinkComponent.dispatch();
                    movingPackets = 0;

                    return;
                }
                
                slot = InventoryUtils.findItem(InventoryUtils.ONLY_HOT_BAR_BEGIN, InventoryUtils.END, Items.golden_apple) - 36;
                if (slot == -1 || mc.thePlayer.getHealth() >= health.get()) {
                    if (eating) {
                        eating = false;
                        BlinkComponent.dispatch();
                        movingPackets = 0;
                    }
                } else {
                    eating = true;
                    BlinkComponent.setExempt(C0EPacketClickWindow.class, C16PacketClientStatus.class,C0DPacketCloseWindow.class,C09PacketHeldItemChange.class);
                    BlinkComponent.blinking = true;
                    if (movingPackets >= 32) {
                        sendPacket(new C09PacketHeldItemChange(slot));
                        sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        mc.thePlayer.itemInUseCount -= 32;
                        BlinkComponent.dispatch();
                        movingPackets = 0;
                        sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        timer.reset();
                    } else if (mc.thePlayer.ticksExisted % 3 == 0) {
                        while (!BlinkComponent.packets.isEmpty()) {
                            final Packet<?> packet = BlinkComponent.packets.poll();

                            if (packet instanceof C03PacketPlayer) {
                                movingPackets--;
                            }

                            BlinkComponent.dispatch();
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onMoveMath(MoveMathEvent event){
        if(eating)
            event.setCancelled(true);
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (mode.is("Dev")) {
            final ScaledResolution resolution = new ScaledResolution(mc);
            final int x = resolution.getScaledWidth() / 2;
            final int y = resolution.getScaledHeight() - 75;
            final float thickness = 5F;

            float percentage = Math.min(movingPackets, 32) / 32f;

            final int width = 100;
            final int half = width / 2;
            animation.animate((width - 2) * percentage, 40);

            RoundedUtils.drawRound(x - half - 1, y - 1 - 12, width + 1, (int) (thickness + 1) + 12 + 3, 2, new Color(getModule(Interface.class).bgColor()));
            RoundedUtils.drawRound(x - half - 1, y - 1, width + 1, (int) (thickness + 1), 2, new Color(getModule(Interface.class).bgColor()));

            RoundedUtils.drawGradientHorizontal(x - half, y + 1, animation.getOutput(), thickness, 2, new Color(getModule(Interface.class).color(0)), new Color(getModule(Interface.class).color(90)));

            Fonts.interRegular.get(15).drawCenteredString("Time", x, y - 1 - 11 + 3, -1);

            Fonts.interRegular.get(12).drawCenteredString(new DecimalFormat("0.0").format(percentage * 100) + "%", x, y + 2, -1);
        }
    }
}
