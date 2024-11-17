package wtf.moonlight.gui.click.skeet.component.impl.sub.comboBox2;

import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.ExpandableComponent;
import wtf.moonlight.gui.click.skeet.component.PredicateComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.text.TextComponent;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ComboBox2TextComponent extends Component implements PredicateComponent, ExpandableComponent
{
    private final ComboBox2Component comboBoxComponent;
    private final TextComponent textComponent;

    public ComboBox2TextComponent(final Component parent, final String name, final Supplier<List<BoolValue>> getValues,
                                  final Supplier<Boolean> isVisible,
                                  final float x, final float y) {
        super(parent, x, y, 40.166668f, 16.0f);
        this.comboBoxComponent = new ComboBox2Component(this, 0.0f, 6.0f, this.getWidth(), 10.0f) {
            @Override
            public boolean isVisible() {
                return isVisible.get();
            }

            @Override
            public List<BoolValue> getValues() {
                return getValues.get();
            }
        };
        this.textComponent = new TextComponent(this, name, 1.0f, 0.0f);
        this.addChild(this.comboBoxComponent);
        this.addChild(this.textComponent);
    }

    public ComboBox2TextComponent(final Component parent, final String name, final Supplier<List<BoolValue>> getValues, final Supplier<Boolean> isVisible) {
        this(parent, name, getValues,isVisible, 0.0f, 0.0f);
    }

    @Override
    public boolean isVisible() {
        return this.comboBoxComponent.isVisible();
    }

    public ComboBox2Component getComboBoxComponent() {
        return this.comboBoxComponent;
    }

    @Override
    public float getExpandedX() {
        return this.comboBoxComponent.getExpandedX();
    }

    @Override
    public float getExpandedY() {
        return this.getY() + this.textComponent.getHeight();
    }

    @Override
    public float getExpandedWidth() {
        return this.comboBoxComponent.getExpandedWidth();
    }

    @Override
    public float getExpandedHeight() {
        return this.comboBoxComponent.getExpandedHeight();
    }

    @Override
    public void setExpanded(final boolean expanded) {
        this.comboBoxComponent.setExpanded(expanded);
    }

    @Override
    public boolean isExpanded() {
        return this.comboBoxComponent.isExpanded();
    }
}
