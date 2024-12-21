package wtf.moonlight.features.command;

import wtf.moonlight.Moonlight;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.SendMessageEvent;
import wtf.moonlight.features.command.impl.*;
import wtf.moonlight.utils.misc.DebugUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CommandManager {

    public List<Command> cmd = new ArrayList<>();

    public CommandManager() {
        addCommands(new HelpCommand(), new ToggleCommand(), new BindCommand(), new HideCommand(), new FriendCommand(), new ConfigCommand(),new OnlineConfigCommand());

        Moonlight.INSTANCE.getModuleManager().getModules().forEach(module -> {
            if(!module.getValues().isEmpty())
                cmd.add(new ModuleCommand(module,module.getValues()));
        });

        Moonlight.INSTANCE.getEventManager().register(this);
    }

    @EventTarget
    public void onSendMessageEvent(final SendMessageEvent event) {
        final String message;
        if ((message = event.getMessage()).startsWith(".")) {
            event.setCancelled(true);
            final String removedPrefix = message.substring(1);
            final String[] arguments = removedPrefix.split(" ");
            if (!removedPrefix.isEmpty() && arguments.length > 0) {
                for (final Command command : cmd) {
                    for (final String alias : command.getAliases()) {
                        if (alias.equalsIgnoreCase(arguments[0])) {
                            try {
                                command.execute(arguments);
                            } catch (CommandExecutionException e) {
                                DebugUtils.sendMessage("Invalid commands syntax. Hint: " + e.getMessage());
                            }
                            return;
                        }
                    }
                }
                DebugUtils.sendMessage("'" + arguments[0] + "' is not a commands. " + "Try '.help'");
            } else {
                DebugUtils.sendMessage("No arguments were supplied. Try '.help'");
            }
        }
    }
    public void addCommands(Command... checks) {
        this.cmd.addAll(Arrays.asList(checks));
    }
}
