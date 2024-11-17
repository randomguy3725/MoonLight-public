package wtf.moonlight.gui.altmanager.utils.alt;

import lombok.Getter;
import net.minecraft.util.ResourceLocation;

@Getter
public abstract class Alt {
    private final String userName;
    private final AccountEnum accountType;
    public String uuid;

    public ResourceLocation head;
    public boolean headTexture;
    public int headTries;

    public Alt(String userName, AccountEnum accountType) {
        this.userName = userName;
        this.accountType = accountType;
    }
}
