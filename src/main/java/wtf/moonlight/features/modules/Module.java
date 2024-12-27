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
package wtf.moonlight.features.modules;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.features.values.Value;
import wtf.moonlight.gui.notification.NotificationType;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.animations.Translate;
import wtf.moonlight.utils.animations.impl.EaseInOutQuad;
import wtf.moonlight.utils.packet.PacketUtils;

import java.util.*;

public abstract class Module implements InstanceAccess {

    private final ModuleInfo moduleInfo;
    @Getter
    private final String name;
    @Getter
    private final ModuleCategory category;
    @Getter
    @Setter
    private int keyBind;
    @Getter
    @Setter
    private String tag = "";
    @Getter
    private final List<Value> values = new ArrayList<>();
    @Getter
    @Setter
    private boolean hidden;
    private boolean state;
    @Getter
    @Setter
    private boolean expanded;
    @Getter
    private final EaseInOutQuad animation = new EaseInOutQuad(175, 1);
    @Getter
    private final Translate translate = new Translate(0.0, 0.0);

    protected Module() {
        this.moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
        Objects.requireNonNull(moduleInfo, "ModuleInfo annotation is missing on " + getClass().getName());
        this.name = moduleInfo.name();
        this.category = moduleInfo.category();
        this.keyBind = moduleInfo.key();
    }

    /**
     * Method called when the module is enabled.
     */
    public void onEnable() {
        // Module-specific implementation
    }

    /**
     * Method called when the module is disabled.
     */
    public void onDisable() {
        // Module-specific implementation
    }

    /**
     * Sets the module's tag based on the global tag configuration.
     *
     * @param tag The tag to set.
     */
    public void setTag(String tag) {
        if (tag != null && !tag.isEmpty()) {
            String tagStyle = Optional.ofNullable(getModule(Interface.class))
                    .map(m -> m.tags.get())
                    .orElse("")
                    .toLowerCase();
            switch (tagStyle) {
                case "simple":
                    this.tag = "§7 " + tag;
                    break;
                case "dash":
                    this.tag = "§7 - " + tag;
                    break;
                case "bracket":
                    this.tag = "§7 [" + tag + "]";
                    break;
                default:
                    this.tag = "";
            }
        } else {
            this.tag = "";
        }
    }

    /**
     * Checks if the module is enabled.
     *
     * @return true if enabled, false otherwise.
     */
    public boolean isEnabled() {
        return state;
    }

    /**
     * Checks if the module is disabled.
     *
     * @return true if disabled, false otherwise.
     */
    public boolean isDisabled() {
        return !state;
    }

    /**
     * Checks if a specific module is enabled.
     *
     * @param module The module class to check.
     * @param <M>    The type of the module.
     * @return true if enabled, false otherwise.
     */
    public <M extends Module> boolean isEnabled(Class<M> module) {
        Module mod = Moonlight.INSTANCE.getModuleManager().getModule(module);
        return mod != null && mod.isEnabled();
    }

    /**
     * Checks if a specific module is disabled.
     *
     * @param module The module class to check.
     * @param <M>    The type of the module.
     * @return true if disabled, false otherwise.
     */
    public <M extends Module> boolean isDisabled(Class<M> module) {
        Module mod = Moonlight.INSTANCE.getModuleManager().getModule(module);
        return mod == null || mod.isDisabled();
    }

    /**
     * Toggles the module's enabled state.
     */
    public void toggle() {
        setEnabled(!isEnabled());
    }

    /**
     * Sets the module's enabled state.
     *
     * @param enabled true to enable, false to disable.
     */
    public void setEnabled(boolean enabled) {
        if (this.state != enabled) {
            this.state = enabled;
            if (enabled) {
                enable();
            } else {
                disable();
            }
        }
    }

    /**
     * Enables the module.
     */
    private void enable() {
        Moonlight.INSTANCE.getEventManager().register(this);
        try {
            onEnable();
            Moonlight.INSTANCE.getNotificationManager().post(NotificationType.OKAY, "Module", getName() + EnumChatFormatting.GREEN + " enabled");
            playClickSound(1.0F);
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Disables the module.
     */
    private void disable() {
        Moonlight.INSTANCE.getEventManager().unregister(this);
        try {
            onDisable();
            Moonlight.INSTANCE.getNotificationManager().post(NotificationType.WARNING, "Module", getName() + EnumChatFormatting.RED + " disabled");
            playClickSound(0.8F);
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Plays the click sound.
     *
     * @param volume The volume of the sound.
     */
    private void playClickSound(float volume) {
        if (mc.thePlayer != null) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.click"), volume));
        }
    }

    /**
     * Handles exceptions by printing the stack trace if the player exists.
     *
     * @param e The exception to handle.
     */
    private void handleException(Exception e) {
        if (mc.thePlayer != null) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a specific module.
     *
     * @param clazz The class of the module to retrieve.
     * @param <M>   The type of the module.
     * @return The module instance or null if not found.
     */
    public <M extends Module> M getModule(Class<M> clazz) {
        return Moonlight.INSTANCE.getModuleManager().getModule(clazz);
    }

    /**
     * Adds multiple values to the module.
     *
     * @param settings The values to add.
     */
    public void addValues(Value... settings) {
        values.addAll(Arrays.asList(settings));
    }

    /**
     * Adds a single value to the module.
     *
     * @param value The value to add.
     */
    public void addValue(Value value) {
        addValues(value);
    }

    /**
     * Sends a packet.
     *
     * @param packet The packet to send.
     */
    public void sendPacket(Packet packet) {
        PacketUtils.sendPacket(packet);
    }

    /**
     * Sends a packet without triggering events.
     *
     * @param packet The packet to send.
     */
    public void sendPacketNoEvent(Packet packet) {
        PacketUtils.sendPacketNoEvent(packet);
    }

    /**
     * Retrieves a value by its name.
     *
     * @param valueName The name of the value to retrieve.
     * @return The corresponding value or null if not found.
     */
    public Value getValue(String valueName) {
        return values.stream()
                .filter(value -> value.getName().equalsIgnoreCase(valueName))
                .findFirst()
                .orElse(null);
    }
}