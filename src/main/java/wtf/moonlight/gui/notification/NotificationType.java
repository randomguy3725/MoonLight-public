/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.gui.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum NotificationType {
    OKAY("okay",new Color(65, 252, 65)),
    INFO("info",new Color(127, 174, 210)),
    NOTIFY("notify",new Color(255, 255, 94)),
    WARNING("warning",new Color(226, 87, 76));
    private final String name;
    private final Color color;
}