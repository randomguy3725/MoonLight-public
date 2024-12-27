/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.features.command;

public abstract class Command {
    public abstract String[] getAliases();

    public abstract void execute(final String[] p0) throws CommandExecutionException;

    public abstract String getUsage();
}
