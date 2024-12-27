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
package wtf.moonlight.gui.click.skeet.component.impl.sub.color;

import wtf.moonlight.gui.click.skeet.SkeetUI;
import wtf.moonlight.gui.click.skeet.component.ButtonComponent;
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.ExpandableComponent;
import wtf.moonlight.gui.click.skeet.component.PredicateComponent;
import wtf.moonlight.gui.click.skeet.LockedResolution;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;

public abstract class ColorPickerComponent extends ButtonComponent implements PredicateComponent, ExpandableComponent
{
    private static final int MARGIN = 3;
    private static final int SLIDER_THICKNESS = 8;

    private static final float SELECTOR_WIDTH = 1;
    private static final float HALF_WIDTH = SELECTOR_WIDTH / 2;

    private static final float OUTLINE_WIDTH = 0.5F;

    private boolean expanded;

    private float hue;
    private float saturation;
    private float brightness;
    private float alpha;

    private boolean colorSelectorDragging;
    private boolean hueSelectorDragging;
    private boolean alphaSelectorDragging;

    public ColorPickerComponent(Component parent, float x, float y, float width, float height) {
        super(parent, x, y, width, height);

        //        float[] hsb = getHSBFromColor(value);
        //        this.hue = hsb[0];
        //        this.saturation = hsb[1];
        //        this.brightness = hsb[2];
        //
        //        this.alpha = (value >> 24 & 0xFF) / 255.0F;
    }

    private static void drawCheckeredBackground(float x, float y, float x2, float y2) {
        Gui.drawRect(x, y, x2, y2, SkeetUI.getSkeetColor(0xFFFFFF));

        for (boolean offset = false; y < y2; y++) {
            for (float x1 = x + ((offset == !offset) ? 1 : 0); x1 < x2; x1 += 2) {
                if (x1 > x2 - 1) {
                    Gui.drawRect(x1, y, x1 + 1, y + 1, SkeetUI.getSkeetColor(0x808080));
                }
            }
        }
    }

    @Override
    public void drawComponent(LockedResolution lockedResolution, int mouseX, int mouseY) {
        final float x = getX();
        final float y = getY();
        final float width = getWidth();
        final float height = getHeight();

        final int black = SkeetUI.getSkeetColor(0);

        Gui.drawRect(x - 0.5, y - 0.5, x + width + 0.5, y + height + 0.5, black);

        final int guiAlpha = (int) SkeetUI.getAlpha();
        {
            final int color = getColor().getRGB();

            final int colorAlpha = color >> 24 & 0xFF;

            final int minAlpha = Math.min(guiAlpha, colorAlpha);

            if (colorAlpha < 255)
                drawCheckeredBackground(x, y, x + width, y + height);

            final int newColor = new Color(
                    color >> 16 & 0xFF,
                    color >> 8 & 0xFF,
                    color & 0xFF,
                    minAlpha).getRGB();

            drawGradientRect(x, y, x + width, y + height, newColor, ColorUtils.darker(newColor, 0.6f));
        }

        if (isExpanded()) {
            GL11.glTranslated(0, 0, 3.0);

            final float expandedX = getExpandedX();
            final float expandedY = getExpandedY();

            final float expandedWidth = getExpandedWidth();
            final float expandedHeight = getExpandedHeight();

            // Background
            {
                Gui.drawRect(expandedX, expandedY,
                        expandedX + expandedWidth, expandedY + expandedHeight, black);

                Gui.drawRect(expandedX + 0.5, expandedY + 0.5,
                        expandedX + expandedWidth - 0.5, expandedY + expandedHeight - 0.5, SkeetUI.getSkeetColor(0x39393B));

                Gui.drawRect(expandedX + 1, expandedY + 1,
                        expandedX + expandedWidth - 1, expandedY + expandedHeight - 1, SkeetUI.getSkeetColor(0x232323));
            }

            final float colorPickerSize = expandedWidth - MARGIN * 3 - SLIDER_THICKNESS;

            final float colorPickerLeft = expandedX + MARGIN;
            final float colorPickerTop = expandedY + MARGIN;
            //final float colorPickerRight = colorPickerLeft + colorPickerSize;
            //final float colorPickerBottom = colorPickerTop + colorPickerSize;
             final float colorPickerRight = colorPickerLeft + colorPickerSize;
             final float colorPickerBottom = colorPickerTop + colorPickerSize;

            final int selectorWhiteOverlayColor = new Color(0xFF, 0xFF, 0xFF, Math.min(guiAlpha, 180)).getRGB();

            // Color picker
            {
                if (mouseX <= colorPickerLeft || mouseY <= colorPickerTop || mouseX >= colorPickerRight || mouseY >= colorPickerBottom)
                    colorSelectorDragging = false;

                Gui.drawRect(colorPickerLeft - 0.5, colorPickerTop - 0.5,
                        colorPickerRight + 0.5, colorPickerBottom + 0.5, SkeetUI.getSkeetColor(0));

                drawColorPickerRect(colorPickerLeft, colorPickerTop, colorPickerRight, colorPickerBottom);

                float colorSelectorX = saturation * (colorPickerRight - colorPickerLeft);
                float colorSelectorY = (1 - brightness) * (colorPickerBottom - colorPickerTop);

                if (colorSelectorDragging) {
                    float wWidth = colorPickerRight - colorPickerLeft;
                    float xDif = mouseX - colorPickerLeft;
                    this.saturation = xDif / wWidth;
                    colorSelectorX = xDif;

                    float hHeight = colorPickerBottom - colorPickerTop;
                    float yDif = mouseY - colorPickerTop;
                    this.brightness = 1 - (yDif / hHeight);
                    colorSelectorY = yDif;

                    updateColor(new Color(Color.HSBtoRGB(hue, saturation, brightness)), false);
                }

                // Color selector
                {
                    final float csLeft = colorPickerLeft + colorSelectorX - HALF_WIDTH;
                    final float csTop = colorPickerTop + colorSelectorY - HALF_WIDTH;
                    final float csRight = colorPickerLeft + colorSelectorX + HALF_WIDTH;
                    final float csBottom = colorPickerTop + colorSelectorY + HALF_WIDTH;


                    Gui.drawRect(csLeft - OUTLINE_WIDTH, csTop - OUTLINE_WIDTH, csLeft, csBottom + OUTLINE_WIDTH,
                            black);

                    Gui.drawRect(csRight, csTop - OUTLINE_WIDTH, csRight + OUTLINE_WIDTH, csBottom + OUTLINE_WIDTH,
                            black);

                    Gui.drawRect(csLeft, csTop - OUTLINE_WIDTH, csRight, csTop,
                            black);

                    Gui.drawRect(csLeft, csBottom, csRight, csBottom + OUTLINE_WIDTH,
                            black);

                    Gui.drawRect(csLeft, csTop, csRight, csBottom, selectorWhiteOverlayColor);
                }
            }

            // Hue bar
            {
                final float hueSliderLeft = colorPickerRight + MARGIN;
                final float hueSliderRight = hueSliderLeft + SLIDER_THICKNESS;

                if (mouseX <= hueSliderLeft || mouseY <= colorPickerTop || mouseX >= hueSliderRight || mouseY >= colorPickerBottom)
                    hueSelectorDragging = false;

                final float hueSliderYDif = colorPickerBottom - colorPickerTop;

                float hueSelectorY = (1 - this.hue) * hueSliderYDif;

                if (hueSelectorDragging) {
                    float yDif = mouseY - colorPickerTop;
                    this.hue = 1 - (yDif / hueSliderYDif);
                    hueSelectorY = yDif;

                    updateColor(new Color(Color.HSBtoRGB(hue, saturation, brightness)), false);
                }

                Gui.drawRect(hueSliderLeft - 0.5, colorPickerTop - 0.5, hueSliderRight + 0.5, colorPickerBottom + 0.5,
                        black);

                final float inc = 0.2F;
                final float times = 1 / inc;
                final float sHeight = colorPickerBottom - colorPickerTop;
                final float size = sHeight / times;
                float sY = colorPickerTop;

                // Draw colored hue bar
                for (int i = 0; i < times; i++) {
                    boolean last = i == times - 1;
                    drawGradientRect(hueSliderLeft, sY, hueSliderRight,
                            sY + size,
                            SkeetUI.getSkeetColor(Color.HSBtoRGB(1 - inc * i, 1.0F, 1.0F)),
                            SkeetUI.getSkeetColor(Color.HSBtoRGB(1 - inc * (i + 1), 1.0F, 1.0F)));
                    if (!last)
                        sY += size;
                }

                // Hue Selector
                {
                    final float hsTop = colorPickerTop + hueSelectorY - HALF_WIDTH;
                    final float hsBottom = colorPickerTop + hueSelectorY + HALF_WIDTH;

                    Gui.drawRect(hueSliderLeft - OUTLINE_WIDTH, hsTop - OUTLINE_WIDTH, hueSliderLeft, hsBottom + OUTLINE_WIDTH,
                            black);

                    Gui.drawRect(hueSliderRight, hsTop - OUTLINE_WIDTH, hueSliderRight + OUTLINE_WIDTH, hsBottom + OUTLINE_WIDTH,
                            black);

                    Gui.drawRect(hueSliderLeft, hsTop - OUTLINE_WIDTH, hueSliderRight, hsTop,
                            black);

                    Gui.drawRect(hueSliderLeft, hsBottom, hueSliderRight, hsBottom + OUTLINE_WIDTH,
                            black);

                    Gui.drawRect(hueSliderLeft, hsTop, hueSliderRight, hsBottom, selectorWhiteOverlayColor);
                }
            }

            // Alpha bar
            {
                final float alphaSliderTop = colorPickerBottom + MARGIN;
                final float alphaSliderBottom = alphaSliderTop + SLIDER_THICKNESS;

                if (mouseX <= colorPickerLeft || mouseY <= alphaSliderTop || mouseX >= colorPickerRight || mouseY >= alphaSliderBottom)
                    alphaSelectorDragging = false;

                int color = Color.HSBtoRGB(hue, saturation, brightness);

                int r = color >> 16 & 0xFF;
                int g = color >> 8 & 0xFF;
                int b = color & 0xFF;

                final float hsHeight = colorPickerRight - colorPickerLeft;

                float alphaSelectorX = alpha * hsHeight;

                if (alphaSelectorDragging) {
                    float xDif = mouseX - colorPickerLeft;
                    this.alpha = xDif / hsHeight;
                    alphaSelectorX = xDif;

                    updateColor(new Color(r, g, b, (int) (alpha * 255)), true);
                }

                Gui.drawRect(colorPickerLeft - 0.5, alphaSliderTop - 0.5, colorPickerRight + 0.5, alphaSliderBottom + 0.5, black);

                drawCheckeredBackground(colorPickerLeft, alphaSliderTop, colorPickerRight, alphaSliderBottom);

                drawGradientRect(colorPickerLeft, alphaSliderTop, colorPickerRight,
                        alphaSliderBottom,
                        true,
                        new Color(r, g, b, 0).getRGB(),
                        new Color(r, g, b, Math.min(guiAlpha, 0xFF)).getRGB());

                // Alpha selector
                {
                    final float asLeft = colorPickerLeft + alphaSelectorX - HALF_WIDTH;
                    final float asRight = colorPickerLeft + alphaSelectorX + HALF_WIDTH;


                    Gui.drawRect(asLeft - OUTLINE_WIDTH,
                            alphaSliderTop,
                            asRight + OUTLINE_WIDTH,
                            alphaSliderBottom,
                            black);

                    Gui.drawRect(asLeft,
                            alphaSliderTop,
                            asRight,
                            alphaSliderBottom,
                            selectorWhiteOverlayColor);
                }

//                GlStateManager.pushMatrix();
//                GlStateManager.translate(getX() + 65.0F, getY() + 33.0F, 0.0F);
//                GlStateManager.scale(0.5D, 0.5D, 0.5D);
//                Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Copy", (float)(Minecraft.getMinecraft().fontRendererObj.getStringWidth("Copy") / 2) - 142, 81.0F, -1);
//                Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Paste", (float)(Minecraft.getMinecraft().fontRendererObj.getStringWidth("Paste") / 2) - 146, 91.0F, -1);
//                GlStateManager.popMatrix();
            }

            GL11.glTranslated(0, 0, -3.0);
        }
    }

//    public static String copy() {
//        String ret = "";
//        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
//        Transferable clipTf = sysClip.getContents(null);
//        if (clipTf != null && clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
//            try {
//                ret = (String)clipTf.getTransferData(DataFlavor.stringFlavor);
//            } catch (Exception var4) {
//                var4.printStackTrace();
//            }
//        }
//
//        return ret;
//    }
//
//    public static void paste(String writeMe) {
//        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
//        Transferable tText = new StringSelection(writeMe);
//        clip.setContents(tText, null);
//    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        super.onMouseClick(mouseX, mouseY, button);

        if (isExpanded() && button == 0) {
            final float expandedX = getExpandedX();
            final float expandedY = getExpandedY();

            final float expandedWidth = getExpandedWidth();

            float colorPickerSize = expandedWidth - 9.0f - 8.0f;
            float colorPickerLeft = expandedX + 3.0f;
            float colorPickerTop = expandedY + 3.0f;
            float colorPickerRight = colorPickerLeft + colorPickerSize;
            float colorPickerBottom = colorPickerTop + colorPickerSize;
            float alphaSliderTop = colorPickerBottom + 3.0f;
            float alphaSliderBottom = alphaSliderTop + 8.0f;
            float hueSliderLeft = colorPickerRight + 3.0f;
            float hueSliderRight = hueSliderLeft + 8.0f;
            this.colorSelectorDragging = !this.colorSelectorDragging && (float)mouseX > colorPickerLeft && (float)mouseY > colorPickerTop && (float)mouseX < colorPickerRight && (float)mouseY < colorPickerBottom;
            this.alphaSelectorDragging = !this.alphaSelectorDragging && (float)mouseX > colorPickerLeft && (float)mouseY > alphaSliderTop && (float)mouseX < colorPickerRight && (float)mouseY < alphaSliderBottom;
            this.hueSelectorDragging = !this.hueSelectorDragging && (float)mouseX > hueSliderLeft && (float)mouseY > colorPickerTop && (float)mouseX < hueSliderRight && (float)mouseY < colorPickerBottom;
        }
    }

    @Override
    public void onMouseRelease(int button) {
        if (colorSelectorDragging)
            colorSelectorDragging = false;
        if (alphaSelectorDragging)
            alphaSelectorDragging = false;
        if (hueSelectorDragging)
            hueSelectorDragging = false;
    }

    private void updateColor(Color hex, boolean hasAlpha) {
        if (hasAlpha) {
            setColor(hex);
        } else {
            setColor(new Color(hex.getRed(),hex.getGreen(),hex.getBlue(),(int) alpha));
        }
    }

    public abstract Color getColor();

    public abstract void setColor(Color color);
    public void onValueChange(int oldValue, int value) {

    }
    private void drawColorPickerRect(float left, float top, float right, float bottom) {
        final int hueBasedColor = SkeetUI.getSkeetColor(Color.HSBtoRGB(hue, 1.0F, 1.0F));

        drawGradientRect(left, top, right, bottom, true, SkeetUI.getSkeetColor(16777215), hueBasedColor);
        drawGradientRect(left, top, right, bottom, 0, SkeetUI.getSkeetColor(0));
    }

    @Override
    public float getExpandedX() {
        return this.getX() + this.getWidth() - 80.333336f;
    }

    @Override
    public float getExpandedY() {
        return getY() + getHeight();
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public void onPress(int mouseButton) {
        if (mouseButton == 0)
            setExpanded(!isExpanded());
    }

    @Override
    public float getExpandedWidth() {
        final float right = getX() + getWidth();
        return right - getExpandedX();
    }

    @Override
    public float getExpandedHeight() {
        return getExpandedWidth();
    }

    public void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, 0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, 0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, 0).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawGradientRect(double left, double top, double right, double bottom, boolean sideways, int startColor, int endColor) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        RenderUtils.color(startColor);
        if (sideways) {
            GL11.glVertex2d(left, top);
            GL11.glVertex2d(left, bottom);
            RenderUtils.color(endColor);
            GL11.glVertex2d(right, bottom);
            GL11.glVertex2d(right, top);
        } else {
            GL11.glVertex2d(left, top);
            RenderUtils.color(endColor);
            GL11.glVertex2d(left, bottom);
            GL11.glVertex2d(right, bottom);
            RenderUtils.color(startColor);
            GL11.glVertex2d(right, top);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}