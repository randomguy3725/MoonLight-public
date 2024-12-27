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
package wtf.moonlight.gui.click.neverlose.components.espelements;

import wtf.moonlight.features.modules.impl.visual.ESP;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.utils.render.GLUtils;
import wtf.moonlight.utils.render.RenderUtils;

import static org.lwjgl.opengl.GL11.*;

public class BoxElement extends Component {
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (INSTANCE.getModuleManager().getModule(ESP.class).box.get()) {
            float x = INSTANCE.getNeverLose().espPreviewComponent.getPosX() + INSTANCE.getNeverLose().getWidth() + 65;
            float y = (float) (INSTANCE.getNeverLose().espPreviewComponent.getPosY() + 45 + 75 * (1 - INSTANCE.getNeverLose().espPreviewComponent.getElementsManage().open.getOutput()));
            float x2 = x + 90;
            float y2 = y + 170;

            glDisable(GL_TEXTURE_2D);
            GLUtils.startBlend();

            glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 0x96);
            glBegin(GL_QUADS);

            // Background
            {
                // Left
                glVertex2f(x, y);
                glVertex2f(x, y2);
                glVertex2f(x + 1.5F, y2);
                glVertex2f(x + 1.5F, y);

                // Right
                glVertex2f(x2 - 1.5F, y);
                glVertex2f(x2 - 1.5F, y2);
                glVertex2f(x2, y2);
                glVertex2f(x2, y);

                // Top
                glVertex2f(x + 1.5F, y);
                glVertex2f(x + 1.5F, y + 1.5F);
                glVertex2f(x2 - 1.5F, y + 1.5F);
                glVertex2f(x2 - 1.5F, y);

                // Bottom
                glVertex2f(x + 1.5F, y2 - 1.5F);
                glVertex2f(x + 1.5F, y2);
                glVertex2f(x2 - 1.5F, y2);
                glVertex2f(x2 - 1.5F, y2 - 1.5F);
            }

            RenderUtils.color(INSTANCE.getModuleManager().getModule(ESP.class).boxColor.get().getRGB());

            // Box
            {
                // Left
                glVertex2f(x + 0.5F, y + 0.5F);
                glVertex2f(x + 0.5F, y2 - 0.5F);
                glVertex2f(x + 1, y2 - 0.5F);
                glVertex2f(x + 1, y + 0.5F);

                // Right
                glVertex2f(x2 - 1, y + 0.5F);
                glVertex2f(x2 - 1, y2 - 0.5F);
                glVertex2f(x2 - 0.5F, y2 - 0.5F);
                glVertex2f(x2 - 0.5F, y + 0.5F);

                // Top
                glVertex2f(x + 0.5F, y + 0.5F);
                glVertex2f(x + 0.5F, y + 1);
                glVertex2f(x2 - 0.5F, y + 1);
                glVertex2f(x2 - 0.5F, y + 0.5F);

                // Bottom
                glVertex2f(x + 0.5F, y2 - 1);
                glVertex2f(x + 0.5F, y2 - 0.5F);
                glVertex2f(x2 - 0.5F, y2 - 0.5F);
                glVertex2f(x2 - 0.5F, y2 - 1);
            }

            RenderUtils.resetColor();

            glEnd();

            glEnable(GL_TEXTURE_2D);
            GLUtils.endBlend();
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }
}
