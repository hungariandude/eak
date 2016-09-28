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

}
