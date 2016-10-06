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
import webshop.common.WebshopServerException;

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

    public Item findItemById(final int id) throws WebshopServerException {
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
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new WebshopServerException("findItemById failed", ex);
        }
    }

    public List<Item> findItemsByName(final String name) throws WebshopServerException {
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
            throw new WebshopServerException("findItemById failed", ex);
        }
    }

    public int insertItem(final Item item) throws WebshopServerException {
        try {
            PreparedStatement statement = connection.prepareStatement("insert into Item (id, name, price) values (?, ?, ?)");
            statement.setInt(1, item.getId());
            statement.setString(2, item.getName());
            statement.setInt(3, item.getPrice());

            return statement.executeUpdate();
        } catch (SQLException ex) {
            throw new WebshopServerException("insertItem failed", ex);
        }
    }

    public int updateItem(final Item item) throws WebshopServerException {
        try {
            PreparedStatement statement = connection.prepareStatement("update Item set name = ?, price = ? where id = ?");
            statement.setString(1, item.getName());
            statement.setInt(2, item.getPrice());
            statement.setInt(3, item.getId());

            return statement.executeUpdate();
        } catch (SQLException ex) {
            throw new WebshopServerException("updateItem failed", ex);
        }
    }

    public int deleteItemById(final int id) throws WebshopServerException {
        try {
            PreparedStatement statement = connection.prepareStatement("delete from Item where id = ?");
            statement.setInt(1, id);

            return statement.executeUpdate();
        } catch (SQLException ex) {
            throw new WebshopServerException("updateItem failed", ex);
        }
    }

}
