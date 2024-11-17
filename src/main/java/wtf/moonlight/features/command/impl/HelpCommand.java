package wtf.moonlight.features.command.impl;

import wtf.moonlight.features.command.Command;
import wtf.moonlight.features.command.CommandManager;
import wtf.moonlight.utils.misc.DebugUtils;

import java.util.Arrays;

public final class HelpCommand extends Command {
    @Override
    public String[] getAliases() {
        return new String[]{"help", "h"};
    }

    @Override
    public void execute(final String[] arguments) {
        for (final Command command : CommandManager.cmd) {
            DebugUtils.sendMessage(Arrays.toString(command.getAliases()) + ": " + command.getUsage());
        }
    }

    @Override
    public String getUsage() {
        return "help/h";
    }
}
