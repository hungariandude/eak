package webshop.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import webshop.common.Item;
import webshop.common.LogUtil;

public class DAO {

    private Connection connection;

    public DAO(final ServerConfiguration config) {
        try {
            Class.forName(config.getJdbcDriverName()).newInstance();

            connection = DriverManager.getConnection(config.getDbUrl());
        } catch (Exception ex) {
            LogUtil.error("Failed to initialize the database conection.");
            throw new RuntimeException(ex);
        }
    }

    public Item findItemById(final int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("select name, price from Item where id = ?");
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                Item item = new Item();

                item.setId(id);
                item.setName(rs.getString("name"));
                item.setPrice(rs.getInt("price"));

                return item;
            }
        } catch (SQLException ex) {
            LogUtil.error("findItemById failed", ex);
        }

        return null;
    }

    public List<Item> findItemsByName(final String name) {
        try {
            PreparedStatement statement = connection.prepareStatement("select * from Item where lower(name) = lower(?)");
            statement.setString(1, name);

            ResultSet rs = statement.executeQuery();

            List<Item> items = new LinkedList<>();

            while (rs.next()) {
                Item item = new Item();

                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getInt("price"));

                items.add(item);
            }

            return items;
        } catch (SQLException ex) {
            LogUtil.error("findItemsByName failed", ex);
        }

        return null;
    }

}
