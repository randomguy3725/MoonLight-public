package wtf.moonlight.gui.altmanager.repository;

import lombok.Getter;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjglx.Sys;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.Moonlight;
import wtf.moonlight.gui.altmanager.mslogin.MicrosoftAuthCallback;
import wtf.moonlight.gui.altmanager.repository.credential.AltCredential;
import wtf.moonlight.gui.altmanager.repository.credential.MicrosoftAltCredential;
import wtf.moonlight.gui.altmanager.utils.Checks;
import wtf.moonlight.gui.button.GuiCustomButton;
import wtf.moonlight.gui.font.FontRenderer;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import static net.minecraft.util.EnumChatFormatting.RED;

public final class GuiAddAlt extends GuiScreen {

    private final AltRepositoryGUI gui;
    @Getter
    private GuiGroupAltLogin groupAltInfo;
    private final BiConsumer<GuiAddAlt, ? super AltCredential> consumer;

    private final String addAltButtonName;
    private final String generateButtonName;
    private final String title;

    private PasswordTextField passwordField;
    private GuiTextField usernameField;
    private final List<GuiCustomButton> buttons = new ArrayList<>();

    public GuiAddAlt(@NotNull AltRepositoryGUI gui,
                     @NotNull String addAltButtonName,
                     @NotNull String title,
                     @NotNull String generateButtonName,
                     @NotNull BiConsumer<GuiAddAlt, ? super AltCredential> consumer) {
        this.gui = gui;
        this.addAltButtonName = addAltButtonName;
        this.title = title;
        this.generateButtonName = generateButtonName;
        this.consumer = consumer;
    }

    @Override
    public void initGui() {
        int height = this.height / 4 + 24;

        this.groupAltInfo = new GuiGroupAltLogin(this, title);

        List<GuiButton> buttonList = this.buttonList;
        buttonList.add(new GuiCustomButton(addAltButtonName, 0, width / 2 - 100, height + 72 + 12, 15, Fonts.interSemiBold.get(20)));
        buttonList.add(new GuiCustomButton("Back", 1, width / 2 - 100, height + 72 + 12 + 24, 15, Fonts.interSemiBold.get(20)));
        buttonList.add(new GuiCustomButton("Import alt", 2, width / 2 - 100, height + 72 + 12 + -24, 15, Fonts.interSemiBold.get(20)));
        buttonList.add(new GuiCustomButton(generateButtonName, 3, width / 2 - 100, height + 72 + 12 + -48, 15, Fonts.interSemiBold.get(20)));
        buttonList.add(new GuiCustomButton("Microsoft", 4, width / 2 - 100, height + 72 + 12 + -72, 15, Fonts.interSemiBold.get(20)));

        this.usernameField = new TokenField(0, mc.fontRendererObj, width / 2 - 100, 60, 200, 20, "Alt Email:");
        this.passwordField = new PasswordTextField(1, mc.fontRendererObj, width / 2 - 100, 100, 200, 20, "Password:");
        usernameField.setFocused(true);

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fontRenderer = Fonts.interMedium.get(18);

        drawRect(0, 0, width, height, new Color(32, 34, 37).getRGB()); // background

        GuiTextField usernameField = this.usernameField;
        PasswordTextField passwordField = this.passwordField;

        usernameField.drawTextBox();
        passwordField.drawTextBox();

        groupAltInfo.drawGroup(mc, mouseX, mouseY);

        RoundedUtils.drawRound((int) ((gui.width - this.width) / 2F), 15, 200, 30, 15, new Color(48, 49, 54));

        if (this.title != null) {
            Fonts.interSemiBold.get(22).drawString(this.title,
                    (int) ((gui.width - this.width) / 2F) + (this.width - Fonts.interSemiBold.get(22).getStringWidth(this.title)) / 2.0F,
                    15 + 5,
                    new Color(198, 198, 198).getRGB());
        }

        if (StringUtils.isBlank(usernameField.getText()) && !usernameField.isFocused()) {
            fontRenderer.drawString("Username / E-Mail", width / 2F - 96, 64, 0xFF888888);
        }

        if (StringUtils.isBlank(passwordField.getText()) && !passwordField.isFocused()) {
            fontRenderer.drawString("Password", width / 2F - 96, 104, 0xFF888888);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])$");

    static boolean isEmail(@NotNull String email) {
        Checks.notBlank(email, "email");
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private void add_login() {
        try {
            if (usernameField.getText().trim().isEmpty()) return;

            AltCredential altCredential = new AltCredential(usernameField.getText(), passwordField.getText());
            String login = altCredential.getLogin();

            if (altCredential.getPassword() == null) {
                if (!login.matches("^[a-zA-Z0-9_]+$")) {
                    groupAltInfo.updateStatus(RED + "Illegal characters in username");
                    return;
                }

                if (login.length() > 16) {
                    groupAltInfo.updateStatus(RED + "Username is too long");
                    return;
                }
            } else if (!isEmail(login)) {
                groupAltInfo.updateStatus(RED + "Illegal e-mail");
            }

            consumer.accept(this, altCredential);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                add_login();
                break;

            case 1:
                mc.displayGuiScreen(gui);
                break;

            case 2:
                AltCredential parts = getDataFromClipboard();
                if (parts == null) return;

                try {
                    usernameField.setText(parts.getLogin());
                    passwordField.setText(parts.getPassword());
                } catch (ArrayIndexOutOfBoundsException ignored) {
                } catch (Throwable t) {
                    Moonlight.LOGGER.warn("An unexpected error occurred while importing alt!", t);
                }

                break;
            case 4:
                final MicrosoftAuthCallback callback = new MicrosoftAuthCallback();

                CompletableFuture<MicrosoftAltCredential> future = callback.start((s, o) -> {

                    groupAltInfo.updateStatus(String.format(s, o[0]));
                });

                Sys.openURL(MicrosoftAuthCallback.url);

                future.whenCompleteAsync((o, t) -> {
                    if (t != null) {

                        groupAltInfo.updateStatus(t.getClass().getName() + ':' + t.getMessage());
                        t.printStackTrace();
                    } else {

                        groupAltInfo.updateStatus("Done");
                        consumer.accept(this, o);
                    }
                });

                break;
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (key == 1) {
            mc.displayGuiScreen(gui);
            return;
        }
        if (key == Keyboard.KEY_RETURN) {
            add_login();
            return;
        }

        switch (character) {
            case '\t':
                boolean passwordFieldFocused = passwordField.isFocused();
                boolean usernameFieldFocused = usernameField.isFocused();

                if (usernameFieldFocused && !passwordFieldFocused) {
                    usernameField.setFocused(false);
                    passwordField.setFocused(true);
                    return;
                }

                usernameField.setFocused(true);
                passwordField.setFocused(false);
                break;

            case '\r':
                buttons.get(0).clickAction.run();
                break;
        }

        usernameField.textboxKeyTyped(character, key);
        passwordField.textboxKeyTyped(character, key);
    }

    @Override
    public void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        } catch (Throwable t) {
            Moonlight.LOGGER.warn(t);
        }

        usernameField.mouseClicked(x2, y2, button);
        passwordField.mouseClicked(x2, y2, button);
    }

    private @Nullable AltCredential getDataFromClipboard() {
        String s = null;

        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                s = ((String) transferable.getTransferData(DataFlavor.stringFlavor)).trim();
            }
        } catch (Throwable ignored) {
        }

        if (s == null) {
            return null;
        }

        int index = s.indexOf(':');
        return index == -1 
                ? s.endsWith("@alt.com") ? new AltCredential(s, null) : null
                : new AltCredential(s.substring(0, index), s.substring(index + 1));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}