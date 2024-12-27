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
package wtf.moonlight.gui.click.skeet.component;

import org.lwjglx.input.Mouse;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.gui.click.skeet.SkeetUI;
import wtf.moonlight.gui.click.skeet.component.impl.GroupBoxComponent;
import wtf.moonlight.gui.click.skeet.LockedResolution;
import wtf.moonlight.utils.animations.Translate;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;

public abstract class TabComponent extends Component
{
    private final String name;
    public float highest = 0;
    public float scrollY = 0;
    public float scrollAni = 0;
    public float minY = -100;
    public float barHeight = 20;

    public TabComponent(final Component parent, final String name, final float x, final float y, final float width, final float height) {
        super(parent, x, y, width, height);
        this.setupChildren();
        this.name = name;
    }
    
    public abstract void setupChildren();
    
    /*@Override
    public void drawComponent(final LockedResolution resolution, final int mouseX, final int mouseY) {
        SkeetUI.FONT_RENDERER.drawString(this.name, this.getX() + 8.0f, this.getY() + 8.0f - 3.0f, SkeetUI.getSkeetColor(16777215), true);
        float x = 8.0f;

        final float borderX = this.getX();
        final float borderY = this.getY();
        float left = borderX + 3.5f;
        float top = borderY + 3.5f;

        for (int i = 0; i < this.children.size(); ++i) {
            final Component child = this.children.get(i);
            child.setX(x);
            if (i < 3) {
                child.setY(14.0f);
            }
            child.drawComponent(resolution, mouseX, mouseY);
            x += 102.333336f;
            if (x + 8.0f + 94.333336f > 315.0f) {
                x = 8.0f;
            }
            if (i > 2) {
                int above = i - 3;
                int totalY = 14;
                do {
                    final Component componentAbove = this.getChildren().get(above);
                    totalY += (int)(componentAbove.getHeight() + 8.0f);
                    above -= 3;
                } while (above >= 0);
                child.setY(totalY + this.scrollAni);
            }
        }

        /*for (ModuleCategory category : ModuleCategory.values()) {
            if (!(category == ModuleCategory.Config)) {
                float xOff = 100.0f - 2.5f + this.getX() - 35;
                float yOff = 322.0f + this.getY() - 20;
                float textX = 100.0f - 2.5f;
                float textY = 322.0f;
                RenderingUtils.rectangleBordered(xOff, yOff - 6.0f, xOff + 295.0f, yOff + 33.0f, 0.5, Colors.getColor(0, 0), Colors.getColor(10, 255));
                RenderingUtils.rectangleBordered((double) xOff + 0.5, (double) yOff - 5.5, (double) (xOff + 295.0f) - 0.5, (double) (yOff + 33.0f) - 0.5, 0.5, Colors.getColor(0, 0), Colors.getColor(48, 255));
                RenderingUtils.rectangle(xOff + 1.0f, yOff - 5.0f, xOff + 294.0f, yOff + 33.0f - 1.0f, Colors.getColor(17, 255));
                RenderingUtils.rectangle(xOff + 5.0f, yOff - 6.0f, xOff + SkeetUI.fs.getWidth("No Settings") + 5.0f, yOff - 4.0f, Colors.getColor(17, 255));
                SkeetUI.fs.drawBlackString("No Settings", textX + left - 34, textY + top - 32.0f, Colors.getColor(220, 255));
            }
        }*/
    //}*/

    @Override
    public void drawComponent(LockedResolution resolution, int mouseX, int mouseY) {
        SkeetUI.FONT_RENDERER.drawString(this.name, this.getX() + 8.0f, this.getY() + 8.0f - 3.0f, SkeetUI.getSkeetColor(16777215), true);
        float x = 8.0f;

        if(MouseUtils.isHovered2(this.getX() + 2, this.getY() + 11, this.getWidth() - 4, this.getHeight() - 13,mouseX, mouseY)) {
            minY = getHeight() - 24;
        }

        for (int i = 0; i < this.children.size(); ++i) {
            final Component child = this.children.get(i);
            child.setX(x);
            if (i < 3) {
                child.setY(14.0f + this.scrollAni);

            }
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtils.scissor((int) this.getX() + 2, (int) this.getY() + 11, (int) this.getWidth() - 4, (int) this.getHeight() - 13);
            child.drawComponent(resolution, mouseX, mouseY);

            x += 102.333336f;
            if (x + 8.0f + 94.333336f > 315.0f) {
                x = 8.0f;
            }
            if (i > 2) {
                int above = i - 3;
                float totalY = 14;

                do {
                    Component componentAbove = this.getChildren().get(above);
                    totalY = totalY + (componentAbove.getHeight() + 8.0f);
                } while ((above -= 3) >= 0);

                if (totalY > this.highest) {
                    this.highest = totalY;
                }

                child.setY(totalY + this.scrollAni);
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        if(MouseUtils.isHovered2(this.getX() + 2, this.getY() + 11, this.getWidth() - 4, this.getHeight() - 13, mouseX, mouseY)) {
            minY -= this.highest;
        }

        if(this.highest > this.getHeight() - 13) {
            if(!MouseUtils.isHovered2(this.getX() + 2, this.getY() + 11, this.getWidth() - 4, this.getHeight() - 13, mouseX, mouseY)) {
                Mouse.getDWheel(); //刷新滚轮
            }
            this.scrollAni = Translate.smoothAnimation(this.scrollAni, scrollY, 50, 0.3f);
        }
    }


    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        for (Component groupBox : getChildren()) {
            for (Component child : groupBox.getChildren()) {
                if (child instanceof ExpandableComponent) {
                    ExpandableComponent expandable = (ExpandableComponent) child;

                    if (expandable.isExpanded()) {
                        final float x = expandable.getExpandedX();
                        final float y = expandable.getExpandedY();
                        if (mouseX >= x && mouseY > y && mouseX <= x + expandable.getExpandedWidth() && mouseY < y + expandable.getExpandedHeight()) {
                            child.onMouseClick(mouseX, mouseY, button);
                            return;
                        }
                        //Close other expanded tabs
                        //expandable.setExpanded(false);
                    }
                }
            }
        }

        for (Component child : getChildren()) {
            if (child.isHovered(mouseX, mouseY)) {
                child.onMouseClick(mouseX, mouseY, button);
                return;
            }
        }


        super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY) {
        for (Component child : getChildren()) {
            if (child instanceof GroupBoxComponent) {
                final GroupBoxComponent groupBox = (GroupBoxComponent) child;

                if (groupBox.isHoveredEntire(mouseX, mouseY)) {
                    return true;
                }
            }
        }

        return super.isHovered(mouseX, mouseY);
    }

    @Override
    public void onMouseScroll(int mouseX, int mouseY, int value) {
        if(MouseUtils.isHovered2(this.getX() + 2, this.getY() + 11, this.getWidth() - 4, this.getHeight() - 13, mouseX, mouseY)) {
            scrollY += value / 6f;
            if (scrollY <= minY)
                scrollY = minY;
            if (scrollY >= 0f)
                scrollY = 0f;
        }

        for (Component child : this.children) {
            child.onMouseScroll(mouseX, mouseY, value);
        }
    }
}
