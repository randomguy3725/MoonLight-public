package wtf.moonlight.gui.click.menu;

import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import wtf.moonlight.MoonLight;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.gui.click.menu.panel.Panel;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;
import wtf.moonlight.utils.render.shader.impl.Shadow;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuGUI extends GuiScreen {

    private final List<Panel> panels = new ArrayList<>();
    private boolean dragging = false;
    @Getter
    private int posX = 300;
    @Getter
    private int posY = 175;
    @Getter
    private int dragX;
    @Getter
    private int dragY;
    @Getter
    private final int width = 500;
    @Getter
    private final int height = 350;
    private Framebuffer shadowFramebuffer = new Framebuffer(1, 1, false);

    public MenuGUI() {
        Arrays.stream(ModuleCategory.values()).filter(moduleCategory -> !(moduleCategory == ModuleCategory.Search))
                .forEach(moduleCategory -> panels
                        .add(new Panel(moduleCategory)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        shadowFramebuffer = RenderUtils.createFrameBuffer(shadowFramebuffer, true);
        shadowFramebuffer.framebufferClear();
        shadowFramebuffer.bindFramebuffer(true);
        RoundedUtils.drawRound(posX, posY, width, height, 6, new Color(20, 20, 20, 255));
        shadowFramebuffer.unbindFramebuffer();
        Shadow.renderBloom(shadowFramebuffer.framebufferTexture, 15, 1);

        RoundedUtils.drawRound(posX, posY, width, height, 6, new Color(20, 20, 20, 255));

        RenderUtils.drawImage(new ResourceLocation("moonlight/img/logo.png"), posX + 10, posY + 5, 45, 45);
        Fonts.interSemiBold.get(22).drawString("MoonLight", posX + 65, posY + 25, -1);
        Fonts.interSemiBold.get(20).drawString("Welcome back," + MoonLight.INSTANCE.getDiscordRP().getName(), posX + width - Fonts.interSemiBold.get(24).getStringWidth("Welcome back," + MoonLight.INSTANCE.getDiscordRP().getName()) - 10, posY + 25, -1);

        if (getSelected() == null) {
            if (!panels.isEmpty()) {
                panels.get(0).setSelected(true);
            }
        }

        if (dragging) {
            posX = mouseX + dragX;
            posY = mouseY + dragY;
        }

        int offset = 0;
        for (Panel panel : panels) {
            if (panel.getCategory() != ModuleCategory.Search) {
                panel.drawScreen(mouseX, mouseY);
                int off = Fonts.interMedium.get(18).getStringWidth(panel.getCategory().getName()) + 5;
                Fonts.interMedium.get(18).drawString(panel.getCategory().getName(), posX + 20 + offset, posY + 55, panel.isSelected() ? -1 : new Color(70, 70, 70, 255).getRGB());
                offset += off;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int offset = 0;
            for (Panel panel : panels) {
                float off = Fonts.interMedium.get(18).getStringWidth(panel.getCategory().getName()) + 5;
                if (handleSearchPanel(panel, mouseX, mouseY)) {
                    continue;
                }

                if (handleCategoryPanel(panel, posX + offset, posY, mouseX, mouseY)) {
                    break;
                }
                offset += off;
            }
            if (MouseUtils.isHovered2(posX, posY, width, 50, mouseX, mouseY)) {
                dragging = true;
                dragX = posX - mouseX;
                dragY = posY - mouseY;
            }
        }
        Panel selected = getSelected();
        if (selected != null) {
            if (!selected.getCategory().getName().equals("Search") && !selected.getCategory().getName().equals("Configs") && !MouseUtils.isHovered2(getPosX(), getPosY() + 65, getWidth(), getHeight() - 65, mouseX, mouseY))
                return;
            selected.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            dragging = false;
        }
        Panel selected = getSelected();
        if (selected != null) {
            selected.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        Panel selected = getSelected();
        if (selected != null) {
            selected.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private boolean handleSearchPanel(Panel panel, int mouseX, int mouseY) {
        if (panel.getCategory() == ModuleCategory.Search && !panels.get(8).isSelected() && MouseUtils.isHovered2(posX + width - 20, posY + 18, 10, 10, mouseX, mouseY)) {
            if (!panel.isSelected()) {
                for (Panel p : panels) {
                    p.setSelected(false);
                }
                panel.setSelected(true);
            } else {
                for (Panel p : panels) {
                    p.setSelected(false);
                }
                panels.get(0).setSelected(true);
            }
            return true;
        }
        return false;
    }

    private boolean handleCategoryPanel(Panel panel, int posX, int posY, int mouseX, int mouseY) {
        if (panel.getCategory() != ModuleCategory.Search && MouseUtils.isHovered2(posX + 20, posY + 55, Fonts.interMedium.get(18).getStringWidth(panel.getCategory().getName()), Fonts.interMedium.get(18).getHeight(), mouseX, mouseY)) {
            for (Panel p : panels) {
                p.setSelected(false);
            }
            panel.setSelected(true);
            return true;
        }
        return false;
    }

    public Panel getSelected() {
        return panels.stream().filter(Panel::isSelected).findAny().orElse(null);
    }
}
