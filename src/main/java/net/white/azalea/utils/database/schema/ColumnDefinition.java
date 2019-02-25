package net.white.azalea.utils.database.schema;

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
    public final String columnType;

    /**
     * Column data type no.
     *
     * see: https://docs.oracle.com/javase/jp/8/docs/api/java/sql/DatabaseMetaData.html
     */
    public final int dataType;

    /**
     * Column size.
     */
    public final int columnSize;

    public ColumnDefinition(String columnName, String columnType, final int dataType, int columnSize) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnSize = columnSize;
        this.dataType = dataType;
    }
}
