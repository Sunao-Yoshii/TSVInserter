package net.white.azalea.utils.database;

/**
 * Column data conversion adapter.
 */
public interface ColumnConverter {

    /**
     * column convert method.
     *
     * @param dataType JDBC DataType.
     * @param value    From src.
     * @return converted value.
     */
    Object conversion(int dataType, String value);

    /**
     * Column name wrapper.
     *
     * eg. [] at SQLServer.
     *
     * @param columnName column definition name.
     * @return wrapped column name.
     */
    String columnNameWrapper(String columnName);
}
