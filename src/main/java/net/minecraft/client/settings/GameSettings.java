package net.minecraft.client.settings;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.optifine.ClearWater;
import net.optifine.CustomColors;
import net.optifine.CustomGuis;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.Lang;
import net.optifine.NaturalTextures;
import net.optifine.RandomEntities;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import net.optifine.util.KeyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.DisplayMode;

public class GameSettings
{
    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson = new Gson();
    private static final ParameterizedType typeListString = new ParameterizedType()
    {
        public Type[] getActualTypeArguments()
        {
            return new Type[] {String.class};
        }
        public Type getRawType()
        {
            return List.class;
        }
        public Type getOwnerType()
        {
            return null;
        }
    };
    private static final String[] GUISCALES = new String[] {"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
    private static final String[] PARTICLES = new String[] {"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
    private static final String[] AMBIENT_OCCLUSIONS = new String[] {"options.ao.off", "options.ao.min", "options.ao.max"};
    private static final String[] STREAM_COMPRESSIONS = new String[] {"options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"};
    private static final String[] STREAM_CHAT_MODES = new String[] {"options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"};
    private static final String[] STREAM_CHAT_FILTER_MODES = new String[] {"options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"};
    private static final String[] STREAM_MIC_MODES = new String[] {"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
    private static final String[] CLOUDS_TYPES = new String[] {"options.off", "options.graphics.fast", "options.graphics.fancy"};
    public float mouseSensitivity = 0.5F;
    public boolean invertMouse;
    public int renderDistanceChunks = -1;
    public boolean viewBobbing = true;
    public boolean anaglyph;
    public boolean fboEnable = true;
    public int limitFramerate = 120;
    public int clouds = 2;
    public boolean fancyGraphics = true;
    public int ambientOcclusion = 2;
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> incompatibleResourcePacks = Lists.newArrayList();
    public EntityPlayer.EnumChatVisibility chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
    public boolean chatColours = true;
    public boolean chatLinks = true;
    public boolean chatLinksPrompt = true;
    public float chatOpacity = 1.0F;
    public boolean snooperEnabled = true;
    public boolean fullScreen;
    public boolean enableVsync = true;
    public boolean useVbo = false;
    public boolean allowBlockAlternatives = true;
    public boolean reducedDebugInfo = false;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus = true;
    private final Set<EnumPlayerModelParts> setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
    public boolean touchscreen;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    public float chatScale = 1.0F;
    public float chatWidth = 1.0F;
    public float chatHeightUnfocused = 0.44366196F;
    public float chatHeightFocused = 1.0F;
    public boolean showInventoryAchievementHint = true;
    public int mipmapLevels = 4;
    private final Map<SoundCategory, Float> mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
    public float streamBytesPerPixel = 0.5F;
    public float streamMicVolume = 1.0F;
    public float streamGameVolume = 1.0F;
    public float streamKbps = 0.5412844F;
    public float streamFps = 0.31690142F;
    public int streamCompression = 1;
    public boolean streamSendMetadata = true;
    public String streamPreferredServer = "";
    public int streamChatEnabled = 0;
    public int streamChatUserFilter = 0;
    public int streamMicToggleBehavior = 0;
    public boolean useNativeTransport = true;
    public boolean entityShadows = true;
    public boolean realmsNotifications = true;
    public KeyBinding keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
    public KeyBinding keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
    public KeyBinding keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
    public KeyBinding keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
    public KeyBinding keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
    public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
    public KeyBinding keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
    public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
    public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
    public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
    public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
    public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
    public KeyBinding keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
    public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
    public KeyBinding keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
    public KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
    public KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
    public KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
    public KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
    public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
    public KeyBinding keyBindStreamStartStop = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
    public KeyBinding keyBindStreamPauseUnpause = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
    public KeyBinding keyBindStreamCommercials = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
    public KeyBinding keyBindStreamToggleMic = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
    public KeyBinding[] keyBindsHotbar = new KeyBinding[] {new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
    public KeyBinding[] keyBindings;
    protected Minecraft mc;
    private File optionsFile;
    public EnumDifficulty difficulty;
    public boolean hideGUI;
    public int thirdPersonView;
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;
    public boolean showLagometer;
    public String lastServer;
    public boolean smoothCamera;
    public boolean debugCamEnable;
    public float fovSetting;
    public float gammaSetting;
    public float saturation;
    public int guiScale;
    public int particleSetting;
    public String language;
    public boolean forceUnicodeFont;
    public int ofFogType = 1;
    public float ofFogStart = 0.8F;
    public int ofMipmapType = 0;
    public boolean ofOcclusionFancy = false;
    public boolean ofSmoothFps = false;
    public boolean ofSmoothWorld = Config.isSingleProcessor();
    public boolean ofLazyChunkLoading = Config.isSingleProcessor();
    public boolean ofRenderRegions = false;
    public boolean ofSmartAnimations = false;
    public float ofAoLevel = 1.0F;
    public int ofAaLevel = 0;
    public int ofAfLevel = 1;
    public int ofClouds = 0;
    public float ofCloudsHeight = 0.0F;
    public int ofTrees = 0;
    public int ofRain = 0;
    public int ofDroppedItems = 0;
    public int ofBetterGrass = 3;
    public int ofAutoSaveTicks = 4000;
    public boolean ofLagometer = false;
    public boolean ofProfiler = false;
    public boolean ofShowFps = false;
    public boolean ofWeather = true;
    public boolean ofSky = true;
    public boolean ofStars = true;
    public boolean ofSunMoon = true;
    public int ofVignette = 0;
    public int ofChunkUpdates = 1;
    public boolean ofChunkUpdatesDynamic = false;
    public int ofTime = 0;
    public boolean ofClearWater = false;
    public boolean ofBetterSnow = false;
    public String ofFullscreenMode = "Default";
    public boolean ofSwampColors = true;
    public boolean ofRandomEntities = true;
    public boolean ofSmoothBiomes = true;
    public boolean ofCustomFonts = true;
    public boolean ofCustomColors = true;
    public boolean ofCustomSky = true;
    public boolean ofShowCapes = true;
    public int ofConnectedTextures = 2;
    public boolean ofCustomItems = true;
    public boolean ofNaturalTextures = false;
    public boolean ofEmissiveTextures = true;
    public boolean ofFastMath = false;
    public boolean ofFastRender = false;
    public int ofTranslucentBlocks = 0;
    public boolean ofDynamicFov = true;
    public boolean ofAlternateBlocks = true;
    public int ofDynamicLights = 3;
    public boolean ofCustomEntityModels = true;
    public boolean ofCustomGuis = true;
    public boolean ofShowGlErrors = true;
    public int ofScreenshotSize = 1;
    public int ofAnimatedWater = 0;
    public int ofAnimatedLava = 0;
    public boolean ofAnimatedFire = true;
    public boolean ofAnimatedPortal = true;
    public boolean ofAnimatedRedstone = true;
    public boolean ofAnimatedExplosion = true;
    public boolean ofAnimatedFlame = true;
    public boolean ofAnimatedSmoke = true;
    public boolean ofVoidParticles = true;
    public boolean ofWaterParticles = true;
    public boolean ofRainSplash = true;
    public boolean ofPortalParticles = true;
    public boolean ofPotionParticles = true;
    public boolean ofFireworkParticles = true;
    public boolean ofDrippingWaterLava = true;
    public boolean ofAnimatedTerrain = true;
    public boolean ofAnimatedTextures = true;
    public static final int DEFAULT = 0;
    public static final int FAST = 1;
    public static final int FANCY = 2;
    public static final int OFF = 3;
    public static final int SMART = 4;
    public static final int ANIM_ON = 0;
    public static final int ANIM_GENERATED = 1;
    public static final int ANIM_OFF = 2;
    public static final String DEFAULT_STR = "Default";
    private static final int[] OF_TREES_VALUES = new int[] {0, 1, 4, 2};
    private static final int[] OF_DYNAMIC_LIGHTS = new int[] {3, 1, 2};
    private static final String[] KEYS_DYNAMIC_LIGHTS = new String[] {"options.off", "options.graphics.fast", "options.graphics.fancy"};
    public KeyBinding ofKeyBindZoom;
    private File optionsFileOF;

    public GameSettings(Minecraft mcIn, File optionsFileIn)
    {
        this.keyBindings = ArrayUtils.addAll(new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindStreamStartStop, this.keyBindStreamPauseUnpause, this.keyBindStreamCommercials, this.keyBindStreamToggleMic, this.keyBindFullscreen, this.keyBindSpectatorOutlines}, this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 70.0F;
        this.language = "en_US";
        this.forceUnicodeFont = false;
        this.mc = mcIn;
        this.optionsFile = new File(optionsFileIn, "options.txt");

        if (mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L)
        {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
            long i = 1000000L;

            if (Runtime.getRuntime().maxMemory() >= 1500L * i)
            {
                GameSettings.Options.RENDER_DISTANCE.setValueMax(48.0F);
            }

            if (Runtime.getRuntime().maxMemory() >= 2500L * i)
            {
                GameSettings.Options.RENDER_DISTANCE.setValueMax(64.0F);
            }
        }
        else
        {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(16.0F);
        }

        this.renderDistanceChunks = mcIn.isJava64bit() ? 12 : 8;
        this.optionsFileOF = new File(optionsFileIn, "optionsof.txt");
        this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 46, "key.categories.misc");
        this.keyBindings = ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom);
        KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[] {this.ofKeyBindZoom});
        this.renderDistanceChunks = 8;
        this.loadOptions();
        Config.initGameSettings(this);
    }

    public GameSettings()
    {
        this.keyBindings = ArrayUtils.addAll(new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindStreamStartStop, this.keyBindStreamPauseUnpause, this.keyBindStreamCommercials, this.keyBindStreamToggleMic, this.keyBindFullscreen, this.keyBindSpectatorOutlines}, this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 70.0F;
        this.language = "en_US";
        this.forceUnicodeFont = false;
    }

    public static String getKeyDisplayString(int key)
    {
        return key < 0 ? I18n.format("key.mouseButton", Integer.valueOf(key + 101)): (key < 256 ? Keyboard.getKeyName(key) : String.format("%c", Character.valueOf((char)(key - 256))).toUpperCase());
    }

    public static boolean isKeyDown(KeyBinding key)
    {
        return key.getKeyCode() != 0 && (key.getKeyCode() < 0 ? Mouse.isButtonDown(key.getKeyCode() + 100) : Keyboard.isKeyDown(key.getKeyCode()));
    }

    public void setOptionKeyBinding(KeyBinding key, int keyCode)
    {
        key.setKeyCode(keyCode);
        this.saveOptions();
    }

    public void setOptionFloatValue(GameSettings.Options settingsOption, float value)
    {
        this.setOptionFloatValueOF(settingsOption, value);

        if (settingsOption == GameSettings.Options.SENSITIVITY)
        {
            this.mouseSensitivity = value;
        }

        if (settingsOption == GameSettings.Options.FOV)
        {
            this.fovSetting = value;
        }

        if (settingsOption == GameSettings.Options.GAMMA)
        {
            this.gammaSetting = value;
        }

        if (settingsOption == GameSettings.Options.FRAMERATE_LIMIT)
        {
            this.limitFramerate = (int)value;
            this.enableVsync = false;

            if (this.limitFramerate <= 0)
            {
                this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                this.enableVsync = true;
            }

            this.updateVSync();
        }

        if (settingsOption == GameSettings.Options.CHAT_OPACITY)
        {
            this.chatOpacity = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED)
        {
            this.chatHeightFocused = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED)
        {
            this.chatHeightUnfocused = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_WIDTH)
        {
            this.chatWidth = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_SCALE)
        {
            this.chatScale = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.MIPMAP_LEVELS)
        {
            int i = this.mipmapLevels;
            this.mipmapLevels = (int)value;

            if ((float)i != value)
            {
                this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.mc.getTextureMapBlocks().setBlurMipmapDirect(false, this.mipmapLevels > 0);
                this.mc.scheduleResourcesRefresh();
            }
        }

        if (settingsOption == GameSettings.Options.BLOCK_ALTERNATIVES)
        {
            this.allowBlockAlternatives = !this.allowBlockAlternatives;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.RENDER_DISTANCE)
        {
            this.renderDistanceChunks = (int)value;
            this.mc.renderGlobal.setDisplayListEntitiesDirty();
        }

        if (settingsOption == GameSettings.Options.STREAM_BYTES_PER_PIXEL)
        {
            this.streamBytesPerPixel = value;
        }

        if (settingsOption == GameSettings.Options.STREAM_VOLUME_MIC)
        {
            this.streamMicVolume = value;
        }

        if (settingsOption == GameSettings.Options.STREAM_VOLUME_SYSTEM)
        {
            this.streamGameVolume = value;
        }

        if (settingsOption == GameSettings.Options.STREAM_KBPS)
        {
            this.streamKbps = value;
        }

        if (settingsOption == GameSettings.Options.STREAM_FPS)
        {
            this.streamFps = value;
        }
    }

    public void setOptionValue(GameSettings.Options settingsOption, int value)
    {
        this.setOptionValueOF(settingsOption, value);

        if (settingsOption == GameSettings.Options.INVERT_MOUSE)
        {
            this.invertMouse = !this.invertMouse;
        }

        if (settingsOption == GameSettings.Options.GUI_SCALE)
        {
            this.guiScale += value;

            if (GuiScreen.isShiftKeyDown())
            {
                this.guiScale = 0;
            }

            DisplayMode displaymode = Config.getLargestDisplayMode();
            int i = displaymode.getWidth() / 320;
            int j = displaymode.getHeight() / 240;
            int k = Math.min(i, j);

            if (this.guiScale < 0)
            {
                this.guiScale = k - 1;
            }

            if (this.mc.isUnicode() && this.guiScale % 2 != 0)
            {
                this.guiScale += value;
            }

            if (this.guiScale < 0 || this.guiScale >= k)
            {
                this.guiScale = 0;
            }
        }

        if (settingsOption == GameSettings.Options.PARTICLES)
        {
            this.particleSetting = (this.particleSetting + value) % 3;
        }

        if (settingsOption == GameSettings.Options.VIEW_BOBBING)
        {
            this.viewBobbing = !this.viewBobbing;
        }

        if (settingsOption == GameSettings.Options.RENDER_CLOUDS)
        {
            this.clouds = (this.clouds + value) % 3;
        }

        if (settingsOption == GameSettings.Options.FORCE_UNICODE_FONT)
        {
            this.forceUnicodeFont = !this.forceUnicodeFont;
            this.mc.fontRendererObj.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
        }

        if (settingsOption == GameSettings.Options.FBO_ENABLE)
        {
            this.fboEnable = !this.fboEnable;
        }

        if (settingsOption == GameSettings.Options.ANAGLYPH)
        {
            if (!this.anaglyph && Config.isShaders())
            {
                Config.showGuiMessage(Lang.get("of.message.an.shaders1"), Lang.get("of.message.an.shaders2"));
                return;
            }

            this.anaglyph = !this.anaglyph;
            this.mc.refreshResources();
        }

        if (settingsOption == GameSettings.Options.GRAPHICS)
        {
            this.fancyGraphics = !this.fancyGraphics;
            this.updateRenderClouds();
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.AMBIENT_OCCLUSION)
        {
            this.ambientOcclusion = (this.ambientOcclusion + value) % 3;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.CHAT_VISIBILITY)
        {
            this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + value) % 3);
        }

        if (settingsOption == GameSettings.Options.STREAM_COMPRESSION)
        {
            this.streamCompression = (this.streamCompression + value) % 3;
        }

        if (settingsOption == GameSettings.Options.STREAM_SEND_METADATA)
        {
            this.streamSendMetadata = !this.streamSendMetadata;
        }

        if (settingsOption == GameSettings.Options.STREAM_CHAT_ENABLED)
        {
            this.streamChatEnabled = (this.streamChatEnabled + value) % 3;
        }

        if (settingsOption == GameSettings.Options.STREAM_CHAT_USER_FILTER)
        {
            this.streamChatUserFilter = (this.streamChatUserFilter + value) % 3;
        }

        if (settingsOption == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR)
        {
            this.streamMicToggleBehavior = (this.streamMicToggleBehavior + value) % 2;
        }

        if (settingsOption == GameSettings.Options.CHAT_COLOR)
        {
            this.chatColours = !this.chatColours;
        }

        if (settingsOption == GameSettings.Options.CHAT_LINKS)
        {
            this.chatLinks = !this.chatLinks;
        }

        if (settingsOption == GameSettings.Options.CHAT_LINKS_PROMPT)
        {
            this.chatLinksPrompt = !this.chatLinksPrompt;
        }

        if (settingsOption == GameSettings.Options.SNOOPER_ENABLED)
        {
            this.snooperEnabled = !this.snooperEnabled;
        }

        if (settingsOption == GameSettings.Options.TOUCHSCREEN)
        {
            this.touchscreen = !this.touchscreen;
        }

        if (settingsOption == GameSettings.Options.USE_FULLSCREEN)
        {
            this.fullScreen = !this.fullScreen;

            if (this.mc.isFullScreen() != this.fullScreen)
            {
                this.mc.toggleFullscreen();
            }
        }

        if (settingsOption == GameSettings.Options.ENABLE_VSYNC)
        {
            this.enableVsync = !this.enableVsync;
            Display.setVSyncEnabled(this.enableVsync);
        }

        if (settingsOption == GameSettings.Options.USE_VBO)
        {
            this.useVbo = !this.useVbo;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.BLOCK_ALTERNATIVES)
        {
            this.allowBlockAlternatives = !this.allowBlockAlternatives;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.REDUCED_DEBUG_INFO)
        {
            this.reducedDebugInfo = !this.reducedDebugInfo;
        }

        if (settingsOption == GameSettings.Options.ENTITY_SHADOWS)
        {
            this.entityShadows = !this.entityShadows;
        }

        if (settingsOption == GameSettings.Options.REALMS_NOTIFICATIONS)
        {
            this.realmsNotifications = !this.realmsNotifications;
        }

        this.saveOptions();
    }

    public float getOptionFloatValue(GameSettings.Options settingOption)
    {
        float f = this.getOptionFloatValueOF(settingOption);
        return f != Float.MAX_VALUE ? f : (settingOption == GameSettings.Options.FOV ? this.fovSetting : (settingOption == GameSettings.Options.GAMMA ? this.gammaSetting : (settingOption == GameSettings.Options.SATURATION ? this.saturation : (settingOption == GameSettings.Options.SENSITIVITY ? this.mouseSensitivity : (settingOption == GameSettings.Options.CHAT_OPACITY ? this.chatOpacity : (settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED ? this.chatHeightFocused : (settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED ? this.chatHeightUnfocused : (settingOption == GameSettings.Options.CHAT_SCALE ? this.chatScale : (settingOption == GameSettings.Options.CHAT_WIDTH ? this.chatWidth : (settingOption == GameSettings.Options.FRAMERATE_LIMIT ? (float)this.limitFramerate : (settingOption == GameSettings.Options.MIPMAP_LEVELS ? (float)this.mipmapLevels : (settingOption == GameSettings.Options.RENDER_DISTANCE ? (float)this.renderDistanceChunks : (settingOption == GameSettings.Options.STREAM_BYTES_PER_PIXEL ? this.streamBytesPerPixel : (settingOption == GameSettings.Options.STREAM_VOLUME_MIC ? this.streamMicVolume : (settingOption == GameSettings.Options.STREAM_VOLUME_SYSTEM ? this.streamGameVolume : (settingOption == GameSettings.Options.STREAM_KBPS ? this.streamKbps : (settingOption == GameSettings.Options.STREAM_FPS ? this.streamFps : 0.0F)))))))))))))))));
    }

    public boolean getOptionOrdinalValue(GameSettings.Options settingOption)
    {
        switch (settingOption)
        {
            case INVERT_MOUSE:
                return this.invertMouse;

            case VIEW_BOBBING:
                return this.viewBobbing;

            case ANAGLYPH:
                return this.anaglyph;

            case FBO_ENABLE:
                return this.fboEnable;

            case CHAT_COLOR:
                return this.chatColours;

            case CHAT_LINKS:
                return this.chatLinks;

            case CHAT_LINKS_PROMPT:
                return this.chatLinksPrompt;

            case SNOOPER_ENABLED:
                return this.snooperEnabled;

            case USE_FULLSCREEN:
                return this.fullScreen;

            case ENABLE_VSYNC:
                return this.enableVsync;

            case USE_VBO:
                return this.useVbo;

            case TOUCHSCREEN:
                return this.touchscreen;

            case STREAM_SEND_METADATA:
                return this.streamSendMetadata;

            case FORCE_UNICODE_FONT:
                return this.forceUnicodeFont;

            case BLOCK_ALTERNATIVES:
                return this.allowBlockAlternatives;

            case REDUCED_DEBUG_INFO:
                return this.reducedDebugInfo;

            case ENTITY_SHADOWS:
                return this.entityShadows;

            case REALMS_NOTIFICATIONS:
                return this.realmsNotifications;

            default:
                return false;
        }
    }

    private static String getTranslation(String[] strArray, int index)
    {
        if (index < 0 || index >= strArray.length)
        {
            index = 0;
        }

        return I18n.format(strArray[index]);
    }

    public String getKeyBinding(GameSettings.Options settingOption)
    {
        String s = this.getKeyBindingOF(settingOption);

        if (s != null)
        {
            return s;
        }
        else
        {
            String s1 = I18n.format(settingOption.getEnumString()) + ": ";

            if (settingOption.getEnumFloat())
            {
                float f1 = this.getOptionFloatValue(settingOption);
                float f = settingOption.normalizeValue(f1);
                return settingOption == GameSettings.Options.MIPMAP_LEVELS && (double)f1 >= 4.0D ? s1 + Lang.get("of.general.max") : (settingOption == GameSettings.Options.SENSITIVITY ? (f == 0.0F ? s1 + I18n.format("options.sensitivity.min") : (f == 1.0F ? s1 + I18n.format("options.sensitivity.max") : s1 + (int)(f * 200.0F) + "%")) : (settingOption == GameSettings.Options.FOV ? (f1 == 70.0F ? s1 + I18n.format("options.fov.min") : (f1 == 110.0F ? s1 + I18n.format("options.fov.max") : s1 + (int)f1)) : (settingOption == GameSettings.Options.FRAMERATE_LIMIT ? (f1 == settingOption.valueMax ? s1 + I18n.format("options.framerateLimit.max") : s1 + (int)f1 + " fps") : (settingOption == GameSettings.Options.RENDER_CLOUDS ? (f1 == settingOption.valueMin ? s1 + I18n.format("options.cloudHeight.min") : s1 + ((int)f1 + 128)) : (settingOption == GameSettings.Options.GAMMA ? (f == 0.0F ? s1 + I18n.format("options.gamma.min") : (f == 1.0F ? s1 + I18n.format("options.gamma.max") : s1 + "+" + (int)(f * 100.0F) + "%")) : (settingOption == GameSettings.Options.SATURATION ? s1 + (int)(f * 400.0F) + "%" : (settingOption == GameSettings.Options.CHAT_OPACITY ? s1 + (int)(f * 90.0F + 10.0F) + "%" : (settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED ? s1 + GuiNewChat.calculateChatboxHeight(f) + "px" : (settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED ? s1 + GuiNewChat.calculateChatboxHeight(f) + "px" : (settingOption == GameSettings.Options.CHAT_WIDTH ? s1 + GuiNewChat.calculateChatboxWidth(f) + "px" : (settingOption == GameSettings.Options.RENDER_DISTANCE ? s1 + (int)f1 + " chunks" : (settingOption == GameSettings.Options.MIPMAP_LEVELS ? (f1 == 0.0F ? s1 + I18n.format("options.off") : s1 + (int)f1) : (f == 0.0F ? s1 + I18n.format("options.off") : s1 + (int)(f * 100.0F) + "%")))))))))))));
            }
            else if (settingOption.getEnumBoolean())
            {
                boolean flag = this.getOptionOrdinalValue(settingOption);
                return flag ? s1 + I18n.format("options.on") : s1 + I18n.format("options.off");
            }
            else if (settingOption == GameSettings.Options.GUI_SCALE)
            {
                return this.guiScale >= GUISCALES.length ? s1 + this.guiScale + "x" : s1 + getTranslation(GUISCALES, this.guiScale);
            }
            else if (settingOption == GameSettings.Options.CHAT_VISIBILITY)
            {
                return s1 + I18n.format(this.chatVisibility.getResourceKey());
            }
            else if (settingOption == GameSettings.Options.PARTICLES)
            {
                return s1 + getTranslation(PARTICLES, this.particleSetting);
            }
            else if (settingOption == GameSettings.Options.AMBIENT_OCCLUSION)
            {
                return s1 + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
            }
            else if (settingOption == GameSettings.Options.STREAM_COMPRESSION)
            {
                return s1 + getTranslation(STREAM_COMPRESSIONS, this.streamCompression);
            }
            else if (settingOption == GameSettings.Options.STREAM_CHAT_ENABLED)
            {
                return s1 + getTranslation(STREAM_CHAT_MODES, this.streamChatEnabled);
            }
            else if (settingOption == GameSettings.Options.STREAM_CHAT_USER_FILTER)
            {
                return s1 + getTranslation(STREAM_CHAT_FILTER_MODES, this.streamChatUserFilter);
            }
            else if (settingOption == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR)
            {
                return s1 + getTranslation(STREAM_MIC_MODES, this.streamMicToggleBehavior);
            }
            else if (settingOption == GameSettings.Options.RENDER_CLOUDS)
            {
                return s1 + getTranslation(CLOUDS_TYPES, this.clouds);
            }
            else if (settingOption == GameSettings.Options.GRAPHICS)
            {
                if (this.fancyGraphics)
                {
                    return s1 + I18n.format("options.graphics.fancy");
                }
                else
                {
                    String s2 = "options.graphics.fast";
                    return s1 + I18n.format("options.graphics.fast");
                }
            }
            else
            {
                return s1;
            }
        }
    }

    public void loadOptions()
    {
        FileInputStream fileinputstream = null;
        label2:
        {
            try
            {
                if (this.optionsFile.exists())
                {
                    BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(fileinputstream = new FileInputStream(this.optionsFile)));
                    String s = "";
                    this.mapSoundLevels.clear();

                    while ((s = bufferedreader.readLine()) != null)
                    {
                        try
                        {
                            String[] astring = s.split(":");

                            if (astring[0].equals("mouseSensitivity"))
                            {
                                this.mouseSensitivity = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("fov"))
                            {
                                this.fovSetting = this.parseFloat(astring[1]) * 40.0F + 70.0F;
                            }

                            if (astring[0].equals("gamma"))
                            {
                                this.gammaSetting = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("saturation"))
                            {
                                this.saturation = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("invertYMouse"))
                            {
                                this.invertMouse = astring[1].equals("true");
                            }

                            if (astring[0].equals("renderDistance"))
                            {
                                this.renderDistanceChunks = Integer.parseInt(astring[1]);
                            }

                            if (astring[0].equals("guiScale"))
                            {
                                this.guiScale = Integer.parseInt(astring[1]);
                            }

                            if (astring[0].equals("particles"))
                            {
                                this.particleSetting = Integer.parseInt(astring[1]);
                            }

                            if (astring[0].equals("bobView"))
                            {
                                this.viewBobbing = astring[1].equals("true");
                            }

                            if (astring[0].equals("anaglyph3d"))
                            {
                                this.anaglyph = astring[1].equals("true");
                            }

                            if (astring[0].equals("maxFps"))
                            {
                                this.limitFramerate = Integer.parseInt(astring[1]);

                                if (this.enableVsync)
                                {
                                    this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                                }

                                if (this.limitFramerate <= 0)
                                {
                                    this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                                }
                            }

                            if (astring[0].equals("fboEnable"))
                            {
                                this.fboEnable = astring[1].equals("true");
                            }

                            if (astring[0].equals("difficulty"))
                            {
                                this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(astring[1]));
                            }

                            if (astring[0].equals("fancyGraphics"))
                            {
                                this.fancyGraphics = astring[1].equals("true");
                                this.updateRenderClouds();
                            }

                            if (astring[0].equals("ao"))
                            {
                                if (astring[1].equals("true"))
                                {
                                    this.ambientOcclusion = 2;
                                }
                                else if (astring[1].equals("false"))
                                {
                                    this.ambientOcclusion = 0;
                                }
                                else
                                {
                                    this.ambientOcclusion = Integer.parseInt(astring[1]);
                                }
                            }

                            if (astring[0].equals("renderClouds"))
                            {
                                if (astring[1].equals("true"))
                                {
                                    this.clouds = 2;
                                }
                                else if (astring[1].equals("false"))
                                {
                                    this.clouds = 0;
                                }
                                else if (astring[1].equals("fast"))
                                {
                                    this.clouds = 1;
                                }
                            }

                            if (astring[0].equals("resourcePacks"))
                            {
                                this.resourcePacks = gson.fromJson(s.substring(s.indexOf(58) + 1), typeListString);

                                if (this.resourcePacks == null)
                                {
                                    this.resourcePacks = Lists.newArrayList();
                                }
                            }

                            if (astring[0].equals("incompatibleResourcePacks"))
                            {
                                this.incompatibleResourcePacks = gson.fromJson(s.substring(s.indexOf(58) + 1), typeListString);

                                if (this.incompatibleResourcePacks == null)
                                {
                                    this.incompatibleResourcePacks = Lists.newArrayList();
                                }
                            }

                            if (astring[0].equals("lastServer") && astring.length >= 2)
                            {
                                this.lastServer = s.substring(s.indexOf(58) + 1);
                            }

                            if (astring[0].equals("lang") && astring.length >= 2)
                            {
                                this.language = astring[1];
                            }

                            if (astring[0].equals("chatVisibility"))
                            {
                                this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(astring[1]));
                            }

                            if (astring[0].equals("chatColors"))
                            {
                                this.chatColours = astring[1].equals("true");
                            }

                            if (astring[0].equals("chatLinks"))
                            {
                                this.chatLinks = astring[1].equals("true");
                            }

                            if (astring[0].equals("chatLinksPrompt"))
                            {
                                this.chatLinksPrompt = astring[1].equals("true");
                            }

                            if (astring[0].equals("chatOpacity"))
                            {
                                this.chatOpacity = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("snooperEnabled"))
                            {
                                this.snooperEnabled = astring[1].equals("true");
                            }

                            if (astring[0].equals("fullscreen"))
                            {
                                this.fullScreen = astring[1].equals("true");
                            }

                            if (astring[0].equals("enableVsync"))
                            {
                                this.enableVsync = astring[1].equals("true");

                                if (this.enableVsync)
                                {
                                    this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                                }

                                this.updateVSync();
                            }

                            if (astring[0].equals("useVbo"))
                            {
                                this.useVbo = astring[1].equals("true");
                            }

                            if (astring[0].equals("hideServerAddress"))
                            {
                                this.hideServerAddress = astring[1].equals("true");
                            }

                            if (astring[0].equals("advancedItemTooltips"))
                            {
                                this.advancedItemTooltips = astring[1].equals("true");
                            }

                            if (astring[0].equals("pauseOnLostFocus"))
                            {
                                this.pauseOnLostFocus = astring[1].equals("true");
                            }

                            if (astring[0].equals("touchscreen"))
                            {
                                this.touchscreen = astring[1].equals("true");
                            }

                            if (astring[0].equals("overrideHeight"))
                            {
                                this.overrideHeight = Integer.parseInt(astring[1]);
                            }

                            if (astring[0].equals("overrideWidth"))
                            {
                                this.overrideWidth = Integer.parseInt(astring[1]);
                            }

                            if (astring[0].equals("heldItemTooltips"))
                            {
                                this.heldItemTooltips = astring[1].equals("true");
                            }

                            if (astring[0].equals("chatHeightFocused"))
                            {
                                this.chatHeightFocused = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("chatHeightUnfocused"))
                            {
                                this.chatHeightUnfocused = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("chatScale"))
                            {
                                this.chatScale = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("chatWidth"))
                            {
                                this.chatWidth = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("showInventoryAchievementHint"))
                            {
                                this.showInventoryAchievementHint = astring[1].equals("true");
                            }

                            if (astring[0].equals("mipmapLevels"))
                            {
                                this.mipmapLevels = Integer.parseInt(astring[1]);
                            }

                            if (astring[0].equals("streamBytesPerPixel"))
                            {
                                this.streamBytesPerPixel = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("streamMicVolume"))
                            {
                                this.streamMicVolume = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("streamSystemVolume"))
                            {
                                this.streamGameVolume = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("streamKbps"))
                            {
                                this.streamKbps = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("streamFps"))
                            {
                                this.streamFps = this.parseFloat(astring[1]);
                            }

                            if (astring[0].equals("streamCompression"))
                            {
                                this.streamCompression = Integer.parseInt(astring[1]);
                            }

                            if (astring[0].equals("streamSendMetadata"))
                            {
                                this.streamSendMetadata = astring[1].equals("true");
                            }

                            if (astring[0].equals("streamPreferredServer") && astring.length >= 2)
                            {
                                this.streamPreferredServer = s.substring(s.indexOf(58) + 1);
                            }

                            if (astring[0].equals("streamChatEnabled"))
                            {
                                this.streamChatEnabled = Integer.parseInt(astring[1]);
                            }

                            if (astring[0].equals("streamChatUserFilter"))
                            {
                                this.streamChatUserFilter = Integer.parseInt(astring[1]);
                            }

                            if (astring[0].equals("streamMicToggleBehavior"))
                            {
                                this.streamMicToggleBehavior = Integer.parseInt(astring[1]);
                            }

                            if (astring[0].equals("forceUnicodeFont"))
                            {
                                this.forceUnicodeFont = astring[1].equals("true");
                            }

                            if (astring[0].equals("allowBlockAlternatives"))
                            {
                                this.allowBlockAlternatives = astring[1].equals("true");
                            }

                            if (astring[0].equals("reducedDebugInfo"))
                            {
                                this.reducedDebugInfo = astring[1].equals("true");
                            }

                            if (astring[0].equals("useNativeTransport"))
                            {
                                this.useNativeTransport = astring[1].equals("true");
                            }

                            if (astring[0].equals("entityShadows"))
                            {
                                this.entityShadows = astring[1].equals("true");
                            }

                            if (astring[0].equals("realmsNotifications"))
                            {
                                this.realmsNotifications = astring[1].equals("true");
                            }

                            for (KeyBinding keybinding : this.keyBindings)
                            {
                                if (astring[0].equals("key_" + keybinding.getKeyDescription()))
                                {
                                    keybinding.setKeyCode(Integer.parseInt(astring[1]));
                                }
                            }

                            for (SoundCategory soundcategory : SoundCategory.values())
                            {
                                if (astring[0].equals("soundCategory_" + soundcategory.getCategoryName()))
                                {
                                    this.mapSoundLevels.put(soundcategory, Float.valueOf(this.parseFloat(astring[1])));
                                }
                            }

                            for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values())
                            {
                                if (astring[0].equals("modelPart_" + enumplayermodelparts.getPartName()))
                                {
                                    this.setModelPartEnabled(enumplayermodelparts, astring[1].equals("true"));
                                }
                            }
                        }
                        catch (Exception exception)
                        {
                            logger.warn("Skipping bad option: " + s);
                            exception.printStackTrace();
                        }
                    }

                    KeyBinding.resetKeyBindingArrayAndHash();
                    bufferedreader.close();
                    break label2;
                }
            }
            catch (Exception exception1)
            {
                logger.error("Failed to load options", exception1);
                break label2;
            }
            finally
            {
                IOUtils.closeQuietly(fileinputstream);
            }

            return;
        }
        this.loadOfOptions();
    }

    private float parseFloat(String str)
    {
        return str.equals("true") ? 1.0F : (str.equals("false") ? 0.0F : Float.parseFloat(str));
    }

    public void saveOptions()
    {
        if (Reflector.FMLClientHandler.exists())
        {
            Object object = Reflector.call(Reflector.FMLClientHandler_instance);

            if (object != null && Reflector.callBoolean(object, Reflector.FMLClientHandler_isLoading))
            {
                return;
            }
        }

        try
        {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.optionsFile));
            printwriter.println("invertYMouse:" + this.invertMouse);
            printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
            printwriter.println("fov:" + (this.fovSetting - 70.0F) / 40.0F);
            printwriter.println("gamma:" + this.gammaSetting);
            printwriter.println("saturation:" + this.saturation);
            printwriter.println("renderDistance:" + this.renderDistanceChunks);
            printwriter.println("guiScale:" + this.guiScale);
            printwriter.println("particles:" + this.particleSetting);
            printwriter.println("bobView:" + this.viewBobbing);
            printwriter.println("anaglyph3d:" + this.anaglyph);
            printwriter.println("maxFps:" + this.limitFramerate);
            printwriter.println("fboEnable:" + this.fboEnable);
            printwriter.println("difficulty:" + this.difficulty.getDifficultyId());
            printwriter.println("fancyGraphics:" + this.fancyGraphics);
            printwriter.println("ao:" + this.ambientOcclusion);

            switch (this.clouds)
            {
                case 0:
                    printwriter.println("renderClouds:false");
                    break;

                case 1:
                    printwriter.println("renderClouds:fast");
                    break;

                case 2:
                    printwriter.println("renderClouds:true");
            }

            printwriter.println("resourcePacks:" + gson.toJson(this.resourcePacks));
            printwriter.println("incompatibleResourcePacks:" + gson.toJson(this.incompatibleResourcePacks));
            printwriter.println("lastServer:" + this.lastServer);
            printwriter.println("lang:" + this.language);
            printwriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
            printwriter.println("chatColors:" + this.chatColours);
            printwriter.println("chatLinks:" + this.chatLinks);
            printwriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
            printwriter.println("chatOpacity:" + this.chatOpacity);
            printwriter.println("snooperEnabled:" + this.snooperEnabled);
            printwriter.println("fullscreen:" + this.fullScreen);
            printwriter.println("enableVsync:" + this.enableVsync);
            printwriter.println("useVbo:" + this.useVbo);
            printwriter.println("hideServerAddress:" + this.hideServerAddress);
            printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
            printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            printwriter.println("touchscreen:" + this.touchscreen);
            printwriter.println("overrideWidth:" + this.overrideWidth);
            printwriter.println("overrideHeight:" + this.overrideHeight);
            printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
            printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
            printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            printwriter.println("chatScale:" + this.chatScale);
            printwriter.println("chatWidth:" + this.chatWidth);
            printwriter.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
            printwriter.println("mipmapLevels:" + this.mipmapLevels);
            printwriter.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
            printwriter.println("streamMicVolume:" + this.streamMicVolume);
            printwriter.println("streamSystemVolume:" + this.streamGameVolume);
            printwriter.println("streamKbps:" + this.streamKbps);
            printwriter.println("streamFps:" + this.streamFps);
            printwriter.println("streamCompression:" + this.streamCompression);
            printwriter.println("streamSendMetadata:" + this.streamSendMetadata);
            printwriter.println("streamPreferredServer:" + this.streamPreferredServer);
            printwriter.println("streamChatEnabled:" + this.streamChatEnabled);
            printwriter.println("streamChatUserFilter:" + this.streamChatUserFilter);
            printwriter.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
            printwriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
            printwriter.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
            printwriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
            printwriter.println("useNativeTransport:" + this.useNativeTransport);
            printwriter.println("entityShadows:" + this.entityShadows);
            printwriter.println("realmsNotifications:" + this.realmsNotifications);

            for (KeyBinding keybinding : this.keyBindings)
            {
                printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getKeyCode());
            }

            for (SoundCategory soundcategory : SoundCategory.values())
            {
                printwriter.println("soundCategory_" + soundcategory.getCategoryName() + ":" + this.getSoundLevel(soundcategory));
            }

            for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values())
            {
                printwriter.println("modelPart_" + enumplayermodelparts.getPartName() + ":" + this.setModelParts.contains(enumplayermodelparts));
            }

            printwriter.close();
        }
        catch (Exception exception)
        {
            logger.error("Failed to save options", exception);
        }

        this.saveOfOptions();
        this.sendSettingsToServer();
    }

    public float getSoundLevel(SoundCategory sndCategory)
    {
        return this.mapSoundLevels.containsKey(sndCategory) ? this.mapSoundLevels.get(sndCategory).floatValue() : 1.0F;
    }

    public void setSoundLevel(SoundCategory sndCategory, float soundLevel)
    {
        this.mc.getSoundHandler().setSoundLevel(sndCategory, soundLevel);
        this.mapSoundLevels.put(sndCategory, Float.valueOf(soundLevel));
    }

    public void sendSettingsToServer()
    {
        if (this.mc.thePlayer != null)
        {
            int i = 0;

            for (EnumPlayerModelParts enumplayermodelparts : this.setModelParts)
            {
                i |= enumplayermodelparts.getPartMask();
            }

            this.mc.thePlayer.sendQueue.addToSendQueue(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, i));
        }
    }

    public Set<EnumPlayerModelParts> getModelParts()
    {
        return ImmutableSet.copyOf(this.setModelParts);
    }

    public void setModelPartEnabled(EnumPlayerModelParts modelPart, boolean enable)
    {
        if (enable)
        {
            this.setModelParts.add(modelPart);
        }
        else
        {
            this.setModelParts.remove(modelPart);
        }

        this.sendSettingsToServer();
    }

    public void switchModelPartEnabled(EnumPlayerModelParts modelPart)
    {
        if (!this.getModelParts().contains(modelPart))
        {
            this.setModelParts.add(modelPart);
        }
        else
        {
            this.setModelParts.remove(modelPart);
        }

        this.sendSettingsToServer();
    }

    public int shouldRenderClouds()
    {
        return this.renderDistanceChunks >= 4 ? this.clouds : 0;
    }

    public boolean isUsingNativeTransport()
    {
        return this.useNativeTransport;
    }

    private void setOptionFloatValueOF(GameSettings.Options p_setOptionFloatValueOF_1_, float p_setOptionFloatValueOF_2_)
    {
        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.CLOUD_HEIGHT)
        {
            this.ofCloudsHeight = p_setOptionFloatValueOF_2_;
            this.mc.renderGlobal.resetClouds();
        }

        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.AO_LEVEL)
        {
            this.ofAoLevel = p_setOptionFloatValueOF_2_;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.AA_LEVEL)
        {
            int i = (int)p_setOptionFloatValueOF_2_;

            if (i > 0 && Config.isShaders())
            {
                Config.showGuiMessage(Lang.get("of.message.aa.shaders1"), Lang.get("of.message.aa.shaders2"));
                return;
            }

            int[] aint = new int[] {0, 2, 4, 6, 8, 12, 16};
            this.ofAaLevel = 0;

            for (int j = 0; j < aint.length; ++j)
            {
                if (i >= aint[j])
                {
                    this.ofAaLevel = aint[j];
                }
            }

            this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
        }

        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.AF_LEVEL)
        {
            int k = (int)p_setOptionFloatValueOF_2_;

            if (k > 1 && Config.isShaders())
            {
                Config.showGuiMessage(Lang.get("of.message.af.shaders1"), Lang.get("of.message.af.shaders2"));
                return;
            }

            for (this.ofAfLevel = 1; this.ofAfLevel * 2 <= k; this.ofAfLevel *= 2)
            {
            }

            this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
            this.mc.refreshResources();
        }

        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.MIPMAP_TYPE)
        {
            int l = (int)p_setOptionFloatValueOF_2_;
            this.ofMipmapType = Config.limit(l, 0, 3);
            this.mc.refreshResources();
        }

        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.FULLSCREEN_MODE)
        {
            int i1 = (int)p_setOptionFloatValueOF_2_ - 1;
            String[] astring = Config.getDisplayModeNames();

            if (i1 < 0 || i1 >= astring.length)
            {
                this.ofFullscreenMode = "Default";
                return;
            }

            this.ofFullscreenMode = astring[i1];
        }
    }

    private float getOptionFloatValueOF(GameSettings.Options p_getOptionFloatValueOF_1_)
    {
        if (p_getOptionFloatValueOF_1_ == GameSettings.Options.CLOUD_HEIGHT)
        {
            return this.ofCloudsHeight;
        }
        else if (p_getOptionFloatValueOF_1_ == GameSettings.Options.AO_LEVEL)
        {
            return this.ofAoLevel;
        }
        else if (p_getOptionFloatValueOF_1_ == GameSettings.Options.AA_LEVEL)
        {
            return (float)this.ofAaLevel;
        }
        else if (p_getOptionFloatValueOF_1_ == GameSettings.Options.AF_LEVEL)
        {
            return (float)this.ofAfLevel;
        }
        else if (p_getOptionFloatValueOF_1_ == GameSettings.Options.MIPMAP_TYPE)
        {
            return (float)this.ofMipmapType;
        }
        else if (p_getOptionFloatValueOF_1_ == GameSettings.Options.FRAMERATE_LIMIT)
        {
            return (float)this.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() && this.enableVsync ? 0.0F : (float)this.limitFramerate;
        }
        else if (p_getOptionFloatValueOF_1_ == GameSettings.Options.FULLSCREEN_MODE)
        {
            if (this.ofFullscreenMode.equals("Default"))
            {
                return 0.0F;
            }
            else
            {
                List list = Arrays.asList(Config.getDisplayModeNames());
                int i = list.indexOf(this.ofFullscreenMode);
                return i < 0 ? 0.0F : (float)(i + 1);
            }
        }
        else
        {
            return Float.MAX_VALUE;
        }
    }

    private void setOptionValueOF(GameSettings.Options p_setOptionValueOF_1_, int p_setOptionValueOF_2_)
    {
        if (p_setOptionValueOF_1_ == GameSettings.Options.FOG_FANCY)
        {
            switch (this.ofFogType)
            {
                case 1:
                    this.ofFogType = 2;

                    if (!Config.isFancyFogAvailable())
                    {
                        this.ofFogType = 3;
                    }

                    break;

                case 2:
                    this.ofFogType = 3;
                    break;

                case 3:
                    this.ofFogType = 1;
                    break;

                default:
                    this.ofFogType = 1;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.FOG_START)
        {
            this.ofFogStart += 0.2F;

            if (this.ofFogStart > 0.81F)
            {
                this.ofFogStart = 0.2F;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SMOOTH_FPS)
        {
            this.ofSmoothFps = !this.ofSmoothFps;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SMOOTH_WORLD)
        {
            this.ofSmoothWorld = !this.ofSmoothWorld;
            Config.updateThreadPriorities();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CLOUDS)
        {
            ++this.ofClouds;

            if (this.ofClouds > 3)
            {
                this.ofClouds = 0;
            }

            this.updateRenderClouds();
            this.mc.renderGlobal.resetClouds();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.TREES)
        {
            this.ofTrees = nextValue(this.ofTrees, OF_TREES_VALUES);
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.DROPPED_ITEMS)
        {
            ++this.ofDroppedItems;

            if (this.ofDroppedItems > 2)
            {
                this.ofDroppedItems = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.RAIN)
        {
            ++this.ofRain;

            if (this.ofRain > 3)
            {
                this.ofRain = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_WATER)
        {
            ++this.ofAnimatedWater;

            if (this.ofAnimatedWater == 1)
            {
                ++this.ofAnimatedWater;
            }

            if (this.ofAnimatedWater > 2)
            {
                this.ofAnimatedWater = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_LAVA)
        {
            ++this.ofAnimatedLava;

            if (this.ofAnimatedLava == 1)
            {
                ++this.ofAnimatedLava;
            }

            if (this.ofAnimatedLava > 2)
            {
                this.ofAnimatedLava = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_FIRE)
        {
            this.ofAnimatedFire = !this.ofAnimatedFire;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_PORTAL)
        {
            this.ofAnimatedPortal = !this.ofAnimatedPortal;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_REDSTONE)
        {
            this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_EXPLOSION)
        {
            this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_FLAME)
        {
            this.ofAnimatedFlame = !this.ofAnimatedFlame;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_SMOKE)
        {
            this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.VOID_PARTICLES)
        {
            this.ofVoidParticles = !this.ofVoidParticles;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.WATER_PARTICLES)
        {
            this.ofWaterParticles = !this.ofWaterParticles;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.PORTAL_PARTICLES)
        {
            this.ofPortalParticles = !this.ofPortalParticles;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.POTION_PARTICLES)
        {
            this.ofPotionParticles = !this.ofPotionParticles;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.FIREWORK_PARTICLES)
        {
            this.ofFireworkParticles = !this.ofFireworkParticles;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.DRIPPING_WATER_LAVA)
        {
            this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_TERRAIN)
        {
            this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_TEXTURES)
        {
            this.ofAnimatedTextures = !this.ofAnimatedTextures;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.RAIN_SPLASH)
        {
            this.ofRainSplash = !this.ofRainSplash;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.LAGOMETER)
        {
            this.ofLagometer = !this.ofLagometer;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SHOW_FPS)
        {
            this.ofShowFps = !this.ofShowFps;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.AUTOSAVE_TICKS)
        {
            int i = 900;
            this.ofAutoSaveTicks = Math.max(this.ofAutoSaveTicks / i * i, i);
            this.ofAutoSaveTicks *= 2;

            if (this.ofAutoSaveTicks > 32 * i)
            {
                this.ofAutoSaveTicks = i;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.BETTER_GRASS)
        {
            ++this.ofBetterGrass;

            if (this.ofBetterGrass > 3)
            {
                this.ofBetterGrass = 1;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CONNECTED_TEXTURES)
        {
            ++this.ofConnectedTextures;

            if (this.ofConnectedTextures > 3)
            {
                this.ofConnectedTextures = 1;
            }

            if (this.ofConnectedTextures == 2)
            {
                this.mc.renderGlobal.loadRenderers();
            }
            else
            {
                this.mc.refreshResources();
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.WEATHER)
        {
            this.ofWeather = !this.ofWeather;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SKY)
        {
            this.ofSky = !this.ofSky;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.STARS)
        {
            this.ofStars = !this.ofStars;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SUN_MOON)
        {
            this.ofSunMoon = !this.ofSunMoon;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.VIGNETTE)
        {
            ++this.ofVignette;

            if (this.ofVignette > 2)
            {
                this.ofVignette = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CHUNK_UPDATES)
        {
            ++this.ofChunkUpdates;

            if (this.ofChunkUpdates > 5)
            {
                this.ofChunkUpdates = 1;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CHUNK_UPDATES_DYNAMIC)
        {
            this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.TIME)
        {
            ++this.ofTime;

            if (this.ofTime > 2)
            {
                this.ofTime = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CLEAR_WATER)
        {
            this.ofClearWater = !this.ofClearWater;
            this.updateWaterOpacity();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.PROFILER)
        {
            this.ofProfiler = !this.ofProfiler;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.BETTER_SNOW)
        {
            this.ofBetterSnow = !this.ofBetterSnow;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SWAMP_COLORS)
        {
            this.ofSwampColors = !this.ofSwampColors;
            CustomColors.updateUseDefaultGrassFoliageColors();
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.RANDOM_ENTITIES)
        {
            this.ofRandomEntities = !this.ofRandomEntities;
            RandomEntities.update();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SMOOTH_BIOMES)
        {
            this.ofSmoothBiomes = !this.ofSmoothBiomes;
            CustomColors.updateUseDefaultGrassFoliageColors();
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_FONTS)
        {
            this.ofCustomFonts = !this.ofCustomFonts;
            this.mc.fontRendererObj.onResourceManagerReload(Config.getResourceManager());
            this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_COLORS)
        {
            this.ofCustomColors = !this.ofCustomColors;
            CustomColors.update();
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_ITEMS)
        {
            this.ofCustomItems = !this.ofCustomItems;
            this.mc.refreshResources();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_SKY)
        {
            this.ofCustomSky = !this.ofCustomSky;
            CustomSky.update();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SHOW_CAPES)
        {
            this.ofShowCapes = !this.ofShowCapes;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.NATURAL_TEXTURES)
        {
            this.ofNaturalTextures = !this.ofNaturalTextures;
            NaturalTextures.update();
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.EMISSIVE_TEXTURES)
        {
            this.ofEmissiveTextures = !this.ofEmissiveTextures;
            this.mc.refreshResources();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.FAST_MATH)
        {
            this.ofFastMath = !this.ofFastMath;
            MathHelper.fastMath = this.ofFastMath;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.FAST_RENDER)
        {
            if (!this.ofFastRender && Config.isShaders())
            {
                Config.showGuiMessage(Lang.get("of.message.fr.shaders1"), Lang.get("of.message.fr.shaders2"));
                return;
            }

            this.ofFastRender = !this.ofFastRender;

            if (this.ofFastRender)
            {
                this.mc.entityRenderer.stopUseShader();
            }

            Config.updateFramebufferSize();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.TRANSLUCENT_BLOCKS)
        {
            if (this.ofTranslucentBlocks == 0)
            {
                this.ofTranslucentBlocks = 1;
            }
            else if (this.ofTranslucentBlocks == 1)
            {
                this.ofTranslucentBlocks = 2;
            }
            else if (this.ofTranslucentBlocks == 2)
            {
                this.ofTranslucentBlocks = 0;
            }
            else
            {
                this.ofTranslucentBlocks = 0;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.LAZY_CHUNK_LOADING)
        {
            this.ofLazyChunkLoading = !this.ofLazyChunkLoading;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.RENDER_REGIONS)
        {
            this.ofRenderRegions = !this.ofRenderRegions;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SMART_ANIMATIONS)
        {
            this.ofSmartAnimations = !this.ofSmartAnimations;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.DYNAMIC_FOV)
        {
            this.ofDynamicFov = !this.ofDynamicFov;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ALTERNATE_BLOCKS)
        {
            this.ofAlternateBlocks = !this.ofAlternateBlocks;
            this.mc.refreshResources();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.DYNAMIC_LIGHTS)
        {
            this.ofDynamicLights = nextValue(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
            DynamicLights.removeLights(this.mc.renderGlobal);
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SCREENSHOT_SIZE)
        {
            ++this.ofScreenshotSize;

            if (this.ofScreenshotSize > 4)
            {
                this.ofScreenshotSize = 1;
            }

            if (!OpenGlHelper.isFramebufferEnabled())
            {
                this.ofScreenshotSize = 1;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_ENTITY_MODELS)
        {
            this.ofCustomEntityModels = !this.ofCustomEntityModels;
            this.mc.refreshResources();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_GUIS)
        {
            this.ofCustomGuis = !this.ofCustomGuis;
            CustomGuis.update();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SHOW_GL_ERRORS)
        {
            this.ofShowGlErrors = !this.ofShowGlErrors;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.HELD_ITEM_TOOLTIPS)
        {
            this.heldItemTooltips = !this.heldItemTooltips;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ADVANCED_TOOLTIPS)
        {
            this.advancedItemTooltips = !this.advancedItemTooltips;
        }
    }

    private String getKeyBindingOF(GameSettings.Options p_getKeyBindingOF_1_)
    {
        String s = I18n.format(p_getKeyBindingOF_1_.getEnumString()) + ": ";

        if (s == null)
        {
            s = p_getKeyBindingOF_1_.getEnumString();
        }

        if (p_getKeyBindingOF_1_ == GameSettings.Options.RENDER_DISTANCE)
        {
            int i1 = (int)this.getOptionFloatValue(p_getKeyBindingOF_1_);
            String s1 = I18n.format("options.renderDistance.tiny");
            int i = 2;

            if (i1 >= 4)
            {
                s1 = I18n.format("options.renderDistance.short");
                i = 4;
            }

            if (i1 >= 8)
            {
                s1 = I18n.format("options.renderDistance.normal");
                i = 8;
            }

            if (i1 >= 16)
            {
                s1 = I18n.format("options.renderDistance.far");
                i = 16;
            }

            if (i1 >= 32)
            {
                s1 = Lang.get("of.options.renderDistance.extreme");
                i = 32;
            }

            if (i1 >= 48)
            {
                s1 = Lang.get("of.options.renderDistance.insane");
                i = 48;
            }

            if (i1 >= 64)
            {
                s1 = Lang.get("of.options.renderDistance.ludicrous");
                i = 64;
            }

            int j = this.renderDistanceChunks - i;
            String s2 = s1;

            if (j > 0)
            {
                s2 = s1 + "+";
            }

            return s + i1 + " " + s2;
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.FOG_FANCY)
        {
            switch (this.ofFogType)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                case 3:
                    return s + Lang.getOff();

                default:
                    return s + Lang.getOff();
            }
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.FOG_START)
        {
            return s + this.ofFogStart;
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.MIPMAP_TYPE)
        {
            switch (this.ofMipmapType)
            {
                case 0:
                    return s + Lang.get("of.options.mipmap.nearest");

                case 1:
                    return s + Lang.get("of.options.mipmap.linear");

                case 2:
                    return s + Lang.get("of.options.mipmap.bilinear");

                case 3:
                    return s + Lang.get("of.options.mipmap.trilinear");

                default:
                    return s + "of.options.mipmap.nearest";
            }
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.SMOOTH_FPS)
        {
            return this.ofSmoothFps ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.SMOOTH_WORLD)
        {
            return this.ofSmoothWorld ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.CLOUDS)
        {
            switch (this.ofClouds)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                case 3:
                    return s + Lang.getOff();

                default:
                    return s + Lang.getDefault();
            }
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.TREES)
        {
            switch (this.ofTrees)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                case 3:
                default:
                    return s + Lang.getDefault();

                case 4:
                    return s + Lang.get("of.general.smart");
            }
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.DROPPED_ITEMS)
        {
            switch (this.ofDroppedItems)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                default:
                    return s + Lang.getDefault();
            }
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.RAIN)
        {
            switch (this.ofRain)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                case 3:
                    return s + Lang.getOff();

                default:
                    return s + Lang.getDefault();
            }
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_WATER)
        {
            switch (this.ofAnimatedWater)
            {
                case 1:
                    return s + Lang.get("of.options.animation.dynamic");

                case 2:
                    return s + Lang.getOff();

                default:
                    return s + Lang.getOn();
            }
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_LAVA)
        {
            switch (this.ofAnimatedLava)
            {
                case 1:
                    return s + Lang.get("of.options.animation.dynamic");

                case 2:
                    return s + Lang.getOff();

                default:
                    return s + Lang.getOn();
            }
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_FIRE)
        {
            return this.ofAnimatedFire ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_PORTAL)
        {
            return this.ofAnimatedPortal ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_REDSTONE)
        {
            return this.ofAnimatedRedstone ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_EXPLOSION)
        {
            return this.ofAnimatedExplosion ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_FLAME)
        {
            return this.ofAnimatedFlame ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_SMOKE)
        {
            return this.ofAnimatedSmoke ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.VOID_PARTICLES)
        {
            return this.ofVoidParticles ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.WATER_PARTICLES)
        {
            return this.ofWaterParticles ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.PORTAL_PARTICLES)
        {
            return this.ofPortalParticles ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.POTION_PARTICLES)
        {
            return this.ofPotionParticles ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.FIREWORK_PARTICLES)
        {
            return this.ofFireworkParticles ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.DRIPPING_WATER_LAVA)
        {
            return this.ofDrippingWaterLava ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_TERRAIN)
        {
            return this.ofAnimatedTerrain ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_TEXTURES)
        {
            return this.ofAnimatedTextures ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.RAIN_SPLASH)
        {
            return this.ofRainSplash ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.LAGOMETER)
        {
            return this.ofLagometer ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.SHOW_FPS)
        {
            return this.ofShowFps ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.AUTOSAVE_TICKS)
        {
            int l = 900;
            return this.ofAutoSaveTicks <= l ? s + Lang.get("of.options.save.45s") : (this.ofAutoSaveTicks <= 2 * l ? s + Lang.get("of.options.save.90s") : (this.ofAutoSaveTicks <= 4 * l ? s + Lang.get("of.options.save.3min") : (this.ofAutoSaveTicks <= 8 * l ? s + Lang.get("of.options.save.6min") : (this.ofAutoSaveTicks <= 16 * l ? s + Lang.get("of.options.save.12min") : s + Lang.get("of.options.save.24min")))));
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.BETTER_GRASS)
        {
            switch (this.ofBetterGrass)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                default:
                    return s + Lang.getOff();
            }
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.CONNECTED_TEXTURES)
        {
            switch (this.ofConnectedTextures)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                default:
                    return s + Lang.getOff();
            }
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.WEATHER)
        {
            return this.ofWeather ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.SKY)
        {
            return this.ofSky ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.STARS)
        {
            return this.ofStars ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.SUN_MOON)
        {
            return this.ofSunMoon ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.VIGNETTE)
        {
            switch (this.ofVignette)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                default:
                    return s + Lang.getDefault();
            }
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.CHUNK_UPDATES)
        {
            return s + this.ofChunkUpdates;
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.CHUNK_UPDATES_DYNAMIC)
        {
            return this.ofChunkUpdatesDynamic ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.TIME)
        {
            return this.ofTime == 1 ? s + Lang.get("of.options.time.dayOnly") : (this.ofTime == 2 ? s + Lang.get("of.options.time.nightOnly") : s + Lang.getDefault());
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.CLEAR_WATER)
        {
            return this.ofClearWater ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.AA_LEVEL)
        {
            String s3 = "";

            if (this.ofAaLevel != Config.getAntialiasingLevel())
            {
                s3 = " (" + Lang.get("of.general.restart") + ")";
            }

            return this.ofAaLevel == 0 ? s + Lang.getOff() + s3 : s + this.ofAaLevel + s3;
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.AF_LEVEL)
        {
            return this.ofAfLevel == 1 ? s + Lang.getOff() : s + this.ofAfLevel;
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.PROFILER)
        {
            return this.ofProfiler ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.BETTER_SNOW)
        {
            return this.ofBetterSnow ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.SWAMP_COLORS)
        {
            return this.ofSwampColors ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.RANDOM_ENTITIES)
        {
            return this.ofRandomEntities ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.SMOOTH_BIOMES)
        {
            return this.ofSmoothBiomes ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_FONTS)
        {
            return this.ofCustomFonts ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_COLORS)
        {
            return this.ofCustomColors ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_SKY)
        {
            return this.ofCustomSky ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.SHOW_CAPES)
        {
            return this.ofShowCapes ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_ITEMS)
        {
            return this.ofCustomItems ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.NATURAL_TEXTURES)
        {
            return this.ofNaturalTextures ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.EMISSIVE_TEXTURES)
        {
            return this.ofEmissiveTextures ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.FAST_MATH)
        {
            return this.ofFastMath ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.FAST_RENDER)
        {
            return this.ofFastRender ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.TRANSLUCENT_BLOCKS)
        {
            return this.ofTranslucentBlocks == 1 ? s + Lang.getFast() : (this.ofTranslucentBlocks == 2 ? s + Lang.getFancy() : s + Lang.getDefault());
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.LAZY_CHUNK_LOADING)
        {
            return this.ofLazyChunkLoading ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.RENDER_REGIONS)
        {
            return this.ofRenderRegions ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.SMART_ANIMATIONS)
        {
            return this.ofSmartAnimations ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.DYNAMIC_FOV)
        {
            return this.ofDynamicFov ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ALTERNATE_BLOCKS)
        {
            return this.ofAlternateBlocks ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.DYNAMIC_LIGHTS)
        {
            int k = indexOf(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
            return s + getTranslation(KEYS_DYNAMIC_LIGHTS, k);
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.SCREENSHOT_SIZE)
        {
            return this.ofScreenshotSize <= 1 ? s + Lang.getDefault() : s + this.ofScreenshotSize + "x";
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_ENTITY_MODELS)
        {
            return this.ofCustomEntityModels ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_GUIS)
        {
            return this.ofCustomGuis ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.SHOW_GL_ERRORS)
        {
            return this.ofShowGlErrors ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.FULLSCREEN_MODE)
        {
            return this.ofFullscreenMode.equals("Default") ? s + Lang.getDefault() : s + this.ofFullscreenMode;
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.HELD_ITEM_TOOLTIPS)
        {
            return this.heldItemTooltips ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.ADVANCED_TOOLTIPS)
        {
            return this.advancedItemTooltips ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == GameSettings.Options.FRAMERATE_LIMIT)
        {
            float f = this.getOptionFloatValue(p_getKeyBindingOF_1_);
            return f == 0.0F ? s + Lang.get("of.options.framerateLimit.vsync") : (f == p_getKeyBindingOF_1_.valueMax ? s + I18n.format("options.framerateLimit.max") : s + (int)f + " fps");
        }
        else
        {
            return null;
        }
    }

    public void loadOfOptions()
    {
        try
        {
            File file1 = this.optionsFileOF;

            if (!file1.exists())
            {
                file1 = this.optionsFile;
            }

            if (!file1.exists())
            {
                return;
            }

            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new FileInputStream(file1), StandardCharsets.UTF_8));
            String s = "";

            while ((s = bufferedreader.readLine()) != null)
            {
                try
                {
                    String[] astring = s.split(":");

                    if (astring[0].equals("ofRenderDistanceChunks") && astring.length >= 2)
                    {
                        this.renderDistanceChunks = Integer.valueOf(astring[1]).intValue();
                        this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 1024);
                    }

                    if (astring[0].equals("ofFogType") && astring.length >= 2)
                    {
                        this.ofFogType = Integer.valueOf(astring[1]).intValue();
                        this.ofFogType = Config.limit(this.ofFogType, 1, 3);
                    }

                    if (astring[0].equals("ofFogStart") && astring.length >= 2)
                    {
                        this.ofFogStart = Float.valueOf(astring[1]).floatValue();

                        if (this.ofFogStart < 0.2F)
                        {
                            this.ofFogStart = 0.2F;
                        }

                        if (this.ofFogStart > 0.81F)
                        {
                            this.ofFogStart = 0.8F;
                        }
                    }

                    if (astring[0].equals("ofMipmapType") && astring.length >= 2)
                    {
                        this.ofMipmapType = Integer.valueOf(astring[1]).intValue();
                        this.ofMipmapType = Config.limit(this.ofMipmapType, 0, 3);
                    }

                    if (astring[0].equals("ofOcclusionFancy") && astring.length >= 2)
                    {
                        this.ofOcclusionFancy = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofSmoothFps") && astring.length >= 2)
                    {
                        this.ofSmoothFps = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofSmoothWorld") && astring.length >= 2)
                    {
                        this.ofSmoothWorld = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofAoLevel") && astring.length >= 2)
                    {
                        this.ofAoLevel = Float.valueOf(astring[1]).floatValue();
                        this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0F, 1.0F);
                    }

                    if (astring[0].equals("ofClouds") && astring.length >= 2)
                    {
                        this.ofClouds = Integer.valueOf(astring[1]).intValue();
                        this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                        this.updateRenderClouds();
                    }

                    if (astring[0].equals("ofCloudsHeight") && astring.length >= 2)
                    {
                        this.ofCloudsHeight = Float.valueOf(astring[1]).floatValue();
                        this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0F, 1.0F);
                    }

                    if (astring[0].equals("ofTrees") && astring.length >= 2)
                    {
                        this.ofTrees = Integer.valueOf(astring[1]).intValue();
                        this.ofTrees = limit(this.ofTrees, OF_TREES_VALUES);
                    }

                    if (astring[0].equals("ofDroppedItems") && astring.length >= 2)
                    {
                        this.ofDroppedItems = Integer.valueOf(astring[1]).intValue();
                        this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
                    }

                    if (astring[0].equals("ofRain") && astring.length >= 2)
                    {
                        this.ofRain = Integer.valueOf(astring[1]).intValue();
                        this.ofRain = Config.limit(this.ofRain, 0, 3);
                    }

                    if (astring[0].equals("ofAnimatedWater") && astring.length >= 2)
                    {
                        this.ofAnimatedWater = Integer.valueOf(astring[1]).intValue();
                        this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
                    }

                    if (astring[0].equals("ofAnimatedLava") && astring.length >= 2)
                    {
                        this.ofAnimatedLava = Integer.valueOf(astring[1]).intValue();
                        this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
                    }

                    if (astring[0].equals("ofAnimatedFire") && astring.length >= 2)
                    {
                        this.ofAnimatedFire = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofAnimatedPortal") && astring.length >= 2)
                    {
                        this.ofAnimatedPortal = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofAnimatedRedstone") && astring.length >= 2)
                    {
                        this.ofAnimatedRedstone = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofAnimatedExplosion") && astring.length >= 2)
                    {
                        this.ofAnimatedExplosion = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofAnimatedFlame") && astring.length >= 2)
                    {
                        this.ofAnimatedFlame = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofAnimatedSmoke") && astring.length >= 2)
                    {
                        this.ofAnimatedSmoke = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofVoidParticles") && astring.length >= 2)
                    {
                        this.ofVoidParticles = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofWaterParticles") && astring.length >= 2)
                    {
                        this.ofWaterParticles = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofPortalParticles") && astring.length >= 2)
                    {
                        this.ofPortalParticles = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofPotionParticles") && astring.length >= 2)
                    {
                        this.ofPotionParticles = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofFireworkParticles") && astring.length >= 2)
                    {
                        this.ofFireworkParticles = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofDrippingWaterLava") && astring.length >= 2)
                    {
                        this.ofDrippingWaterLava = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofAnimatedTerrain") && astring.length >= 2)
                    {
                        this.ofAnimatedTerrain = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofAnimatedTextures") && astring.length >= 2)
                    {
                        this.ofAnimatedTextures = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofRainSplash") && astring.length >= 2)
                    {
                        this.ofRainSplash = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofLagometer") && astring.length >= 2)
                    {
                        this.ofLagometer = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofShowFps") && astring.length >= 2)
                    {
                        this.ofShowFps = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofAutoSaveTicks") && astring.length >= 2)
                    {
                        this.ofAutoSaveTicks = Integer.valueOf(astring[1]).intValue();
                        this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
                    }

                    if (astring[0].equals("ofBetterGrass") && astring.length >= 2)
                    {
                        this.ofBetterGrass = Integer.valueOf(astring[1]).intValue();
                        this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
                    }

                    if (astring[0].equals("ofConnectedTextures") && astring.length >= 2)
                    {
                        this.ofConnectedTextures = Integer.valueOf(astring[1]).intValue();
                        this.ofConnectedTextures = Config.limit(this.ofConnectedTextures, 1, 3);
                    }

                    if (astring[0].equals("ofWeather") && astring.length >= 2)
                    {
                        this.ofWeather = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofSky") && astring.length >= 2)
                    {
                        this.ofSky = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofStars") && astring.length >= 2)
                    {
                        this.ofStars = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofSunMoon") && astring.length >= 2)
                    {
                        this.ofSunMoon = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofVignette") && astring.length >= 2)
                    {
                        this.ofVignette = Integer.valueOf(astring[1]).intValue();
                        this.ofVignette = Config.limit(this.ofVignette, 0, 2);
                    }

                    if (astring[0].equals("ofChunkUpdates") && astring.length >= 2)
                    {
                        this.ofChunkUpdates = Integer.valueOf(astring[1]).intValue();
                        this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
                    }

                    if (astring[0].equals("ofChunkUpdatesDynamic") && astring.length >= 2)
                    {
                        this.ofChunkUpdatesDynamic = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofTime") && astring.length >= 2)
                    {
                        this.ofTime = Integer.valueOf(astring[1]).intValue();
                        this.ofTime = Config.limit(this.ofTime, 0, 2);
                    }

                    if (astring[0].equals("ofClearWater") && astring.length >= 2)
                    {
                        this.ofClearWater = Boolean.valueOf(astring[1]).booleanValue();
                        this.updateWaterOpacity();
                    }

                    if (astring[0].equals("ofAaLevel") && astring.length >= 2)
                    {
                        this.ofAaLevel = Integer.valueOf(astring[1]).intValue();
                        this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
                    }

                    if (astring[0].equals("ofAfLevel") && astring.length >= 2)
                    {
                        this.ofAfLevel = Integer.valueOf(astring[1]).intValue();
                        this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
                    }

                    if (astring[0].equals("ofProfiler") && astring.length >= 2)
                    {
                        this.ofProfiler = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofBetterSnow") && astring.length >= 2)
                    {
                        this.ofBetterSnow = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofSwampColors") && astring.length >= 2)
                    {
                        this.ofSwampColors = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofRandomEntities") && astring.length >= 2)
                    {
                        this.ofRandomEntities = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofSmoothBiomes") && astring.length >= 2)
                    {
                        this.ofSmoothBiomes = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofCustomFonts") && astring.length >= 2)
                    {
                        this.ofCustomFonts = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofCustomColors") && astring.length >= 2)
                    {
                        this.ofCustomColors = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofCustomItems") && astring.length >= 2)
                    {
                        this.ofCustomItems = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofCustomSky") && astring.length >= 2)
                    {
                        this.ofCustomSky = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofShowCapes") && astring.length >= 2)
                    {
                        this.ofShowCapes = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofNaturalTextures") && astring.length >= 2)
                    {
                        this.ofNaturalTextures = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofEmissiveTextures") && astring.length >= 2)
                    {
                        this.ofEmissiveTextures = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofLazyChunkLoading") && astring.length >= 2)
                    {
                        this.ofLazyChunkLoading = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofRenderRegions") && astring.length >= 2)
                    {
                        this.ofRenderRegions = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofSmartAnimations") && astring.length >= 2)
                    {
                        this.ofSmartAnimations = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofDynamicFov") && astring.length >= 2)
                    {
                        this.ofDynamicFov = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofAlternateBlocks") && astring.length >= 2)
                    {
                        this.ofAlternateBlocks = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofDynamicLights") && astring.length >= 2)
                    {
                        this.ofDynamicLights = Integer.valueOf(astring[1]).intValue();
                        this.ofDynamicLights = limit(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                    }

                    if (astring[0].equals("ofScreenshotSize") && astring.length >= 2)
                    {
                        this.ofScreenshotSize = Integer.valueOf(astring[1]).intValue();
                        this.ofScreenshotSize = Config.limit(this.ofScreenshotSize, 1, 4);
                    }

                    if (astring[0].equals("ofCustomEntityModels") && astring.length >= 2)
                    {
                        this.ofCustomEntityModels = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofCustomGuis") && astring.length >= 2)
                    {
                        this.ofCustomGuis = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofShowGlErrors") && astring.length >= 2)
                    {
                        this.ofShowGlErrors = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofFullscreenMode") && astring.length >= 2)
                    {
                        this.ofFullscreenMode = astring[1];
                    }

                    if (astring[0].equals("ofFastMath") && astring.length >= 2)
                    {
                        this.ofFastMath = Boolean.valueOf(astring[1]).booleanValue();
                        MathHelper.fastMath = this.ofFastMath;
                    }

                    if (astring[0].equals("ofFastRender") && astring.length >= 2)
                    {
                        this.ofFastRender = Boolean.valueOf(astring[1]).booleanValue();
                    }

                    if (astring[0].equals("ofTranslucentBlocks") && astring.length >= 2)
                    {
                        this.ofTranslucentBlocks = Integer.valueOf(astring[1]).intValue();
                        this.ofTranslucentBlocks = Config.limit(this.ofTranslucentBlocks, 0, 2);
                    }

                    if (astring[0].equals("key_" + this.ofKeyBindZoom.getKeyDescription()))
                    {
                        this.ofKeyBindZoom.setKeyCode(Integer.parseInt(astring[1]));
                    }
                }
                catch (Exception exception)
                {
                    Config.dbg("Skipping bad option: " + s);
                    exception.printStackTrace();
                }
            }

            KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[] {this.ofKeyBindZoom});
            KeyBinding.resetKeyBindingArrayAndHash();
            bufferedreader.close();
        }
        catch (Exception exception1)
        {
            Config.warn("Failed to load options");
            exception1.printStackTrace();
        }
    }

    public void saveOfOptions()
    {
        try
        {
            PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFileOF), StandardCharsets.UTF_8));
            printwriter.println("ofFogType:" + this.ofFogType);
            printwriter.println("ofFogStart:" + this.ofFogStart);
            printwriter.println("ofMipmapType:" + this.ofMipmapType);
            printwriter.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
            printwriter.println("ofSmoothFps:" + this.ofSmoothFps);
            printwriter.println("ofSmoothWorld:" + this.ofSmoothWorld);
            printwriter.println("ofAoLevel:" + this.ofAoLevel);
            printwriter.println("ofClouds:" + this.ofClouds);
            printwriter.println("ofCloudsHeight:" + this.ofCloudsHeight);
            printwriter.println("ofTrees:" + this.ofTrees);
            printwriter.println("ofDroppedItems:" + this.ofDroppedItems);
            printwriter.println("ofRain:" + this.ofRain);
            printwriter.println("ofAnimatedWater:" + this.ofAnimatedWater);
            printwriter.println("ofAnimatedLava:" + this.ofAnimatedLava);
            printwriter.println("ofAnimatedFire:" + this.ofAnimatedFire);
            printwriter.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
            printwriter.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
            printwriter.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
            printwriter.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
            printwriter.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
            printwriter.println("ofVoidParticles:" + this.ofVoidParticles);
            printwriter.println("ofWaterParticles:" + this.ofWaterParticles);
            printwriter.println("ofPortalParticles:" + this.ofPortalParticles);
            printwriter.println("ofPotionParticles:" + this.ofPotionParticles);
            printwriter.println("ofFireworkParticles:" + this.ofFireworkParticles);
            printwriter.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
            printwriter.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
            printwriter.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
            printwriter.println("ofRainSplash:" + this.ofRainSplash);
            printwriter.println("ofLagometer:" + this.ofLagometer);
            printwriter.println("ofShowFps:" + this.ofShowFps);
            printwriter.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
            printwriter.println("ofBetterGrass:" + this.ofBetterGrass);
            printwriter.println("ofConnectedTextures:" + this.ofConnectedTextures);
            printwriter.println("ofWeather:" + this.ofWeather);
            printwriter.println("ofSky:" + this.ofSky);
            printwriter.println("ofStars:" + this.ofStars);
            printwriter.println("ofSunMoon:" + this.ofSunMoon);
            printwriter.println("ofVignette:" + this.ofVignette);
            printwriter.println("ofChunkUpdates:" + this.ofChunkUpdates);
            printwriter.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
            printwriter.println("ofTime:" + this.ofTime);
            printwriter.println("ofClearWater:" + this.ofClearWater);
            printwriter.println("ofAaLevel:" + this.ofAaLevel);
            printwriter.println("ofAfLevel:" + this.ofAfLevel);
            printwriter.println("ofProfiler:" + this.ofProfiler);
            printwriter.println("ofBetterSnow:" + this.ofBetterSnow);
            printwriter.println("ofSwampColors:" + this.ofSwampColors);
            printwriter.println("ofRandomEntities:" + this.ofRandomEntities);
            printwriter.println("ofSmoothBiomes:" + this.ofSmoothBiomes);
            printwriter.println("ofCustomFonts:" + this.ofCustomFonts);
            printwriter.println("ofCustomColors:" + this.ofCustomColors);
            printwriter.println("ofCustomItems:" + this.ofCustomItems);
            printwriter.println("ofCustomSky:" + this.ofCustomSky);
            printwriter.println("ofShowCapes:" + this.ofShowCapes);
            printwriter.println("ofNaturalTextures:" + this.ofNaturalTextures);
            printwriter.println("ofEmissiveTextures:" + this.ofEmissiveTextures);
            printwriter.println("ofLazyChunkLoading:" + this.ofLazyChunkLoading);
            printwriter.println("ofRenderRegions:" + this.ofRenderRegions);
            printwriter.println("ofSmartAnimations:" + this.ofSmartAnimations);
            printwriter.println("ofDynamicFov:" + this.ofDynamicFov);
            printwriter.println("ofAlternateBlocks:" + this.ofAlternateBlocks);
            printwriter.println("ofDynamicLights:" + this.ofDynamicLights);
            printwriter.println("ofScreenshotSize:" + this.ofScreenshotSize);
            printwriter.println("ofCustomEntityModels:" + this.ofCustomEntityModels);
            printwriter.println("ofCustomGuis:" + this.ofCustomGuis);
            printwriter.println("ofShowGlErrors:" + this.ofShowGlErrors);
            printwriter.println("ofFullscreenMode:" + this.ofFullscreenMode);
            printwriter.println("ofFastMath:" + this.ofFastMath);
            printwriter.println("ofFastRender:" + this.ofFastRender);
            printwriter.println("ofTranslucentBlocks:" + this.ofTranslucentBlocks);
            printwriter.println("key_" + this.ofKeyBindZoom.getKeyDescription() + ":" + this.ofKeyBindZoom.getKeyCode());
            printwriter.close();
        }
        catch (Exception exception)
        {
            Config.warn("Failed to save options");
            exception.printStackTrace();
        }
    }

    private void updateRenderClouds()
    {
        switch (this.ofClouds)
        {
            case 1:
                this.clouds = 1;
                break;

            case 2:
                this.clouds = 2;
                break;

            case 3:
                this.clouds = 0;
                break;

            default:
                if (this.fancyGraphics)
                {
                    this.clouds = 2;
                }
                else
                {
                    this.clouds = 1;
                }
        }
    }

    public void resetSettings()
    {
        this.renderDistanceChunks = 8;
        this.viewBobbing = true;
        this.anaglyph = false;
        this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.enableVsync = false;
        this.updateVSync();
        this.mipmapLevels = 4;
        this.fancyGraphics = true;
        this.ambientOcclusion = 2;
        this.clouds = 2;
        this.fovSetting = 70.0F;
        this.gammaSetting = 0.0F;
        this.guiScale = 0;
        this.particleSetting = 0;
        this.heldItemTooltips = true;
        this.useVbo = false;
        this.forceUnicodeFont = false;
        this.ofFogType = 1;
        this.ofFogStart = 0.8F;
        this.ofMipmapType = 0;
        this.ofOcclusionFancy = false;
        this.ofSmartAnimations = false;
        this.ofSmoothFps = false;
        Config.updateAvailableProcessors();
        this.ofSmoothWorld = Config.isSingleProcessor();
        this.ofLazyChunkLoading = false;
        this.ofRenderRegions = false;
        this.ofFastMath = false;
        this.ofFastRender = false;
        this.ofTranslucentBlocks = 0;
        this.ofDynamicFov = true;
        this.ofAlternateBlocks = true;
        this.ofDynamicLights = 3;
        this.ofScreenshotSize = 1;
        this.ofCustomEntityModels = true;
        this.ofCustomGuis = true;
        this.ofShowGlErrors = true;
        this.ofAoLevel = 1.0F;
        this.ofAaLevel = 0;
        this.ofAfLevel = 1;
        this.ofClouds = 0;
        this.ofCloudsHeight = 0.0F;
        this.ofTrees = 0;
        this.ofRain = 0;
        this.ofBetterGrass = 3;
        this.ofAutoSaveTicks = 4000;
        this.ofLagometer = false;
        this.ofShowFps = false;
        this.ofProfiler = false;
        this.ofWeather = true;
        this.ofSky = true;
        this.ofStars = true;
        this.ofSunMoon = true;
        this.ofVignette = 0;
        this.ofChunkUpdates = 1;
        this.ofChunkUpdatesDynamic = false;
        this.ofTime = 0;
        this.ofClearWater = false;
        this.ofBetterSnow = false;
        this.ofFullscreenMode = "Default";
        this.ofSwampColors = true;
        this.ofRandomEntities = true;
        this.ofSmoothBiomes = true;
        this.ofCustomFonts = true;
        this.ofCustomColors = true;
        this.ofCustomItems = true;
        this.ofCustomSky = true;
        this.ofShowCapes = true;
        this.ofConnectedTextures = 2;
        this.ofNaturalTextures = false;
        this.ofEmissiveTextures = true;
        this.ofAnimatedWater = 0;
        this.ofAnimatedLava = 0;
        this.ofAnimatedFire = true;
        this.ofAnimatedPortal = true;
        this.ofAnimatedRedstone = true;
        this.ofAnimatedExplosion = true;
        this.ofAnimatedFlame = true;
        this.ofAnimatedSmoke = true;
        this.ofVoidParticles = true;
        this.ofWaterParticles = true;
        this.ofRainSplash = true;
        this.ofPortalParticles = true;
        this.ofPotionParticles = true;
        this.ofFireworkParticles = true;
        this.ofDrippingWaterLava = true;
        this.ofAnimatedTerrain = true;
        this.ofAnimatedTextures = true;
        Shaders.setShaderPack("OFF");
        Shaders.configAntialiasingLevel = 0;
        Shaders.uninit();
        Shaders.storeConfig();
        this.updateWaterOpacity();
        this.mc.refreshResources();
        this.saveOptions();
    }

    public void updateVSync()
    {
        Display.setVSyncEnabled(this.enableVsync);
    }

    private void updateWaterOpacity()
    {
        if (Config.isIntegratedServerRunning())
        {
            Config.waterOpacityChanged = true;
        }

        ClearWater.updateWaterOpacity(this, this.mc.theWorld);
    }

    public void setAllAnimations(boolean p_setAllAnimations_1_)
    {
        int i = p_setAllAnimations_1_ ? 0 : 2;
        this.ofAnimatedWater = i;
        this.ofAnimatedLava = i;
        this.ofAnimatedFire = p_setAllAnimations_1_;
        this.ofAnimatedPortal = p_setAllAnimations_1_;
        this.ofAnimatedRedstone = p_setAllAnimations_1_;
        this.ofAnimatedExplosion = p_setAllAnimations_1_;
        this.ofAnimatedFlame = p_setAllAnimations_1_;
        this.ofAnimatedSmoke = p_setAllAnimations_1_;
        this.ofVoidParticles = p_setAllAnimations_1_;
        this.ofWaterParticles = p_setAllAnimations_1_;
        this.ofRainSplash = p_setAllAnimations_1_;
        this.ofPortalParticles = p_setAllAnimations_1_;
        this.ofPotionParticles = p_setAllAnimations_1_;
        this.ofFireworkParticles = p_setAllAnimations_1_;
        this.particleSetting = p_setAllAnimations_1_ ? 0 : 2;
        this.ofDrippingWaterLava = p_setAllAnimations_1_;
        this.ofAnimatedTerrain = p_setAllAnimations_1_;
        this.ofAnimatedTextures = p_setAllAnimations_1_;
    }

    private static int nextValue(int p_nextValue_0_, int[] p_nextValue_1_)
    {
        int i = indexOf(p_nextValue_0_, p_nextValue_1_);

        if (i < 0)
        {
            return p_nextValue_1_[0];
        }
        else
        {
            ++i;

            if (i >= p_nextValue_1_.length)
            {
                i = 0;
            }

            return p_nextValue_1_[i];
        }
    }

    private static int limit(int p_limit_0_, int[] p_limit_1_)
    {
        int i = indexOf(p_limit_0_, p_limit_1_);
        return i < 0 ? p_limit_1_[0] : p_limit_0_;
    }

    private static int indexOf(int p_indexOf_0_, int[] p_indexOf_1_)
    {
        for (int i = 0; i < p_indexOf_1_.length; ++i)
        {
            if (p_indexOf_1_[i] == p_indexOf_0_)
            {
                return i;
            }
        }

        return -1;
    }

    public enum Options
    {
        INVERT_MOUSE("options.invertMouse", false, true),
        SENSITIVITY("options.sensitivity", true, false),
        FOV("options.fov", true, false, 30.0F, 110.0F, 1.0F),
        GAMMA("options.gamma", true, false),
        SATURATION("options.saturation", true, false),
        RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
        VIEW_BOBBING("options.viewBobbing", false, true),
        ANAGLYPH("options.anaglyph", false, true),
        FRAMERATE_LIMIT("options.framerateLimit", true, false, 0.0F, 260.0F, 5.0F),
        FBO_ENABLE("options.fboEnable", false, true),
        RENDER_CLOUDS("options.renderClouds", false, false),
        GRAPHICS("options.graphics", false, false),
        AMBIENT_OCCLUSION("options.ao", false, false),
        GUI_SCALE("options.guiScale", false, false),
        PARTICLES("options.particles", false, false),
        CHAT_VISIBILITY("options.chat.visibility", false, false),
        CHAT_COLOR("options.chat.color", false, true),
        CHAT_LINKS("options.chat.links", false, true),
        CHAT_OPACITY("options.chat.opacity", true, false),
        CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
        SNOOPER_ENABLED("options.snooper", false, true),
        USE_FULLSCREEN("options.fullscreen", false, true),
        ENABLE_VSYNC("options.vsync", false, true),
        USE_VBO("options.vbo", false, true),
        TOUCHSCREEN("options.touchscreen", false, true),
        CHAT_SCALE("options.chat.scale", true, false),
        CHAT_WIDTH("options.chat.width", true, false),
        CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
        CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
        MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
        FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
        STREAM_BYTES_PER_PIXEL("options.stream.bytesPerPixel", true, false),
        STREAM_VOLUME_MIC("options.stream.micVolumne", true, false),
        STREAM_VOLUME_SYSTEM("options.stream.systemVolume", true, false),
        STREAM_KBPS("options.stream.kbps", true, false),
        STREAM_FPS("options.stream.fps", true, false),
        STREAM_COMPRESSION("options.stream.compression", false, false),
        STREAM_SEND_METADATA("options.stream.sendMetadata", false, true),
        STREAM_CHAT_ENABLED("options.stream.chat.enabled", false, false),
        STREAM_CHAT_USER_FILTER("options.stream.chat.userFilter", false, false),
        STREAM_MIC_TOGGLE_BEHAVIOR("options.stream.micToggleBehavior", false, false),
        BLOCK_ALTERNATIVES("options.blockAlternatives", false, true),
        REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
        ENTITY_SHADOWS("options.entityShadows", false, true),
        REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
        FOG_FANCY("of.options.FOG_FANCY", false, false),
        FOG_START("of.options.FOG_START", false, false),
        MIPMAP_TYPE("of.options.MIPMAP_TYPE", true, false, 0.0F, 3.0F, 1.0F),
        SMOOTH_FPS("of.options.SMOOTH_FPS", false, false),
        CLOUDS("of.options.CLOUDS", false, false),
        CLOUD_HEIGHT("of.options.CLOUD_HEIGHT", true, false),
        TREES("of.options.TREES", false, false),
        RAIN("of.options.RAIN", false, false),
        ANIMATED_WATER("of.options.ANIMATED_WATER", false, false),
        ANIMATED_LAVA("of.options.ANIMATED_LAVA", false, false),
        ANIMATED_FIRE("of.options.ANIMATED_FIRE", false, false),
        ANIMATED_PORTAL("of.options.ANIMATED_PORTAL", false, false),
        AO_LEVEL("of.options.AO_LEVEL", true, false),
        LAGOMETER("of.options.LAGOMETER", false, false),
        SHOW_FPS("of.options.SHOW_FPS", false, false),
        AUTOSAVE_TICKS("of.options.AUTOSAVE_TICKS", false, false),
        BETTER_GRASS("of.options.BETTER_GRASS", false, false),
        ANIMATED_REDSTONE("of.options.ANIMATED_REDSTONE", false, false),
        ANIMATED_EXPLOSION("of.options.ANIMATED_EXPLOSION", false, false),
        ANIMATED_FLAME("of.options.ANIMATED_FLAME", false, false),
        ANIMATED_SMOKE("of.options.ANIMATED_SMOKE", false, false),
        WEATHER("of.options.WEATHER", false, false),
        SKY("of.options.SKY", false, false),
        STARS("of.options.STARS", false, false),
        SUN_MOON("of.options.SUN_MOON", false, false),
        VIGNETTE("of.options.VIGNETTE", false, false),
        CHUNK_UPDATES("of.options.CHUNK_UPDATES", false, false),
        CHUNK_UPDATES_DYNAMIC("of.options.CHUNK_UPDATES_DYNAMIC", false, false),
        TIME("of.options.TIME", false, false),
        CLEAR_WATER("of.options.CLEAR_WATER", false, false),
        SMOOTH_WORLD("of.options.SMOOTH_WORLD", false, false),
        VOID_PARTICLES("of.options.VOID_PARTICLES", false, false),
        WATER_PARTICLES("of.options.WATER_PARTICLES", false, false),
        RAIN_SPLASH("of.options.RAIN_SPLASH", false, false),
        PORTAL_PARTICLES("of.options.PORTAL_PARTICLES", false, false),
        POTION_PARTICLES("of.options.POTION_PARTICLES", false, false),
        FIREWORK_PARTICLES("of.options.FIREWORK_PARTICLES", false, false),
        PROFILER("of.options.PROFILER", false, false),
        DRIPPING_WATER_LAVA("of.options.DRIPPING_WATER_LAVA", false, false),
        BETTER_SNOW("of.options.BETTER_SNOW", false, false),
        FULLSCREEN_MODE("of.options.FULLSCREEN_MODE", true, false, 0.0F, (float)Config.getDisplayModes().length, 1.0F),
        ANIMATED_TERRAIN("of.options.ANIMATED_TERRAIN", false, false),
        SWAMP_COLORS("of.options.SWAMP_COLORS", false, false),
        RANDOM_ENTITIES("of.options.RANDOM_ENTITIES", false, false),
        SMOOTH_BIOMES("of.options.SMOOTH_BIOMES", false, false),
        CUSTOM_FONTS("of.options.CUSTOM_FONTS", false, false),
        CUSTOM_COLORS("of.options.CUSTOM_COLORS", false, false),
        SHOW_CAPES("of.options.SHOW_CAPES", false, false),
        CONNECTED_TEXTURES("of.options.CONNECTED_TEXTURES", false, false),
        CUSTOM_ITEMS("of.options.CUSTOM_ITEMS", false, false),
        AA_LEVEL("of.options.AA_LEVEL", true, false, 0.0F, 16.0F, 1.0F),
        AF_LEVEL("of.options.AF_LEVEL", true, false, 1.0F, 16.0F, 1.0F),
        ANIMATED_TEXTURES("of.options.ANIMATED_TEXTURES", false, false),
        NATURAL_TEXTURES("of.options.NATURAL_TEXTURES", false, false),
        EMISSIVE_TEXTURES("of.options.EMISSIVE_TEXTURES", false, false),
        HELD_ITEM_TOOLTIPS("of.options.HELD_ITEM_TOOLTIPS", false, false),
        DROPPED_ITEMS("of.options.DROPPED_ITEMS", false, false),
        LAZY_CHUNK_LOADING("of.options.LAZY_CHUNK_LOADING", false, false),
        CUSTOM_SKY("of.options.CUSTOM_SKY", false, false),
        FAST_MATH("of.options.FAST_MATH", false, false),
        FAST_RENDER("of.options.FAST_RENDER", false, false),
        TRANSLUCENT_BLOCKS("of.options.TRANSLUCENT_BLOCKS", false, false),
        DYNAMIC_FOV("of.options.DYNAMIC_FOV", false, false),
        DYNAMIC_LIGHTS("of.options.DYNAMIC_LIGHTS", false, false),
        ALTERNATE_BLOCKS("of.options.ALTERNATE_BLOCKS", false, false),
        CUSTOM_ENTITY_MODELS("of.options.CUSTOM_ENTITY_MODELS", false, false),
        ADVANCED_TOOLTIPS("of.options.ADVANCED_TOOLTIPS", false, false),
        SCREENSHOT_SIZE("of.options.SCREENSHOT_SIZE", false, false),
        CUSTOM_GUIS("of.options.CUSTOM_GUIS", false, false),
        RENDER_REGIONS("of.options.RENDER_REGIONS", false, false),
        SHOW_GL_ERRORS("of.options.SHOW_GL_ERRORS", false, false),
        SMART_ANIMATIONS("of.options.SMART_ANIMATIONS", false, false);

        private final boolean enumFloat;
        private final boolean enumBoolean;
        private final String enumString;
        private final float valueStep;
        private final float valueMin;
        private float valueMax;

        public static GameSettings.Options getEnumOptions(int ordinal)
        {
            for (GameSettings.Options gamesettings$options : values())
            {
                if (gamesettings$options.returnEnumOrdinal() == ordinal)
                {
                    return gamesettings$options;
                }
            }

            return null;
        }

        Options(String str, boolean isFloat, boolean isBoolean)
        {
            this(str, isFloat, isBoolean, 0.0F, 1.0F, 0.0F);
        }

        Options(String str, boolean isFloat, boolean isBoolean, float valMin, float valMax, float valStep)
        {
            this.enumString = str;
            this.enumFloat = isFloat;
            this.enumBoolean = isBoolean;
            this.valueMin = valMin;
            this.valueMax = valMax;
            this.valueStep = valStep;
        }

        public boolean getEnumFloat()
        {
            return this.enumFloat;
        }

        public boolean getEnumBoolean()
        {
            return this.enumBoolean;
        }

        public int returnEnumOrdinal()
        {
            return this.ordinal();
        }

        public String getEnumString()
        {
            return this.enumString;
        }

        public float getValueMax()
        {
            return this.valueMax;
        }

        public void setValueMax(float value)
        {
            this.valueMax = value;
        }

        public float normalizeValue(float value)
        {
            return MathHelper.clamp_float((this.snapToStepClamp(value) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
        }

        public float denormalizeValue(float value)
        {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(value, 0.0F, 1.0F));
        }

        public float snapToStepClamp(float value)
        {
            value = this.snapToStep(value);
            return MathHelper.clamp_float(value, this.valueMin, this.valueMax);
        }

        private float snapToStep(float value)
        {
            if (this.valueStep > 0.0F)
            {
                value = this.valueStep * (float)Math.round(value / this.valueStep);
            }

            return value;
        }
    }
}
