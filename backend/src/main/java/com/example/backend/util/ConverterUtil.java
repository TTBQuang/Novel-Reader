package com.example.backend.util;

import com.example.backend.enums.NovelStatus;

import java.util.*;
import java.util.stream.Collectors;

public class ConverterUtil {
    public static Set<Long> convertStringToSet(String input) {
        String[] stringArray = input.split(",");
        Set<Long> longSet = new HashSet<>();

        for (String str : stringArray) {
            longSet.add(Long.parseLong(str.trim()));
        }

        return longSet;
    }

    public static Set<NovelStatus> convertStringToEnumSet(String input) {
        String[] statusValues = input.split(",");

        return Arrays.stream(statusValues)
                .map(NovelStatus::valueOf)
                .collect(Collectors.toSet());
    }
}
