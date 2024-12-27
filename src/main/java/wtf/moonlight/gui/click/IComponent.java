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
package wtf.moonlight.gui.click;

import wtf.moonlight.utils.InstanceAccess;

public interface IComponent extends InstanceAccess {
    default void drawScreen(int mouseX, int mouseY) {
    }

    default void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    default void mouseReleased(int mouseX, int mouseY, int state) {
    }

    default void keyTyped(char typedChar, int keyCode) {
    }
}
