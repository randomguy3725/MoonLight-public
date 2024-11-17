package net.optifine.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.src.Config;
import net.optifine.http.IFileDownloadListener;

import java.nio.charset.StandardCharsets;

public class PlayerConfigurationReceiver implements IFileDownloadListener
{
    private String player = null;

    public PlayerConfigurationReceiver(String player)
    {
        this.player = player;
    }

    public void fileDownloadFinished(String url, byte[] bytes, Throwable exception)
    {
        if (bytes != null)
        {
            try
            {
                String s = new String(bytes, StandardCharsets.US_ASCII);
                JsonParser jsonparser = new JsonParser();
                JsonElement jsonelement = jsonparser.parse(s);
                PlayerConfigurationParser playerconfigurationparser = new PlayerConfigurationParser(this.player);
                PlayerConfiguration playerconfiguration = playerconfigurationparser.parsePlayerConfiguration(jsonelement);

                if (playerconfiguration != null)
                {
                    playerconfiguration.setInitialized(true);
                    PlayerConfigurations.setPlayerConfiguration(this.player, playerconfiguration);
                }
            }
            catch (Exception exception1)
            {
                Config.dbg("Error parsing configuration: " + url + ", " + exception1.getClass().getName() + ": " + exception1.getMessage());
            }
        }
    }
}
