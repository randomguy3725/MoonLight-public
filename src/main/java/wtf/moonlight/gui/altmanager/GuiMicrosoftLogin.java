package wtf.moonlight.gui.altmanager;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.apache.commons.io.IOUtils;
import wtf.moonlight.gui.altmanager.utils.alt.microsoft.MicrosoftLogin;
import wtf.moonlight.gui.font.Fonts;

import java.io.IOException;

public final class GuiMicrosoftLogin extends GuiScreen {
    private volatile MicrosoftLogin microsoftLogin;
    private volatile boolean closed = false;

    private final GuiScreen parentScreen;

    public GuiMicrosoftLogin(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;

        final Thread thread = new Thread("MicrosoftLogin Thread") {
            @Override
            public void run() {
                try {
                    microsoftLogin = new MicrosoftLogin();

                    while (!closed) {
                        if (microsoftLogin.isLogged()) {
                            IOUtils.closeQuietly(microsoftLogin);

                            closed = true;

                            microsoftLogin.setStatus("Login success! " + microsoftLogin.getUserName());

                            mc.session = new Session(microsoftLogin.getUserName(), microsoftLogin.getUuid(), microsoftLogin.getAccessToken(), "mojang");

                            break;
                        }
                    }
                } catch (Throwable e) {
                    closed = true;

                    e.printStackTrace();

                    IOUtils.closeQuietly(microsoftLogin);

                    microsoftLogin.setStatus("Login failed! " + e.getClass().getName() + ":" + e.getMessage());
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.id == 0) {
            if (microsoftLogin != null && !closed) {
                microsoftLogin.close();
                closed = true;
            }

            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 + 50, "Back"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (microsoftLogin == null) {
            Fonts.interBold.get(15).drawCenteredString(EnumChatFormatting.YELLOW + "Logging in...", width / 2.0f, height / 2.0f - 5f, -1);
        } else {
            Fonts.interBold.get(15).drawCenteredString(microsoftLogin.getStatus(), width / 2.0f, height / 2.0f - 5f, -1);
        }
    }
}
