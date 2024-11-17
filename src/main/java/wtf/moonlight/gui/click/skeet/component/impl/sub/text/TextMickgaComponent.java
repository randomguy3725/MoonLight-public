package wtf.moonlight.gui.click.skeet.component.impl.sub.text;

import wtf.moonlight.gui.click.skeet.SkeetUI;
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.LockedResolution;
import wtf.moonlight.gui.font.FontRenderer;

public class TextMickgaComponent extends Component
{
    private static final FontRenderer FONT_RENDERER = SkeetUI.FONT_RENDERER;
    private final String text;

    public TextMickgaComponent(final Component parent, final String text, final float x, final float y) {
        super(parent, x, y, FONT_RENDERER.getStringWidth(text), TextMickgaComponent.FONT_RENDERER.getHeight());
        this.text = text;
    }

    @Override
    public void drawComponent(final LockedResolution resolution, final int mouseX, final int mouseY) {
        if (SkeetUI.shouldRenderText()) {
            FONT_RENDERER.drawString(this.text, this.getX(), this.getY() + 0.42f, SkeetUI.getSkeetColor(0xE6E6E6));
        }
    }
}
