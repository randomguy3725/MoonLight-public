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
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.ExpandableComponent;
import wtf.moonlight.gui.click.skeet.component.PredicateComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.text.TextComponent;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorPickerTextComponent extends Component implements PredicateComponent, ExpandableComponent
{
    private final ColorPickerComponent colorPicker;
    private final TextComponent textComponent;
    private static final int COLOR_PICKER_WIDTH = 11;

    public ColorPickerTextComponent(final Component parent, final String text, final Supplier<Color> getColor, final Consumer<Color> setColor, final Supplier<Boolean> isVisible, final float x, final float y) {
        super(parent, x, y, 0.0f, 5.0f);
        this.textComponent = new TextComponent(this, text, 1.0f, 1.0f);
        this.addChild(this.colorPicker = new ColorPickerComponent(this, SkeetUI.HALF_GROUP_BOX - COLOR_PICKER_WIDTH, 0.0f, 11.0f, 5.0f) {
            @Override
            public Color getColor() {
                return getColor.get();
            }
            
            @Override
            public void setColor(final Color color) {
                setColor.accept(color);
            }
            
            @Override
            public boolean isVisible() {
                return isVisible.get();
            }
        });
        this.addChild(this.textComponent);
    }
    public ColorPickerTextComponent(final Component parent, final String text, final Supplier<Color> getColor, final Consumer<Color> setColor, final Supplier<Boolean> isVisible) {
        this(parent, text, getColor, setColor, isVisible, 0.0f, 0.0f);
    }

    @Override
    public float getWidth() {
        return 13.0f + textComponent.getWidth();
    }
    @Override
    public boolean isVisible() {
        return this.colorPicker.isVisible();
    }
    
    @Override
    public float getExpandedX() {
        return this.colorPicker.getExpandedX();
    }
    
    @Override
    public float getExpandedY() {
        return this.colorPicker.getY() + this.colorPicker.getHeight();
    }
    
    @Override
    public float getExpandedWidth() {
        return this.colorPicker.getExpandedWidth();
    }
    
    @Override
    public float getExpandedHeight() {
        return this.colorPicker.getExpandedHeight();
    }
    
    @Override
    public void setExpanded(final boolean expanded) {
        this.colorPicker.setExpanded(expanded);
    }
    
    @Override
    public boolean isExpanded() {
        return this.colorPicker.isExpanded();
    }
}
