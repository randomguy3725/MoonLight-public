package wtf.moonlight.features.command.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.command.Command;
import wtf.moonlight.utils.misc.DebugUtils;
import wtf.moonlight.utils.misc.HttpUtils;

import java.io.IOException;
import java.util.Locale;

public class OnlineConfigCommand extends Command {
    @Override
    public String getUsage() {
        return "onlineconfig/ocf <load> <config>";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"onlineconfig", "ocf"};
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            DebugUtils.sendMessage("Usage: " + getUsage());
            return;
        }

        String url = Moonlight.INSTANCE.getClientCloud();

        switch (args[1]) {
            case "load":
                JsonObject config;
                try {
                    config = new JsonParser().parse(HttpUtils.get(
                            url + "/configs/" + args[2].toLowerCase(Locale.getDefault())
                    )).getAsJsonObject();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (Moonlight.INSTANCE.getConfigManager().loadOnlineConfig(Moonlight.INSTANCE.getConfigManager().getSetting(),config)) {
                    DebugUtils.sendMessage("Loaded config: " + args[2]);
                } else {
                    DebugUtils.sendMessage("Invalid config: " + args[2]);
                }
                break;
        }
    }
}
