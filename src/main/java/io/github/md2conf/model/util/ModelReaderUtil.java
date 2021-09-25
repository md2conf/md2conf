package io.github.md2conf.model.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.md2conf.model.ConfluenceContent;

import java.io.File;
import java.io.IOException;

public class ModelReaderUtil {

    static ObjectMapper jsonObjectMapper = new ObjectMapper(new JsonFactory());
    static ObjectMapper yamlObjectMapper = new ObjectMapper(new YAMLFactory());

    static {
        jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        yamlObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ConfluenceContent readFromYamlOrJson(File file) throws IOException {
            if (file.getName().endsWith(".yaml")) {
                return yamlObjectMapper.readValue(file, ConfluenceContent.class);
            } else {
                return jsonObjectMapper.readValue(file, ConfluenceContent.class);
            }
    }
}
