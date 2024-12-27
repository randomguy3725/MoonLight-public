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
package wtf.moonlight.gui.click.skeet.component;

public abstract class ButtonComponent extends Component
{
    public ButtonComponent(final Component parent, final float x, final float y, final float width, final float height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void onMouseClick(final int mouseX, final int mouseY, final int button) {
        if (this.isHovered(mouseX, mouseY)) {
            this.onPress(button);
        }
        super.onMouseClick(mouseX, mouseY, button);
    }
    
    public abstract void onPress(final int mouseButton);
}
