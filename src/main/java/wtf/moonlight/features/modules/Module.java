/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight-public
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
import wtf.moonlight.gui.notification.NotificationManager;
import wtf.moonlight.gui.notification.NotificationType;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.animations.Translate;
import wtf.moonlight.utils.animations.impl.EaseInOutQuad;
import wtf.moonlight.utils.packet.PacketUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module implements InstanceAccess {

    private final ModuleInfo moduleInfo = getClass().getAnnotation(ModuleInfo.class);
    @Getter
    private final String name = moduleInfo.name();
    @Getter
    private final ModuleCategory category = moduleInfo.category();
    @Getter
    @Setter
    private int keyBind = moduleInfo.key();
    @Getter
    private String tag = "";
    @Getter
    public List<Value> values = new ArrayList<>();
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

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void setTag(String tag) {
        if (tag == null || !tag.isEmpty()) {
            switch (getModule(Interface.class).tags.get().toLowerCase()) {
                case "simple":
                    this.tag = "\u00A77 " + tag;
                    break;

                case "dash":
                    this.tag = "\u00A77 - " + tag;
                    break;

                case "bracket":
                    this.tag = "\u00A77 [" + tag + "]";
                    break;

                default:
                    this.tag = "";
            }
        } else {
            this.tag = "";
        }
    }

    public boolean isEnabled() {
        return state;
    }

    public boolean isDisabled() {
        return !state;
    }

    public boolean isEnabled(Class module) {
        return Moonlight.INSTANCE.getModuleManager().getModule(module).isEnabled();
    }

    public boolean isDisabled(Class module) {
        return Moonlight.INSTANCE.getModuleManager().getModule(module).isDisabled();
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public void setEnabled(boolean state) {
        if (this.state != state) {
            this.state = state;
            if (state)
                enable();
            else
                disable();
        }
    }

    public final void enable() {
        Moonlight.INSTANCE.getEventManager().register(this);
        try {
            onEnable();
        } catch (Exception exception) {
            if (mc.thePlayer != null)
                exception.printStackTrace();
        }
        Moonlight.INSTANCE.getNotificationManager().post(NotificationType.OKAY, "Module", getName() + EnumChatFormatting.GREEN + " enabled");
        if (mc.thePlayer != null)
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.click"), 1F));
    }

    public final void disable() {
        Moonlight.INSTANCE.getEventManager().unregister(this);
        try {
            onDisable();
        } catch (Exception exception) {
            if (mc.thePlayer != null)
                exception.printStackTrace();
        }
        Moonlight.INSTANCE.getNotificationManager().post(NotificationType.WARNING, "Module", getName() + EnumChatFormatting.RED + " disabled");
        if (mc.thePlayer != null)
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.click"), 0.8F));
    }

    public <M extends Module> M getModule(Class<M> clazz) {
        return INSTANCE.getModuleManager().getModule(clazz);
    }

    public void addValue(Value... setting) {
        values.addAll(Arrays.asList(setting));
    }

    public void sendPacket(Packet packet) {
        PacketUtils.sendPacket(packet);
    }

    public void sendPacketNoEvent(Packet packet) {
        PacketUtils.sendPacketNoEvent(packet);
    }

    public void addValue(Value value) {
        this.values.add(value);
    }
    public Value getValue(final String valueName) {
        return this.values.stream().filter(value -> value.getName().toLowerCase().equals(valueName.toLowerCase())).findFirst().orElse(null);
    }
}
