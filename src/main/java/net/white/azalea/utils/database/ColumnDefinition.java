package net.white.azalea.utils.database;

/**
 * Contains column schema.
 *
 * @author S.yoshii
 */
public class ColumnDefinition {
    /**
     * Column name.
     */
    public final String columnName;

    /**
     * Column schema type.
     */
    public final int columnType;

    /**
     * Column size.
     */
    public final int columnSize;

    public ColumnDefinition(String columnName, int columnType, int columnSize) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnSize = columnSize;
    }
}
