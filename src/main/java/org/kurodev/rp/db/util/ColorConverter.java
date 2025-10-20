package org.kurodev.rp.db.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.awt.Color;

@Converter(autoApply = true)
public class ColorConverter implements AttributeConverter<Color, String> {

    @Override
    public String convertToDatabaseColumn(Color color) {
        if (color == null) return null;
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public Color convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        return Color.decode(dbData);
    }
}
