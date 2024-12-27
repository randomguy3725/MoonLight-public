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
package wtf.moonlight.features.modules.impl.visual;

import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.SliderValue;

@ModuleInfo(name = "AspectRatio", category = ModuleCategory.Visual)
public class AspectRatio extends Module {
    public final SliderValue aspect = new SliderValue("Aspect",1.0f, 0.1f, 5.0f, 0.1f,this);
}
