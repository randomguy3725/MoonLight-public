package wtf.moonlight.gui.click.skeet.component.impl.sub.comboBox;

import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.ExpandableComponent;
import wtf.moonlight.gui.click.skeet.component.PredicateComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.text.TextComponent;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ComboBoxTextComponent extends Component implements PredicateComponent, ExpandableComponent
{
    private final ComboBoxComponent comboBoxComponent;
    private final TextComponent textComponent;

    public ComboBoxTextComponent(final Component parent, final String name, final Supplier<String[]> getValues, final Consumer<Integer> setValueByIndex,
                                 final Supplier<String> getValue, final Supplier<Boolean> isVisible,
                                 final float x, final float y) {
        super(parent, x, y, 40.166668f, 16.0f);
        this.comboBoxComponent = new ComboBoxComponent(this, 0.0f, 6.0f, this.getWidth(), 10.0f) {
            @Override
            public boolean isVisible() {
                return isVisible.get();
            }
            
            @Override
            public String getValue() {
                return getValue.get();
            }
            
            @Override
            public void setValue(final int index) {
                setValueByIndex.accept(index);
            }
            
            @Override
            public String[] getValues() {
                return getValues.get();
            }
        };
        this.textComponent = new TextComponent(this, name, 1.0f, 0.0f);
        this.addChild(this.comboBoxComponent);
        this.addChild(this.textComponent);
    }
    
    public ComboBoxTextComponent(final Component parent, final String name, final Supplier<String[]> getValues, final Consumer<Integer> setValueByIndex, final Supplier<String> getValue,  final Supplier<Boolean> isVisible) {
        this(parent, name, getValues, setValueByIndex, getValue, isVisible, 0.0f, 0.0f);
    }
    
    @Override
    public boolean isVisible() {
        return this.comboBoxComponent.isVisible();
    }
    
    public ComboBoxComponent getComboBoxComponent() {
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
