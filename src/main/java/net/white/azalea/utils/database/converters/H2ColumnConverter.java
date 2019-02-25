package net.white.azalea.utils.database.converters;

import net.white.azalea.utils.database.ColumnConverter;

/**
 * H2Database column converter.
 */
public class H2ColumnConverter implements ColumnConverter {

    @Override
    public Object conversion(int dataType, String value) {
        // TODO: implementation.
        return null;
    }

    @Override
    public String columnNameWrapper(String columnName) {
        // NOOP.
        return columnName;
    }
}
