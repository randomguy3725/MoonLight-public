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

import net.minecraft.util.MovingObjectPosition;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.TickEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.utils.misc.SpoofSlotUtils;
import wtf.moonlight.utils.player.PlayerUtils;

@ModuleInfo(name = "AutoTool", category = ModuleCategory.Player)
public class AutoTool extends Module {

    public final BoolValue spoof = new BoolValue("Spoof",false,this);
    public final BoolValue switchBack = new BoolValue("Switch Back",true,this,() -> !spoof.get());
    private int oldSlot;
    public boolean wasDigging;
    @Override
    public void onDisable() {
        if (this.wasDigging) {
            mc.thePlayer.inventory.currentItem = this.oldSlot;
            this.wasDigging = false;
        }
        SpoofSlotUtils.stopSpoofing();
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && PlayerUtils.findTool(mc.objectMouseOver.getBlockPos()) != -1) {
            if (!this.wasDigging) {
                this.oldSlot = mc.thePlayer.inventory.currentItem;
                if (this.spoof.get()) {
                    SpoofSlotUtils.startSpoofing(this.oldSlot);
                }
            }
            mc.thePlayer.inventory.currentItem = PlayerUtils.findTool(mc.objectMouseOver.getBlockPos());
            this.wasDigging = true;
        } else if (this.wasDigging && (switchBack.get() || spoof.get())) {
            mc.thePlayer.inventory.currentItem = this.oldSlot;
            SpoofSlotUtils.stopSpoofing();
            this.wasDigging = false;
        } else {
            this.oldSlot = mc.thePlayer.inventory.currentItem;
        }
    }
}
