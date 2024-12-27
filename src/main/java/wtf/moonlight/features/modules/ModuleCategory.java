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
package wtf.moonlight.features.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModuleCategory {

    Combat("Combat"),
    Legit("Legit"),
    Movement("Movement"),
    Player("Player"),
    Misc("Misc"),
    Exploit("Exploit"),
    Visual("Visuals"),
    Config("Configs"),
    Search("Search");

    private final String name;

}