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
package wtf.moonlight.gui.click.skeet.component;

import wtf.moonlight.gui.click.skeet.LockedResolution;

import java.util.ArrayList;
import java.util.List;

public class Component
{
    protected final List<Component> children = new ArrayList<>();
    private final Component parent;
    private float x;
    private float y;
    private float width;
    private float height;
    
    public Component(final Component parent, final float x, final float y, final float width, final float height) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Component getParent() {
        return this.parent;
    }
    
    public void addChild(final Component child) {
        this.children.add(child);
    }
    
    public void drawComponent(final LockedResolution lockedResolution, final int mouseX, final int mouseY) {
        for (final Component child : this.children) {
            child.drawComponent(lockedResolution, mouseX, mouseY);
        }
    }
    
    public void onMouseClick(final int mouseX, final int mouseY, final int button) {
        for (final Component child : this.children) {
            child.onMouseClick(mouseX, mouseY, button);
        }
    }
    
    public void onMouseRelease(final int button) {
        for (final Component child : this.children) {
            child.onMouseRelease(button);
        }
    }
    
    public void onKeyPress(final int keyCode) {
        for (final Component child : this.children) {
            child.onKeyPress(keyCode);
        }
    }

    public float getX() {
        Component familyMember = this.parent;
        float familyTreeX = this.x;
        while (familyMember != null) {
            familyTreeX += familyMember.x;
            familyMember = familyMember.parent;
        }
        return familyTreeX;
    }
    
    public void setX(final float x) {
        this.x = x;
    }
    
    public boolean isHovered(final int mouseX, final int mouseY) {
        final float x;
        final float y;
        return mouseX >= (x = this.getX()) && mouseY >= (y = this.getY()) && mouseX <= x + this.getWidth() && mouseY <= y + this.getHeight();
    }
    
    public float getY() {
        Component familyMember = this.parent;
        float familyTreeY = this.y;
        while (familyMember != null) {
            familyTreeY += familyMember.y;
            familyMember = familyMember.parent;
        }
        return familyTreeY;
    }
    
    public void setY(final float y) {
        this.y = y;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public void setWidth(final float width) {
        this.width = width;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public void setHeight(final float height) {
        this.height = height;
    }
    
    public List<Component> getChildren() {
        return this.children;
    }

    public void keyTyped(char typed, int keyCode) {
        for (final Component child : this.children) {
            child.keyTyped(typed,keyCode);
        }
    }

    public void onMouseScroll(int mouseX, int mouseY, int value) {
        for (Component child : this.children) {
            child.onMouseScroll(mouseX, mouseY, value);
        }
    }
}
