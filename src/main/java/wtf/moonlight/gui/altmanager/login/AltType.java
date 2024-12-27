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
package wtf.moonlight.gui.altmanager.login;

import org.apache.commons.lang3.StringUtils;

public enum AltType {

    CRACKED,
    PREMIUM;

    private final String capitalized;

    AltType() {
        this.capitalized = StringUtils.capitalize(name().toLowerCase());
    }

    public String getCapitalized() {
        return this.capitalized;
    }

}
