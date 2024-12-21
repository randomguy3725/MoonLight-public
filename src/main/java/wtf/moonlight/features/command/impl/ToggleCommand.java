package wtf.moonlight.features.command.impl;

import wtf.moonlight.Moonlight;
import wtf.moonlight.features.command.Command;
import wtf.moonlight.features.command.CommandExecutionException;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.utils.misc.DebugUtils;

public final class ToggleCommand extends Command {
    @Override
    public String[] getAliases() {
        return new String[]{"toggle", "t"};
    }

    @Override
    public void execute(final String[] arguments) throws CommandExecutionException {
        if (arguments.length == 2) {
            final String moduleName = arguments[1];
            for (final Module module : Moonlight.INSTANCE.getModuleManager().getModules()) {
                if (module.getName().replaceAll(" ", "").equalsIgnoreCase(moduleName)) {
                    module.toggle();
                    DebugUtils.sendMessage(module.getName() + " has been " + (module.isEnabled() ? "\u00a7AEnabled\u00a77." : "\u00a7CDisabled\u00a77."));
                    return;
                }
            }
        }
        throw new CommandExecutionException(this.getUsage());
    }

    @Override
    public String getUsage() {
        return "toggle/t <module name>";
    }
}
