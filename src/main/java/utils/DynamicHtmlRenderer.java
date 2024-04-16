package utils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicHtmlRenderer {

    private static final Logger log = LoggerFactory.getLogger(DynamicHtmlRenderer.class);
    private static final Handlebars handlebars = new Handlebars(
        new ClassPathTemplateLoader("/templates", ".html"));

    public static String renderUserList(String templatePath, Collection<User> collection)
        throws IOException {
        Template template = handlebars.compile(templatePath);
        Map<String, Object> model = new HashMap<>();
        model.put("users", collection);
        return template.apply(model);
    }
}