package org.kurodev.rp.db.util;

import java.awt.*;
import java.util.Optional;

public enum ColorPalette {
    // Reds
    RED(new Color(255, 0, 0)),
    LIGHT_RED(new Color(255, 102, 102)),
    DARK_RED(new Color(139, 0, 0)),
    CRIMSON(new Color(220, 20, 60)),
    SALMON(new Color(250, 128, 114)),

    // Oranges
    ORANGE(new Color(255, 165, 0)),
    LIGHT_ORANGE(new Color(255, 200, 102)),
    DARK_ORANGE(new Color(204, 102, 0)),
    CORAL(new Color(255, 127, 80)),
    AMBER(new Color(255, 191, 0)),

    // Yellows
    YELLOW(new Color(255, 255, 0)),
    LIGHT_YELLOW(new Color(255, 255, 153)),
    DARK_YELLOW(new Color(204, 204, 0)),
    GOLD(new Color(255, 215, 0)),
    KHAKI(new Color(240, 230, 140)),

    // Greens
    GREEN(new Color(0, 128, 0)),
    LIGHT_GREEN(new Color(144, 238, 144)),
    DARK_GREEN(new Color(0, 100, 0)),
    LIME(new Color(0, 255, 0)),
    OLIVE(new Color(107, 142, 35)),

    // Cyans / Teals
    CYAN(new Color(0, 255, 255)),
    LIGHT_CYAN(new Color(224, 255, 255)),
    DARK_CYAN(new Color(0, 139, 139)),
    TEAL(new Color(0, 128, 128)),
    AQUAMARINE(new Color(127, 255, 212)),

    // Blues
    BLUE(new Color(0, 0, 255)),
    LIGHT_BLUE(new Color(173, 216, 230)),
    DARK_BLUE(new Color(0, 0, 139)),
    SKY_BLUE(new Color(135, 206, 235)),
    NAVY(new Color(0, 0, 128)),

    // Purples / Violets
    PURPLE(new Color(128, 0, 128)),
    LIGHT_PURPLE(new Color(216, 191, 216)),
    DARK_PURPLE(new Color(75, 0, 130)),
    VIOLET(new Color(238, 130, 238)),
    MAGENTA(new Color(255, 0, 255)),

    // Pinks
    PINK(new Color(248, 158, 212)),
    LIGHT_PINK(new Color(255, 182, 193)),
    HOT_PINK(new Color(255, 105, 180)),
    DEEP_PINK(new Color(255, 20, 147)),
    FUCHSIA(new Color(255, 0, 255)),

    // Browns / Neutrals
    BROWN(new Color(139, 69, 19)),
    LIGHT_BROWN(new Color(210, 180, 140)),
    DARK_BROWN(new Color(92, 51, 23)),
    BEIGE(new Color(245, 245, 220)),
    TAN(new Color(210, 180, 140)),

    // Grays
    LIGHT_GRAY(new Color(211, 211, 211)),
    GRAY(new Color(128, 128, 128)),
    DARK_GRAY(new Color(64, 64, 64)),

    // Extremes
    WHITE(new Color(255, 255, 255)),
    BLACK(new Color(0, 0, 0));

    private final Color awtColor;

    ColorPalette(Color awtColor) {
        this.awtColor = awtColor;
    }

    public Color getColor() {
        return awtColor;
    }

    public String toHex() {
        return String.format("#%02x%02x%02x", awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
    }

    public Optional<ColorPalette> find(String name) {
        name = name.toUpperCase();
        ColorPalette[] values = values();
        for (ColorPalette value : values) {
            if (value.name().equals(name)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}

