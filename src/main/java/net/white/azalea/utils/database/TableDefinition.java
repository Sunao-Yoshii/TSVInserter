package net.white.azalea.utils.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains columns and table name.
 *
 * @author S.Yoshii
 */
public class TableDefinition {

    /**
     * Columns definition.
     */
    public final List<ColumnDefinition> columnDefinitions;

    /**
     * Database schema name.
     */
    public final String schemaName;

    /**
     * Table name.
     */
    public final String tableName;

    /**
     * Getter for table name.
     *
     * @return table name.
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * Getter for database schema name.
     *
     * @return schema name.
     */
    public String getSchemaName() {
        return this.schemaName;
    }

    public TableDefinition(final String schemaName, final String tableName) {
        this(schemaName, tableName, new ArrayList<>());
    }

    public TableDefinition(
            final String schemaName,
            final String tableName,
            final List<ColumnDefinition> columns) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnDefinitions = columns;
    }

    /**
     * Check for equality that table name.
     *
     * @param tableName test target tableName.
     * @return return true if contains same table name.
     */
    public boolean isSameName(String tableName) {
        return this.tableName.toLowerCase()
                .equals(tableName.toLowerCase());
    }
}
