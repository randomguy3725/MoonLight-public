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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.player.PlayerUtils;

@ModuleInfo(name = "BlockHit",category = ModuleCategory.Legit)
public class BlockHit extends Module {

    public final BoolValue swingCheck = new BoolValue("Swing Check",true,this);

    public TimerUtils timer = new TimerUtils();
    public EntityPlayer target;
    public boolean predicted;

    @Override
    public void onEnable() {
        timer.reset();
        predicted = false;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event){
        setTag("Predict");
        target = PlayerUtils.getTarget(16);
    }

    @EventTarget
    public void onPacket(PacketEvent event) {

        Packet packet = event.getPacket();

        /*if (packet instanceof S19PacketEntityStatus s19 && s19.getEntity(mc.theWorld) == mc.thePlayer && getModule(KillAura.class).isHoldingSword()) {
            event.setCancelled(true);
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
            s19.processPacket(mc.getNetHandler());
        }*/

        /*if (packet instanceof C02PacketUseEntity c02 &&
                c02.getAction() == C02PacketUseEntity.Action.ATTACK &&
                mc.theWorld.getEntityByID(c02.getEntityId()) instanceof EntityPlayer target &&
                timer.hasTimeElapsed(500) &&
                target.hurtTime == 0 &&
                getModule(KillAura.class).isHoldingSword()
        ) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
            timer.reset();
        }*/

        if ((swingCheck.get() && mc.thePlayer.isSwingInProgress || !swingCheck.get()) && !(packet instanceof C02PacketUseEntity)&& getModule(KillAura.class).isHoldingSword() && target != null && target.swingProgressInt > 0 && (mc.thePlayer.hurtTime == 0 && timer.hasTimeElapsed(500) || mc.thePlayer.hurtTime == 9)) {
            mc.gameSettings.keyBindUseItem.setPressed(true);
            predicted = true;
            timer.reset();
        }

        if(predicted && (mc.thePlayer.isBlocking() || !getModule(KillAura.class).isHoldingSword())) {
            mc.gameSettings.keyBindUseItem.setPressed(false);
            predicted = false;
        }
    }
}
