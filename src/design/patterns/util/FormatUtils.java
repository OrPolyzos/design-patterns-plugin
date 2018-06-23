package design.patterns.util;

public class FormatUtils {

    public static String toUpperCaseFirstLetterString(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static String toLowerCaseFirstLetterString(String word) {
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }
}
