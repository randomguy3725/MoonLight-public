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
package wtf.moonlight.features.config;

import com.google.gson.JsonObject;
import lombok.Getter;
import wtf.moonlight.Moonlight;

import java.io.File;
import java.io.IOException;

@Getter
public class Config {
    private final File file;
    private final String name;

    public Config(String name) {
        this.name = name;
        this.file = new File(Moonlight.INSTANCE.getMainDir(), name + ".json");
    }

    public void loadConfig(JsonObject object){

    }

    public JsonObject saveConfig(){
        return null;
    }
}
