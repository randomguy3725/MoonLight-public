/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.features.modules.impl.combat;

import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.MouseOverEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;

@ModuleInfo(name = "Reach", category = ModuleCategory.Combat)
public class Reach extends Module {

    public final SliderValue min = new SliderValue("Min Range", 3.0F, 3, 6F, .1f, this);
    public final SliderValue max = new SliderValue("Max Range", 3.3F, 3, 6F, .1f, this);

    @EventTarget
    public void onMouseOver(MouseOverEvent event) {
        event.setRange(MathUtils.randomizeDouble(min.getMin(), max.getMax()));
    }
}
