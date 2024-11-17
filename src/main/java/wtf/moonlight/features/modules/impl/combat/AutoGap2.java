package wtf.moonlight.features.modules.impl.combat;

import com.viaversion.viarewind.utils.PacketUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import wtf.moonlight.MoonLight;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.TickEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.MoveMathEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.notification.NotificationManager;
import wtf.moonlight.gui.notification.NotificationType;
import wtf.moonlight.utils.packet.BlinkComponent;
import wtf.moonlight.utils.player.InventoryUtils;

@ModuleInfo(name = "AutoGap2", category = ModuleCategory.Combat)
public class AutoGap2 extends Module {

    public SliderValue duringSendTicks = new SliderValue("DuringSendTicks", 1,0, 10,1, this);
    public SliderValue c03s = new SliderValue("C03Eat",32, 11 ,40, 1, this);
    public SliderValue delay = new SliderValue("Delay", 9,0, 10,11, this);
    public BoolValue auto = new BoolValue("Auto",true,this);
    private int gappleSlot = -1;
    public static int storedC03 = 0;
    public static boolean eating = false;
    public static boolean sending = false;

    public static boolean restart = false;

    @Override
    public void onEnable() {
        storedC03 = 0;
        this.gappleSlot = InventoryUtils.findItem(36, 45, Items.golden_apple);
        if (this.gappleSlot != -1) {
            this.gappleSlot -= 36;
        }
    }

    @Override
    public void onDisable() {
        eating = false;
        BlinkComponent.dispatch();
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (mc.thePlayer == null || mc.thePlayer.isDead) {
            BlinkComponent.dispatch();
            toggle();
            return;
        }
        if (this.gappleSlot == -1) {
            MoonLight.INSTANCE.getNotificationManager().post(NotificationType.WARNING,"Auto Gapple Disabled","Because u dont have any gapple.");
            toggle();
            return;
        }
        if (eating) {
            if (!BlinkComponent.blinking) {
                BlinkComponent.blinking = true;
            }
        } else {
            eating = true;
        }
        if (storedC03 >= c03s.get()) {
            eating = false;
            sending = true;
            sendPacketNoEvent(new C09PacketHeldItemChange(this.gappleSlot));
            sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(this.gappleSlot + 36).getStack()));
            BlinkComponent.dispatch();
            sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            sending = false;
            toggle();
            MoonLight.INSTANCE.getNotificationManager().post(NotificationType.OKAY,"Gapple","Eaten");
            if (auto.get()){
                setEnabled(true);
            }
            return;
        }
        if ((mc.thePlayer.ticksExisted % delay.get()) == 0) {
            releaseC03((int) duringSendTicks.get());
        }
    }

    public void releaseC03(int amount) {
        int i = 0;
        for (int j = 0; j < BlinkComponent.packets.size(); j++) {
            Packet<?> packet = BlinkComponent.packets.poll();
            sendPacketNoEvent(packet);
            if (packet instanceof C03PacketPlayer) {
                storedC03--;
                i++;
            }
            if (i >= amount) {
                break;
            }
        }
    }

    @EventTarget
    public void onMoveMath(MoveMathEvent event){
        if(eating)
            event.setCancelled(true);
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getState() == PacketEvent.State.OUTGOING && event.getPacket() instanceof C07PacketPlayerDigging c07 && c07.getStatus().equals(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)) {
            event.setCancelled(true);
        }
    }
}
