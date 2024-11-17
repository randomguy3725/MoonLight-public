package wtf.moonlight.gui.click.skeet.component.impl.sub.comboBox2;

import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.gui.click.skeet.SkeetUI;
import wtf.moonlight.gui.click.skeet.component.ButtonComponent;
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.ExpandableComponent;
import wtf.moonlight.gui.click.skeet.component.PredicateComponent;
import wtf.moonlight.gui.click.skeet.LockedResolution;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.gui.font.FontRenderer;
import wtf.moonlight.utils.misc.StringUtils;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ComboBox2Component extends ButtonComponent implements PredicateComponent, ExpandableComponent
{
    private boolean expanded;

    public ComboBox2Component(final Component parent, final float x, final float y, final float width, final float height) {
        super(parent, x, y, width, height);
    }

    private String getDisplayString() {
        final List<BoolValue> values = this.getValues();
        final List<BoolValue> selected = new ArrayList<>();

        for (BoolValue value : values){
            if(value.get())
                selected.add(value);
            else selected.remove(value);
        }

        final int len = selected.size();
        switch (len) {
            case 0: {
                return "...";
            }
            case 1: {
                return StringUtils.upperSnakeCaseToPascal(selected.get(0).getName());
            }
            default: {
                final StringBuilder sb = new StringBuilder(StringUtils.upperSnakeCaseToPascal(selected.get(0).getName())).append(", ");
                for (int i = 1; i < len; ++i) {
                    sb.append(StringUtils.upperSnakeCaseToPascal(selected.get(i).getName()));
                    if (i != len - 1) {
                        sb.append(", ");
                    }
                }
                return sb.toString();
            }
        }
    }

    @Override
    public void drawComponent(final LockedResolution lockedResolution, final int mouseX, final int mouseY) {
        final float x = this.getX();
        final float y = this.getY();
        final float width = this.getWidth();
        final float height = this.getHeight();
        Gui.drawRect(x, y, x + width, y + height, new Color(10, 10, 10, (int) SkeetUI.getAlpha()).getRGB());
        final boolean hovered = this.isHovered(mouseX, mouseY);
        if (hovered) {
            Gui.drawRect(x + 0.5f, y + 0.5f, x + width - 0.5f, y + height - 0.5f, new Color(90, 90, 90, (int) SkeetUI.getAlpha()).getRGB());
        }
        if (hovered) {
            RenderUtils.drawGradientRect(x + 1.0f, y + 1.0f, width - 1.0f, height - 1.0f, false, new Color(31, 31, 31, (int) SkeetUI.getAlpha()).getRGB(), new Color(36, 36, 36, (int) SkeetUI.getAlpha()).getRGB());
        } else {
            RenderUtils.drawGradientRect(x + 0.5f, y + 0.5f, width - 0.5f, height - 0.5f, false, new Color(31, 31, 31, (int) SkeetUI.getAlpha()).getRGB(), new Color(36, 36, 36, (int) SkeetUI.getAlpha()).getRGB());
        }
        GL11.glColor4f(0.6f, 0.6f, 0.6f, (float) SkeetUI.getAlpha() / 255.0f);
        RenderUtils.drawArrow(x + width - 5.0f, y + height / 2.0f - 0.5f, 3.0f, this.isExpanded());
        if (SkeetUI.shouldRenderText()) {
            //GL11.glEnable(3089);
            //RenderUtils.scissor((int) x + 2, (int) y + 1, (int) width - 8, (int) height - 1);
            SkeetUI.FONT_RENDERER.drawString(this.getDisplayString(), x + 2.0f, y + height / 3.0f, SkeetUI.getSkeetColor(9868950));
            //GL11.glDisable(3089);
        }
        GL11.glTranslatef(0.0f, 0.0f, 2.0f);
        if (expanded) {
            final List<BoolValue> values = this.getValues();
            final float dropDownHeight = values.size() * height;
            Gui.drawRect(x, y + height, x + width, y + height + dropDownHeight + 0.5f, SkeetUI.getSkeetColor(855309));
            float valueBoxHeight = height;
            final List<BoolValue> enums = this.getValues();

            final List<BoolValue> selectedV = new ArrayList<>();

            for (BoolValue value : values){
                if(value.get() && value.canDisplay())
                    selectedV.add(value);
                else selectedV.remove(value);
            }

            for (final BoolValue value : enums) {
                boolean valueBoxHovered = mouseX >= x && mouseY >= y + valueBoxHeight &&
                        mouseX <= x + width && mouseY < y + valueBoxHeight + height;
                Gui.drawRect(x + 0.5F, y + valueBoxHeight,
                        x + width - 0.5F, y + valueBoxHeight + height,
                        SkeetUI.getSkeetColor(valueBoxHovered ? ColorUtils.darker(0x232323, 0.7F) : 0x232323));
                final boolean selected;
                selected = selectedV.contains(value);

                int color = selected ? SkeetUI.getSkeetColor() : SkeetUI.getSkeetColor(0xDCDCDC);
                FontRenderer fr;
                if (selected || valueBoxHovered) {
                    fr = SkeetUI.GROUP_BOX_HEADER_RENDERER;
                } else {
                    fr = SkeetUI.FONT_RENDERER;
                }
                fr.drawString(StringUtils.upperSnakeCaseToPascal(value.getName()), x + 2, y + valueBoxHeight + 4, color);
                valueBoxHeight += height;
            }
        }
        GL11.glTranslatef(0.0f, 0.0f, -2.0f);
    }

    @Override
    public void onMouseClick(final int mouseX, final int mouseY, final int button) {
        if (this.isHovered(mouseX, mouseY)) {
            this.onPress(button);
        }

        if (this.expanded && button == 0) {
            final float x = this.getX();
            final float y = this.getY();
            final float height = this.getHeight();
            final float width = this.getWidth();
            float valueBoxHeight = height;
            for (int i = 0; i < this.getValues().size(); ++i) {
                if (mouseX >= x && mouseY >= y + valueBoxHeight && mouseX <= x + width && mouseY < y + valueBoxHeight + height) {
                    getValues().get(i).set(!getValues().get(i).get());
                    break;
                }

                valueBoxHeight += height;
            }
        }
    }

    private void expandOrClose() {
        this.setExpanded(!this.isExpanded());
    }

    @Override
    public void onPress(final int mouseButton) {
        if (mouseButton == 0) {
            this.expandOrClose();
        }
    }
    @Override
    public float getExpandedX() {
        return this.getX();
    }
    @Override
    public float getExpandedY() {
        return this.getY();
    }
    public abstract List<BoolValue> getValues();
    @Override
    public boolean isExpanded() {
        return this.expanded;
    }
    @Override
    public void setExpanded(final boolean expanded) {
        this.expanded = expanded;
    }
    @Override
    public float getExpandedWidth() {
        return this.getWidth();
    }

    @Override
    public float getExpandedHeight() {
        final float height = this.getHeight();
        return height + this.getValues().size() * height + height;
    }
}
