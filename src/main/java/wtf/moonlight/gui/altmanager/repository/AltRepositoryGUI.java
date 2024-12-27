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
package wtf.moonlight.gui.altmanager.repository;


import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import com.thealtening.api.response.AccountDetails;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.gui.altmanager.login.AltLoginThread;
import wtf.moonlight.gui.altmanager.login.AltType;
import wtf.moonlight.gui.altmanager.login.SessionUpdatingAltLoginListener;
import wtf.moonlight.gui.altmanager.mslogin.Auth;
import wtf.moonlight.gui.altmanager.repository.credential.AltCredential;
import wtf.moonlight.gui.altmanager.repository.credential.AlteningAltCredential;
import wtf.moonlight.gui.altmanager.repository.credential.MicrosoftAltCredential;
import wtf.moonlight.gui.altmanager.repository.hypixel.HypixelProfile;
import wtf.moonlight.gui.altmanager.repository.hypixel.HypixelProfileFactory;
import wtf.moonlight.gui.altmanager.repository.tclient.TClient;
import wtf.moonlight.gui.altmanager.utils.FakeEntityPlayer;
import wtf.moonlight.gui.button.GuiCustomButton;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.gui.notification.NotificationType;
import wtf.moonlight.utils.misc.FilteredArrayList;
import wtf.moonlight.utils.misc.HttpResponse;
import wtf.moonlight.utils.misc.HttpUtils;
import wtf.moonlight.utils.render.shader.impl.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static net.minecraft.util.EnumChatFormatting.GREEN;
import static net.minecraft.util.EnumChatFormatting.RED;

public class AltRepositoryGUI extends GuiScreen {
    
    static final int PLAYER_BOX_WIDTH = 320;
    static final int PLAYER_BOX_HEIGHT = 36;
    static final float PLAYER_BOX_SPACE = 3F;
    static final int BUTTON_WIDTH = PLAYER_BOX_WIDTH / 2 - 1;
    static final int BUTTON_HEIGHT = 30;
    static final int VERTICAL_MARGIN = 25;
    static final int HORIZONTAL_MARGIN = 15;
    static final int SCROLL_ALTS = 1;

    @NonNull
    private final Moonlight moonlight;
    @NonNull
    private final TClient tclient = new TClient();
    private final Logger logger = LogManager.getLogger();
    @Getter
    @Setter
    private Alt currentAlt;

    private GuiGroupPlayerBox groupPlayerBox;

    @NonNull
    private EnumSort sortType = EnumSort.DATE;
    private GuiCustomButton sortButton;

    private TokenField searchField;
    private final FilteredArrayList<Alt, Alt> alts = new FilteredArrayList<>(
            ObjectLists.synchronize(new ObjectArrayList<>()), alt -> {
        if (this.searchField == null || StringUtils.isBlank(this.searchField.getText())) return alt;
        if (alt == null) return null;

        String s;

        if (alt.getPlayer() != null && alt.getPlayer().getName() != null) {
            s = alt.getPlayer().getName();
        } else if (alt.getCredential() != null) {
            s = alt.getCredential().getLogin();
        } else {
            return null;
        }

        return s.toLowerCase().startsWith(this.searchField.getText().trim().toLowerCase()) ? alt : null;
    }, () -> this.sortType.getComparator());
    
    private DynamicTexture viewportTexture = new DynamicTexture(256, 256);
    @Getter
    private String tokenContent = "";

    private int panoramaTimer;
    private ResourceLocation backgroundTexture;
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};

    public AltRepositoryGUI(@NonNull Moonlight moonlight) {
        this.moonlight = moonlight;
        loadAlts();
        if (StringUtils.isBlank(this.tokenContent)) {
            try {
                this.tokenContent = Strings.nullToEmpty(readApiKey());
            } catch (IOException e) {
                this.logger.error("An error occurred while reading data file", e);
                this.tokenContent = "";
            }
        }
    }

    public void updateScreen() {
        ++this.panoramaTimer;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 69:
                StringSelection stringSelection = new StringSelection(mc.session.getUsername());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                break;
            case 0:
                this.mc.displayGuiScreen(
                        new GuiAddAlt(this, "Add Alt", "Add Alt", "Generate and save", (gui, credentials) -> {
                            addAlt(credentials);
                            saveAlts();

                            this.mc.displayGuiScreen(this);
                        }));

                break;
            case 1: 
                removeCurrentAlt();
                break;
            case 2:
                this.mc.displayGuiScreen(
                        new GuiAddAlt(this, "Login", "Alt Login", "Generate and log in", (gui, credentials) -> {
                            gui.getGroupAltInfo().updateStatus(GREEN + "Logging in...");

                            if (!(credentials instanceof MicrosoftAltCredential)){
                                new AltLoginThread(credentials, new SessionUpdatingAltLoginListener() {

                                    @Override
                                    public void onLoginSuccess(AltType altType, Session session) {
                                        super.onLoginSuccess(altType, session);
                                        final StringBuilder builder = new StringBuilder(
                                                "Logged in! Username: " + session.getUsername());

                                        if (credentials instanceof AlteningAltCredential) {
                                            final AlteningAltCredential alteningCredential = (AlteningAltCredential) credentials;

                                            final AccountDetails details = alteningCredential.getDetails();
                                            final String hypixelRank = details.getHypixelRank();

                                            builder.append(" | ").append(details.getHypixelLevel()).append(" Lvl")
                                                    .append(hypixelRank != null ? " | " + hypixelRank : "");
                                        }

                                        moonlight.getNotificationManager().post(NotificationType.INFO,builder.toString());
                                        AltRepositoryGUI.this.mc.displayGuiScreen(AltRepositoryGUI.this);
                                    }

                                    @Override
                                    public void onLoginFailed() {
                                        gui.getGroupAltInfo().updateStatus(RED + "Invalid credentials!");
                                        AltRepositoryGUI.this.mc.displayGuiScreen(AltRepositoryGUI.this);
                                    }
                                }).run();
                            } else {
                                try {
                                    MicrosoftAltCredential microsoftAltCredential = (MicrosoftAltCredential) credentials;
                                    Map.Entry<String, String> authRefreshTokens = Auth.refreshToken(microsoftAltCredential.getRefreshToken());
                                    String xblToken = Auth.authXBL(authRefreshTokens.getKey());
                                    Map.Entry<String, String> xstsTokenUserhash = Auth.authXSTS(xblToken);
                                    String accessToken = Auth.authMinecraft(xstsTokenUserhash.getValue(), xstsTokenUserhash.getKey());
                                    
                                    if (!Alt.accountCheck(accessToken))
                                        return;

                                    mc.session = new Session(microsoftAltCredential.getName(),
                                            microsoftAltCredential.getUUID().toString(),
                                            accessToken, "msa");
                                    AltRepositoryGUI.this.mc.displayGuiScreen(AltRepositoryGUI.this);
                                    moonlight.getNotificationManager().post(NotificationType.OKAY,"Logged in! Username: " + microsoftAltCredential.getName());
                                } catch (Exception e) {
                                    moonlight.getNotificationManager().post(NotificationType.WARNING,"Failed to logged in");
                                }
                            }
                        }));
                break;
            case 3:
                refreshAlts();
                break;
            case 5:
                final EnumSort[] values = EnumSort.values();

                this.sortType = values[(this.sortType.ordinal() + 1) % values.length];
                this.sortButton.displayString = this.sortType.getCriteria();

                this.alts.update();
                setScrolledAndUpdate(0);

                break;
            case 70:
                try {
                    JOptionPane optionPane = new JOptionPane("Minecraft accesstoken");
                    optionPane.setWantsInput(true);
                    JDialog dialog = optionPane.createDialog("TokenLogin Dialog");
                    dialog.setAlwaysOnTop(true);
                    dialog.setVisible(true);
                    dialog.dispose();
                    String token = (String) optionPane.getInputValue();
                    if (!Objects.equals(token, "")) {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + token);
                        headers.put("User-Agent", "MojangSharp/0.1");
                        headers.put("Charset", "UTF-8");
                        headers.put("connection", "keep-alive");
                        String playerStatsRaw = HttpUtil.get(new URL("https://api.minecraftservices.com/minecraft/profile"), headers);
                        JSONObject playerStats = new JSONObject(playerStatsRaw);
                        String name = playerStats.getString("name");
                        String uuid = playerStats.getString("id");
                        
                        if (!Alt.accountCheck(token))
                            break;
                        
                        Session session = new Session(name, uuid, token, "msa");
                        mc.session = session;

                        moonlight.getNotificationManager().post(NotificationType.OKAY,"Logged in! " + name);
                    }
                } catch (IOException e) {
                    if (e.getMessage().indexOf("Server returned HTTP response code: 401 for URL") != -1) {
                        moonlight.getNotificationManager().post(NotificationType.WARNING,"Oops, something went wrong. It seems that your accesstoken expired.");
                    } else {
                        moonlight.getNotificationManager().post(NotificationType.WARNING,"Oops, something went wrong. See logs for details.");
                        e.printStackTrace();
                    }
                } catch (Throwable e) {
                    moonlight.getNotificationManager().post(NotificationType.WARNING,"Oops, something went wrong. See logs for details.");
                    e.printStackTrace();
                }
                break;
            case 71:
                try {
                    JFileChooser jFileChooser = new JFileChooser() {
                        @Override
                        protected JDialog createDialog(Component parent) throws HeadlessException {
                            JDialog dialog = super.createDialog(parent);
                            dialog.setModal(true);
                            dialog.setAlwaysOnTop(true);
                            return dialog;
                        }
                    };
                    int returnVal = jFileChooser.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File skinFile = jFileChooser.getSelectedFile();
                        String url = "https://api.minecraftservices.com/minecraft/profile/skins";
                        Map<String, Object> keyValues = new HashMap<>();
                        Map<String, File> filePathMap = new HashMap<>();
                        Map<String, Object> headers = new HashMap<>();

                        if (!skinFile.getName().endsWith(".png")) {
                            moonlight.getNotificationManager().post(NotificationType.WARNING,"Its seems that the file isn't a skin..");
                            break;
                        }
                        
                        int result = JOptionPane.showConfirmDialog((Component) null, "Is this a slim skin?", "alert", JOptionPane.YES_NO_CANCEL_OPTION);
                        if (result == JOptionPane.CANCEL_OPTION) break;
                        String skinType;
                        if (result == JOptionPane.YES_OPTION) {
                            skinType = "slim";
                        } else {
                            skinType = "classic";
                        }

                        keyValues.put("variant", skinType);
                        filePathMap.put("file", skinFile);
                        headers.put("Accept", "*/*");
                        headers.put("Authorization", "Bearer " + mc.session.getToken());
                        headers.put("User-Agent", "MojangSharp/0.1");

                        HttpResponse response = HttpUtils.postFormData(url, filePathMap, keyValues, headers);
                        if (response.getCode() == 200 || response.getCode() == 204) {
                            moonlight.getNotificationManager().post(NotificationType.OKAY,"Skin changed!");
                        } else {
                            moonlight.getNotificationManager().post(NotificationType.WARNING,"Failed to change skin.");
                            logger.error(response);
                        }
                    }
                } catch (Exception e) {
                    moonlight.getNotificationManager().post(NotificationType.WARNING,"Failed to change skin.");
                    e.printStackTrace();
                }
                break;
            /*case 72:
                try {
                    String url = JOptionPane.showInputDialog("TokenXGP URL:");

                    String ua = "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/116.0";

                    String token = HttpUtil.get(new URL(url), ua);

                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer "+token);
                    headers.put("User-Agent", "MojangSharp/0.1");
                    headers.put("Charset", "UTF-8");
                    headers.put("connection", "keep-alive");
                    String playerStatsRaw = HttpUtil.get(new URL("https://api.minecraftservices.com/minecraft/profile"), headers);
                    JSONObject playerStats = new JSONObject(playerStatsRaw);
                    String name = playerStats.getString("name");
                    String uuid = playerStats.getString("id");

                    if (!Alt.accountCheck(token))
                        break;

                    Session session = new Session(name, uuid, token, "msa");
                    mc.session = session;

                    moonlight.getNotificationManager().post(NotificationType.OKAY,"Logged in! " + name);
                } catch (Throwable e) {
                    moonlight.getNotificationManager().post(NotificationType.WARNING,"Oops, something went wrong. Maybe ur token expired?");
                    e.printStackTrace();
                }
                break;*/

        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (int i = 0; i < this.buttonList.size(); ++i) {
            GuiButton button = this.buttonList.get(i);

            if (button.mousePressed(this.mc, mouseX, mouseY)) {
                this.selectedButton = button;

                button.playPressSound(this.mc.getSoundHandler());
                actionPerformed(button);
                return;
            }
        }

        for (int i = 0; i < this.visibleAlts.size(); i++) {
            Alt alt = this.visibleAlts.get(i);

            if (alt.mouseClicked(this.altWidth, VERTICAL_MARGIN + i * (PLAYER_BOX_HEIGHT + PLAYER_BOX_SPACE), mouseX,
                    mouseY)) {
                return;
            }
        }

        if (mouseX >= 3 && mouseX <= 3 + 9 && mouseY >= VERTICAL_MARGIN && mouseY <= VERTICAL_MARGIN + this.sliderHeight) {
            final float perAlt = (this.sliderHeight - VERTICAL_MARGIN) / this.alts.size();
            boolean b = mouseY >= VERTICAL_MARGIN + perAlt * this.scrolled;

            if (b && mouseY <= Math.min(VERTICAL_MARGIN + perAlt * this.visibleAltsCount,
                    this.sliderHeight) + VERTICAL_MARGIN + perAlt * this.scrolled) {
                this.dragging = true;
            } else {
                setScrolledAndUpdate(
                        this.scrolled + MathHelper.ceiling_double_int(this.alts.size() / 5.0D) * (b ? 1 : -1));
            }
        }

        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
    
    }

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    static {
        DECIMAL_FORMAT.setGroupingSize(3);
    }

    private float altWidth = 0;
    @Getter
    private float altBoxAnimationStep, altBoxAlphaStep;
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        final TokenField oldSearchField = this.searchField;

        final int altInfoX = this.width - PLAYER_BOX_WIDTH - HORIZONTAL_MARGIN;
        final int altInfoH = this.height - VERTICAL_MARGIN - 6 * (3 + 1) - BUTTON_HEIGHT * 3;
        this.altWidth = -HORIZONTAL_MARGIN + this.width - HORIZONTAL_MARGIN - PLAYER_BOX_WIDTH - HORIZONTAL_MARGIN;
        this.altBoxAnimationStep = this.altWidth / 564.0F * Alt.FHD_ANIMATION_STEP;
        this.altBoxAlphaStep = 0xFF / (this.altWidth / Alt.FHD_ANIMATION_STEP);
        this.searchField = new TokenField(0, this.mc.fontRendererObj, this.width - 740, 4, 180, 20, "Search:");
        this.sortType = EnumSort.DATE;

        if (oldSearchField != null) {
            this.searchField.setText(oldSearchField.getText());
        }

        this.groupPlayerBox = new GuiGroupPlayerBox(altInfoX, VERTICAL_MARGIN, PLAYER_BOX_WIDTH, altInfoH,
                this::getSelectedAlt);
        this.groupPlayerBox.addLine(alt -> "In-game name: " + alt.getPlayer().getName());;

        this.sliderHeight = -VERTICAL_MARGIN + this.height + -DOWN_MARGIN;
        final int oldVisibleAltsCount = this.visibleAltsCount;
        this.visibleAltsCount = getVisibleAltsCount();

        if (oldVisibleAltsCount < this.visibleAltsCount && this.alts.size() - this.scrolled < this.visibleAltsCount) {
            setScrolledAndUpdate(this.alts.size() - this.visibleAltsCount);
        }

        updateVisibleAlts();

        this.buttonList.add(new GuiCustomButton("Add", 0, altInfoX, altInfoH + VERTICAL_MARGIN + 5, BUTTON_WIDTH - 1,
                BUTTON_HEIGHT, 15, Fonts.interSemiBold.get(28)));
        this.buttonList.add(new GuiCustomButton("Remove", 1, altInfoX + BUTTON_WIDTH + 3, altInfoH + VERTICAL_MARGIN + 5,
                BUTTON_WIDTH - 1, BUTTON_HEIGHT, 15, Fonts.interSemiBold.get(28)));
        this.buttonList
                .add(new GuiCustomButton("Direct Login", 2, altInfoX, altInfoH + VERTICAL_MARGIN + 5 + BUTTON_HEIGHT + 6,
                        PLAYER_BOX_WIDTH, BUTTON_HEIGHT, 15, Fonts.interSemiBold.get(28)));
        this.buttonList
                .add(new GuiCustomButton("Refresh", 3, altInfoX, altInfoH + VERTICAL_MARGIN + 5 + (BUTTON_HEIGHT + 6) * 2,
                        PLAYER_BOX_WIDTH, BUTTON_HEIGHT, 15, Fonts.interSemiBold.get(28)));

        this.buttonList.add(new GuiCustomButton("Change Skin", 71, this.width - 150, 2, 75, 20, 8, Fonts.interSemiBold.get(16)));
        //this.buttonList.add(new GuiCustomButton("TokenXGP (URL)", 72, this.width - 150, 2, 75, 20, 8, Fonts.interSemiBold.get(16)));
        this.buttonList.add(new GuiCustomButton("TokenLogin", 70, this.width - 70, 2, 55, 20, 8, Fonts.interSemiBold.get(16)));
        this.buttonList
                .add(this.sortButton = new GuiCustomButton(this.sortType.getCriteria(), 5, this.width - 550, 2, 100, 20,
                        8, Fonts.interSemiBold.get(16)));
        this.buttonList.add(new GuiCustomButton("Copy IGN", 69, this.width - 445, 2, 95, 20, 8, Fonts.interSemiBold.get(16)));
    
    }

    private static final float DOWN_MARGIN = 5;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {

            GlStateManager.disableCull();

            MainMenu.draw(Moonlight.INSTANCE.getStartTimeLong());

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(-1f, -1f);
            GL11.glVertex2f(-1f, 1f);
            GL11.glVertex2f(1f, 1f);
            GL11.glVertex2f(1f, -1f);
            GL11.glEnd();
            GL20.glUseProgram(0);
            final int altsCount = this.alts.size();
            final float perAlt = this.sliderHeight / this.alts.size();

            if (this.dragging) {
                int sliderValue = MathHelper
                        .clamp_int(mouseY - VERTICAL_MARGIN, 0, (int) this.sliderHeight - VERTICAL_MARGIN);
                final int altIndex = (int) (sliderValue / this.sliderHeight * this.alts.size());

                setScrolledAndUpdate(altIndex);
            }

            drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 100).getRGB());
            Fonts.interSemiBold.get(28).drawString("M O O N L I G H T", HORIZONTAL_MARGIN, 5, 0xFFFFFFFF);
            Fonts.interSemiBold.get(28).drawString("M", HORIZONTAL_MARGIN, 5,
                    this.moonlight.getModuleManager().getModule(Interface.class).color());

            this.searchField.drawTextBox();

            drawRect(3, VERTICAL_MARGIN,
                    9, this.sliderHeight, SCROLL_BAR_EMPTY_COLOR);
            drawRect(3, VERTICAL_MARGIN + Math.min(perAlt * this.scrolled, this.sliderHeight - Math.min(perAlt * this.visibleAltsCount, this.sliderHeight)),
                    9, Math.min(perAlt * this.visibleAltsCount, this.sliderHeight), SCROLL_BAR_SELECTED_COLOR);

            this.groupPlayerBox.drawGroup(this.mc, mouseX, mouseY);

            if (!this.alts.isEmpty()) {
                if (altsCount > this.visibleAltsCount) scrollWithWheel(altsCount);

                for (int i = 0; i < this.visibleAlts.size(); i++) {
                    final Alt alt = this.visibleAlts.get(i);
                    alt.drawAlt(this.altWidth, (int) (VERTICAL_MARGIN + i * (PLAYER_BOX_HEIGHT + PLAYER_BOX_SPACE)),
                            mouseX, mouseY);
                }

                final Alt selectedAlt = getSelectedAlt();
                if (selectedAlt != null) selectedAlt.drawEntity(mouseX, mouseY);
            }

            super.drawScreen(mouseX, mouseY, partialTicks);
        } catch (Throwable t) {
            this.logger.warn("scrolled: " + this.scrolled, t);
        }
    }

    private static final int SCROLL_BAR_EMPTY_COLOR = new Color(0, 0, 0, 50).getRGB();
    private static final int SCROLL_BAR_SELECTED_COLOR = new Color(255, 255, 255, 30).getRGB();

    private int scrolled;
    private List<Alt> visibleAlts;
    private int visibleAltsCount;

    private float sliderHeight;
    private boolean dragging;

    private void setScrolledAndUpdate(int value) {
        setScrolled(value);
        updateVisibleAlts();
    }

    private void setScrolled(int value) {
        this.scrolled = MathHelper.clamp_int(value, 0, Math.max(0, this.alts.size() - this.visibleAltsCount));
    }

    private void updateVisibleAlts() {
        if (this.alts.size() - this.scrolled < this.visibleAltsCount) {
            setScrolled(this.alts.size() - this.visibleAltsCount);
        }

        final int size = this.alts.size();
        this.visibleAlts = this.alts.subList(MathHelper.clamp_int(this.scrolled, 0, size),
                MathHelper.clamp_int(this.scrolled + this.visibleAltsCount, 0, size));
    }

    private void scrollWithWheel(int altsCount) {
        final int mouse = Mouse.getDWheel();
        final int newValue;

        if (mouse == 0) {
            return;
        } else if (mouse < 0) {
            newValue = this.scrolled + SCROLL_ALTS;
        } else {
            newValue = this.scrolled - SCROLL_ALTS;
        }

        if (newValue >= 0 && newValue <= altsCount - this.visibleAltsCount) {
            setScrolledAndUpdate(newValue);
        }
    }

    private int getVisibleAltsCount() {
        final float value = (this.height - VERTICAL_MARGIN) / (PLAYER_BOX_HEIGHT + PLAYER_BOX_SPACE);
        return MathHelper.floor_float(value);
    }


    @Nullable
    public String readApiKey() throws IOException {
        final Path dataPath = this.moonlight.getDataFolder().resolve("Data.txt");

        if (Files.notExists(dataPath)) {
            return null;
        }

        final List<String> lines = Files.readAllLines(dataPath);
        return !lines.isEmpty() ? this.tokenContent = lines.get(0) : null;
    }

    @Nullable
    public Alt getRandomAlt() {
        List<Alt> alts = this.alts.stream().filter(alt1 -> alt1.getUnbanDate() == 0L && !alt1.isInvalid()).collect(Collectors.toList());

        if (alts.isEmpty()) return null;

        final Alt alt = alts.get(alts.size() == 1 ? 0 : ThreadLocalRandom.current().nextInt(0, alts.size() - 1));
        alt.select();

        return alt;
    }

    public Alt getSelectedAlt() {
        return this.alts.stream().filter(Alt::isSelected).findAny().orElse(null);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);

        saveAlts();

        try {
            Files.write(this.moonlight.getDataFolder().resolve("Data.txt"),
                    this.tokenContent.getBytes(StandardCharsets.UTF_8), CREATE, TRUNCATE_EXISTING);
        } catch (Throwable t) {
            this.logger.error("Unable to reach clients folder", t);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragging = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (isKeyComboCtrlV(keyCode)) {
            pasteAltsFromClipboard();
        } else if (isKeyComboCtrlC(keyCode)) {
            Alt selectedAlt = getSelectedAlt();

            if (selectedAlt == null) return;

            final String credential = selectedAlt.getCredential().toString();
            final StringSelection selection = new StringSelection(credential);

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        } else {
            try {
                switch (keyCode) {
                    case Keyboard.KEY_NUMPAD8:
                    case Keyboard.KEY_UP: {
                        Alt previous = null;

                        for (int i = 0; i < this.alts.size(); i++) {
                            final Alt alt = this.alts.get(i);

                            if (alt.isSelected()) {
                                if (previous != null) selectAlt(alt, previous, i - 1);
                                return;
                            } else {
                                previous = alt;
                            }
                        }

                        break;
                    }

                    case Keyboard.KEY_NUMPAD2:
                    case Keyboard.KEY_DOWN:
                        final int size = this.alts.size();

                        for (int i = 0; i < size; i++) {
                            final Alt alt = this.alts.get(i);

                            if (alt.isSelected()) {
                                if (i + 1 < size) selectAlt(alt, this.alts.get(i + 1), i + 1);
                                return;
                            }
                        }

                        break;

                    case Keyboard.KEY_F5:
                        refreshAlts();
                        break;

                    case Keyboard.KEY_NUMPADENTER:
                    case Keyboard.KEY_RETURN: {
                        final Alt alt = getSelectedAlt();
                        if (alt != null) alt.logIn(true);

                        break;
                    }

                    case Keyboard.KEY_DELETE: {
                        removeCurrentAlt();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (this.searchField.textboxKeyTyped(typedChar, keyCode) && this.searchField.isEnabled) {
            this.alts.update();
            setScrolledAndUpdate(0);
        }

        super.keyTyped(typedChar, keyCode);
    }

    void selectAlt(@Nullable Alt oldValue, @NonNull Alt newValue, Integer newValueIndex) {
        if (newValueIndex == null) {
            if ((newValueIndex = this.alts.indexOf(newValue)) == -1) {
                return;
            }
        }

        if (oldValue != null) {
            oldValue.setSelectedProperty(false);
        }

        newValue.setSelectedProperty(true);

        if (newValueIndex < this.scrolled) {
            setScrolledAndUpdate(newValueIndex);
        } else if (newValueIndex >= this.scrolled + this.visibleAltsCount) {
            setScrolledAndUpdate(newValueIndex - this.visibleAltsCount + 1);
        }
    }

    private void refreshAlts() {
        loadAlts();
        setScrolledAndUpdate(0);
    }

    @SuppressWarnings("unchecked")
    private void pasteAltsFromClipboard() {
        try {
            final Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (contents == null) return;

            final Stream<String> stream;

            if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                stream = Arrays.stream(((String) contents.getTransferData(DataFlavor.stringFlavor)).split("\n"));
            } else if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                stream = ((List<File>) contents.getTransferData(DataFlavor.javaFileListFlavor)).stream().map(file -> {
                    try {
                        return Files.lines(file.toPath());
                    } catch (Exception e) {
                        return null;
                    }
                }).filter(Objects::nonNull).flatMap(s -> s);
            } else {
                return;
            }

            final Set<Object> seen = ConcurrentHashMap.newKeySet();

            stream.map(s -> {
                        if (!s.endsWith("@alt.com")) {
                            final int index = s.indexOf(':');
                            return index == -1 ? null : new String[]{s.substring(0, index), s.substring(index + 1)};
                        } else {
                            return new String[]{s, new String(new char[ThreadLocalRandom.current().nextInt(7) + 1]).replace(
                                    '\0', 'a')};
                        }
                    }).filter(Objects::nonNull).filter(alt -> !alt[0].trim().isEmpty() && !alt[1].trim().isEmpty())
                    .filter(t -> seen.add(t[0]))
                    .sorted(Comparator.comparing(o -> o[0])).forEach(alt -> addAlt(new AltCredential(alt[0], alt[1])));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeCurrentAlt() {
        Alt previous = null;

        for (Alt alt : this.alts) {
            if (alt.isSelected()) {
                removeAlt(alt);

                if (previous != null) previous.setSelectedProperty(true);
                return;
            } else {
                previous = alt;
            }
        }

        saveAlts();
    }

    public boolean hasAlt(@NonNull Alt credential) {
        return this.alts.getUnfiltered().stream()
                .anyMatch(alt -> alt.getPlayer().getName().equals(credential.getPlayer().getName()));
    }

    public Alt addAlt(@NonNull AltCredential credential) {
        final Alt alt;

        if (credential instanceof AlteningAltCredential) {
            final AlteningAltCredential alteningCredential = (AlteningAltCredential) credential;

            final AccountDetails details = alteningCredential.getDetails();
            alt = new Alt(credential,
                    new FakeEntityPlayer(new GameProfile(UUID.randomUUID(), alteningCredential.getName()), null),
                    HypixelProfileFactory.hypixelProfile(details.getHypixelRank(), Math.max(1, details.getHypixelLevel())), this, 0L, false);
        } else if (credential instanceof MicrosoftAltCredential) {
            MicrosoftAltCredential m = (MicrosoftAltCredential) credential;

            alt = new Alt(credential, new FakeEntityPlayer(new GameProfile(m.getUUID(), m.getName()), null),
                    HypixelProfile.empty(), this, 0L, false);
        } else {
            final String login = credential.getLogin();
            final String name = GuiAddAlt.isEmail(login) ? "<Unknown Name>" : login;

            alt = new Alt(credential, new FakeEntityPlayer(new GameProfile(UUID.randomUUID(), name), null),
                    HypixelProfile.empty(), this, 0L, false);
        }

        if (!hasAlt(alt)) {

            this.alts.add(alt);
            updateVisibleAlts();

            alt.select();
            return alt;
        } else {
            moonlight.getNotificationManager().post(NotificationType.WARNING,"Account is already added!");
            return null;
        }
    }

    public void removeAlt(@NonNull Alt alt) {
        if (this.alts.remove(alt)) {
            updateVisibleAlts();
        }
    }

    public void loadAlts() {
        this.alts.clear();

        try {
            final NBTTagCompound tagCompound = CompressedStreamTools
                    .read(new File(Moonlight.INSTANCE.getMainDir(), "alts.ml"));
            if (tagCompound == null) return;

            final NBTTagList altListTag = tagCompound.getTagList("alts", 10);

            for (int i = 0; i < altListTag.tagCount(); ++i) {
                final Alt alt;

                try {
                    alt = Alt.fromNBT(this, altListTag.getCompoundTagAt(i));
                } catch (Throwable t) {
                    this.logger.error("Failed to parse account: " + altListTag.getCompoundTagAt(i).toString(), t);
                    continue;
                }

                this.alts.add(alt);
            }
        } catch (Exception e) {
            this.logger.error("Couldn't load alt list", e);
        }

        updateVisibleAlts();
    }

    public void saveAlts() {
        try {
            final NBTTagList tagList = new NBTTagList();

            for (Alt alt : this.alts.getUnfiltered()) {
                tagList.appendTag(alt.asNBTCompound());
            }

            final NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setTag("alts", tagList);

            CompressedStreamTools.safeWrite(tagCompound, new File(Moonlight.INSTANCE.getMainDir(), "alts.ml"));
        } catch (Exception e) {
            this.logger.error("Couldn't save alt list", e);
        }
    }

    private enum EnumSort {

        DATE("Date", (o1, o2) -> 0),
        LEVEL("Level", (o1, o2) -> {
            if (o1.getHypixelProfile() == null) {
                if (o2.getHypixelProfile() == null) {
                    return 0;
                } else {
                    return 1;
                }
            } else if (o2.getHypixelProfile() == null) {
                return -1;
            }

            return Integer.compare(o2.getHypixelProfile().getLevel(), o1.getHypixelProfile().getLevel());
        }),
        NAME("Name", (o1, o2) -> o1.getPlayer().getGameProfile().getName()
                .compareTo(o2.getPlayer().getGameProfile().getName())),
        EMAIL("Email", (o1, o2) -> o1.getCredential().getLogin().compareTo(o2.getCredential().getLogin())),
        ;

        private final String criteria;
        private final Comparator<Alt> comparator;

        EnumSort(String criteria, Comparator<Alt> comparator) {
            this.criteria = criteria;
            this.comparator = comparator;
        }

        @NonNull
        public String getCriteria() {
            return "By " + this.criteria;
        }

        public Comparator<Alt> getComparator() {
            return this.comparator;
        }
    }

   
    public List<Alt> getAlts() {
        return (List<Alt>) this.alts.getUnfiltered();
    }

    public TClient getTClient() {
        return this.tclient;
    }

}
