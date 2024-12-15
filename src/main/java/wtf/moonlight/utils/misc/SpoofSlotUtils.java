package wtf.moonlight.utils.misc;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.utils.InstanceAccess;

public class SpoofSlotUtils implements InstanceAccess {

    private static int spoofedSlot;

    @Getter
    private static boolean spoofing;

    public static void startSpoofing(int slot) {
        spoofing = true;
        spoofedSlot = slot;
    }

    public static void stopSpoofing() {
        spoofing = false;
    }

    public static int getSpoofedSlot() {
        return spoofing ? spoofedSlot : mc.thePlayer.inventory.currentItem;
    }

    public static ItemStack getSpoofedStack() {
        return spoofing ? mc.thePlayer.inventory.getStackInSlot(spoofedSlot) : mc.thePlayer.inventory.getCurrentItem();
    }

    @EventTarget
    public void onWorld(WorldEvent event){
        stopSpoofing();
    }
}