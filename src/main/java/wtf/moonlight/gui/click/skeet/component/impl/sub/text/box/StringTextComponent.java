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
package wtf.moonlight.gui.click.skeet.component.impl.sub.text.box;

import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.PredicateComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.text.TextComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class StringTextComponent extends Component implements PredicateComponent {
    private final StringComponent stringComponent;

    public StringTextComponent(Component parent, String text, final Supplier<String> getValue, final Consumer<String> setValue, final Supplier<Boolean> isVisible, float x, float y) {
        super(parent, x, y, 41.0f, 16.0f);
        this.stringComponent = new StringComponent(this, -1.0f, 6.0f, this.getWidth(), 8.5f){

            @Override
            public void onPress(int key) {
            }

            @Override
            public String getValue() {
                return getValue.get();
            }

            @Override
            public void setValue(String value) {
                setValue.accept(value);
            }

            @Override
            public boolean isVisible() {
                return isVisible.get();
            }
        };
        this.addChild(this.stringComponent);
        this.addChild(new TextComponent(this, text, 1.0f, 0.0f));
    }

    public StringTextComponent(Component parent, String text, Supplier<String> getValue, Consumer<String> setValue, Supplier<Boolean> isVisible) {
        this(parent, text, getValue, setValue, isVisible, 0.0f, 0.0f);
    }

    public StringTextComponent(Component parent, String text, Supplier<String> getValue, Consumer<String> setValue) {
        this(parent, text, getValue, setValue, () -> true);
    }

    @Override
    public float getHeight() {
        return 6.0f + super.getHeight();
    }

    @Override
    public boolean isVisible() {
        return this.stringComponent.isVisible();
    }
}

