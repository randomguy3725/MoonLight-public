package net.optifine.util;

import java.util.Comparator;
import org.lwjglx.opengl.DisplayMode;

public class DisplayModeComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        DisplayMode displaymode = (DisplayMode)o1;
        DisplayMode displaymode1 = (DisplayMode)o2;
        return displaymode.getWidth() != displaymode1.getWidth() ? displaymode.getWidth() - displaymode1.getWidth() : (displaymode.getHeight() != displaymode1.getHeight() ? displaymode.getHeight() - displaymode1.getHeight() : (displaymode.getBitsPerPixel() != displaymode1.getBitsPerPixel() ? displaymode.getBitsPerPixel() - displaymode1.getBitsPerPixel() : (displaymode.getFrequency() != displaymode1.getFrequency() ? displaymode.getFrequency() - displaymode1.getFrequency() : 0)));
    }
}
