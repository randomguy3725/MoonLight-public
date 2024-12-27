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
package wtf.moonlight.events.impl.render;

import lombok.AllArgsConstructor;
import wtf.moonlight.events.impl.Event;

@AllArgsConstructor
public class ChatGUIEvent implements Event {
    public int mouseX, mouseY;
}
