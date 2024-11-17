package wtf.moonlight.gui.font;

import java.awt.*;

@FunctionalInterface
public interface GradientApplier {
    Color colour(int i);
}
