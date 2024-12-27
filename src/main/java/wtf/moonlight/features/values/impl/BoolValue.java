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
package wtf.moonlight.features.values.impl;


import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.values.Value;

import java.util.function.Supplier;

public class BoolValue extends Value {
    private boolean value;
    public float anim;

    public BoolValue(String name, boolean value, Module module, Supplier<Boolean> visible) {
        super(name, module, visible);
        this.value = value;
    }

    public BoolValue(String name, boolean value, Module module) {
        super(name, module, () -> true);
        this.value = value;
    }

    public BoolValue(String name, boolean value) {
        super(name, null, () -> true);
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    public void toggle() {
        value = !value;
    }

    public void set(boolean value) {
        this.value = value;
    }
}
