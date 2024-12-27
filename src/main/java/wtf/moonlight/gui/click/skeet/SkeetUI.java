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
package wtf.moonlight.gui.click.skeet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjglx.input.Mouse;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.impl.visual.ClickGUI;
import wtf.moonlight.features.values.Value;
import wtf.moonlight.features.values.impl.*;
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.TabComponent;
import wtf.moonlight.gui.click.skeet.component.impl.GroupBoxComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.checkBox.CheckBoxTextComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.color.ColorPickerTextComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.comboBox.ComboBoxTextComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.comboBox2.ComboBox2TextComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.key.KeyBindComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.slider.SliderTextComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.text.box.StringTextComponent;
import wtf.moonlight.gui.font.FontRenderer;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Translate;
import wtf.moonlight.utils.misc.StringUtils;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.GLUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unused")
public final class SkeetUI extends GuiScreen {

    public static final FontRenderer GROUP_BOX_HEADER_RENDERER = new FontRenderer(new Font("Tahoma", Font.BOLD, 11), true);
    public static final FontRenderer ICONS_RENDERER = Fonts.skeet.get(35);
    public static final FontRenderer FONT_RENDERER = new FontRenderer(new Font("Tahoma", Font.PLAIN, 9), false);
    public static final FontRenderer KEYBIND_FONT_RENDERER = new FontRenderer(new Font("Tahoma", Font.PLAIN, 8), false);         
    public static final FontRenderer SLIDER_BOX_RENDERER = new FontRenderer(new Font("Tahoma", Font.BOLD, 10), false);
    public static final FontRenderer fs = new FontRenderer(new Font("Tahoma Bold", Font.PLAIN, 11), true);
    private static final ResourceLocation BACKGROUND_IMAGE = new ResourceLocation("moonlight/texture/exhi/skeetchainmail.png");
    private static final char[] ICONS = new char[] { 'E', 'G', 'F', 'I', 'H', 'D', 'C' };
    private static final int TAB_SELECTOR_HEIGHT = 230 / ICONS.length;
    public static double alpha;
    public static boolean open;
    private final Component rootComponent;
    private final Component tabSelectorComponent;
    public double targetAlpha;
    public boolean closed;
    private boolean dragging;
    private float prevX, prevY;
    private int selectorIndex;
    private TabComponent selectedTab;
    float hue = 0;

    public static final int GROUP_BOX_LEFT_MARGIN = 3;
    private static final int TAB_SELECTOR_WIDTH = 48;
    public static final float USABLE_AREA_WIDTH = 370.0f - TAB_SELECTOR_WIDTH - 3.5f * 2;
    public static final float GROUP_BOX_WIDTH = (USABLE_AREA_WIDTH - 8.0f * 4) / 3;
    public static final float HALF_GROUP_BOX = (GROUP_BOX_WIDTH - 2.0F) / 2 - GROUP_BOX_LEFT_MARGIN * 2;

    @Override
    public void initGui() {
        super.initGui();
    }

    public SkeetUI() {
        this.rootComponent = new Component(null, 0.0f, 0.0f, 370, 350.0f) {

            @Override
            public void drawComponent(final LockedResolution lockedResolution, final int mouseX, final int mouseY) {
                if (dragging) {
                    this.setX(Math.max(1.5f, Math.min(lockedResolution.getWidth() - 1.5f - this.getWidth(), mouseX - prevX)));
                    this.setY(Math.max(1.5f, Math.min(lockedResolution.getHeight() - 1.5f - this.getHeight(), mouseY - prevY)));
                }

                final float borderX = this.getX();
                final float borderY = this.getY();
                final float width = this.getWidth();
                final float height = this.getHeight();

                // Border
                Gui.drawRect(borderX, borderY, borderX + width, borderY + height, new Color(10, 10, 10, (int) alpha).getRGB());
                Gui.drawRect(borderX + 0.5f, borderY + 0.5f, borderX + width - 0.5f, borderY + height - 0.5f, new Color(60, 60, 60, (int) alpha).getRGB());
                Gui.drawRect(borderX + 1.0f, borderY + 1.0f, borderX + width - 1.0f, borderY + height - 1.0f, new Color(40, 40, 40, (int) alpha).getRGB());
                Gui.drawRect(borderX + 3.0f, borderY + 3.0f, borderX + width - 3.0f, borderY + height - 3.0f, new Color(47, 47, 47, (int) alpha).getRGB());

                float left = borderX + 3.5f;
                float top = borderY + 3.5f;
                float right = borderX + width - 3.5f;
                final float bottom = borderY + height - 3.5f;

                // Background
                if (hue > 255.0f) {
                    hue = 0.0f;
                }
                float h = hue;
                float h2 = hue + 85.0f;
                float h3 = hue + 175.0f;

                if (h2 > 255.0f) {
                    h2 -= 255.0f;
                }
                if (h3 > 255.0f) {
                    h3 -= 255.0f;
                }

                final Color no = Color.getHSBColor(h / 255.0f, 0.55f, 1.0f);
                final Color yes = Color.getHSBColor(h2 / 255.0f, 0.55f, 1.0f);
                final Color bruh = Color.getHSBColor(h3 / 255.0f, 0.55f, 1.0f);

                // draw Rainbow background
                ++hue;
                if (Moonlight.INSTANCE.getModuleManager().getModule(ClickGUI.class).rainbow.get()) {
                    drawGradientSideways(left, top, right, bottom, ColorUtils.reAlpha(yes, (int) alpha).getRGB(), ColorUtils.reAlpha(bruh, (int) alpha).getRGB());
                    if (alpha > 20.0) {
                        Gui.drawRect(left, top, right, bottom, new Color(21, 21, 21, 120).getRGB());
                    }
                } else {
                    Gui.drawRect(left, top, right, bottom, getSkeetColor(new Color(21, 21, 21).getRGB()));
                }

                if (Moonlight.INSTANCE.getModuleManager().getModule(ClickGUI.class).rainbow.get()) {
                    Gui.drawRect(left, top, right, bottom, new Color(21, 21, 21, 205).getRGB());
                }

                GL11.glDisable(2929);
                GL11.glEnable(2929);

                if (alpha > 20.0) {
                    GL11.glEnable(GL11.GL_SCISSOR_TEST);
                    RenderUtils.scissor((int) left, (int) top, (int) (right - left), (int) (bottom - top));
                    // ??? Retard code
                    Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_IMAGE);
                    RenderUtils.drawImage(BACKGROUND_IMAGE, left, top, 325, 275);
                    RenderUtils.drawImage(BACKGROUND_IMAGE, left + 325.0f, top + 2.0f, 325, 275);
                    RenderUtils.drawImage(BACKGROUND_IMAGE, left + 1.0f, top + 275.0f, 325, 275);
                    RenderUtils.drawImage(BACKGROUND_IMAGE, left + 326.0f, top + 277.0f, 325, 275);
                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                }

                //Rainbow bar
                final float xDif = (right - left) / 2.0f;
                top += 0.5f;
                left += 0.5f;
                right -= 0.5f;
                drawGradientRect(left, top, left + xDif, top + 1.5f - 0.5f, true, new Color(55, 177, 218, (int)alpha).getRGB(), new Color(204, 77, 198, (int)alpha).getRGB());
                drawGradientRect(left + xDif, top, right, top + 1.5f - 0.5f, true, new Color(204, 77, 198, (int)alpha).getRGB(), new Color(204, 227, 53, (int)alpha).getRGB());

                if (alpha >= 70.0) {
                    Gui.drawRect(left, top + 1.5f - 1.0f, right, top + 1.5f - 0.5f, 1879048192);
                }

//                for (ModuleCategory category : ModuleCategory.values()) {
//                    if (!(category == ModuleCategory.Config)) {
//                        float xOff = 100.0f - 2.5f + rootComponent.getX() - 35;
//                        float yOff = 322.0f + rootComponent.getY() - 20;
//                        float textX = 100.0f - 2.5f;
//                        float textY = 322.0f;
//                        RenderingUtils.rectangleBordered(xOff, yOff - 6.0f, xOff + 295.0f, yOff + 33.0f, 0.5, new Color(0, 0), new Color(10, 255));
//                        RenderingUtils.rectangleBordered((double) xOff + 0.5, (double) yOff - 5.5, (double) (xOff + 295.0f) - 0.5, (double) (yOff + 33.0f) - 0.5, 0.5, new Color(0, 0), new Color(48, 255));
//                        RenderingUtils.rectangle(xOff + 1.0f, yOff - 5.0f, xOff + 294.0f, yOff + 33.0f - 1.0f, new Color(17, 255));
//                        RenderingUtils.rectangle(xOff + 5.0f, yOff - 6.0f, xOff + fs.getWidth("No Settings") + 5.0f, yOff - 4.0f, new Color(17, 255));
//                        fs.drawBlackString("No Settings", textX + left - 34, textY + top - 32.0f, new Color(220, 255));
//                    }
//                }

                for (final Component child : this.children) {
                    if (child instanceof TabComponent && selectedTab != child) {
                        continue;
                    }
                    child.drawComponent(lockedResolution, mouseX, mouseY);
                }
            }

            @Override
            public void onKeyPress(final int keyCode) {
                for (final Component child : this.children) {
                    if (child instanceof TabComponent && selectedTab != child) {
                        continue;
                    }
                    child.onKeyPress(keyCode);
                }
            }
            
            @Override
            public void onMouseClick(final int mouseX, final int mouseY, final int button) {
                for (Component child : children) {
                    if (child instanceof TabComponent) {
                        if (selectedTab != child) {
                            continue;
                        }

                        if (child.isHovered(mouseX, mouseY)) {
                            child.onMouseClick(mouseX, mouseY, button);
                            break;
                        }
                    }

                    child.onMouseClick(mouseX, mouseY, button);
                }

                // Dragging
                if (button == 0 && isHovered(mouseX, mouseY)) {
                    for (Component tabOrSideBar : getChildren()) {
                        if (tabOrSideBar instanceof TabComponent) {
                            if (selectedTab != tabOrSideBar)
                                continue;
                            for (Component groupBox : tabOrSideBar.getChildren()) {
                                if (groupBox instanceof GroupBoxComponent) {
                                    GroupBoxComponent groupBoxComponent = (GroupBoxComponent) groupBox;

                                    if (groupBoxComponent.isHoveredEntire(mouseX, mouseY)) {
                                        return;
                                    }
                                }
                            }
                        } else if (tabOrSideBar.isHovered(mouseX, mouseY))
                            return;
                    }

                    dragging = true;
                    prevX = mouseX - getX();
                    prevY = mouseY - getY();
                }
            }
            
            @Override
            public void onMouseRelease(final int button) {
                super.onMouseRelease(button);
                dragging = false;
            }

            @Override
            public void onMouseScroll(int mouseX, int mouseY, int value) {
                for (Component child : this.children) {
                    if (child instanceof TabComponent && SkeetUI.this.selectedTab != child) continue;
                    child.onMouseScroll(mouseX, mouseY, value * 6);
                }
            }
        };

        for (ModuleCategory category : ModuleCategory.values()) {
            final TabComponent categoryTab = new TabComponent(this.rootComponent, StringUtils.upperSnakeCaseToPascal(category.name()), 51.5f, 5.0f, 315.0f, 341.5f) {

                @SuppressWarnings("all")
                public void setupChildren() {
                    final List<Module> modulesInCategory = Moonlight.INSTANCE.getModuleManager().getModulesByCategory(category);
                    for (final Module module : modulesInCategory) {
                        final GroupBoxComponent groupBoxComponent = new GroupBoxComponent(this, module.getName(), 0.0f, 0.0f, 94.333336f, 6.0f);
                        final CheckBoxTextComponent enabledButton = new CheckBoxTextComponent(groupBoxComponent, "Enable", module::isEnabled, module::setEnabled);
                        enabledButton.addChild(new KeyBindComponent(enabledButton, module::getKeyBind, module::setKeyBind, 0, 1f));
                        groupBoxComponent.addChild(enabledButton);
                        this.addChild(groupBoxComponent);
                        for (final Value property : module.getValues()) {
                            Component component = null;
                            if (property instanceof BoolValue booleanProperty) {
                                component = new CheckBoxTextComponent(groupBoxComponent, property.getName(), booleanProperty::get, booleanProperty::set, booleanProperty::canDisplay);
                            } else if (property instanceof TextValue stringProperty) {
                                component = new StringTextComponent(groupBoxComponent, property.getName(), stringProperty::get, stringProperty::setText, stringProperty::canDisplay);
                            } else if (property instanceof SliderValue doubleProperty) {
                                component = new SliderTextComponent(groupBoxComponent, property.getName(), doubleProperty::get, doubleProperty::setValue, doubleProperty::getMin, doubleProperty::getMax, doubleProperty::getIncrement, doubleProperty::canDisplay);
                            } else if (property instanceof ModeValue enumProperty) {
                                component = new ComboBoxTextComponent(groupBoxComponent, property.getName(), enumProperty::getModes, enumProperty::set, enumProperty::get, enumProperty::canDisplay);
                            } else if (property instanceof MultiBoolValue enumProperty) {
                                component = new ComboBox2TextComponent(groupBoxComponent, property.getName(), () -> enumProperty.getValues(), enumProperty::canDisplay);
                            } else if(property instanceof ColorValue colorProperty){
                                component = new ColorPickerTextComponent(groupBoxComponent, property.getName(), colorProperty::get, colorProperty::set, colorProperty::canDisplay);
                            }

                            if (component != null)
                                groupBoxComponent.addChild(component);
                        }
                        groupBoxComponent.getChildren().sort(Comparator.comparing(Component::getHeight));
                    }
                    this.getChildren().sort(Comparator.comparing(Component::getHeight).reversed());

                }
            };

            this.rootComponent.addChild(categoryTab);
        }

        this.selectedTab = (TabComponent) this.rootComponent.getChildren().get(this.selectorIndex);
        this.tabSelectorComponent = new Component(this.rootComponent, 3.5f, 5.0f, 48.0f, 341.5f) {
            private double selectorY;
            
            @Override
            public void onMouseClick(final int mouseX, final int mouseY, final int button) {
                if (isHovered(mouseX, mouseY)) {
                    final float mouseYOffset = mouseY - tabSelectorComponent.getY() - 10;
                    if (mouseYOffset > 0 && mouseYOffset < tabSelectorComponent.getHeight() - 10) {
                        selectorIndex = Math.min(ICONS.length - 1, (int) (mouseYOffset / TAB_SELECTOR_HEIGHT));
                        selectedTab = (TabComponent) rootComponent.getChildren().get(selectorIndex);
                    }
                }
            }
            
            @Override
            public void drawComponent(final LockedResolution resolution, final int mouseX, final int mouseY) {
                this.selectorY = Translate.anim(this.selectorY, selectorIndex * TAB_SELECTOR_HEIGHT + 10, 3);
                final float x = this.getX();
                final float y = this.getY();
                final float width = this.getWidth();
                final float height = this.getHeight();
                final int innerColor = new Color(0, 0, 0, (int)alpha).getRGB();
                final int outerColor = new Color(48, 48, 48, (int)alpha).getRGB();

                // Top
                Gui.drawRect(x, y, x + width, y + this.selectorY, getSkeetColor(789516));

                Gui.drawRect(x + width - 1.0f, y, x + width, y + this.selectorY, innerColor);
                Gui.drawRect(x + width - 0.5f, y, x + width, y + this.selectorY, outerColor);
                Gui.drawRect(x, y + this.selectorY - 1.0, x + width - 0.5f, y + this.selectorY, innerColor);
                Gui.drawRect(x, y + this.selectorY - 0.5, x + width, y + this.selectorY, outerColor);

                // Bottom
                Gui.drawRect(x, y + this.selectorY + TAB_SELECTOR_HEIGHT, x + width, y + height, getSkeetColor(789516));
                Gui.drawRect(x + width - 1.0f, y + this.selectorY + TAB_SELECTOR_HEIGHT, x + width, y + height, innerColor);
                Gui.drawRect(x + width - 0.5f, y + this.selectorY + TAB_SELECTOR_HEIGHT, x + width, y + height, outerColor);
                Gui.drawRect(x, y + this.selectorY + TAB_SELECTOR_HEIGHT, x + width - 0.5f, y + this.selectorY + TAB_SELECTOR_HEIGHT + 1.0, innerColor);
                Gui.drawRect(x, y + this.selectorY + TAB_SELECTOR_HEIGHT, x + width, y + this.selectorY + TAB_SELECTOR_HEIGHT + 0.5, outerColor);if (shouldRenderText()) {
                    for (int i = 0; i < ICONS.length; ++i) {
                        final String c = String.valueOf(ICONS[i]);
                        ICONS_RENDERER.drawString(c, x + 24.0f - ICONS_RENDERER.getStringWidth(c) / 2.0f - 1.0f
                                , y + 10.0f + i * TAB_SELECTOR_HEIGHT + TAB_SELECTOR_HEIGHT / 2.0f - ICONS_RENDERER.getHeight() / 2.0f
                                , getSkeetColor((i == selectorIndex) ? new Color(185, 185, 185, (int)alpha).getRGB() : new Color(100, 100, 100, (int)alpha).getRGB()));
                    }
                }
            }
        };
        this.rootComponent.addChild(this.tabSelectorComponent);
    }
    
    public static double getAlpha() {
        return alpha;
    }
    
    public static int getSkeetColor() {
        return getSkeetColor(Moonlight.INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get().getRGB());
    }

    public static boolean shouldRenderText() {
        return alpha > 20.0;
    }
    
    private static boolean isVisible() {
        return open || alpha > 0.0;
    }
    
    public static int getSkeetColor(final int color) {
        final int r = color >> 16 & 0xFF;
        final int g = color >> 8 & 0xFF;
        final int b = color & 0xFF;
        final int a = (int)alpha;
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) | (a & 0xFF) << 24;
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 1) {
            if (open) {
                targetAlpha = 0.0;
                open = false;
                dragging = false;
            }
        } else {
            this.rootComponent.onKeyPress(keyCode);
            this.rootComponent.keyTyped(typedChar,keyCode);
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (!open && alpha == 0.0 && !this.closed) {
            mc.displayGuiScreen(null);
            return;
        }
        if (isVisible()) {
            alpha = Translate.anim(alpha, this.targetAlpha, 1000 * 2);
            this.rootComponent.drawComponent(new LockedResolution(this.width,this.height), mouseX, mouseY);
            this.rootComponent.onMouseScroll(mouseX, mouseY, Mouse.getDWheel() * 6);
        }
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        if (isVisible()) {
            this.rootComponent.onMouseClick(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        if (isVisible()) {
            this.rootComponent.onMouseRelease(state);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static void drawGradientSideways(final double left, final double top, final double right, final double bottom, final int col1, final int col2) {
        final float f = (col1 >> 24 & 0xFF) / 255.0f;
        final float f2 = (col1 >> 16 & 0xFF) / 255.0f;
        final float f3 = (col1 >> 8 & 0xFF) / 255.0f;
        final float f4 = (col1 & 0xFF) / 255.0f;
        final float f5 = (col2 >> 24 & 0xFF) / 255.0f;
        final float f6 = (col2 >> 16 & 0xFF) / 255.0f;
        final float f7 = (col2 >> 8 & 0xFF) / 255.0f;
        final float f8 = (col2 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawGradientRect(final double left, final double top, final double right, final double bottom, final boolean sideways, final int startColor, final int endColor) {
        GL11.glDisable(3553);
        GLUtils.startBlend();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
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
        GL11.glDisable(3042);
        GL11.glShadeModel(7424);
        GL11.glEnable(3553);
    }
}
