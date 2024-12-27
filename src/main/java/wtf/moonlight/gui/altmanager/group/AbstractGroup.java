package wtf.moonlight.gui.altmanager.group;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.moonlight.gui.font.FontRenderer;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.RenderUtils;

public abstract class AbstractGroup extends Gui {

    protected final FontRenderer titleFontRenderer;
    protected final String title;
    protected boolean hidden;

    protected int xPosition;
    protected final int yPosition;
    protected final int width;
    protected final int height;

    protected AbstractGroup(@Nullable String title,
                            int xPosition,
                            int yPosition,
                            int width,
                            int height,
                            @NotNull FontRenderer titleFontRenderer) {
        this.title = title != null && !(title = title.trim()).isEmpty() ? title : null;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.titleFontRenderer = titleFontRenderer;
    }

    protected AbstractGroup(String title,
                            int xPosition,
                            int yPosition,
                            int width,
                            int height) {
        this(title, xPosition, yPosition, width, height, Fonts.interSemiBold.get(35));
    }

    public void drawGroup(Minecraft mc, int mouseX, int mouseY) {
        if (this.hidden) return;

        RenderUtils.drawRect(this.xPosition, this.yPosition, this.width, this.height, 0xD2FFFFFF);

        if (this.title != null) {
            this.titleFontRenderer.drawString(this.title,
                    this.xPosition + (this.width - this.titleFontRenderer.getStringWidth(this.title)) / 2.0F,
                    this.yPosition + 4,
                    0);
        }
    }

}
