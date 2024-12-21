package wtf.moonlight.features.modules.impl.movement;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.TickEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.PostStepEvent;
import wtf.moonlight.events.impl.player.PreStepEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.packet.PacketUtils;
import wtf.moonlight.utils.player.MovementUtils;
import wtf.moonlight.utils.player.PlayerUtils;

@ModuleInfo(name = "Step", category = ModuleCategory.Movement)
public class Step extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"NCP"}, "NCP", this);
    public final SliderValue timer = new SliderValue("Timer", 1, 0.05f, 1, 0.05f, this);
    public final SliderValue delay = new SliderValue("Delay", 1000, 0, 2500, 1, this);
    public final DoubleList MOTION = DoubleList.of(.42, .75, 1);
    private long lastStep = -1;
    private boolean stepped = false;

    @Override
    public void onDisable() {
        mc.thePlayer.stepHeight = 0.6f;
        mc.timer.timerSpeed = 1;
    }

    @EventTarget
    public void onPostStep(PostStepEvent event) {
        switch (mode.get()) {
            case "NCP":
                if (event.getHeight() == 1 && mc.thePlayer.onGround && !PlayerUtils.inLiquid()) {
                    Block block = PlayerUtils.getBlock(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    if (block instanceof BlockStairs || block instanceof BlockSlab) return;

                    mc.timer.timerSpeed = timer.get();
                    stepped = true;
                    for (double motion : MOTION) {
                        MovementUtils.strafe();
                        sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + motion, mc.thePlayer.posZ, false));
                    }
                    mc.thePlayer.stepHeight = 0.6f;
                }
                break;
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (stepped) {
            mc.timer.timerSpeed = 1;
            stepped = false;
        }
        if (System.currentTimeMillis() - lastStep > delay.get())
            mc.thePlayer.stepHeight = 1;
    }

}
