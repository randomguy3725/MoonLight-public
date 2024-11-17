package wtf.moonlight.utils;

import net.minecraft.client.Minecraft;
import wtf.moonlight.MoonLight;

public interface InstanceAccess {

    Minecraft mc = Minecraft.getMinecraft();

    MoonLight INSTANCE = MoonLight.INSTANCE;
}

