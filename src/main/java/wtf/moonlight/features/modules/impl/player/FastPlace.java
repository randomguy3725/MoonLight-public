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

import net.minecraft.item.ItemBlock;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.player.PlayerUtils;

@ModuleInfo(name = "FastPlace", category = ModuleCategory.Player)
public class FastPlace extends Module {

    public final SliderValue speed = new SliderValue("Speed", 1, 0, 4, this);

    @EventTarget
    public void onMotion(MotionEvent event) {
        setTag(MathUtils.incValue(speed.get(), 1) + "");
        if (!PlayerUtils.nullCheck())
            return;
        if (mc.thePlayer.getHeldItem() == null)
            return;
        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)
            mc.rightClickDelayTimer = (int) speed.get();
    }
}
