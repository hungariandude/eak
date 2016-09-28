package webshop.server;

import webshop.common.CommonConfiguration;

public class ServerConfiguration extends CommonConfiguration {

    public ServerConfiguration() {
        super("server.properties");
    }

    public String getJdbcDriverName() {
        return properties.getProperty("jdbc.driver.name");
    }

    public String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public String getDbUsername() {
        return properties.getProperty("db.username");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password");
    }

}
