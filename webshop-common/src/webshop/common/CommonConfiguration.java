package webshop.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommonConfiguration {

    protected final Properties properties;

    public CommonConfiguration(final String propertiesFilePath) {
        try (InputStream is = getClass().getResourceAsStream(propertiesFilePath)) {
            properties = new Properties();
            properties.load(is);
        } catch (IOException ex) {
            LogUtil.error("Failed to load the configuration.");
            throw new RuntimeException(ex);
        }
    }

    public String getServerPort() {
        return properties.getProperty("server.port");
    }

}