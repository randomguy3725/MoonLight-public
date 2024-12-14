package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.MoveMathEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.ContinualAnimation;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.packet.BlinkComponent;
import wtf.moonlight.utils.packet.PacketUtils;
import wtf.moonlight.utils.player.InventoryUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.text.DecimalFormat;

@ModuleInfo(name = "AutoGap", category = ModuleCategory.Combat)
public class AutoGap extends Module {

    public final SliderValue health = new SliderValue("Health", 15, 1, 20, 0.5f, this);
    public final BoolValue onlyWhileKillAura = new BoolValue("Only While Kill Aura",false,this);
    public final SliderValue storeDelay = new SliderValue("Store Delay", 5, 0, 10, 1, this);
    public final SliderValue storePauseTicks = new SliderValue("Store Pause Ticks", 3, 1, 3, 1, this);
    private final TimerUtils timer = new TimerUtils();
    public boolean working = false;
    private int stored = 0;
    private int pauseTicks = 0;
    private boolean storeDelayed = false;
    private boolean toCancel = false;
    private final ContinualAnimation animation = new ContinualAnimation();

    @Override
    public void onDisable() {
        working = false;
        stored = 0;
        reset();
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.isDead || mc.thePlayer.getHealth() >= health.get() || (onlyWhileKillAura.get() && getModule(KillAura.class).target == null)) {
            working = false;
            return;
        }

        if (stored >= 33 && working) {
            reset();

            int foodSlot = InventoryUtils.findItem(0,9,Items.golden_apple);
            sendPacketNoEvent(new C09PacketHeldItemChange(foodSlot));
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            for (int i = 0; i < stored; i++) {
                sendPacketNoEvent(new C03PacketPlayer(mc.thePlayer.onGround));
            }
            sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            working = false;
        }

        if (InventoryUtils.findItem(0,9,Items.golden_apple) != -1) {
            working = true;
        } else {
            working = false;
            stored = 0;
            reset();
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if(event.isPost())
            return;
        if (toCancel)
            event.setCancelled(true);
        toCancel = false;
    }


    @EventTarget
    public void onMoveMath(MoveMathEvent event) {
        if (!working) return;

        if (!storeDelayed && stored % (int) storeDelay.get() == 1) {
            storeDelayed = true;
            pauseTicks = (int) storePauseTicks.get();
        }

        if (pauseTicks > 0) {
            pauseTicks--;
        } else {
            event.setCancelled(true);
            stored++;
            storeDelayed = false;
            toCancel = true;
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (working) {
            final ScaledResolution resolution = event.getScaledResolution();
            final int x = resolution.getScaledWidth() / 2;
            final int y = resolution.getScaledHeight() - 75;
            final float thickness = 5F;

            float percentage = Math.min(stored, 33) / 33f;

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

    public void reset() {
        //working = false;
        //stored = 0;
        pauseTicks = 0;
        storeDelayed = false;
        toCancel = false;
    }
}
