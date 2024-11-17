package wtf.moonlight.features.modules.impl.player;

import net.minecraft.util.MovingObjectPosition;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.TickEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.utils.player.PlayerUtils;

@ModuleInfo(name = "AutoTool", category = ModuleCategory.Player)
public class AutoTool extends Module {

    @EventTarget
    public void onTick(TickEvent event) {
        if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (PlayerUtils.findTool(mc.objectMouseOver.getBlockPos()) != -1)
                mc.thePlayer.inventory.currentItem = PlayerUtils.findTool(mc.objectMouseOver.getBlockPos());
        }
    }
}
