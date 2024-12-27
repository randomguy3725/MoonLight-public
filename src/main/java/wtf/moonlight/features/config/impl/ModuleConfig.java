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
package wtf.moonlight.features.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.config.Config;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.values.Value;
import wtf.moonlight.features.values.impl.*;
import wtf.moonlight.utils.render.ColorUtils;

import java.awt.*;

public class ModuleConfig extends Config {
    public ModuleConfig(String name) {
        super(name);
    }
    @Override
    public void loadConfig(JsonObject object) {
        for (Module module : Moonlight.INSTANCE.getModuleManager().getModules()) {
            if (object.has(module.getName())) {

                JsonObject moduleObject = object.get(module.getName()).getAsJsonObject();

                if (moduleObject.has("State")) {
                    module.setEnabled(moduleObject.get("State").getAsBoolean());
                }

                if (moduleObject.has("Key")) {
                    module.setKeyBind(moduleObject.get("Key").getAsInt());
                }
                if (moduleObject.has("Hidden")) {
                    module.setHidden(moduleObject.get("Hidden").getAsBoolean());
                }

                if (moduleObject.has("Values")) {
                    JsonObject valuesObject = moduleObject.get("Values").getAsJsonObject();

                    for (Value value : module.getValues()) {
                        if (valuesObject.has(value.getName())) {
                            JsonElement theValue = valuesObject.get(value.getName());
                            if (value instanceof SliderValue sliderValue) {
                                sliderValue.setValue(theValue.getAsNumber().floatValue());
                            }
                            if (value instanceof BoolValue boolValue) {
                                boolValue.set(theValue.getAsBoolean());
                            }
                            if (value instanceof ModeValue modeValue) {
                                modeValue.set(theValue.getAsString());
                            }
                            if (value instanceof MultiBoolValue multiBoolValue) {
                                if (!theValue.getAsString().isEmpty()) {
                                    String[] strings = theValue.getAsString().split(", ");
                                    multiBoolValue.getToggled().forEach(option -> option.set(false));
                                    for (String string : strings) {
                                        multiBoolValue.getValues().stream().filter(setting -> setting.getName().equalsIgnoreCase(string)).forEach(boolValue -> boolValue.set(true));
                                    }
                                }
                            }
                            if (value instanceof ColorValue colorValue) {
                                JsonObject colorValues = theValue.getAsJsonObject();
                                colorValue.set(ColorUtils.applyOpacity(new Color(colorValues.get("RGB").getAsInt()), colorValues.get("Alpha").getAsFloat()));
                            }
                            if (value instanceof TextValue textValue) {
                                textValue.setText(theValue.getAsString());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public JsonObject saveConfig() {
        JsonObject object = new JsonObject();
        for (Module module : Moonlight.INSTANCE.getModuleManager().getModules()) {
            JsonObject moduleObject = new JsonObject();

            moduleObject.addProperty("State", module.isEnabled());
            moduleObject.addProperty("Key", module.getKeyBind());
            moduleObject.addProperty("Hidden", module.isHidden());

            JsonObject valuesObject = new JsonObject();

            for (Value value : module.getValues()) {
                if (value instanceof SliderValue sliderValue) {
                    valuesObject.addProperty(value.getName(), sliderValue.get());
                }
                if (value instanceof BoolValue boolValue) {
                    valuesObject.addProperty(value.getName(), boolValue.get());
                }
                if (value instanceof ModeValue modeValue) {
                    valuesObject.addProperty(value.getName(), modeValue.get());
                }
                if (value instanceof MultiBoolValue multiBoolValue) {
                    valuesObject.addProperty(value.getName(), multiBoolValue.isEnabled());
                }
                if (value instanceof ColorValue colorValue) {
                    JsonObject colorValues = new JsonObject();
                    colorValues.addProperty("RGB", Color.HSBtoRGB(colorValue.getHue(), colorValue.getSaturation(), colorValue.getBrightness()));
                    colorValues.addProperty("Alpha", colorValue.getAlpha());
                    valuesObject.add(colorValue.getName(), colorValues);
                }
                if (value instanceof TextValue textValue) {
                    valuesObject.addProperty(value.getName(), textValue.getText());
                }
            }

            moduleObject.add("Values", valuesObject);
            object.add(module.getName(), moduleObject);
        }
        return object;
    }
}