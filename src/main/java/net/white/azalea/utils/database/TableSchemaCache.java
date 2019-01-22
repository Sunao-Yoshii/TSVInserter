package net.white.azalea.utils.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Read and cache table schema information.
 *
 * @author S.Yoshii
 */
public class TableSchemaCache {

    /**
     * Singleton instance.
     */
    private static final TableSchemaCache $_INSTANCE = new TableSchemaCache();

    /**
     * Get singleton object.
     *
     * @return TableSchemaCache class instance.
     */
    public static TableSchemaCache getInstance() {
        return $_INSTANCE;
    }

    /**
     * Table list.
     */
    private final List<TableDefinition> tableDefinitions;

    public TableSchemaCache() {
        this.tableDefinitions = new ArrayList<>();
    }

    /**
     * Find or load table definition.
     *
     * @param connection Database connection for search schema.
     * @param schemaName Database schema name.
     * @param tableName  Table name that searching for.
     * @return TableDefinition instance if it find.
     * @throws SQLException throw if cannot read schemas.
     */
    public TableDefinition getTableDefinition(Connection connection, String schemaName, String tableName) throws SQLException {

        // Search from cache.
        for (TableDefinition def : this.tableDefinitions) {
            if (def.isSameName(tableName)) {
                return def;
            }
        }

        // Read new schema if not known
        TableDefinition table = new TableDefinition(schemaName, tableName);
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet rs = metaData.getColumns(null, schemaName, tableName, "%")) {
            while (rs.next()) {
                table.columnDefinitions.add(new ColumnDefinition(
                        rs.getString("COLUMN_NAME"),
                        rs.getInt("TYPE_NAME"),
                        rs.getInt("COLUMN_SIZE")
                ));
            }
        }

        // cache
        this.tableDefinitions.add(table);

        return table;
    }
}
