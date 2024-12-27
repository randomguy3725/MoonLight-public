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
package wtf.moonlight.gui.widget.impl;

import net.minecraft.item.ItemStack;
import wtf.moonlight.events.impl.render.Shader2DEvent;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.gui.widget.Widget;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

public class InventoryWidget extends Widget {
    public InventoryWidget() {
        super("Inventory");
        this.x = 0f;
        this.y = 0.6f;
    }

    @Override
    public void onShader(Shader2DEvent event) {

    }

    @Override
    public void render() {
        float x = renderX;
        float y = renderY;
        float itemWidth = 14;
        float itemHeight = 14;
        float y1 = 17.0F;
        float x1 = 0.7F;
        RoundedUtils.drawRound(x, y + 0, itemWidth + 135, itemHeight, 5.0F, new Color(setting.bgColor(),true));
        RoundedUtils.drawRound(x, y + 17, itemWidth + 135, itemHeight - 1, 5.5F, new Color(setting.bgColor(),true));
        RoundedUtils.drawRound(x, y + 33, itemWidth + 135, itemHeight - 1, 5.5F, new Color(setting.bgColor(),true));
        RoundedUtils.drawRound(x, y + 49, itemWidth + 135, itemHeight - 1, 5.5F, new Color(setting.bgColor(),true));
        RenderUtils.drawRect(x + 16.0F, y + 3.0F, 0.4F, 8.5F, new Color(200, 200, 200, 100).getRGB());
        Fonts.interSemiBold.get(15).drawString("Inventory", x + 19.0F, (double) (y + 5.5F), new Color(255, 255, 255, 255).getRGB());
        Fonts.nursultan.get(16).drawString("A", x + 5.0F, (double) (y + 6.0F), -1);
        for (int i = 9; i < 36; ++i) {
            ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
            RenderUtils.renderItemStack(slot, x + 0.7F, y + 17.5F, 0.80F);
            x += itemWidth;
            x += x1;
            if (i == 17) {
                y += y1 - 1;
                x -= itemWidth * 9.0F;
                x -= x1 * 8.5F;
            }

            if (i == 26) {
                y += y1 - 1;
                x -= itemWidth * 9.0F;
                x -= x1 * 9.0F;
            }
        }

        width = (itemWidth * 9.1F + x1 * 9.0F);
        height = (itemHeight * 3.0F + 19.0F);
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Inventory");
    }
}
