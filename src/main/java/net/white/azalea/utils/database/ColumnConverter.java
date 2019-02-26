package net.white.azalea.utils.database;

import java.io.IOException;

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
     * @throws IOException parse or read error.
     */
    Object conversion(int dataType, String value) throws IOException;

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
