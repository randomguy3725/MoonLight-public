package wtf.moonlight.gui.mainmenu;

import lombok.Getter;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.util.EnumChatFormatting;
import wtf.moonlight.Moonlight;
import wtf.moonlight.gui.altmanager.GuiAltManager;
import wtf.moonlight.gui.button.MenuButton;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RoundedUtils;
import wtf.moonlight.utils.render.shader.impl.Blur;
import wtf.moonlight.utils.render.shader.impl.MainMenu;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiMainMenu extends GuiScreen {

    private final List<MenuButton> buttons = List.of(
            new MenuButton("Singleplayer"),
            new MenuButton("Multiplayer"),
            new MenuButton("Alts"),
            new MenuButton("Options"),
            new MenuButton("Shutdown"));

    private final List<ChangeLog> logs = new ArrayList<>();

    public GuiMainMenu() {
        logs.add(new ChangeLog("1.8x autoblock for Hypixel", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("Hypixel tower/towermove", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("New Hypixel rotations for scaffold", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("Glide speed bypassing Hypixel", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("New notification mode", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("TargetHUD showing up from far away", ChangeLogType.FIX));
        logs.add(new ChangeLog("Issues with towermove", ChangeLogType.FIX));
    }

    @Override
    public void initGui() {
        buttons.forEach(MenuButton::initGui);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        MainMenu.draw(Moonlight.INSTANCE.getStartTimeLong());

        float buttonWidth = 140;
        float buttonHeight = 25;

        int count = 20;

        RoundedUtils.drawRound(width / 2f - buttonWidth / 2f - 20,(height / 2f) - 60,buttonWidth + 20 * 2,200,10,new Color(0,0,0,64));

        Blur.startBlur();
        RoundedUtils.drawRound(width / 2f - buttonWidth / 2f - 20,(height / 2f) - 60,buttonWidth + 20 * 2,200,10,new Color(0,0,0,64));
        Blur.endBlur(10,3);

        Fonts.interBold.get(35).drawCenteredString(Moonlight.INSTANCE.getClientName(), (width / 2f - buttonWidth / 2f) + buttonWidth / 2, (height / 2f) + count - (buttons.size() * buttonHeight) / 2f, -1);
        Fonts.interMedium.get(14).drawStringWithShadow("Welcome back," + EnumChatFormatting.AQUA + Moonlight.INSTANCE.getDiscordRP().getName(), width - (2 + Fonts.interMedium.get(14).getStringWidth("Welcome back," + Moonlight.INSTANCE.getDiscordRP().getName())), height - (2 + Fonts.interMedium.get(14).getHeight()), -1);

        for (MenuButton button : buttons) {
            button.x = width / 2f - buttonWidth / 2f;
            button.y = ((height / 2f) + count - (buttons.size() * buttonHeight) / 2f) + Fonts.interBold.get(35).getHeight() + 2;
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.clickAction = () -> {
                switch (button.text) {
                    case "Singleplayer" -> mc.displayGuiScreen(new GuiSelectWorld(this));
                    case "Multiplayer" -> mc.displayGuiScreen(new GuiMultiplayer(this));
                    case "Alts" -> mc.displayGuiScreen(new GuiAltManager(this));
                    case "Options" -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                    case "Shutdown" -> mc.shutdown();
                }
            };
            button.drawScreen(mouseX, mouseY);
            count += (int) (buttonHeight + 3);
        }

        int i = 0;

        for (ChangeLog changeLog : logs) {
            if (changeLog != null) {
                if (changeLog.getLog() != null) {
                    Fonts.interBold.get(20).drawStringWithShadow("ChangeLog:", 5, 5, -1);
                    Fonts.interBold.get(15).drawStringWithShadow(changeLog.type.character + changeLog.getLog(), 5, i * (Fonts.interBold.get(20).getHeight()) + Fonts.interBold.get(20).getHeight() + 2, changeLog.type.stringColor);
                }
                i++;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public static class ChangeLog {

        @Getter
        private final String log;
        private final ChangeLogType type;

        public ChangeLog(String log, ChangeLogType type) {
            this.log = log;
            this.type = type;
        }
    }

    public enum ChangeLogType {
        ADDITION("[+]", new Color(54, 239, 61).getRGB()),
        FIX("[~]", new Color(255, 225, 99).getRGB()),
        IMPROVEMENT("[*]", new Color(103, 241, 114).getRGB()),
        REMOVAL("[-]", new Color(255, 64, 64).getRGB()),
        OTHER("[?]", new Color(180, 72, 180).getRGB());

        @Getter
        private final String character;
        private final int stringColor;

        ChangeLogType(String character, int stringColor) {
            this.character = character;
            this.stringColor = stringColor;
        }
    }
}
