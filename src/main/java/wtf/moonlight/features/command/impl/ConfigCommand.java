package wtf.moonlight.features.command.impl;

import wtf.moonlight.Moonlight;
import wtf.moonlight.features.command.Command;
import wtf.moonlight.features.config.Config;
import wtf.moonlight.features.config.impl.ModuleConfig;
import wtf.moonlight.utils.misc.DebugUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigCommand extends Command {
    @Override
    public String getUsage() {
        return "config/cf/preset <load/save/list> <config>";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"config", "cf", "preset"};
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            DebugUtils.sendMessage("Usage: " + getUsage());
            return;
        }
        if (args.length != 3 && !args[1].equals("list")) {
            DebugUtils.sendMessage("Usage: " + getUsage());
            return;
        }
        List<String> fileList = new ArrayList<>();

        File directory = Moonlight.INSTANCE.getMainDir();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    fileList.add(file.getName().replace(".json", ""));
                }
            }
        }
        if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
            DebugUtils.sendMessage("Configs: " + fileList);
            return;
        }

        ModuleConfig cfg = new ModuleConfig(args[2]);

        switch (args[1]) {
            case "load":
                if (Moonlight.INSTANCE.getConfigManager().loadConfig(cfg)) {
                    DebugUtils.sendMessage("Loaded config: " + args[2]);
                } else {
                    DebugUtils.sendMessage("Invalid config: " + args[2]);
                }
                break;
            case "save":
                if (Moonlight.INSTANCE.getConfigManager().saveConfig(cfg)) {
                    DebugUtils.sendMessage("Saved config: " + args[2]);
                } else {
                    DebugUtils.sendMessage("Invalid config: " + args[2]);
                }
                break;
        }
    }
}
