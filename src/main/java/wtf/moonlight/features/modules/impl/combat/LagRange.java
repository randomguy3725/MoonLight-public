package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.AttackEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.animations.ContinualAnimation;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.packet.PingSpoofComponent;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;

@ModuleInfo(name = "LagRange", category = ModuleCategory.Combat)
public class LagRange extends Module {
    private final ModeValue mode = new ModeValue("Mode",new String[]{"Range","Attack"},"Range",this);
    private final SliderValue attackRange = new SliderValue("Search Range", 4, 4, 15, this);
    private final SliderValue minRange = new SliderValue("Min Range", 4, 4, 15, this,() -> mode.is("Range"));
    private final SliderValue maxRange = new SliderValue("Max Range", 6, 4, 15, this,() -> mode.is("Range"));
    private final SliderValue everyMS = new SliderValue("Every MS", 1, 200, 1000, this);
    private final SliderValue delayMS = new SliderValue("Delay MS", 1, 200, 1000, this);
    private final BoolValue velocity = new BoolValue("Velocity", true, this);
    private final BoolValue teleport = new BoolValue("Teleport", true, this);
    private final BoolValue displayPrevPos = new BoolValue("Display Prev Pos",true,this);
    private boolean blinking = false, picked = false,attacked;
    private final TimerUtils delay = new TimerUtils();
    private final TimerUtils ever = new TimerUtils();
    private double x, y, z;
    public EntityPlayer target;

    @Override
    public void onEnable() {
        blinking = false;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {

        target = PlayerUtils.getTarget(maxRange.get() + 1);

        switch (mode.get()) {
            case "Range": {

                if (target != null && MathUtils.inBetween(minRange.get(), maxRange.get(), PlayerUtils.getDistanceToEntityBox(target)) && mc.thePlayer.canEntityBeSeen(target)) {
                    if (ever.hasTimeElapsed(everyMS.get())) {
                        blinking = true;
                    }

                    if (delay.hasTimeElapsed(delayMS.get()) && blinking) {
                        blinking = false;
                        delay.reset();
                    }

                    if (blinking) {
                        if (!picked) {
                            x = mc.thePlayer.posX;
                            y = mc.thePlayer.posY;
                            z = mc.thePlayer.posZ;
                            picked = true;
                        }

                        PingSpoofComponent.spoof(999999999, true, teleport.get(), velocity.get(), true, true, true);
                        ever.reset();
                    } else {
                        PingSpoofComponent.dispatch();
                        picked = false;
                    }
                } else {
                    PingSpoofComponent.dispatch();
                    picked = false;
                    if (delay.hasTimeElapsed(delayMS.get()) && blinking) {
                        blinking = false;
                        delay.reset();
                    }
                }
            }
            case "Attack":
                if (target != null && PlayerUtils.getDistanceToEntityBox(target) <= attackRange.get() && mc.thePlayer.canEntityBeSeen(target)) {
                    if (attacked) {
                        PingSpoofComponent.dispatch();
                        picked = false;
                        blinking = false;
                        ever.reset();
                    } else {
                        if (ever.hasTimeElapsed(everyMS.get())) {
                            blinking = true;
                        }
                        if (blinking) {
                            if (!picked) {
                                x = mc.thePlayer.posX;
                                y = mc.thePlayer.posY;
                                z = mc.thePlayer.posZ;
                                picked = true;
                            }

                            PingSpoofComponent.spoof(999999999, true, teleport.get(), velocity.get(), true, true, true);
                        } else {
                            PingSpoofComponent.dispatch();
                            picked = false;
                        }
                    }
                } else {
                    PingSpoofComponent.dispatch();
                    picked = false;
                    if (delay.hasTimeElapsed(delayMS.get()) && blinking) {
                        blinking = false;
                        delay.reset();
                    }
                }
                break;
        }
    }

    @EventTarget
    public void onAttack(AttackEvent event) {
        if (target == event.getTargetEntity()) {
            attacked = true;
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (displayPrevPos.get() && blinking) {
            double x = this.x - mc.getRenderManager().viewerPosX;
            double y = this.y - mc.getRenderManager().viewerPosY;
            double z = this.z - mc.getRenderManager().viewerPosZ;
            AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox().expand(0.1D, 0.1, 0.1);
            AxisAlignedBB axis = new AxisAlignedBB(box.minX - mc.thePlayer.posX + x, box.minY - mc.thePlayer.posY + y, box.minZ - mc.thePlayer.posZ + z, box.maxX - mc.thePlayer.posX + x, box.maxY - mc.thePlayer.posY + y, box.maxZ - mc.thePlayer.posZ + z);
            RenderUtils.drawAxisAlignedBB(axis, true, new Color(255, 255, 255, 150).getRGB());
        }
    }
}