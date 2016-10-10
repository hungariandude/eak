package hu.elte.iszraai.webshop.server;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hu.elte.iszraai.webshop.common.LogUtil;
import hu.elte.iszraai.webshop.common.WebshopServerException;
import hu.elte.iszraai.webshop.common.annotations.Column;
import hu.elte.iszraai.webshop.common.annotations.Entity;
import hu.elte.iszraai.webshop.common.annotations.Id;

public class DAO<E, I extends Serializable> {

    private static class ColumnData {
        private String  name;
        private boolean nullable;
        private int     length;
        private Field   field;

        public ColumnData(final String name, final boolean nullable, final int length, final Field field) {
            this.name = name;
            this.nullable = nullable;
            this.length = length;
            this.field = field;
        }
    }

    private Connection              connection;

    private String                  tableName;
    private Class<E>                entityClass;
    private ColumnData              idColumn;
    private List<ColumnData>        notIdColumns = new ArrayList<>();
    private List<ColumnData>        allColumns   = new ArrayList<>();
    private Map<String, ColumnData> columnMap    = new HashMap<>();

    public DAO(final ServerConfiguration config, final Class<E> entityClass) {
        this.entityClass = entityClass;

        initEntity();

        initConnection(config);

        createTableIfNotExists();
    }

    private void initEntity() {
        Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
        if (entityAnnotation == null) {
            throw new IllegalArgumentException(
                    "The type " + entityClass.getName() + " is not annotated with @" + Entity.class.getSimpleName());
        }

        if (!entityAnnotation.tableName().isEmpty()) {
            tableName = entityAnnotation.tableName();
        } else {
            tableName = entityClass.getSimpleName().toUpperCase();
        }

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                ColumnData column = getColumnDataFromField(field);
                allColumns.add(column);
                columnMap.put(field.getName(), column);

                if (field.isAnnotationPresent(Id.class)) {
                    if (idColumn != null) {
                        throw new IllegalArgumentException("The type " + entityClass.getName()
                                + " has multiple fields annotated with @" + Id.class.getSimpleName());
                    } else {
                        idColumn = column;
                    }
                } else {
                    notIdColumns.add(column);
                }
            }
        }

        if (idColumn == null) {
            throw new IllegalArgumentException(
                    "The type " + entityClass.getName() + " does not have a field annotated with @" + Id.class.getSimpleName());
        }
    }

    private ColumnData getColumnDataFromField(final Field field) {
        field.setAccessible(true);

        Column columnAnnotation = field.getAnnotation(Column.class);

        String name = !columnAnnotation.name().isEmpty() ? columnAnnotation.name() : field.getName();

        return new ColumnData(name, columnAnnotation.nullable(), columnAnnotation.length(), field);
    }

    private void initConnection(final ServerConfiguration config) {
        try {
            Class.forName(config.getJdbcDriverName()).newInstance();

            connection = DriverManager.getConnection(config.getDbUrl());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            LogUtil.error("Failed to initialize the database connection.");
            throw new RuntimeException(ex);
        }
    }

    private void createTableIfNotExists() {
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            if (!tables.next()) {
                // table does not exist
                StringBuilder createTableSqlBuilder = new StringBuilder();
                createTableSqlBuilder.append("create table ").append(tableName).append(" ( ");
                for (ColumnData column : allColumns) {
                    createTableSqlBuilder.append(getCreateTableRowByColumnData(column)).append(", ");
                }
                createTableSqlBuilder.append("primary key (").append(idColumn.name).append(") )");

                String createTableSql = createTableSqlBuilder.toString();
                LogUtil.debug(createTableSql);

                Statement statement = connection.createStatement();
                statement.executeUpdate(createTableSql);
            }
        } catch (SQLException ex) {
            LogUtil.error("Failed to create the database table: " + tableName);
            throw new RuntimeException(ex);
        }
    }

    private String getCreateTableRowByColumnData(final ColumnData column) {
        StringBuilder sb = new StringBuilder();
        sb.append(column.name).append(' ').append(getSqlTypeByJavaType(idColumn));
        if (!column.nullable) {
            sb.append(" not null");
        }
        return sb.toString();
    }

    private String getSqlTypeByJavaType(final ColumnData column) {
        Class<?> type = column.field.getType();

        // lazy implementation...
        if (Integer.class == type || Integer.TYPE == type) {
            return "integer";
        } else if (String.class == type) {
            return "varchar(" + column.length + ")";
        } else {
            throw new IllegalArgumentException("The Java type " + type.getName() + " is a not supported column type.");
        }
    }

    public E findById(final I id) throws WebshopServerException {
        try {
            String sql = "select * from " + tableName + " where " + idColumn.name + " = ?";
            LogUtil.debug(sql);

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, id);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                E entity = entityClass.newInstance();

                for (ColumnData column : allColumns) {
                    column.field.set(entity, rs.getObject(column.name));
                }

                return entity;
            } else {
                return null;
            }
        } catch (SQLException | InstantiationException | IllegalAccessException ex) {
            throw new WebshopServerException("findById failed", ex);
        }
    }

    public List<E> findAllByProperty(final String propertyName, final Object value) throws WebshopServerException {
        try {
            ColumnData columnOfProperty = columnMap.get(propertyName);
            if (columnOfProperty == null) {
                throw new WebshopServerException(
                        "The type " + entityClass.getName() + " does not have a field named '" + propertyName + "'.");
            }

            String sql = "select * from " + tableName + " where " + columnOfProperty.name + " = ?";
            LogUtil.debug(sql);

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, value);

            ResultSet rs = statement.executeQuery();

            List<E> entities = new LinkedList<>();

            while (rs.next()) {
                E entity = entityClass.newInstance();

                for (ColumnData column : allColumns) {
                    column.field.set(entity, rs.getObject(column.name));
                }

                entities.add(entity);
            }

            return entities;
        } catch (SQLException | InstantiationException | IllegalAccessException | SecurityException ex) {
            throw new WebshopServerException("findAllByProperty failed", ex);
        }
    }

    public int insert(final E entity) throws WebshopServerException {
        try {
            StringBuilder sqlBuilder = new StringBuilder("insert into ");
            sqlBuilder.append(tableName).append(" ( ");
            StringBuilder paramsStrBuilder = new StringBuilder();
            for (ColumnData column : allColumns) {
                sqlBuilder.append(column.name).append(", ");
                paramsStrBuilder.append("?, ");
            }

            sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
            paramsStrBuilder.delete(paramsStrBuilder.length() - 2, paramsStrBuilder.length());

            sqlBuilder.append(" ) values ( ").append(paramsStrBuilder).append(" )");

            String sql = sqlBuilder.toString();
            LogUtil.debug(sql);

            PreparedStatement statement = connection.prepareStatement(sql);

            for (int i = 0; i < allColumns.size(); ++i) {
                Object fieldValue = allColumns.get(i).field.get(entity);
                statement.setObject(i + 1, fieldValue);
            }

            return statement.executeUpdate();
        } catch (SQLException | IllegalArgumentException | IllegalAccessException ex) {
            throw new WebshopServerException("insert failed", ex);
        }
    }

    public int update(final E entity) throws WebshopServerException {
        try {
            StringBuilder sqlBuilder = new StringBuilder("update ");
            sqlBuilder.append(tableName).append(" set ");
            for (ColumnData column : notIdColumns) {
                sqlBuilder.append(column.name).append(" = ?, ");
            }

            sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());

            sqlBuilder.append(" where ").append(idColumn.name).append(" = ?");

            String sql = sqlBuilder.toString();
            LogUtil.debug(sql);

            PreparedStatement statement = connection.prepareStatement(sql);

            for (int i = 0; i < notIdColumns.size(); ++i) {
                Object fieldValue = notIdColumns.get(i).field.get(entity);
                statement.setObject(i + 1, fieldValue);
            }
            statement.setObject(allColumns.size(), idColumn.field.get(entity));

            return statement.executeUpdate();
        } catch (SQLException | IllegalArgumentException | IllegalAccessException ex) {
            throw new WebshopServerException("update failed", ex);
        }
    }

    public int deleteById(final I id) throws WebshopServerException {
        try {
            String sql = "delete from " + tableName + " where " + idColumn.name + " = ?";
            LogUtil.debug(sql);

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, id);

            return statement.executeUpdate();
        } catch (SQLException ex) {
            throw new WebshopServerException("deleteById failed", ex);
        }
    }

}
