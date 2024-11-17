package wtf.moonlight.gui.click.augustus;

import net.minecraft.client.gui.GuiScreen;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.render.MouseUtils;

import java.io.IOException;

public class AugustusGUI extends GuiScreen implements InstanceAccess {
    private float posX = -1337, posY = -1337;
    private final boolean dragging = false;
    private final float draggingX = -1337;
    private final float draggingY = -1337;
    private final float width = 600;
    private final float height = 400;
    private final ModuleCategory selectedCategory = ModuleCategory.Combat;

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.posX == -1337 || this.posY == -1337) {
            this.posX = (float) super.width / 2 - this.width / 2;
            this.posY = (float) super.height / 2 - this.height / 2;
        }
        if(MouseUtils.isHovered2(posX, posY, width, 17,mouseX, mouseY) && dragging) {
            posX = mouseX - draggingX;
            posY = mouseY - draggingY;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
