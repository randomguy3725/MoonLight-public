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
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static wtf.moonlight.gui.click.neverlose.NeverLose.*;

public class PotionHUDWidget extends Widget {

    public PotionHUDWidget() {
        super("Potion HUD");

        this.x = 0;
        this.y = 0.0f;
    }

    private final ContinualAnimation widthAnimation = new ContinualAnimation();
    private final ContinualAnimation heightAnimation = new ContinualAnimation();
    @Override
    public void onShader(Shader2DEvent event) {
        ArrayList<PotionEffect> potions = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        if (setting.potionHudMode.is("Default")) {
            widthAnimation.animate(width, 18);
            potions.sort(Comparator.comparingDouble(effect -> -Fonts.interRegular.get(16).getStringWidth(Objects.requireNonNull(I18n.format(Potion.potionTypes[effect.getPotionID()].getName())))));
            float yOffset = 0;
            heightAnimation.animate(potions.size() * 13 - 14, 18);
            RoundedUtils.drawRound(renderX, renderY + yOffset, widthAnimation.getOutput(), Fonts.interBold.get(15).getHeight() + 12f + heightAnimation.getOutput() + 4 + 2, 4, new Color(setting.bgColor(), true));
            Fonts.interBold.get(15).drawString("Potions Status", renderX + 5, renderY + 5.5, setting.color());
            width = (MathHelper.clamp_int(!potions.isEmpty() ? Fonts.interRegular.get(16).getStringWidth(Objects.requireNonNull(potions.stream().max(Comparator.comparingDouble(effect -> Fonts.interRegular.get(16).getStringWidth(Objects.requireNonNull(I18n.format(Potion.potionTypes[effect.getPotionID()].getName()))))).stream().findFirst().orElse(null)).getEffectName()) + 20 : 0, 80, 999));
            height = ((Fonts.interRegular.get(15).getHeight() + 2 + (12 + heightAnimation.getOutput())));
        }
        if (setting.potionHudMode.is("Sexy")) {
            width = 92;
            height = heightAnimation.getOutput();

            RoundedUtils.drawRound(renderX, renderY, width, height, 6, new Color(setting.bgColor(), true));

            heightAnimation.animate(20 + potions.size() * 10, 20);
        }

        if (setting.potionHudMode.is("Type 1")) {
            RoundedUtils.drawRound(renderX, renderY, width, height, 4, new Color(setting.bgColor(), true));
        }

        if (setting.potionHudMode.is("NeverLose")) {

            height = (15);
            width = MathHelper.clamp_float(!potions.isEmpty() ? Fonts.interSemiBold.get(16).getStringWidth(Objects.requireNonNull(potions.stream().max(Comparator.comparingDouble(effect -> Fonts.interSemiBold.get(16).getStringWidth(Objects.requireNonNull(I18n.format(Potion.potionTypes[effect.getPotionID()].getName()))))).stream().findFirst().orElse(null)).getEffectName()) + 20 : 0, 80, 999);
            widthAnimation.animate(width, 18);
            potions.sort(Comparator.comparingDouble(effect -> -Fonts.interSemiBold.get(16).getStringWidth(Objects.requireNonNull(I18n.format(Potion.potionTypes[effect.getPotionID()].getName())))));

            float yOffset = 18;
            RoundedUtils.drawRound(renderX, renderY, widthAnimation.getOutput(), 14f, 4, bgColor);
            RoundedUtils.drawRound(renderX, renderY + yOffset, widthAnimation.getOutput(), 12f + heightAnimation.getOutput(), 4, ColorUtils.applyOpacity(bgColor, 1f));
        }
    }
    @Override
    public void render() {
        ArrayList<PotionEffect> potions = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        if (setting.potionHudMode.is("Default")) {
            widthAnimation.animate(width, 18);
            potions.sort(Comparator.comparingDouble(effect -> -Fonts.interRegular.get(16).getStringWidth(Objects.requireNonNull(I18n.format(Potion.potionTypes[effect.getPotionID()].getName())))));
            float yOffset = 0;
            heightAnimation.animate(potions.size() * 13 - 14, 18);
            RoundedUtils.drawRound(renderX, renderY + yOffset, widthAnimation.getOutput(), Fonts.interBold.get(15).getHeight() + 12f + heightAnimation.getOutput() + 4 + 2, 4, new Color(setting.bgColor(), true));
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

        if (setting.potionHudMode.is("Nursultan")) {

            int maxWidth = calculateMaxPotionWidth();
            int offset = calculatePotionOffset();

            heightAnimation.animate(offset, 200);
            widthAnimation.animate(maxWidth, 200);

            width = (maxWidth);
            height = (8 + offset);

            float posX = renderX;
            float posY = renderY;

            RoundedUtils.drawRound(posX + 1.3F, posY + 4.5F, widthAnimation.getOutput(), 13F, 2.5F, new Color(setting.bgColor(), true));
            RenderUtils.drawRect(posX + 16F, 5.5F + 1f, 0.7F, 9.0F, new Color(75, 75, 75).getRGB());

            Fonts.nursultan.get(16).drawString("E", posX + 5F, posY + 10F, Color.WHITE.getRGB());

            Fonts.interRegular.get(15).drawString("Active Potions", posX + 19F, posY + 8F + 1f, Color.WHITE.getRGB());

            int itemOffset = 0;
            for (PotionEffect effect : potions) {
                Potion potion = Potion.potionTypes[effect.getPotionID()];
                String potionName = I18n.format(Potion.potionTypes[potion.getId()].getName());
                String durationText = Potion.getDurationString(effect);
                int amplifier = effect.getAmplifier();
                String potionNameWithLevel = amplifier > 0 ? potionName + " " + (amplifier + 1) : potionName;

                float xPos = posX + 1.3F;
                float yPos = posY + 20 + itemOffset;
                float width = widthAnimation.getOutput() - 5f;
                float height = 9F;
                float radius = 2.5F;

                RoundedUtils.drawRound(xPos, yPos, width + 5, height, radius, new Color(setting.bgColor(), true));

                float nameX = posX + 5.5f;
                float nameY = posY + 23.5f + itemOffset;
                Fonts.interSemiBold.get(13).drawString(potionNameWithLevel, nameX - 2, nameY, Color.WHITE.getRGB());

                float durationX = posX + maxWidth - 35;
                Fonts.interSemiBold.get(13).drawString(durationText, durationX + 6, nameY, Color.WHITE.getRGB());

                RenderUtils.drawRect(durationX + 24f, posY + 22f + itemOffset, 0.7F, 5F, new Color(75, 75, 75).getRGB());

                if (potion.hasStatusIcon()) {
                    int iconIndex = potion.getStatusIconIndex();
                    float u = iconIndex % 8 * 18;
                    float v = 198 + (float) iconIndex / 8 * 18;

                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                    mc.getTextureManager().bindTexture(GuiContainer.inventoryBackground);

                    Gui.drawScaledCustomSizeModalRect(
                            (int) (durationX + 27),
                            (int) (posY + 21 + itemOffset),
                            u, v,
                            18, 18,
                            8, 8,
                            256, 256);

                    GL11.glDisable(GL11.GL_BLEND);
                }

                itemOffset += 12;
            }
        }

        if (setting.potionHudMode.is("Sexy")) {

            width = 92;
            height = heightAnimation.getOutput();

            RoundedUtils.drawRound(renderX, renderY, width, height, 6, new Color(setting.bgColor(), true));

            Fonts.interSemiBold.get(13).drawString("Potions", renderX + 8, renderY + 7 + 2, -1);

            Fonts.nursultan.get(14).drawString("E", renderX + width - 16, renderY + 9, setting.color(0));

            float offset = renderY + 21;
            for (PotionEffect potion : potions) {

                String name = I18n.format(Potion.potionTypes[potion.getPotionID()].getName()) + " " + (potion.getAmplifier() > 0 ? I18n.format("enchantment.level." + (potion.getAmplifier() + 1)) : "");
                String duration = Potion.getDurationString(potion);

                Fonts.interRegular.get(11).drawString(name, renderX + 8, offset, -1);
                Fonts.interRegular.get(11).drawString(duration, renderX + width - 8 - Fonts.interRegular.get(11).getStringWidth(duration), offset, -1);

                offset += 10;
            }

            heightAnimation.animate(20 + potions.size() * 10, 20);
        }

        if (setting.potionHudMode.is("Type 1")) {

            float posX = renderX;
            float posY = renderY;
            float fontSize = 13;
            float padding = 5;
            float iconSizeX = 10;

            String name = "Potions";

            RoundedUtils.drawRound(posX, posY, width, height, 4, new Color(setting.bgColor(), true));
            Fonts.interMedium.get(fontSize).drawCenteredString(name, posX - 22 + width / 2, posY + padding + 0.5f + 2, -1);

            float imagePosX = posX + width - iconSizeX - padding;
            Fonts.nursultan.get(fontSize).drawString("E", imagePosX + 2f, posY + 7f + 2, setting.color());

            posY += Fonts.interMedium.get(fontSize).getHeight() + padding * 2;

            float maxWidth = Fonts.interMedium.get(fontSize).getStringWidth(name) + padding * 2;
            float localHeight = Fonts.interMedium.get(fontSize).getHeight() + padding * 2;

            RoundedUtils.drawRound(posX + 0.5f, posY, width - 1, 1.25f, 3, new Color(ColorUtils.darker(setting.color(), 0.4f)));
            posY += 3f;

            for (PotionEffect effect : potions) {
                Potion potion = Potion.potionTypes[effect.getPotionID()];
                String potionName = I18n.format(Potion.potionTypes[potion.getId()].getName());
                String durationText = Potion.getDurationString(effect);
                String nameText = potionName + " " + (effect.getAmplifier() > 0 ? I18n.format("enchantment.level." + (effect.getAmplifier() + 1)) : "");
                float nameWidth = Fonts.interMedium.get(fontSize).getStringWidth(nameText);

                float bindWidth = Fonts.interMedium.get(fontSize).getStringWidth(durationText);

                float localWidth = nameWidth + bindWidth + padding * 3;

                Fonts.interMedium.get(fontSize).drawString(nameText, posX + padding, posY + 2, -1);
                Fonts.interMedium.get(fontSize).drawString(durationText, posX + width - padding - bindWidth, posY + 2, -1);

                if (localWidth > maxWidth) {
                    maxWidth = localWidth;
                }

                posY += Fonts.interMedium.get(fontSize).getHeight() + padding;
                localHeight += Fonts.interMedium.get(fontSize).getHeight() + padding;
            }
            width = Math.max(maxWidth, 80);
            height = localHeight + 2.5f;
        }

        if (setting.potionHudMode.is("NeverLose")) {

            height = (15);
            width = MathHelper.clamp_float(!potions.isEmpty() ? Fonts.interSemiBold.get(16).getStringWidth(Objects.requireNonNull(potions.stream().max(Comparator.comparingDouble(effect -> Fonts.interSemiBold.get(16).getStringWidth(Objects.requireNonNull(I18n.format(Potion.potionTypes[effect.getPotionID()].getName()))))).stream().findFirst().orElse(null)).getEffectName()) + 20 : 0, 80, 999);
            widthAnimation.animate(width, 18);
            potions.sort(Comparator.comparingDouble(effect -> -Fonts.interSemiBold.get(16).getStringWidth(Objects.requireNonNull(I18n.format(Potion.potionTypes[effect.getPotionID()].getName())))));

            float yOffset = 18;
            RoundedUtils.drawRound(renderX, renderY, widthAnimation.getOutput(), 14f, 4, ColorUtils.applyOpacity(bgColor, 1f));
            Fonts.nursultan.get(15).drawString("E ", renderX + 5, renderY + 5.5f, iconRGB);
            Fonts.interSemiBold.get(15).drawString("Potions Status", renderX + 5 + Fonts.nursultan.get(15).getStringWidth("E "), renderY + 5.5, textRGB);

            heightAnimation.animate(potions.size() * 13 - 14, 18);

            RoundedUtils.drawRound(renderX, renderY + yOffset, widthAnimation.getOutput(), 12f + heightAnimation.getOutput(), 4, ColorUtils.applyOpacity(bgColor, 1f));

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
                    drawTexturedModalRect((renderX + 4) * 9 / 4.5f, (renderY + yOffset + 1f + potions.indexOf(potion) * 13) * 9 / 4.5f, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                    GL11.glScaled(2, 2, 2);
                    RenderHelper.disableStandardItemLighting();
                    GL11.glPopMatrix();
                }

                Fonts.interSemiBold.get(16).drawString(potionString, (renderX + 15), (renderY + yOffset + 4) + potions.indexOf(potion) * 13, textRGB);
                Fonts.interSemiBold.get(14).drawCenteredString(durationString, (renderX - 6 + widthAnimation.getOutput() - Fonts.interSemiBold.get(16).getStringWidth(durationString)) + Fonts.interSemiBold.get(16).getStringWidth(durationString) / 2f + 3, (renderY + yOffset + 4) + potions.indexOf(potion) * 13, iconRGB);
                //yOffset += (float) (16);
            }

        }
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Potion HUD") && !setting.potionHudMode.is("Exhi");
    }

    private int calculateMaxPotionWidth() {
        List<PotionEffect> potions = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        int maxWidth = 80;

        for (PotionEffect effect : potions) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            String potionName = I18n.format(Potion.potionTypes[potion.getId()].getName());
            int amplifier = effect.getAmplifier();
            String potionNameWithLevel = amplifier > 0 ? potionName + " " + (amplifier + 1) : potionName;

            float nameWidth = Fonts.interSemiBold.get(13).getStringWidth(potionNameWithLevel);
            float iconWidth = 16;
            float totalWidth = nameWidth + iconWidth + 30;

            if (totalWidth > maxWidth) {
                maxWidth = (int) totalWidth;
            }
        }

        return maxWidth;
    }

    private int calculatePotionOffset() {
        List<PotionEffect> potions = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        return potions.isEmpty() ? -1 : potions.size() * 12;
    }
}
