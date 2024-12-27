/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.util.ResourceLocation;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.utils.render.GifRenderer;

@ModuleInfo(name = "GifTest", category = ModuleCategory.Visual)
public class GifTest extends Module {

    GifRenderer gif = new GifRenderer(new ResourceLocation("moonlight/texture/gif/test.gif"));
    @EventTarget
    public void onRender2D(Render2DEvent event){
        gif.drawTexture((float) event.getScaledResolution().getScaledWidth() / 2, (float) event.getScaledResolution().getScaledHeight() / 2,this.gif.getWidth(),this.gif.getHeight());
        gif.update();
    }
}
