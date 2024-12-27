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
package wtf.moonlight.features.values.impl;

import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.values.Value;
import wtf.moonlight.utils.render.ColorUtils;

import java.awt.*;
import java.util.function.Supplier;

@Getter
@Setter
public class ColorValue extends Value {
    private float hue = 0;
    private float saturation = 1;
    private float brightness = 1;
    private float alpha = 1;
    private boolean rainbow = false;

    public ColorValue(String name, Color color, Module module, Supplier<Boolean> visible) {
        super(name, module, visible);
        set(color);
    }

    public ColorValue(String name, Color color, Module module) {
        super(name, module, () -> true);
        set(color);
    }

    public Color get() {
        return ColorUtils.applyOpacity(Color.getHSBColor(hue, saturation, brightness), alpha);
    }

    public void set(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        alpha = color.getAlpha() / 255.0f;
    }
}
