package com.example.backend.converter;

import com.example.backend.enums.NovelStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NovelStatusConverter implements AttributeConverter<NovelStatus, String> {

    @Override
    public String convertToDatabaseColumn(NovelStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getStatus();
    }

    @Override
    public NovelStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return NovelStatus.fromValue(dbData);
    }
}
