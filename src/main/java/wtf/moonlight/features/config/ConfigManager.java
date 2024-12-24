package wtf.moonlight.features.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.config.impl.ModuleConfig;
import wtf.moonlight.features.config.impl.WidgetConfig;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages the loading and saving of configurations.
 */
@Getter
public class ConfigManager {

    // Managed configurations
    private final ModuleConfig setting = new ModuleConfig("default");
    private final WidgetConfig elements = new WidgetConfig("elements");

    // Gson instance for JSON operations
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Current active configuration
    @Getter
    @Setter
    private String currentConfig = "default";

    /**
     * Initializes the ConfigManager by loading all configurations.
     */
    public ConfigManager() {
        loadConfigs();
    }

    /**
     * Loads a configuration from its associated file.
     *
     * @param config The configuration to load.
     * @return {@code true} if the configuration was loaded successfully, {@code false} otherwise.
     */
    public boolean loadConfig(Config config) {
        if (config == null) {
            Moonlight.LOGGER.warn("Attempted to load a null configuration.");
            return false;
        }

        try (FileReader reader = new FileReader(config.getFile())) {
            JsonParser parser = new JsonParser(); // Create an instance
            JsonObject jsonObject = parser.parse(reader).getAsJsonObject(); // Use instance method
            config.loadConfig(jsonObject);
            Moonlight.LOGGER.info("Loaded config: {}", config.getName());
            return true;
        } catch (IOException e) {
            Moonlight.LOGGER.error("Failed to load config: {}", config.getName(), e);
            return false;
        }
    }

    /**
     * Loads a configuration from an online source represented by a JsonObject.
     *
     * @param config     The configuration to load.
     * @param jsonObject The JsonObject containing the configuration data.
     * @return {@code true} if the configuration was loaded successfully, {@code false} otherwise.
     */
    public boolean loadOnlineConfig(Config config, JsonObject jsonObject) {
        if (config == null || jsonObject == null) {
            Moonlight.LOGGER.warn("Config or JsonObject is null. Cannot load online config.");
            return false;
        }

        try {
            config.loadConfig(jsonObject);
            Moonlight.LOGGER.info("Loaded online config: {}", config.getName());
            return true;
        } catch (Exception e) {
            Moonlight.LOGGER.error("Failed to load online config: {}", config.getName(), e);
            return false;
        }
    }

    /**
     * Saves a configuration to its associated file.
     *
     * @param config The configuration to save.
     * @return {@code true} if the configuration was saved successfully, {@code false} otherwise.
     */
    public boolean saveConfig(Config config) {
        if (config == null) {
            Moonlight.LOGGER.warn("Attempted to save a null configuration.");
            return false;
        }

        JsonObject jsonObject = config.saveConfig();
        String jsonString = gson.toJson(jsonObject);

        try (FileWriter writer = new FileWriter(config.getFile())) {
            writer.write(jsonString);
            Moonlight.LOGGER.info("Saved config: {}", config.getName());
            return true;
        } catch (IOException e) {
            Moonlight.LOGGER.error("Failed to save config: {}", config.getName(), e);
            return false;
        }
    }

    /**
     * Saves all managed configurations.
     */
    public void saveConfigs() {
        if (!saveConfig(setting)) {
            Moonlight.LOGGER.warn("Failed to save setting config.");
        }
        if (!saveConfig(elements)) {
            Moonlight.LOGGER.warn("Failed to save elements config.");
        }
    }

    /**
     * Loads all managed configurations.
     */
    public void loadConfigs() {
        if (!loadConfig(setting)) {
            Moonlight.LOGGER.warn("Failed to load setting config.");
        }
        if (!loadConfig(elements)) {
            Moonlight.LOGGER.warn("Failed to load elements config.");
        }
    }
}