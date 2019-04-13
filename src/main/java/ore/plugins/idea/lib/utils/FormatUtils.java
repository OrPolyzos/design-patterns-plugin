package ore.plugins.idea.lib.utils;

public class FormatUtils {

    public static String toFirstLetterUpperCase(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static String toFirstLetterLowerCase(String word) {
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

    public static String toPlural(String singular) {
        String consonants = "bcdfghjklmnpqrstvwxz";

        switch (singular) {
            case "Person":
                return "People";
            case "Trash":
                return "Trash";
            case "Life":
                return "Lives";
            case "Man":
                return "Men";
            case "Woman":
                return "Women";
            case "Child":
                return "Children";
            case "Foot":
                return "Feet";
            case "Tooth":
                return "Teeth";
            case "Dozen":
                return "Dozen";
            case "Hundred":
                return "Hundred";
            case "Thousand":
                return "Thousand";
            case "Million":
                return "Million";
            case "Datum":
                return "Data";
            case "Criterion":
                return "Criteria";
            case "Analysis":
                return "Analyses";
            case "Fungus":
                return "Fungi";
            case "Index":
                return "Indices";
            case "Matrix":
                return "Matrices";
            case "Settings":
                return "Settings";
            case "UserSettings":
                return "UserSettings";
            default:
                if (consonants.contains(String.valueOf(singular.charAt(singular.length() - 2)))) {
                    // Handle ending with "o" (if preceeded by a consonant, end with -es, otherwise -s: Potatoes and Radios)
                    if (singular.endsWith("o")) {
                        return singular + "es";
                    }
                    // Handle ending with "y" (if preceeded by a consonant, end with -ies, otherwise -s: Companies and Trays)
                    if (singular.endsWith("y")) {
                        return singular.substring(0, singular.length() - 1) + "ies";
                    }
                }

                // Ends with a whistling sound: boxes, buzzes, churches, passes
                if (singular.endsWith("s") || singular.endsWith("sh") || singular.endsWith("ch") || singular.endsWith("x") || singular.endsWith("z")) {
                    return singular + "es";
                }
                return singular + "s";
        }
    }
}
