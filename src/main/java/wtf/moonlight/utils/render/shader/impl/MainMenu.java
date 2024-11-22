package wtf.moonlight.utils.render.shader.impl;

import net.minecraft.client.gui.ScaledResolution;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.render.shader.ShaderUtils;

public class MainMenu implements InstanceAccess {
    private static final ShaderUtils mainmenu = new ShaderUtils("mainmenu");

    public static void draw(long initTime) {
        ScaledResolution sr = new ScaledResolution(mc);
        mainmenu.init();
        mainmenu.setUniformf("TIME", (float) (System.currentTimeMillis() - initTime) / 1000);
        mainmenu.setUniformf("RESOLUTION", (float) ((double) sr.getScaledWidth() * sr.getScaleFactor()), (float) ((double) sr.getScaledHeight() * sr.getScaleFactor()));
        ShaderUtils.drawFixedQuads();
        mainmenu.unload();
    }
}
