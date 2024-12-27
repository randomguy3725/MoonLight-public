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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MultiBoolValue extends Value {
    public List<BoolValue> options;
    public int index;
    public float animation;

    public MultiBoolValue(String name, List<BoolValue> options, Module module, Supplier<Boolean> visible) {
        super(name, module, visible);
        this.options = options;
        index = options.size();
    }

    public MultiBoolValue(String name, List<BoolValue> options, Module module) {
        super(name, module, () -> true);
        this.options = options;
        index = options.size();
    }

    public boolean isEnabled(String name) {
        return Objects.requireNonNull(this.options.stream().filter((option) -> option.getName().equalsIgnoreCase(name)).findFirst().orElse(null)).get();
    }

    public void set(String name, boolean value) {
        Objects.requireNonNull(this.options.stream().filter((option) -> option.getName().equalsIgnoreCase(name)).findFirst().orElse(null)).set(value);
    }

    public List<BoolValue> getToggled() {
        return this.options.stream().filter(BoolValue::get).collect(Collectors.toList());
    }

    public String isEnabled() {
        List<String> includedOptions = new ArrayList<>();
        for (BoolValue option : options) {
            if (option.get()) {
                includedOptions.add(option.getName());
            }
        }
        return String.join(", ", includedOptions);
    }

    public void set(int index, boolean value) {
        this.options.get(index).set(value);
    }

    public boolean isEnabled(int index) {
        return this.options.get(index).get();
    }

    public List<BoolValue> getValues() {
        return this.options;
    }
}
