package wtf.moonlight.gui.click.neverlose.components.espelements;

import wtf.moonlight.features.modules.impl.visual.ESP;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.GLUtils;
import wtf.moonlight.utils.render.RenderUtils;

import static org.lwjgl.opengl.GL11.*;

public class HealthElement extends Component {
    private int x,y;
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        x = INSTANCE.getNeverLose().espPreviewComponent.getPosX() + INSTANCE.getNeverLose().getWidth() + 65;
        y = (int) (INSTANCE.getNeverLose().espPreviewComponent.getPosY() + 45 + 75 * (1 - INSTANCE.getNeverLose().espPreviewComponent.getElementsManage().open.getOutput()));

        float y2 = y + 170;

        if (INSTANCE.getModuleManager().getModule(ESP.class).healthBar.get()) {

            glDisable(GL_TEXTURE_2D);
            GLUtils.startBlend();

            float healthBarLeft = x - 2.5F;
            float healthBarRight = x - 0.5F;
            final float health = mc.thePlayer.getHealth();
            final float maxHealth = mc.thePlayer.getMaxHealth();
            final float healthPercentage = health / maxHealth;

            glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 0x96);

            glBegin(GL_QUADS);

            // Background
            {
                glVertex2f(healthBarLeft, y);
                glVertex2f(healthBarLeft, y2);

                glVertex2f(healthBarRight, y2);
                glVertex2f(healthBarRight, y);
            }

            healthBarLeft += 0.5F;
            healthBarRight -= 0.5F;

            final float heightDif = y - y2;
            final float healthBarHeight = heightDif * healthPercentage;

            final float topOfHealthBar = y2 + 0.5F + healthBarHeight;

            final int color = ColorUtils.getColorFromPercentage(healthPercentage);

            RenderUtils.color(color);

            // Bar
            {
                glVertex2f(healthBarLeft, topOfHealthBar);
                glVertex2f(healthBarLeft, y2 - 0.5F);

                glVertex2f(healthBarRight, y2 - 0.5F);
                glVertex2f(healthBarRight, topOfHealthBar);
            }


            final float absorption = mc.thePlayer.getAbsorptionAmount();

            final float absorptionPercentage = Math.min(1.0F, absorption / 20.0F);

            final int absorptionColor = INSTANCE.getModuleManager().getModule(ESP.class).absorptionColor.get().getRGB();

            final float absorptionHeight = heightDif * absorptionPercentage;

            final float topOfAbsorptionBar = y2 + 0.5F + absorptionHeight;

            RenderUtils.color(absorptionColor);

            // Absorption Bar
            {
                glVertex2f(healthBarLeft, topOfAbsorptionBar);
                glVertex2f(healthBarLeft, y2 - 0.5F);

                glVertex2f(healthBarRight, y2 - 0.5F);
                glVertex2f(healthBarRight, topOfAbsorptionBar);
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
