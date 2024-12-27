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
import wtf.moonlight.features.values.impl.BoolValue;

@ModuleInfo(name = "Rotation", category = ModuleCategory.Visual)
public class Rotation extends Module {

    public final BoolValue body = new BoolValue("Render Body", true, this);
    public final BoolValue realistic = new BoolValue("Realistic", true, this, body::get);
    public final BoolValue fixAim = new BoolValue("Fix Aim", true, this);
}
