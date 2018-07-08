package com.design.patterns.util;

public class FormatUtils {

    public static String toUpperCaseFirstLetterString(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static String toLowerCaseFirstLetterString(String word) {
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }

    public static String camelCaseToUpperCaseWithUnderScore(String word) {
        if (word.length() == 0) return word;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(word.charAt(0)).toUpperCase());
        for (int i = 1; i < word.length(); i++) {
            if (Character.isUpperCase(word.charAt(i))) {
                stringBuilder.append("_");
            }
            stringBuilder.append(String.valueOf(word.charAt(i)).toUpperCase());
        }
        return stringBuilder.toString();
    }
}
