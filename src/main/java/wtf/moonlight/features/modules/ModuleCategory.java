package wtf.moonlight.features.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModuleCategory {

    Combat("Combat"),
    Movement("Movement"),
    Player("Player"),
    World("World"),
    Visual("Visual"),
    Misc("Misc"),
    Exploit("Exploit"),
    Search("Search");

    private final String name;

}