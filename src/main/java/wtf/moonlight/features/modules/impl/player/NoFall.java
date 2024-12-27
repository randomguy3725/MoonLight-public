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
package wtf.moonlight.features.modules.impl.player;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.movement.Scaffold;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.packet.BlinkComponent;
import wtf.moonlight.utils.player.MovementUtils;
import wtf.moonlight.utils.player.PlayerUtils;

@ModuleInfo(name = "NoFall", category = ModuleCategory.Player)
public class NoFall extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"NoGround", "Extra", "Blink", "Watchdog"}, "NoGround", this);
    public final SliderValue minDistance = new SliderValue("Min Distance", 3, 0, 8, 1, this, () -> !mode.is("NoGround"));
    private boolean blinked = false;
    private boolean prevOnGround = false;
    private double fallDistance = 0;
    private boolean timed = false;

    @Override
    public void onEnable() {
        if (PlayerUtils.nullCheck())
            this.fallDistance = mc.thePlayer.fallDistance;
    }

    @Override
    public void onDisable() {
        if (blinked) {
            BlinkComponent.dispatch();
            blinked = false;
        }
        mc.timer.timerSpeed = 1f;
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        setTag(mode.get());
        if (!PlayerUtils.nullCheck())
            return;

        if (event.isPost())
            return;

        if (mc.thePlayer.onGround)
            fallDistance = 0;
        else {
            fallDistance += (float) Math.max(mc.thePlayer.lastTickPosY - event.getY(), 0);

            fallDistance -= MovementUtils.predictedMotionY(mc.thePlayer.motionY, 1);
        }

        if (mc.thePlayer.capabilities.allowFlying) return;
        if (isVoid()) {
            if (blinked) {
                BlinkComponent.dispatch();
                blinked = false;
            }
            return;
        }

        switch (mode.get()) {
            case "NoGround":
                event.setOnGround(false);
                break;

            case "Extra":
                float extra$fallDistance = mc.thePlayer.fallDistance;
                mc.timer.timerSpeed = 1f;
                if (extra$fallDistance - mc.thePlayer.motionY > minDistance.get()) {
                    mc.timer.timerSpeed = 0.5f;
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
                    mc.thePlayer.fallDistance = 0;
                }
                break;

            case "Watchdog":
                if (fallDistance >= minDistance.get() && !isEnabled(Scaffold.class)) {
                    mc.timer.timerSpeed = (float) 0.5;
                    timed = true;
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
                    fallDistance = 0;
                } else if (timed) {
                    mc.timer.timerSpeed = 1;
                    timed = false;
                }
                break;
            case "Blink":
                if (mc.thePlayer.onGround) {
                    if (blinked) {
                        BlinkComponent.dispatch();
                        blinked = false;
                    }

                    this.prevOnGround = true;
                } else if (this.prevOnGround) {
                    if (shouldBlink()) {
                        if (!BlinkComponent.blinking)
                            BlinkComponent.blinking = true;
                        blinked = true;
                    }

                    prevOnGround = false;
                } else if (PlayerUtils.isBlockUnder() && BlinkComponent.blinking && (this.fallDistance - mc.thePlayer.motionY) >= minDistance.get()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    this.fallDistance = 0.0F;
                }
                break;
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        if (mode.is("Blink")) {
            if (blinked)
                mc.fontRendererObj.drawStringWithShadow("Blinking: " + BlinkComponent.packets.size(), (float) sr.getScaledWidth() / 2.0F - (float) mc.fontRendererObj.getStringWidth("Blinking: " + BlinkComponent.packets.size()) / 2.0F, (float) sr.getScaledHeight() / 2.0F + 13.0F, -1);
        }
    }

    private boolean isVoid() {
        return PlayerUtils.overVoid(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    private boolean shouldBlink() {
        return !mc.thePlayer.onGround && !PlayerUtils.isBlockUnder((int) Math.floor(minDistance.get())) && PlayerUtils.isBlockUnder() && !getModule(Scaffold.class).isEnabled();
    }
}
