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
package wtf.moonlight.gui.button;

public interface Button {

    void initGui();

    void drawScreen(int mouseX, int mouseY);

    void mouseClicked(int mouseX, int mouseY, int button);

}