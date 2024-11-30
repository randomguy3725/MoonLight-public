package wtf.moonlight.features.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import wtf.moonlight.MoonLight;
import wtf.moonlight.features.config.impl.ModuleConfig;
import wtf.moonlight.features.config.impl.WidgetConfig;

import java.io.*;
import java.util.ArrayList;

@Getter
public class ConfigManager {

    public final ModuleConfig setting = new ModuleConfig("default");
    public final WidgetConfig elements = new WidgetConfig("elements");
    public final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager() {
        loadConfigs();
    }

    public boolean loadConfig(Config config) {
        if (config == null) {
            return false;
        }
        try {
            JsonObject object = new JsonParser().parse(new FileReader(config.getFile())).getAsJsonObject();
            config.loadConfig(object);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public boolean loadOnlineConfig(Config config,JsonObject object) {
        if (config == null) {
            return false;
        }
        config.loadConfig(object);
        return true;
    }

    public boolean saveConfig(Config config) {
        if (config == null) {
            return false;
        }
        String contentPrettyPrint = new GsonBuilder().setPrettyPrinting().create().toJson(config.saveConfig());
        config.saveConfig();
        try {
            FileWriter writer = new FileWriter(config.getFile());
            writer.write(contentPrettyPrint);
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void saveConfigs() {
        saveConfig(setting);
        saveConfig(elements);
    }

    public void loadConfigs() {
        loadConfig(setting);
        loadConfig(elements);
    }
}