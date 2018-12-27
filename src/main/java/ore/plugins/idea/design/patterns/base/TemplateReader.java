package ore.plugins.idea.design.patterns.base;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public interface TemplateReader {

    default String getTemplate(String templatePath) {
        InputStream inputStream = TemplateReader.class.getResourceAsStream(templatePath);
        String template;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            template = scanner.useDelimiter("\\A").next();
        }
        return template.replaceAll("\r\n", "\n");
    }
}
