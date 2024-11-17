package wtf.moonlight.features.config;

import com.google.gson.JsonObject;
import lombok.Getter;
import wtf.moonlight.MoonLight;

import java.io.File;
import java.io.IOException;

@Getter
public class Config {
    private final File file;
    private final String name;

    public Config(String name) {
        this.name = name;
        this.file = new File(MoonLight.INSTANCE.getMainDir(), name + ".json");
    }

    public void loadConfig(JsonObject object){

    }

    public JsonObject saveConfig(){
        return null;
    }
}
