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
package wtf.moonlight.utils.misc;

import java.util.concurrent.ThreadLocalRandom;

public class StringUtils {
    public static final String sb = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";

    public static String ticksToElapsedTime(int ticks) {
        int i = ticks / 20;
        int j = i / 60;
        i = i % 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }

    public static String upperSnakeCaseToPascal(String string) {
        return string.charAt(0) + string.substring(1).toLowerCase();
    }

    public static String randomString(String pool, int length) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append(pool.charAt(ThreadLocalRandom.current().nextInt(0, pool.length())));
        }

        return builder.toString();
    }
}
