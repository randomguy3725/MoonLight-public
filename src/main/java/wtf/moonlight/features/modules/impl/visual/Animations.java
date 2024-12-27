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

import lombok.Getter;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;

@ModuleInfo(name = "Animations", category = ModuleCategory.Visual)
@Getter
public class Animations extends Module {

    private final BoolValue old = new BoolValue("Old", false, this);
    private final ModeValue type = new ModeValue("Block Anim", new String[]{"Swank", "Swing", "Swang", "Swong", "Swaing", "Punch", "Push", "Stella", "Styles", "Slide", "Interia", "Ethereal", "1.7", "Sigma", "Exhibition", "Old Exhibition", "Smooth"}, "1.7", this, () -> !old.get());
    private final BoolValue blockWhenSwing = new BoolValue("Block When Swing", false, this);
    private final ModeValue hit = new ModeValue("Hit", new String[]{"Vanilla", "Smooth"}, "Vanilla", this, () -> !old.get());
    private final SliderValue slowdown = new SliderValue("Slow Down", 0, -5, 15, 1, this);
    private final SliderValue downscaleFactor = new SliderValue("Scale", 0f, 0.0f, 0.5f, .1f, this);
    private final BoolValue rotating = new BoolValue("Rotating", false, this, () -> !old.get());
    private final SliderValue x = new SliderValue("Item-X", 0.0F, -1.0F, 1.0F, .05f, this);
    private final SliderValue y = new SliderValue("Item-Y", 0.0F, -1.0F, 1.0F, .05f, this);
    private final SliderValue z = new SliderValue("Item-Z", 0.0F, -1.0F, 1.0F, .05f, this);
    private final SliderValue bx = new SliderValue("Block-X", 0.0F, -1.0F, 1.0F, .05f, this);
    private final SliderValue by = new SliderValue("Block-Y", 0.0F, -1.0F, 1.0F, .05f, this);
    private final SliderValue bz = new SliderValue("Block-Z", 0.0F, -1.0F, 1.0F, .05f, this);
    private final BoolValue walking = new BoolValue("Funny", false, this);
}
