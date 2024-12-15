package wtf.moonlight.gui.font;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import wtf.moonlight.MoonLight;

import java.awt.*;
import java.io.InputStream;

public enum Fonts {
    interBold("inter/Inter_Bold"),
    interMedium("inter/Inter_Medium"),
    interRegular("inter/Inter_Regular"),
    interSemiBold("inter/Inter_SemiBold"),
    psRegular("product-sans/Regular"),
    psBold("product-sans/Bold"),
    nursultan("others/Nursultan"),
    Tahoma("others/Exhi"),
    skeet("others/skeet"),
    noti("others/noti"),
    noti2("others/noti2");
    private final String file;
    private final Float2ObjectMap<FontRenderer> fontMap = new Float2ObjectArrayMap<>();

    public FontRenderer get(float size) {
        return this.fontMap.computeIfAbsent(size, font -> {
            try {
                return create(this.file, size,true);
            } catch (Exception var5) {
                throw new RuntimeException("Unable to load font: " + this, var5);
            }
        });
    }

    public FontRenderer get(float size,boolean antiAlias) {
        return this.fontMap.computeIfAbsent(size, font -> {
            try {
                return create(this.file, size,antiAlias);
            } catch (Exception var5) {
                throw new RuntimeException("Unable to load font: " + this, var5);
            }
        });
    }

    public FontRenderer create(String file, float size,boolean antiAlias) {
        Font font = null;

        try {
            InputStream in = Preconditions.checkNotNull(
                    MoonLight.class.getResourceAsStream("/assets/minecraft/" + MoonLight.INSTANCE.getClientName().toLowerCase() + "/font/" + file + ".ttf"), "Font resource is null"
            );
            font = Font.createFont(0, in).deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create font", ex);
        }
        if (font != null) {
            return new FontRenderer(font,antiAlias);
        } else {
            throw new RuntimeException("Failed to create font");
        }
    }

    Fonts(String file) {
        this.file = file;
    }
}
