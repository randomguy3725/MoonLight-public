package wtf.moonlight.gui.widget.impl;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.impl.render.Shader2DEvent;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.gui.widget.Widget;
import wtf.moonlight.utils.animations.ContinualAnimation;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

public class PotionHUDWidget extends Widget {

    public PotionHUDWidget() {
        super("Potion HUD");

        this.x = 0;
        this.y = 0.0f;
    }

    @Override
    public void onShader(Shader2DEvent event) {

    }

    private final ContinualAnimation widthAnimation = new ContinualAnimation();
    private final ContinualAnimation heightAnimation = new ContinualAnimation();
    private float animation;

    @Override
    public void render() {
        if (setting.potionHudMode.is("Default")) {
            ArrayList<PotionEffect> potions = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
            widthAnimation.animate(width, 18);
            potions.sort(Comparator.comparingDouble(effect -> -Fonts.interRegular.get(16).getStringWidth(Objects.requireNonNull(I18n.format(Potion.potionTypes[effect.getPotionID()].getName())))));
            float yOffset = 0;
            heightAnimation.animate(potions.size() * 13 - 14, 18);
            RoundedUtils.drawRound(renderX, renderY + yOffset, widthAnimation.getOutput(), Fonts.interBold.get(15).getHeight() + 12f + heightAnimation.getOutput() + 4 + 2, 4, new Color(setting.bgColor(),true));
            Fonts.interBold.get(15).drawString("Potions Status", renderX + 5, renderY + 5.5, setting.color());
            width = (MathHelper.clamp_int(!potions.isEmpty() ? Fonts.interRegular.get(16).getStringWidth(Objects.requireNonNull(potions.stream().max(Comparator.comparingDouble(effect -> Fonts.interRegular.get(16).getStringWidth(Objects.requireNonNull(I18n.format(Potion.potionTypes[effect.getPotionID()].getName()))))).stream().findFirst().orElse(null)).getEffectName()) + 20 : 0, 80, 999));
            height = ((Fonts.interRegular.get(15).getHeight() + 2 + (12 + heightAnimation.getOutput())));
            for (PotionEffect potion : potions) {
                String potionString = I18n.format(Potion.potionTypes[potion.getPotionID()].getName()) + " " + (potion.getAmplifier() > 0 ? I18n.format("enchantment.level." + (potion.getAmplifier() + 1)) : "");
                String durationString = Potion.getDurationString(potion);
                if (Potion.potionTypes[potion.getPotionID()].hasStatusIcon()) {
                    GL11.glPushMatrix();
                    RenderUtils.resetColor();
                    RenderHelper.enableGUIStandardItemLighting();
                    int i1 = Potion.potionTypes[potion.getPotionID()].getStatusIconIndex();
                    GL11.glScaled(0.5, 0.5, 0.5);
                    mc.getTextureManager().bindTexture(GuiContainer.inventoryBackground);
                    Gui.drawTexturedModalRect((renderX + 4) * 9 / 4.5f, ((4 + 2 + Fonts.interRegular.get(15).getHeight() + renderY + yOffset + 0.5f + potions.indexOf(potion) * 13) * 9) / 4.5f, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                    GL11.glScaled(2, 2, 2);
                    RenderHelper.disableStandardItemLighting();
                    GL11.glPopMatrix();
                }
                Fonts.interRegular.get(16).drawString(potionString, (renderX + 15), (Fonts.interRegular.get(15).getHeight() + renderY + yOffset + 4 + 2 + 2) + potions.indexOf(potion) * 13, -1);
                Fonts.interRegular.get(14).drawCenteredString(durationString, (1 + renderX - 6 + widthAnimation.getOutput() - Fonts.interRegular.get(16).getStringWidth(durationString)) + Fonts.interRegular.get(16).getStringWidth(durationString) / 2f + 3, (renderY + yOffset + 4 + Fonts.interBold.get(15).getHeight() + 2 + 2 + 1) + potions.indexOf(potion) * 13, -1);
            }
        }

        if(setting.potionHudMode.is("Nursultan")){
            float posX = renderX;
            float posY = renderY;
            float textPosX = posX + 7.5F;
            float textPosY = posY + 11.5F;
            String name = "Active potions";
            RoundedUtils.drawRound(posX, posY + 1.0F, this.animation, 15.5F, 3.0F, new Color(setting.bgColor(),true));
            Fonts.interRegular.get(14).drawString("Active potions", posX + 19.0F, posY + 7.0F + 0.5F, -1);
            Fonts.nursultan.get(16).drawString("E", textPosX - 2.8F, textPosY - 3.8F, setting.color());
            posY += 18.5F;
            float maxWidth = Fonts.interRegular.get(13).getStringWidth(name) + 10.0F;
            float localHeight = 16.5F;
            posY += 0.4F;

            for(Iterator<PotionEffect> var14 = mc.thePlayer.getActivePotionEffects().iterator(); var14.hasNext(); localHeight += 13.5F) {

                PotionEffect ef = var14.next();
                int amp = ef.getAmplifier();
                String ampStr = "";
                String var24;
                if (amp >= 1 && amp <= 9) {
                    var24 = "enchantment.level." + (amp + 1);
                    ampStr = " " + I18n.format(var24);
                }

                var24 = I18n.format(ef.getEffectName());
                String nameText = var24 + ampStr;
                float nameWidth = Fonts.interRegular.get(13).getStringWidth(nameText);
                String bindText = Potion.getDurationString(ef);
                float bindWidth = Fonts.interRegular.get(13).getStringWidth(bindText);
                float localWidth = nameWidth + bindWidth + 15.0F;
                RoundedUtils.drawRound(posX, posY, this.animation, 11.0F, 2.5F, new Color(setting.bgColor(),true));
                Fonts.interRegular.get(13).drawString(nameText, posX + 3.4F, posY + 4.5F, -1);
                Fonts.interRegular.get(13).drawString(bindText, posX + this.animation - 4.0F - bindWidth, posY + 4.5F, -1);
                if (localWidth > maxWidth) {
                    maxWidth = localWidth;
                }

                posY += 13.3F;
            }

            this.animation = Math.max(maxWidth, 80.0F);
            height = localHeight + 2.5F;
            width = (this.animation);
        }
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Potion HUD") && !setting.potionHudMode.is("Exhi");
    }
}
