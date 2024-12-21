package wtf.moonlight.utils;

import net.minecraft.client.Minecraft;
import wtf.moonlight.Moonlight;

public interface InstanceAccess {

    Minecraft mc = Minecraft.getMinecraft();

    Moonlight INSTANCE = Moonlight.INSTANCE;
}

