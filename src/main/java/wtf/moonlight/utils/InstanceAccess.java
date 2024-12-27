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
package wtf.moonlight.utils;

import net.minecraft.client.Minecraft;
import wtf.moonlight.Moonlight;

public interface InstanceAccess {

    Minecraft mc = Minecraft.getMinecraft();

    Moonlight INSTANCE = Moonlight.INSTANCE;
}

