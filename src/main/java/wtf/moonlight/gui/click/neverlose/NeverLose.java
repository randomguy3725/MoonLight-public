package wtf.moonlight.gui.click.neverlose;

import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.impl.visual.ESP;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.gui.click.neverlose.panel.Panel;
import wtf.moonlight.gui.click.neverlose.components.ESPPreviewComponent;
import wtf.moonlight.gui.click.neverlose.panel.config.ConfigPanel;
import wtf.moonlight.gui.click.neverlose.panel.search.SearchPanel;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeverLose extends GuiScreen {
    private final List<Panel> panels = new ArrayList<>();
    private boolean dragging = false;
    @Getter
    private int posX = 40,posY = 40,dragX,dragY,width = 520,height = 420;
    //Search
    private final Animation hover = new DecelerateAnimation(250,1);
    public final ESPPreviewComponent espPreviewComponent = new ESPPreviewComponent();
    public static Color bgColor,
            bgColor2,bgColor3,bgColor4,
            topColor,categoryBgColor,lineColor,lineColor2,
            outlineColor,outlineColor2,sliderBarColor,
            sliderCircleColor,boolCircleColor,boolBgColor,
            boolCircleColor2,boolBgColor2, sliderBgColor;
    public static int textRGB,iconRGB,
            outlineTextRGB,moduleTextRGB;
    public NeverLose() {
        Arrays.stream(ModuleCategory.values()).filter(moduleCategory -> !(moduleCategory == ModuleCategory.Config || moduleCategory == ModuleCategory.Search))
              .forEach(moduleCategory -> panels
              .add(new wtf.moonlight.gui.click.neverlose.panel.Panel(moduleCategory)));
        panels.add(new SearchPanel(ModuleCategory.Search));
        panels.add(new ConfigPanel(ModuleCategory.Config));
        //setting
        sliderBarColor = new Color(0x046190).darker();
        sliderCircleColor = new Color(0x2482ff);
        sliderBgColor = new Color(0x000f25).darker();

        boolBgColor = new Color(0x000314);
        boolBgColor2 = new Color(0x00173a);

        boolCircleColor = new Color(0x00BBFF);
        boolCircleColor2 = new Color(0x7a899a);
        //bg
        topColor = new Color(0x111821).darker();
        bgColor = new Color(0x000C18);
        bgColor2 = new Color(0xDA081222, true);
        bgColor3 = new Color(0x3400BEFF, true).darker().darker().darker().darker();
        bgColor4 = new Color(0x001020);
        //line
        lineColor = new Color(0x131c29);
        lineColor2 = new Color(0x031124).brighter();
        // outline
        outlineColor = new Color(0x051321).brighter();
        outlineColor2 = new Color(0x00193A).darker();
        //text
        textRGB = -1;
        iconRGB = new Color(0x00BBFF).getRGB();
        outlineTextRGB = new Color(0x00BBFF).getRGB();
        moduleTextRGB = new Color(0x2c313b).getRGB();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //color
        //setting
        sliderBarColor = new Color(0x046190).darker();
        sliderCircleColor = new Color(0x2482ff);
        sliderBgColor = new Color(0x000f25).darker();

        boolBgColor = new Color(0x000314);
        boolBgColor2 = new Color(0x00173a);

        boolCircleColor = new Color(0x00BBFF);
        boolCircleColor2 = new Color(0x7a899a);

        //bg
        topColor = new Color(0x111821).darker();
        bgColor = new Color(0x000C18);
        bgColor2 = new Color(0xDA081222, true);
        bgColor3 = new Color(0x3400BEFF, true).darker().darker().darker().darker();
        bgColor4 = new Color(0x001020);
        //line
        lineColor = new Color(0x131c29);
        lineColor2 = new Color(0x031124).brighter();
        // outline
        outlineColor = new Color(0x051321).brighter();
        outlineColor2 = new Color(0x00193A).darker();
        //text
        textRGB = -1;
        iconRGB = new Color(0x00BBFF).getRGB();
        outlineTextRGB = new Color(0x00BBFF).getRGB() ;
        moduleTextRGB = new Color(0x2c313b).getRGB();
        //set default selection
        if (getSelected() == null) {
            if (!panels.isEmpty()) {
                panels.get(0).setSelected(true);
            }
        }
        //update dragging coordinate
        if (dragging) {
            posX = mouseX + dragX;
            posY = mouseY + dragY;
        }
        //blur
        //Moonlight.INSTANCE.getModuleManager().getModule(PostProcessing.class).blurScreen();
        //bg
        RoundedUtils.drawRound(posX,posY,width,height,6f,bgColor2);
        RoundedUtils.drawRound(posX + 136,posY,width - 136,height,6f,bgColor);
        //top
        RoundedUtils.drawRound(posX + 136,posY,width - 136,46,6f,topColor);
        //cover
        RoundedUtils.drawRound(posX + 136,posY,4,height,0,bgColor);
        //top cover
        RoundedUtils.drawRound(posX + 136,posY,4,46,0,topColor);
        RoundedUtils.drawRound(posX + 136,posY + 44,width - 136,4,0,topColor);

        //line
        RoundedUtils.drawRound(posX + 135,posY,.8f,height,0,lineColor);
        RoundedUtils.drawRound(posX + 136,posY + 48,width - 136,.8f,0,lineColor);
        //title
        Fonts.interBold.get(36).drawCenteredStringWithOutline(Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).clientName.getText(),posX + 65,posY + 12,textRGB,outlineTextRGB);
        //user info
        RoundedUtils.drawRound(posX,posY + 384,134,.8f,0,lineColor);
        RenderUtils.drawCircle(posX + 21,posY + 403,0,360,13,1,true,new Color(0x0b0d13).getRGB());
        Fonts.interSemiBold.get(16).drawString("Unknown",posX + 40,posY + 396,textRGB);
        Fonts.interSemiBold.get(16).drawString(EnumChatFormatting.GRAY + "Till: ",posX + 40,posY + 406,-1);
        Fonts.interSemiBold.get(16).drawString("Lifetime",posX + 40 + Fonts.interSemiBold.get(16).getStringWidth(EnumChatFormatting.GRAY + "Till: "),posY + 406,iconRGB);
        //panel
        Fonts.interSemiBold.get(14).drawString("Rage",posX + 14,posY + 42,Color.GRAY.getRGB());
        Fonts.interSemiBold.get(14).drawString("Common",posX + 14,posY + 103,Color.GRAY.getRGB());
        Fonts.interSemiBold.get(14).drawString("Visuals",posX + 14,posY + 212,Color.GRAY.getRGB());
        Fonts.interSemiBold.get(14).drawString("Presets",posX + 14,posY + 249,Color.GRAY.getRGB());
        //search
        hover.setDirection(MouseUtils.isHovered2(posX + width - 20,posY + 18,10,10,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!panels.get(8).isSelected()){
            if (!panels.get(7).isSelected()) {
                Fonts.neverlose.get(20).drawString("j", posX + width - 20, posY + 21, ColorUtils.interpolateColor2(new Color(textRGB), new Color(textRGB).darker().darker(), (float) hover.getOutput()));
            } else {
                Fonts.neverlose.get(24).drawString("j", posX + width - 21, posY + 20, ColorUtils.interpolateColor2(new Color(textRGB), new Color(textRGB).darker().darker(), (float) hover.getOutput()));
            }
        }
        for (Panel panel : panels) {
            categoryBgColor = ColorUtils.applyOpacity(new Color(0, 52, 84), (float) panel.getAnimation().getOutput());
            if (panel.getCategory() != ModuleCategory.Search) {
                panel.drawScreen(mouseX, mouseY);
                if (panel.isSelected()) {
                    RoundedUtils.drawRound(posX + 8, panel.getCategory().ordinal() >= 7 ? posY + 92 + panel.getCategory().ordinal() * 24 : panel.getCategory().ordinal() >= 6 ? posY + 78 + panel.getCategory().ordinal() * 24 : panel.getCategory().ordinal() >= 2 ? posY + 65 + panel.getCategory().ordinal() * 24
                            : posY + 52 + panel.getCategory().ordinal() * 24, 120, 19, 5, categoryBgColor);
                    if (panel.getCategory() == ModuleCategory.Visual && Moonlight.INSTANCE.getModuleManager().getModule(ESP.class).isEnabled()) {
                        espPreviewComponent.drawScreen(mouseX, mouseY);
                    }
                }
                Fonts.interSemiBold.get(18).drawString(panel.getCategory().getName(), posX + 34, (panel.getCategory().ordinal() >= 7 ? posY + 99 + panel.getCategory().ordinal() * 24 : panel.getCategory().ordinal() >= 6 ? posY + 85 + panel.getCategory().ordinal() * 24 : panel.getCategory().ordinal() >= 2 ? posY + 72 + panel.getCategory().ordinal() * 24
                        : posY + 59 + panel.getCategory().ordinal() * 24), textRGB);
            }else if (!panels.get(8).isSelected()) {
                panel.drawScreen(mouseX,mouseY);
            }
            //icon
            renderIcon(panel,iconRGB);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
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
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0){
            for (Panel panel : panels) {
                if (handleSearchPanel(panel, mouseX, mouseY)) {
                    continue;
                }

                if (handleCategoryPanel(panel, mouseX, mouseY)) {
                    break;
                }
            }
            if (MouseUtils.isHovered2(posX,posY,136,42, mouseX, mouseY)) {
                dragging = true;
                dragX = posX - mouseX;
                dragY = posY - mouseY;
            }
        }
        Panel selected = getSelected();
        if (selected != null) {
            espPreviewComponent.mouseClicked(mouseX,mouseY,mouseButton);
            if (!selected.getCategory().getName().equals("Search") && !selected.getCategory().getName().equals("Configs") && !MouseUtils.isHovered2(getPosX() + 140, getPosY() + 49, 380, 368,mouseX,mouseY)) return;
            selected.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
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

    private boolean handleCategoryPanel(Panel panel, int mouseX, int mouseY) {
        if (panel.getCategory() != ModuleCategory.Search && MouseUtils.isHovered2(posX + 8, panel.getCategory().ordinal() >= 7 ? posY + 92 + panel.getCategory().ordinal() * 24 : panel.getCategory().ordinal() >= 6 ? posY + 78 + panel.getCategory().ordinal() * 24 : panel.getCategory().ordinal() >= 2 ? posY + 65 + panel.getCategory().ordinal() * 24
                : posY + 52 + panel.getCategory().ordinal() * 24, 120, 19, mouseX, mouseY)) {
            for (Panel p : panels) {
                p.setSelected(false);
            }
            panel.setSelected(true);
            return true;
        }
        return false;
    }
    public void stuffToBlur(){
        RoundedUtils.drawRound(posX,posY,135,height,6f,bgColor);
        if (panels.get(6).isSelected() && Moonlight.INSTANCE.getModuleManager().getModule(ESP.class).isEnabled()) {
            RoundedUtils.drawRoundOutline(posX + width + 12,posY + 10,200,height - 20,2,.1f, ColorUtils.applyOpacity(bgColor2,1f),new Color(0x08111d).brighter());
        }
    }
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0){
            dragging = false;
        }
        Panel selected = getSelected();
        if (selected != null) {
            selected.mouseReleased(mouseX, mouseY, state);
        }
        if (panels.get(6).isSelected() && Moonlight.INSTANCE.getModuleManager().getModule(ESP.class).isEnabled()) {
            espPreviewComponent.mouseReleased(mouseX,mouseY,state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void initGui() {
        super.initGui();
    }
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    public Panel getSelected() {
        return panels.stream().filter(Panel::isSelected).findAny().orElse(null);
    }
    public void renderIcon(Panel panel,int color){
        Color icon = new Color(color);
        float y = panel.getCategory().ordinal() >= 7 ? posY + 98 + panel.getCategory().ordinal() * 24 :panel.getCategory().ordinal() >= 6 ? posY + 85 + panel.getCategory().ordinal() * 24 : panel.getCategory().ordinal() >= 2 ? posY + 72 + panel.getCategory().ordinal() * 24
                : posY + 59 + panel.getCategory().ordinal() * 24;
        switch (panel.getCategory().getName()){
            case "Combat":
                Fonts.neverlose.get(24).drawString("a",getPosX() + 12,y - 1,icon.getRGB());
                break;
            case "Player":
                Fonts.neverlose.get(24).drawString("b",getPosX() + 12,y - 1,icon.getRGB());
                break;
            case "World":
                Fonts.neverlose.get(24).drawCenteredStringWithShadow("v",getPosX() + 17,y - 1,icon.getRGB());
                break;
            case "Configs":
                Fonts.neverlose.get(30).drawString("K",getPosX() + 14,y - 1,icon.getRGB());
                break;
            case "Movement":
                Fonts.neverlose.get(24).drawString("f",getPosX() + 12,y - 1,icon.getRGB());
                break;
            case "Legit":
                Fonts.neverlose.get(24).drawString("e",getPosX() + 12,y - 1,icon.getRGB());
                break;
            case "Misc":
                Fonts.neverlose.get(24).drawString("l",getPosX() + 12,y,icon.getRGB());
                break;
            case "Exploit":
                Fonts.neverlose.get(24).drawString("g",getPosX() + 12.5,y - 1,icon.getRGB());
                break;
            case "Visuals":
                Fonts.noti2.get(24).drawString("d",getPosX() + 14,y + 1,icon.getRGB());
                break;
        }
    }

}
