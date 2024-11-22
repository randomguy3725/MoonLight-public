package wtf.moonlight;

import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import net.minecraft.client.Minecraft;
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
import wtf.moonlight.gui.click.dropdown2.MoonGUI;
import wtf.moonlight.gui.click.skeet.SkeetUI;
import wtf.moonlight.gui.notification.NotificationManager;
import wtf.moonlight.gui.widget.WidgetManager;
import wtf.moonlight.utils.discord.DiscordInfo;
import wtf.moonlight.utils.packet.BadPacketsComponent;
import wtf.moonlight.utils.packet.BlinkComponent;
import wtf.moonlight.utils.packet.PingSpoofComponent;
import wtf.moonlight.utils.player.FallDistanceComponent;
import wtf.moonlight.utils.player.RotationUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.Objects;

@Getter
public class MoonLight {
    public final String clientName = "MoonLight";
    public final String version = "Alpha";
    public static MoonLight INSTANCE = new MoonLight();
    private final File mainDir = new File(Minecraft.getMinecraft().mcDataDir, clientName);
    private EventManager eventManager;
    private NotificationManager notificationManager;
    private ModuleManager moduleManager;
    private ConfigManager configManager;
    private WidgetManager widgetManager;
    private CommandManager commandManager;
    private FriendManager friendManager;
    private MenuGUI menuGUI;
    private DropdownGUI dropdownGUI;
    private MoonGUI moonGUI;
    private SkeetUI skeetGUI;
    private DiscordInfo discordRP;
    public TrayIcon trayIcon;
    public static final Logger LOGGER = LogManager.getLogger();
    @Getter
    private int startTime;
    @Getter
    private long startTimeLong;
    public boolean loaded;

    public void init() {
        loaded = false;
        if (!mainDir.exists()) {
            mainDir.mkdir();
        }
        Display.setTitle(clientName + " " + version + " | " + Sys.getVersion());
        eventManager = new EventManager();
        notificationManager = new NotificationManager();
        moduleManager = new ModuleManager();
        widgetManager = new WidgetManager();
        configManager = new ConfigManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        menuGUI = new MenuGUI();
        dropdownGUI = new DropdownGUI();
        moonGUI = new MoonGUI();
        skeetGUI = new SkeetUI();

        eventManager.register(new ScaffoldCounter());
        eventManager.register(new RotationUtils());
        eventManager.register(new FallDistanceComponent());
        eventManager.register(new BadPacketsComponent());
        eventManager.register(new PingSpoofComponent());
        eventManager.register(new BlinkComponent());

        startTime = (int) System.currentTimeMillis();
        startTimeLong = System.currentTimeMillis();

        try {
            ViaMCP.create();
            ViaMCP.INSTANCE.initAsyncSlider();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            discordRP = new DiscordInfo();
            discordRP.init();
        } catch (Throwable throwable) {
            System.out.println("Failed to setup Discord RPC.");
        }

        if (System.getProperties().getProperty("os.name").toLowerCase().contains("windows") && SystemTray.isSupported()) {
            try {
                trayIcon = new TrayIcon(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/assets/minecraft/moonlight/img/logo.png"))));
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Ratted");

            try {
                SystemTray.getSystemTray().add(trayIcon);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }

            trayIcon.displayMessage("Ratted",
                    "Sexy"
                    , TrayIcon.MessageType.WARNING);
        }

        loaded = true;

    }

    public void onStop() {
        discordRP.stop();
        configManager.saveConfigs();
    }
}
