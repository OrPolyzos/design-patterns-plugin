package ore.plugins.idea.lib.provider;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public interface TemplateProvider {

    default String provideTemplateContent(String templatePath) {
        InputStream inputStream = TemplateProvider.class.getResourceAsStream(templatePath);
        String template;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            template = scanner.useDelimiter("\\A").next();
        }
        return template.replaceAll("\r\n", "\n");
    }

    default String extractTemplateReplacementValue(String value) {
        return "%<".concat(value).concat(">");
    }
}
