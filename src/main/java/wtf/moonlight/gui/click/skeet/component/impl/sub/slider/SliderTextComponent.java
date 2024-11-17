package wtf.moonlight.gui.click.skeet.component.impl.sub.slider;

import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.PredicateComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.text.TextComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SliderTextComponent extends Component implements PredicateComponent
{
    private final SliderComponent sliderComponent;
    
    public SliderTextComponent(final Component parent, final String text, final Supplier<Float> getValue, final Consumer<Float> setValue, final Supplier<Float> getMin, final Supplier<Float> getMax, final Supplier<Float> getIncrement, final Supplier<Boolean> isVisible, final float x, final float y) {
        super(parent, x, y, 40.166668f, 4.0f);
        this.addChild(this.sliderComponent = new SliderComponent(this, 0.0f, 6.0f, this.getWidth(), 4.0f) {
            @Override
            public float getValue() {
                return getValue.get();
            }
            
            @Override
            public void setValue(final float value) {
                setValue.accept(value);
            }
            
            @Override
            public float getMin() {
                return getMin.get();
            }
            
            @Override
            public float getMax() {
                return getMax.get();
            }
            
            @Override
            public float getIncrement() {
                return getIncrement.get();
            }
            
            @Override
            public boolean isVisible() {
                return isVisible.get();
            }
        });
        this.addChild(new TextComponent(this, text, 1.0f, 0.0f));
    }
    
    public SliderTextComponent(final Component parent, final String text, final Supplier<Float> getValue, final Consumer<Float> setValue, final Supplier<Float> getMin, final Supplier<Float> getMax, final Supplier<Float> getIncrement, final Supplier<Boolean> isVisible) {
        this(parent, text, getValue, setValue, getMin, getMax, getIncrement, isVisible, 0.0f, 0.0f);
    }
    
    @Override
    public float getHeight() {
        return 6.0f + super.getHeight();
    }
    
    @Override
    public boolean isVisible() {
        return this.sliderComponent.isVisible();
    }
}
