package net.optifine.player;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.src.Config;

public class PlayerConfiguration
{
    private PlayerItemModel[] playerItemModels = new PlayerItemModel[0];
    private boolean initialized = false;

    public void renderPlayerItems(ModelBiped modelBiped, AbstractClientPlayer player, float scale, float partialTicks)
    {
        if (this.initialized)
        {
            for (int i = 0; i < this.playerItemModels.length; ++i)
            {
                PlayerItemModel playeritemmodel = this.playerItemModels[i];
                playeritemmodel.render(modelBiped, player, scale, partialTicks);
            }
        }
    }

    public boolean isInitialized()
    {
        return this.initialized;
    }

    public void setInitialized(boolean initialized)
    {
        this.initialized = initialized;
    }

    public PlayerItemModel[] getPlayerItemModels()
    {
        return this.playerItemModels;
    }

    public void addPlayerItemModel(PlayerItemModel playerItemModel)
    {
        this.playerItemModels = (PlayerItemModel[]) Config.addObjectToArray(this.playerItemModels, playerItemModel);
    }
}
