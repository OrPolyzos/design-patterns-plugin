package ore.plugins.idea.design.patterns.base;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import static java.nio.charset.StandardCharsets.UTF_8;

public interface TemplateReader {

    default String getTemplate(String templatePath) {
        InputStream inputStream = TemplateReader.class.getResourceAsStream(templatePath);
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(inputStream, writer, UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString().replaceAll("\r\n", "\n");
    }
}
