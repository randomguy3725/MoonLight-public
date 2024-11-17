package wtf.moonlight.gui.click.dropdown2;

import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.gui.click.dropdown2.panel.CategoryPanel;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoonGUI extends GuiScreen {

    @Getter
    private final Animation openingAnimation = new DecelerateAnimation(200, 1);
    private boolean closing;
    private final List<CategoryPanel> panels = new ArrayList<>();
    public int scroll;

    public MoonGUI() {
        openingAnimation.setDirection(Direction.BACKWARDS);
        for (ModuleCategory category : ModuleCategory.values()) {
            if (category == ModuleCategory.Search)
                continue;
            panels.add(new CategoryPanel(category));
            for (CategoryPanel panel : panels) {
                float width = 10 + panel.getCategory().ordinal() * (panel.getWidth() + 10);
                panel.setX(width);
                panel.setY(10);
            }
        }
    }

    @Override
    public void initGui() {
        closing = false;
        openingAnimation.setDirection(Direction.FORWARDS);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if (Mouse.hasWheel()) {
            final float wheel = Mouse.getDWheel();

            if (wheel != 0)
                scroll += wheel > 0 ? 15 : -15;
        }

        mouseY -= scroll;

        GlStateManager.translate(0, scroll, 0);
        if (closing) {
            openingAnimation.setDirection(Direction.BACKWARDS);
            if (openingAnimation.finished(Direction.BACKWARDS)) {
                mc.displayGuiScreen(null);
            }
        }

        int finalMouseY = mouseY;
        panels.forEach(panel -> panel.drawScreen(mouseX, finalMouseY));
        GlStateManager.translate(0, -scroll, 0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        mouseY -= scroll;
        GlStateManager.translate(0, scroll, 0);
        int finalMouseY = mouseY;
        panels.forEach(panel -> panel.mouseClicked(mouseX, finalMouseY, mouseButton));
        GlStateManager.translate(0, -scroll, 0);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {

        mouseY -= scroll;
        GlStateManager.translate(0, scroll, 0);
        int finalMouseY = mouseY;
        panels.forEach(panel -> panel.mouseReleased(mouseX, finalMouseY, state));
        GlStateManager.translate(0, -scroll, 0);
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            closing = true;
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

