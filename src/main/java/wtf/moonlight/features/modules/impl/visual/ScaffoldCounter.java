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
package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.events.impl.render.Shader2DEvent;
import wtf.moonlight.features.modules.impl.movement.Scaffold;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

public class ScaffoldCounter implements InstanceAccess {
    private final Animation anim = new DecelerateAnimation(175, 1);

    @EventTarget
    public void drawCounter(Render2DEvent event) {
        Scaffold scaffold = INSTANCE.getModuleManager().getModule(Scaffold.class);
        switch (scaffold.counter.get().toLowerCase()) {
            case "normal": {
                anim.setDirection(scaffold.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
                //if (!scaffold.isEnabled() && anim.isDone()) return;
                int slot = scaffold.getBlockSlot();
                ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
                int count = slot == -1 ? 0 : scaffold.getBlockCount();
                String countStr = String.valueOf(count);
                ScaledResolution sr = new ScaledResolution(mc);
                float x, y;
                float output = (float) anim.getOutput();
                float blockWH = heldItem != null ? 15 : -2;
                int spacing = 3;
                String text = "§l" + countStr + "§r block" + (count != 1 ? "s" : "");
                float textWidth = Fonts.interBold.get(18).getStringWidth(text);

                float totalWidth = ((textWidth + blockWH + spacing) + 6) * output;
                x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
                y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
                float height = 20;
                GL11.glPushMatrix();
                RenderUtils.scissor(x - 1.5, y - 1.5, totalWidth + 3, height + 3);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RoundedUtils.drawRound(x, y, totalWidth, height, 5, new Color(INSTANCE.getModuleManager().getModule(Interface.class).bgColor(),true));

                Fonts.interBold.get(18).drawString(text, x + 3 + blockWH + spacing, y + height / 2F - Fonts.interBold.get(18).getHeight() / 2F + 2.5f, -1);

                if (heldItem != null) {
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(heldItem, (int) x + 3, (int) (y + 10 - (blockWH / 2)));
                    RenderHelper.disableStandardItemLighting();
                }
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                GL11.glPopMatrix();
                break;
            }
            case "exhibition": {
                if (!scaffold.isEnabled()) return;
                int c = ColorUtils.getColor(255, 0, 0, 150);
                if (scaffold.getBlockCount() >= 64 && 128 > scaffold.getBlockCount()) {
                    c = ColorUtils.getColor(255, 255, 0, 150);
                } else if (scaffold.getBlockCount() >= 128) {
                    c = ColorUtils.getColor(0, 255, 0, 150);
                }
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                mc.fontRendererObj.drawString(String.valueOf(scaffold.getBlockCount()), scaledResolution.getScaledWidth() / 2 - (mc.fontRendererObj.getStringWidth(String.valueOf(scaffold.getBlockCount())) / 2) - 1, scaledResolution.getScaledHeight() / 2 - 36, 0xff000000, false);
                mc.fontRendererObj.drawString(String.valueOf(scaffold.getBlockCount()), scaledResolution.getScaledWidth() / 2 - (mc.fontRendererObj.getStringWidth(String.valueOf(scaffold.getBlockCount())) / 2) + 1, scaledResolution.getScaledHeight() / 2 - 36, 0xff000000, false);
                mc.fontRendererObj.drawString(String.valueOf(scaffold.getBlockCount()), scaledResolution.getScaledWidth() / 2 - (mc.fontRendererObj.getStringWidth(String.valueOf(scaffold.getBlockCount())) / 2), scaledResolution.getScaledHeight() / 2 - 35, 0xff000000, false);
                mc.fontRendererObj.drawString(String.valueOf(scaffold.getBlockCount()), scaledResolution.getScaledWidth() / 2 - (mc.fontRendererObj.getStringWidth(String.valueOf(scaffold.getBlockCount())) / 2), scaledResolution.getScaledHeight() / 2 - 37, 0xff000000, false);
                mc.fontRendererObj.drawString(String.valueOf(scaffold.getBlockCount()), scaledResolution.getScaledWidth() / 2 - (mc.fontRendererObj.getStringWidth(String.valueOf(scaffold.getBlockCount())) / 2), scaledResolution.getScaledHeight() / 2 - 36, c, false);
                break;
            }
            case "simple": {
                if (!scaffold.isEnabled()) return;
                int c = ColorUtils.getColor(255, 0, 0, 150);
                if (scaffold.getBlockCount() >= 64 && 128 > scaffold.getBlockCount()) {
                    c = ColorUtils.getColor(255, 255, 0, 150);
                } else if (scaffold.getBlockCount() >= 128) {
                    c = ColorUtils.getColor(0, 255, 0, 150);
                }
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                Fonts.interMedium.get(18).drawCenteredStringWithShadow(String.valueOf(scaffold.getBlockCount()), scaledResolution.getScaledWidth() / 2f, scaledResolution.getScaledHeight() / 2f + 10, new Color(c).brighter().getRGB());
                break;
            }
        }
    }

    @EventTarget
    public void drawShader2D(Shader2DEvent event) {
        Scaffold scaffold = INSTANCE.getModuleManager().getModule(Scaffold.class);
        switch (scaffold.counter.get().toLowerCase()) {
            case "normal": {
                anim.setDirection(scaffold.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
                //if (!scaffold.isEnabled() && anim.isDone()) return;
                int slot = scaffold.getBlockSlot();
                ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
                int count = slot == -1 ? 0 : scaffold.getBlockCount();
                String countStr = String.valueOf(count);
                ScaledResolution sr = new ScaledResolution(mc);
                float x, y;
                float output = (float) anim.getOutput();
                float blockWH = heldItem != null ? 15 : -2;
                int spacing = 3;
                String text = "§l" + countStr + "§r block" + (count != 1 ? "s" : "");
                float textWidth = Fonts.interBold.get(18).getStringWidth(text);
                float totalWidth = ((textWidth + blockWH + spacing) + 6) * output;
                x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
                y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
                float height = 20;
                GL11.glPushMatrix();
                RenderUtils.scissor(x - 1.5, y - 1.5, totalWidth + 3, height + 3);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RoundedUtils.drawRound(x, y, totalWidth, height, 5, new Color(INSTANCE.getModuleManager().getModule(Interface.class).bgColor(),true));
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                GL11.glPopMatrix();
                break;
            }
        }
    }
}
