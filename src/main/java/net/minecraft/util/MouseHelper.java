package net.minecraft.util;

import org.lwjglx.input.Mouse;
import org.lwjglx.opengl.Display;

public class MouseHelper
{
    public int deltaX;
    public int deltaY;

    public void grabMouseCursor()
    {
        Mouse.setGrabbed(true);
        this.deltaX = 0;
        this.deltaY = 0;
    }

    public void ungrabMouseCursor()
    {
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        Mouse.setGrabbed(false);
    }

    public void mouseXYChange()
    {
        this.deltaX = Mouse.getDX();
        this.deltaY = Mouse.getDY();
    }
}
