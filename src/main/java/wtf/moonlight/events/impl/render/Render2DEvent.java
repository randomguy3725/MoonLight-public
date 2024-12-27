/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.events.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;
import wtf.moonlight.events.impl.Event;

@Getter
@AllArgsConstructor
public class Render2DEvent implements Event {
    private final float partialTicks;
    private final ScaledResolution scaledResolution;
}

