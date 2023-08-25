package by.sakujj.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@UtilityClass
public class PropertiesUtil {

    public static Properties newProperties(String filename) {
        try(InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename)){
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}