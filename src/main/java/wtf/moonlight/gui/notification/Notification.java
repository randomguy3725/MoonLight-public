package wtf.moonlight.gui.notification;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Translate;
import wtf.moonlight.utils.animations.impl.EaseOutSine;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;


@Getter
public class Notification implements InstanceAccess {

    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    private final TimerUtils timerUtils;
    private final Animation animation;
    private final Translate translate;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, Moonlight.INSTANCE.getNotificationManager().getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;
        this.time = (long) (time * 1000);
        timerUtils = new TimerUtils();
        this.notificationType = type;
        this.animation = new EaseOutSine(250, 1);
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        this.translate = new Translate(sr.getScaledWidth() - this.getWidth(), sr.getScaledHeight() - getHeight());
    }
    
    public double getWidth(){
        double width = 0;
        switch (INSTANCE.getModuleManager().getModule(Interface.class).notificationMode.get()){
            case "Default":
                width = Fonts.interMedium.get(15).getStringWidth(getDescription()) + 5;
                break;
            case "Test":
                width = Math.max(Fonts.interSemiBold.get(15).getStringWidth(getTitle()), Fonts.interSemiBold.get(15).getStringWidth(getDescription())) + 5;
                break;
            case "Test2":
                width = Math.max(Fonts.interSemiBold.get(17).getStringWidth(getTitle()), Fonts.interRegular.get(17).getStringWidth(getDescription())) + 10;
                break;
            case "Exhi":
                width = Math.max(100.0f, Math.max(Fonts.interRegular.get(18).getStringWidth(getTitle()) + 2, Fonts.interRegular.get(14).getStringWidth(getDescription())) + 24.0f);
                break;
            case "Type 2":
                width = Math.max(100.0f, Math.max(Fonts.interRegular.get(18).getStringWidth(getTitle()), Fonts.interRegular.get(14).getStringWidth(getDescription())) + 70);
                break;
            case "Type 3":
                width = Math.max(Fonts.interRegular.get(22).getStringWidth(getTitle()), Fonts.interRegular.get(20).getStringWidth(getDescription())) + 50;
                break;
        }
        return width;
    }

    public double getHeight(){
        double height = 0;
        switch (INSTANCE.getModuleManager().getModule(Interface.class).notificationMode.get()){
            case "Default":
                height = 16;
                break;
            case "Test":
                height = Fonts.interRegular.get(15).getHeight() * 2 + 2;
                break;
            case "Test2":
                height = 33;
                break;
            case "Exhi":
                height = 26;
                break;
            case "Type 2":
                height = 30;
                break;
            case "Type 3":
                height = 35;
                break;
        }
        return height;
    }
}