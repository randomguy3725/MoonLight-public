package wtf.moonlight.gui.altmanager.repository;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.Moonlight;
import wtf.moonlight.gui.altmanager.login.AltLoginThread;
import wtf.moonlight.gui.altmanager.login.AltType;
import wtf.moonlight.gui.altmanager.login.SessionUpdatingAltLoginListener;
import wtf.moonlight.gui.altmanager.mslogin.Auth;
import wtf.moonlight.gui.altmanager.repository.credential.AltCredential;
import wtf.moonlight.gui.altmanager.repository.credential.MicrosoftAltCredential;
import wtf.moonlight.gui.altmanager.repository.hypixel.HypixelProfile;
import wtf.moonlight.gui.altmanager.repository.hypixel.HypixelProfileFactory;
import wtf.moonlight.gui.altmanager.utils.FakeEntityPlayer;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.gui.notification.NotificationType;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN;

@Getter
@Setter
public class Alt {

    private final AltRepositoryGUI repository;

    private final AltCredential credential;
    private FakeEntityPlayer player;
    private final HypixelProfile hypixelProfile;
    private long unbanDate;

    private boolean invalid;

    public Alt(@NotNull AltCredential credential,
               @NotNull FakeEntityPlayer player,
               @Nullable HypixelProfile hypixelProfile,
               @NotNull AltRepositoryGUI repository, Long unbanDate, boolean invalid) {
        this.repository = repository;
        this.credential = credential;
        this.player = player;
        this.hypixelProfile = hypixelProfile;
        this.unbanDate = unbanDate;
        this.invalid = invalid;
    }

    protected boolean mouseClicked(float width, float y, int mouseX, int mouseY) {
        if (!isHovered(width, y, mouseX, mouseY)) return false;

        if (Minecraft.getSystemTime() - lastClickTime < 250L) {
            logIn(true);
        } else {
            select();
        }

        this.lastClickTime = Minecraft.getSystemTime();
        return true;
    }

    public void drawAlt(float width, int y, int mouseX, int mouseY) {

        RenderUtils.drawRoundedRect(AltRepositoryGUI.HORIZONTAL_MARGIN, y, width, AltRepositoryGUI.PLAYER_BOX_HEIGHT, 5,
                (!isSelected() ? DEFAULT_COLOR : SELECTED_COLOR));

        if (triedAuthorizing() && alpha > 0) {
            RenderUtils.drawRoundedRect(AltRepositoryGUI.HORIZONTAL_MARGIN, y, animationX,
                    AltRepositoryGUI.PLAYER_BOX_HEIGHT, 5, (int) Math.max(0, alpha) << 24 | (isLoginSuccessful() ?
                            SUCCESS_LOGIN_COLOR :
                            FAILED_LOGIN_COLOR));
            renderAltBox(width, mouseX, mouseY);
        }

        drawSkull(player, y);
        Fonts.interSemiBold.get(20).drawString((invalid ? EnumChatFormatting.STRIKETHROUGH : "") + player.getName(), 53, y + 3, TEXT_SELECTED_COLOR);

        Fonts.interSemiBold.get(12).drawString("Email: " + credential.getLogin(), 53, y + AltRepositoryGUI.HORIZONTAL_MARGIN,
                TEXT_SELECTED_COLOR);

        String password = credential.getPassword();

        if (StringUtils.isNotBlank(password)) {
            Fonts.interSemiBold.get(12).drawString("Password:", 53, y + 24, TEXT_SELECTED_COLOR);
            Fonts.interSemiBold.get(12).drawString(new String(new char[password.length()]).replace('\0', '*'),
                    Fonts.interMedium.get(12).getStringWidth("Password:") + 57, y + 25, TEXT_SELECTED_COLOR);
        }

        if (repository.getCurrentAlt() == this) {
            Fonts.interSemiBold.get(20).drawString("Logged", width - 35, y + AltRepositoryGUI.PLAYER_BOX_HEIGHT / 2F - 5,
                    new Color(255, 255, 255, 50).getRGB());
        }

        if (unbanDate != -1) {
            if (unbanDate - System.currentTimeMillis() < 0) {
                unbanDate = 0;
            }
        }

        if (unbanDate != 0) {
            String unbansIn;
            if (unbanDate != -1) {
                int seconds = (int) ((unbanDate - System.currentTimeMillis()) / 1000);
                String days = seconds > 86400 ? (seconds / 86400) + "d " : "";
                seconds = !days.equals("") ? seconds % 86400 : seconds;
                String hours = seconds > 3600 ? seconds / 3600 + "h " : "";
                seconds = !hours.equals("") ? seconds % 3600 : seconds;
                String minutes = seconds > 60 ? seconds / 60 + "m " : "";
                unbansIn = days + hours + minutes;
            } else {
                unbansIn = "Permed";
            }

            Fonts.interSemiBold.get(20).drawString(unbansIn, width - Fonts.interSemiBold.get(20).getStringWidth(unbansIn) - (repository.getCurrentAlt() == this ? 45 : 0) + 5, y + AltRepositoryGUI.PLAYER_BOX_HEIGHT / 2F - 5,
                    TEXT_SELECTED_COLOR);
        }

    
    }

    private void drawSkull(@NotNull FakeEntityPlayer player, int scrolled) {
        Minecraft mc = Minecraft.getMinecraft();

        mc.getTextureManager().bindTexture(player.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(18, scrolled + 2, 8.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
    }

    private final TimerUtils timer = new TimerUtils();
    private float alpha = 255;
    private float animationX = 0;

    private void renderAltBox(float width, int mouseX, int mouseY) {
        float altBoxAlphaStep = repository.getAltBoxAlphaStep();

        if (timer.hasTimeElapsed(UPDATE_MILLIS_DELAY) && alpha > 0) {
            this.alpha -= altBoxAlphaStep;
            timer.reset();
        }

        if (animationX < width) {
            this.animationX = Math.min(animationX + repository.getAltBoxAnimationStep(), width);
        }
    }

    private long lastTimeAlreadyLogged;

    public CompletableFuture<Session> logIn(boolean trippsol) {
        CompletableFuture<Session> sessionCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Session session = null;

            if (!isLoggingIn() && !isLoginSuccessful()) {
                setLoggingIn(true);

                if (credential instanceof MicrosoftAltCredential) {
                    try {
                        final MicrosoftAltCredential cast = (MicrosoftAltCredential) credential;

                        Map.Entry<String, String> authRefreshTokens = Auth.refreshToken(cast.getRefreshToken());
                        String xblToken = Auth.authXBL(authRefreshTokens.getKey());
                        Map.Entry<String, String> xstsTokenUserhash = Auth.authXSTS(xblToken);
                        String accessToken = Auth.authMinecraft(xstsTokenUserhash.getValue(), xstsTokenUserhash.getKey());
                        
                        if (Alt.accountCheck(accessToken)) {
                            session = new Session(cast.getName(), cast.getUUID().toString(), accessToken, "mojang");

                            Minecraft.getMinecraft().session = session;

                            repository.getAlts().forEach(Alt::resetLogged);
                            repository.setCurrentAlt(Alt.this);
                            setGameProfile(session.getProfile());
                            setLoginProperty(true);
                            setInvalid(false);

                            if (hypixelProfile != null) {
                                Moonlight.INSTANCE.getNotificationManager().post(NotificationType.NOTIFY,"Logged in! " + Alt.this);
                            }
                        }
                    } catch (Throwable e) {
                        setLoginProperty(false);
                        setInvalid(true);
                        Moonlight.INSTANCE.getNotificationManager().post(NotificationType.NOTIFY,e.getClass().getName() + ':' + e.getMessage());
                    }
                } else {
                    session = new AltLoginThread(credential, new SessionUpdatingAltLoginListener() {

                        @Override
                        public void onLoginSuccess(AltType type, Session session) {
                            super.onLoginSuccess(type, session);

                            repository.getAlts().forEach(Alt::resetLogged);
                            repository.setCurrentAlt(Alt.this);
                            setGameProfile(session.getProfile());
                            setLoginProperty(true);
                            setInvalid(false);

                            if (hypixelProfile != null) {
                                Moonlight.INSTANCE.getNotificationManager().post(NotificationType.NOTIFY,"Logged in! " + Alt.this.toString());
                            }

                        }

                        @Override
                        public void onLoginFailed() {
                            setLoginProperty(false);
                            setInvalid(true);
                            Moonlight.INSTANCE.getNotificationManager().post(NotificationType.NOTIFY,"Invalid credentials!");
                        }
                    }).run();
                }

                setLoggingIn(false);

                this.alpha = 255;
                this.animationX = 0;
            } else if (isLoggingIn()) {
                if (System.currentTimeMillis() > lastTimeAlreadyLogged + 150) {
                    Moonlight.INSTANCE.getNotificationManager().post(NotificationType.NOTIFY,"Already trying logging in!");
                    this.lastTimeAlreadyLogged = System.currentTimeMillis();
                }
            } else if (isLoginSuccessful()) {
                if (System.currentTimeMillis() > lastTimeAlreadyLogged + 150) {
                    Moonlight.INSTANCE.getNotificationManager().post(NotificationType.NOTIFY,"Already logged in!");
                    this.lastTimeAlreadyLogged = System.currentTimeMillis();
                }
            }

            return session;
        }, ForkJoinPool.commonPool());

        return sessionCompletableFuture.whenCompleteAsync((session, throwable) -> {
            if (throwable != null) {
                Moonlight.LOGGER.warn("An error occurred while logging in!", throwable);
                return;
            }

            if (isLoginSuccessful() && session != null && session.getProfile() != null && session.getProfile()
                    .getId() != null) {
                boolean wasNull = hypixelProfile == null;

                try {
                    Moonlight.INSTANCE.getNotificationManager().post(NotificationType.OKAY, "Logged in! " + toString());

                    repository.saveAlts();
                } catch (Throwable t) {
                    Moonlight.LOGGER.warn("An unexpected error occurred while loading Hypixel profile!", t);
                }
            }
        });
    }

    private static final byte SELECTED_POSITION = 0;
    private static final byte AUTHORIZED_POSITION = 1;
    private static final byte LOGGED_POSITION = 2;
    private static final byte LOGGING_IN_POSITION = 3;

    private byte state = 0b000;

    private void modifyState(byte pos, boolean b) {
        byte mask = (byte) (1 << pos);

        if (!b) {
            this.state = (byte) (state & ~mask);
        } else {
            this.state = (byte) (state & ~mask | 1 << pos & mask);
        }
    }

    private boolean state(byte pos) {
        byte mask = (byte) (1 << pos);
        return (state & mask) == mask;
    }

    public void resetLogged() {
        modifyState(AUTHORIZED_POSITION, false);
        modifyState(LOGGED_POSITION, false);
    }

    private void setLoginProperty(boolean b) {
        modifyState(AUTHORIZED_POSITION, true);
        modifyState(LOGGED_POSITION, b);
    }

    public boolean isLoginSuccessful() {
        return triedAuthorizing() && state(LOGGED_POSITION);
    }

    public boolean isLoginUnsuccessful() {
        return triedAuthorizing() && !state(LOGGED_POSITION);
    }

    public boolean triedAuthorizing() {
        return state(AUTHORIZED_POSITION);
    }

    void setSelectedProperty(boolean b) {
        modifyState(SELECTED_POSITION, b);
    }

    public boolean isSelected() {
        return state(SELECTED_POSITION);
    }

    public boolean isLoggingIn() {
        return state(LOGGING_IN_POSITION);
    }

    private void setLoggingIn(boolean b) {
        modifyState(LOGGING_IN_POSITION, b);
    }


    public void setGameProfile(@NotNull GameProfile gameProfile) {
        setupPlayer(gameProfile, null);

        Minecraft mc = Minecraft.getMinecraft();

        gameProfile.getProperties().clear();
        gameProfile.getProperties().putAll(mc.fillSessionProfileProperties());

        MinecraftProfileTexture profileTexture = mc.getSessionService().getTextures(gameProfile, false).get(SKIN);

        if (profileTexture != null) {
            mc.addScheduledTask(
                    () -> mc.getSkinManager().loadSkin(profileTexture, SKIN, (type, skinLocation, texture) -> {
                        setupPlayer(gameProfile, skinLocation);
                    }));
        }
    }

    void setupPlayer(@NotNull GameProfile gameProfile, @Nullable ResourceLocation skinLocation) {
        Minecraft mc = Minecraft.getMinecraft();
        this.player = new FakeEntityPlayer(gameProfile, skinLocation);

        mc.getRenderManager().cacheActiveRenderInfo(player.worldObj, mc.fontRendererObj, player, player,
                mc.gameSettings, 0.0F);
    }

    long lastClickTime;

    public void select() {
        Alt selected = repository.getAlts().stream().filter(Alt::isSelected).findAny().orElse(null);
        if (selected != null) selected.setSelectedProperty(false);

        setSelectedProperty(true);
        repository.selectAlt(selected, this, null);
    }

    private static final float MODEL_SCALE_FACTOR = 0.71F;
    private static final int MODEL_BOTTOM_MARGIN = 24;

    public void drawEntity(int mouseX, int mouseY) {
        if (player != null) {
            Minecraft mc = Minecraft.getMinecraft();

            int width = repository.width;
            int height = repository.height;

            final int distanceToSide = (int) (AltRepositoryGUI.HORIZONTAL_MARGIN + AltRepositoryGUI.PLAYER_BOX_WIDTH / 2F);
            float targetHeight = height / 3f * MODEL_SCALE_FACTOR;

            int posX = width - distanceToSide;
            int posY = AltRepositoryGUI.VERTICAL_MARGIN + height - AltRepositoryGUI.VERTICAL_MARGIN - 6 * 4 - 3 * AltRepositoryGUI.BUTTON_HEIGHT - MODEL_BOTTOM_MARGIN;
            int mouseX1 = width - distanceToSide - mouseX;
            float mouseY1 = height / 2F + player.height * targetHeight - player.height * targetHeight * (player
                    .getEyeHeight() / player.height) - mouseY;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.enableColorMaterial();
            GlStateManager.pushMatrix();
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GlStateManager.translate((float) posX, (float) posY, 50.0F);
            GL11.glScalef(-targetHeight, targetHeight, targetHeight);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);

            float tanX = (float) Math.atan(mouseX1 / 40.0F);
            float tanY = -((float) Math.atan(mouseY1 / 40.0F));

            GlStateManager.rotate(tanY * 20.0F, 1.0F, 0.0F, 0.0F);
            player.renderYawOffset = tanX * 20.0F;
            player.rotationYaw = tanX * 40.0F;
            player.rotationPitch = tanY * 20.0F;
            player.rotationYawHead = player.rotationYaw;
            player.prevRotationYawHead = player.rotationYaw;
            GlStateManager.translate(0.0F, 0.0F, 0.0F);

            try {
                RenderManager renderManager = mc.getRenderManager();
                renderManager.setPlayerViewY(180.0F);
                renderManager.setRenderShadow(false);
                renderManager.renderEntityWithPosYaw(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
                renderManager.setRenderShadow(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }
    }

    static final float FHD_ANIMATION_STEP = 5;
    private static final int UPDATES_PER_SECOND = 100;

    private static final int UPDATE_MILLIS_DELAY = 1_000 / UPDATES_PER_SECOND;

    private static final int DEFAULT_COLOR = new Color(0, 0, 0,75).getRGB();
    private static final int SELECTED_COLOR = new Color(0, 0, 0,100).getRGB();
    private static final int TEXT_DEFAULT_COLOR = 0xFF868386;
    private static final int TEXT_SELECTED_COLOR = new Color(198, 198, 198).getRGB();
    private static final int SUCCESS_LOGIN_COLOR = 0x6E8D3D;
    private static final int FAILED_LOGIN_COLOR = 0x9E3939;

    private boolean isHovered(float width, float y, int mouseX, int mouseY) {
        return mouseX >= AltRepositoryGUI.HORIZONTAL_MARGIN && mouseX <= width + AltRepositoryGUI.HORIZONTAL_MARGIN && mouseY >= y && mouseY <= y + AltRepositoryGUI.PLAYER_BOX_HEIGHT;
    }

    @NotNull
    public static Alt fromNBT(AltRepositoryGUI gui, @NotNull NBTTagCompound tagCompound) {
        String login = tagCompound.getString("login");
        String password = tagCompound.getString("password", null);
        HypixelProfile hypixelProfile = HypixelProfileFactory.fromNBT(tagCompound.getCompoundTag("hypixel", null));
        hypixelProfile = hypixelProfile != null ? hypixelProfile : HypixelProfile.empty();

        NBTTagCompound profileTag = tagCompound.getCompoundTag("profile", null);
        GameProfile profile = NBTUtil.readGameProfileFromNBT(profileTag);
        FakeEntityPlayer fakeEntityPlayer = new FakeEntityPlayer(Objects.requireNonNull(profile), null);
        Long unbanDate = Long.parseLong(tagCompound.getString("unbanDate", null));
        String rank = "NONE";
        double networkLevel = 1;
        if (tagCompound.hasKey("networkLevel")) {
            networkLevel = Double.parseDouble(tagCompound.getString("networkLevel", null));
        }
        if (tagCompound.hasKey("rank")) {
            rank = tagCompound.getString("rank", null);
        }
        boolean invalid = false;
        if(tagCompound.hasKey("invalid")){
            invalid = tagCompound.getBoolean("invalid");
        }

        final AltCredential credential;

        if (tagCompound.hasKey("Microsoft") && tagCompound.getBoolean("Microsoft")) {
            credential = new MicrosoftAltCredential(tagCompound.getString("Name"), tagCompound.getString("RefreshToken"), UUID.fromString(tagCompound.getString("UUID")));
        } else {
            credential = new AltCredential(login, password);
        }

        return new Alt(credential, fakeEntityPlayer, hypixelProfile, gui, unbanDate, invalid);
    }

    public NBTBase asNBTCompound() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setString("unbanDate", String.valueOf(unbanDate));
        compound.setString("login", credential.getLogin());
        compound.setBoolean("invalid",invalid);
        if (credential.getPassword() != null) compound.setString("password", credential.getPassword());
        if (hypixelProfile != null) compound.setTag("hypixel", hypixelProfile.asNBTCompound());
        compound.setTag("profile", NBTUtil.writeGameProfile(new NBTTagCompound(), player.getGameProfile()));

        if (credential instanceof MicrosoftAltCredential) {
            final MicrosoftAltCredential cast = (MicrosoftAltCredential) credential;

            compound.setBoolean("Microsoft", true);
            compound.setString("Name", cast.getName());
            compound.setString("UUID", cast.getUUID().toString());
            compound.setString("RefreshToken", cast.getRefreshToken());
        }

        return compound;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Username: " + player.getGameProfile().getName());

        if (hypixelProfile != null) {
            String hypixelRank = hypixelProfile.getRank();
            int hypixelLevel = hypixelProfile.getLevel();

            if (hypixelLevel > 1) builder.append(" | ").append(hypixelLevel).append(" Lvl");
            if (hypixelRank != null && !hypixelRank.equalsIgnoreCase("default"))
                builder.append(" | ").append(hypixelRank);
        }

        return builder.toString();
    }

    public static boolean accountCheck(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer "+token);
        headers.put("User-Agent", "MojangSharp/0.1");
        headers.put("Charset", "UTF-8");
        headers.put("connection", "keep-alive");

        try {
            String attributesRaw = HttpUtil.get(new URL("https://api.minecraftservices.com/player/attributes"), headers);
            JSONObject attributes = new JSONObject(attributesRaw);
            
            JSONObject privileges = attributes.getJSONObject("privileges");
            JSONObject multiPlayerServerPrivilege = privileges.getJSONObject("multiplayerServer");
            if (!multiPlayerServerPrivilege.getBoolean("enabled")) {
                Moonlight.INSTANCE.getNotificationManager().post(NotificationType.NOTIFY,"Oops, this player don't have privilege to play online server.");
                return false;
            }
            
            if (attributes.has("banStatus")) {
                JSONObject bannedScopes = attributes.getJSONObject("banStatus").getJSONObject("bannedScopes");
                if (bannedScopes.has("MULTIPLAYER")) {
                    JSONObject multiplayerBan = bannedScopes.getJSONObject("MULTIPLAYER");
                    if (!bannedScopes.has("expires") ||
                            multiplayerBan.get("expires") == null ||
                            multiplayerBan.getLong("expires") >= System.currentTimeMillis()) {
                        Moonlight.INSTANCE.getNotificationManager().post(NotificationType.NOTIFY,"Oops, this player got banned from mojang.");
                        return false;
                    }
                }
            }
        } catch (Throwable e) {
            Moonlight.INSTANCE.getNotificationManager().post(NotificationType.WARNING,"Failed to get player attributes.");
            e.printStackTrace();
        }

        return true;
    }

}
