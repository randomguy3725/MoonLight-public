package wtf.moonlight;

import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjglx.Sys;
import org.lwjglx.opengl.Display;
import wtf.moonlight.events.EventManager;
import wtf.moonlight.features.command.CommandManager;
import wtf.moonlight.features.config.ConfigManager;
import wtf.moonlight.features.friend.FriendManager;
import wtf.moonlight.features.modules.ModuleManager;
import wtf.moonlight.features.modules.impl.visual.ScaffoldCounter;
import wtf.moonlight.gui.click.dropdown.DropdownGUI;
import wtf.moonlight.gui.click.menu.MenuGUI;
import wtf.moonlight.gui.click.skeet.SkeetUI;
import wtf.moonlight.gui.notification.NotificationManager;
import wtf.moonlight.gui.notification.NotificationType;
import wtf.moonlight.gui.widget.WidgetManager;
import wtf.moonlight.utils.discord.DiscordInfo;
import wtf.moonlight.utils.misc.SpoofSlotUtils;
import wtf.moonlight.utils.packet.BadPacketsComponent;
import wtf.moonlight.utils.packet.BlinkComponent;
import wtf.moonlight.utils.packet.PingSpoofComponent;
import wtf.moonlight.utils.player.FallDistanceComponent;
import wtf.moonlight.utils.player.RotationUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Getter
public class Moonlight {

    // Logger instance for logging events and errors
    public static final Logger LOGGER = LogManager.getLogger(Moonlight.class);

    // Singleton instance of Moonlight
    public static final Moonlight INSTANCE = new Moonlight();

    // Client information
    public final String clientName = "Moonlight";
    public final String version = "Alpha";
    public final String clientCloud = "https://randomguy3725.github.io/MoonLightCloud/";

    // Directory for configuration files and other data
    private final File mainDir = new File(Minecraft.getMinecraft().mcDataDir, clientName);

    // Managers and GUI components
    private EventManager eventManager;
    private NotificationManager notificationManager;
    private ModuleManager moduleManager;
    private ConfigManager configManager;
    private WidgetManager widgetManager;
    private CommandManager commandManager;
    private FriendManager friendManager;
    private MenuGUI menuGUI;
    private DropdownGUI dropdownGUI;
    private SkeetUI skeetGUI;
    private DiscordInfo discordRP;

    // System Tray icon
    private TrayIcon trayIcon;

    // Start time tracking
    private int startTime;
    private long startTimeLong;

    // Load status
    private boolean loaded;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private Moonlight() {
        // Private constructor to prevent external instantiation
    }

    /**
     * Provides access to the Singleton instance of Moonlight.
     *
     * @return the single instance of Moonlight
     */
    public static Moonlight getInstance() {
        return INSTANCE;
    }

    /**
     * Initializes the Moonlight client by setting up directories, managers, event handlers,
     * system tray, Discord RPC, and other components.
     */
    public void init() {
        loaded = false;

        setupMainDirectory();
        setupDisplayTitle();
        initializeManagers();
        registerEventHandlers();
        initializeStartTime();
        initializeViaMCP();
        setupDiscordRPC();
        setupSystemTray();
        handleFastRender();

        loaded = true;
        LOGGER.info("{} {} initialized successfully.", clientName, version);
    }

    /**
     * Sets up the main directory for configurations and other client data.
     */
    private void setupMainDirectory() {
        if (!mainDir.exists()) {
            boolean dirCreated = mainDir.mkdir();
            if (dirCreated) {
                LOGGER.info("Created main directory at {}", mainDir.getAbsolutePath());
            } else {
                LOGGER.warn("Failed to create main directory at {}", mainDir.getAbsolutePath());
            }
            Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MUSIC, 0);
        } else {
            LOGGER.info("Main directory already exists at {}", mainDir.getAbsolutePath());
        }
    }

    /**
     * Sets up the display title for the client window.
     */
    private void setupDisplayTitle() {
        String osVersion = Sys.getVersion();
        String title = String.format("%s %s | %s", clientName, version, osVersion);
        Display.setTitle(title);
        LOGGER.info("Display title set to: {}", title);
    }

    /**
     * Initializes all manager and GUI components used by the client.
     */
    private void initializeManagers() {
        eventManager = new EventManager();
        notificationManager = new NotificationManager();
        moduleManager = new ModuleManager();
        widgetManager = new WidgetManager();
        configManager = new ConfigManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        menuGUI = new MenuGUI();
        dropdownGUI = new DropdownGUI();
        skeetGUI = new SkeetUI();
    }

    /**
     * Registers event handlers with the EventManager.
     */
    private void registerEventHandlers() {
        eventManager.register(new ScaffoldCounter());
        eventManager.register(new RotationUtils());
        eventManager.register(new FallDistanceComponent());
        eventManager.register(new BadPacketsComponent());
        eventManager.register(new PingSpoofComponent());
        eventManager.register(new BlinkComponent());
        eventManager.register(new SpoofSlotUtils());

        LOGGER.info("Event handlers registered.");
    }

    /**
     * Initializes the start time for tracking the client's uptime.
     */
    private void initializeStartTime() {
        startTime = (int) System.currentTimeMillis();
        startTimeLong = System.currentTimeMillis();
        LOGGER.info("Start time initialized: {} ms", startTime);
    }

    /**
     * Initializes ViaMCP for handling Minecraft protocol modifications.
     */
    private void initializeViaMCP() {
        ViaMCP.create();
        ViaMCP.INSTANCE.initAsyncSlider();
        LOGGER.info("ViaMCP initialized.");
    }

    /**
     * Sets up Discord Rich Presence for the client.
     */
    private void setupDiscordRPC() {
        try {
            discordRP = new DiscordInfo();
            discordRP.init();
            LOGGER.info("Discord Rich Presence initialized.");
        } catch (Throwable throwable) {
            LOGGER.error("Failed to set up Discord RPC.", throwable);
        }
    }

    /**
     * Sets up the system tray icon if supported by the operating system.
     */
    private void setupSystemTray() {
        if (isWindows() && SystemTray.isSupported()) {
            try {
                Image trayImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/assets/minecraft/moonlight/img/logo.png")));
                trayIcon = new TrayIcon(trayImage, clientName);
                trayIcon.setImageAutoSize(true);
                trayIcon.setToolTip(clientName);

                SystemTray.getSystemTray().add(trayIcon);
                trayIcon.displayMessage(clientName, "Client started successfully.", TrayIcon.MessageType.INFO);

                LOGGER.info("System tray icon added.");
            } catch (IOException | AWTException | NullPointerException e) {
                LOGGER.error("Failed to create or add TrayIcon.", e);
            }
        } else {
            LOGGER.warn("System tray not supported or not running on Windows.");
        }
    }

    /**
     * Handles the Fast Render setting by disabling it if enabled, due to compatibility issues.
     */
    private void handleFastRender() {
        if (Minecraft.getMinecraft().gameSettings.ofFastRender) {
            notificationManager.post(NotificationType.WARNING, "Fast Rendering has been disabled", "due to compatibility issues");
            Minecraft.getMinecraft().gameSettings.ofFastRender = false;
            LOGGER.info("Fast Rendering was disabled due to compatibility issues.");
        }
    }

    /**
     * Checks if the operating system is Windows.
     *
     * @return {@code true} if the OS is Windows, {@code false} otherwise.
     */
    private boolean isWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("windows");
    }

    /**
     * Stops the Moonlight client, performing necessary cleanup tasks such as stopping Discord RPC
     * and saving configurations.
     */
    public void onStop() {
        if (discordRP != null) {
            discordRP.stop();
            LOGGER.info("Discord Rich Presence stopped.");
        }
        configManager.saveConfigs();
        LOGGER.info("All configurations saved.");
    }
}
