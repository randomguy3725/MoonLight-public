package wtf.moonlight.features.command.impl;

import wtf.moonlight.Moonlight;
import wtf.moonlight.features.command.Command;
import wtf.moonlight.features.config.impl.ModuleConfig;
import wtf.moonlight.utils.misc.DebugUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigCommand extends Command {

    private enum Action {
        LOAD, SAVE, LIST, CREATE, REMOVE, OPENFOLDER, CURRENT;

        public static Action fromString(String action) {
            try {
                return Action.valueOf(action.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    @Override
    public String getUsage() {
        return "config/cf/preset <load/save/list/create/remove/openfolder/current> <config>";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"config", "cf", "preset"};
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            DebugUtils.sendMessage("Usage: " + getUsage());
            return;
        }

        Action action = Action.fromString(args[1]);
        if (action == null) {
            DebugUtils.sendMessage("Invalid action. Usage: " + getUsage());
            return;
        }

        switch (action) {
            case LIST:
                handleList();
                break;
            case OPENFOLDER:
                handleOpenFolder();
                break;
            case CURRENT:
                handleCurrent();
                break;
            default:
                if (args.length < 3) {
                    DebugUtils.sendMessage("Action '" + action.name().toLowerCase() + "' requires an additional argument. Usage: " + getUsage());
                    return;
                }
                String configName = args[2];
                switch (action) {
                    case LOAD:
                        handleLoad(configName);
                        break;
                    case SAVE:
                        handleSave(configName, true);
                        break;
                    case CREATE:
                        handleCreate(configName);
                        break;
                    case REMOVE:
                        handleRemove(configName);
                        break;
                    default:
                        DebugUtils.sendMessage("Unknown action. Usage: " + getUsage());
                }
                break;
        }
    }

    private void handleList() {
        List<String> configs = getConfigList();
        if (configs.isEmpty()) {
            DebugUtils.sendMessage("No configurations found.");
        } else {
            DebugUtils.sendMessage("Configs: " + String.join(", ", configs));
        }
    }

    private void handleOpenFolder() {
        File directory = Moonlight.INSTANCE.getMainDir();
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(directory);
                DebugUtils.sendMessage("Opened config folder.");
            } catch (IOException e) {
                DebugUtils.sendMessage("Failed to open config folder.");
                e.printStackTrace();
            }
        } else {
            DebugUtils.sendMessage("Opening folder is not supported on this system.");
        }
    }

    private void handleCurrent() {
        String currentConfig = Moonlight.INSTANCE.getConfigManager().getCurrentConfig();
        if (currentConfig != null) {
            DebugUtils.sendMessage("Current config: " + currentConfig);
        } else {
            DebugUtils.sendMessage("No config is currently loaded.");
        }
    }

    private void handleLoad(String configName) {
        ModuleConfig cfg = new ModuleConfig(configName);
        if (Moonlight.INSTANCE.getConfigManager().loadConfig(cfg)) {
            Moonlight.INSTANCE.getConfigManager().setCurrentConfig(configName);
            DebugUtils.sendMessage("Loaded config: " + configName);
        } else {
            DebugUtils.sendMessage("Invalid config: " + configName);
        }
    }

    private void handleSave(String configName) {
        handleSave(configName, true);
    }

    /**
     * Saves the current configuration.
     *
     * @param configName The name of the configuration to save.
     * @param notify     Whether to send a success/failure message.
     */
    private void handleSave(String configName, boolean notify) {
        ModuleConfig cfg = new ModuleConfig(configName);
        if (Moonlight.INSTANCE.getConfigManager().saveConfig(cfg)) {
            if (notify) {
                DebugUtils.sendMessage("Saved config: " + configName);
            }
        } else {
            if (notify) {
                DebugUtils.sendMessage("Failed to save config: " + configName);
            }
        }
    }

    private void handleCreate(String configName) {
        File configFile = new File(Moonlight.INSTANCE.getMainDir(), configName + ".json");
        try {
            if (configFile.createNewFile()) {
                Moonlight.INSTANCE.getConfigManager().setCurrentConfig(configName);
                DebugUtils.sendMessage("Created config and set as current: " + configName);
                // Automatically save the newly created config
                handleSave(configName, false); // Pass false to avoid duplicate messages
                DebugUtils.sendMessage("Automatically saved config: " + configName);
            } else {
                DebugUtils.sendMessage("Config already exists: " + configName);
            }
        } catch (IOException e) {
            DebugUtils.sendMessage("Failed to create config: " + configName);
            e.printStackTrace();
        }
    }

    private void handleRemove(String configName) {
        File configFile = new File(Moonlight.INSTANCE.getMainDir(), configName + ".json");
        if (configFile.exists()) {
            if (configFile.delete()) {
                DebugUtils.sendMessage("Removed config: " + configName);
            } else {
                DebugUtils.sendMessage("Failed to remove config: " + configName);
            }
        } else {
            DebugUtils.sendMessage("Config does not exist: " + configName);
        }
    }

    private List<String> getConfigList() {
        File directory = Moonlight.INSTANCE.getMainDir();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return List.of();
        }
        return Arrays.stream(files)
                .filter(File::isFile)
                .map(file -> file.getName().replaceFirst("\\.json$", ""))
                .collect(Collectors.toList());
    }
}