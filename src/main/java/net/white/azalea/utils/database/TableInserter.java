package net.white.azalea.utils.database;


import net.white.azalea.utils.database.schema.ColumnDefinition;
import net.white.azalea.utils.database.schema.TableDefinition;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Insert to target table.
 *
 * @author S.Yoshii
 */
public class TableInserter {

    private final Connection connection;
    private final String schema;
    private final TableSchemaCache tableSchemaCache;

    public TableInserter(Connection connection, String schemaName) {
        this(connection, schemaName, TableSchemaCache.getInstance());
    }

    public TableInserter(Connection connection, String schemaName, TableSchemaCache tableSchemaCache) {
        this.connection = connection;
        this.schema = schemaName;
        this.tableSchemaCache = tableSchemaCache;
    }

    /**
     * Execute Insert query.
     *
     * @param tableName target table name.
     * @param source    data source.
     * @return Inserted record values.
     * @throws SQLException if cannot insert.
     * @throws IOException find undefined column name or cannot access data source.
     */
    public List<Map<String, Object>> insert(
            final String tableName,
            final DataSource source,
            final ColumnConverter binder
    ) throws SQLException, IOException {
        return this.insert(tableName, source, binder, new NoopBiFunction(), new NoopBiFunction());
    }

    /**
     * Insert to table.
     *
     * @param <R>       response type.
     * @param tableName target table name.
     * @param source    insert data sources.
     * @param binder    data conversion adapter.
     * @param prepare   event handler that call before insert sql execution.
     * @param post      event handler that call after insert sql execution.
     * @return insertion result.
     * @throws SQLException Insertion failed or handler error.
     * @throws IOException  Can't load data source.
     */
    public <R> List<R> insert(
            String tableName,
            DataSource source,
            ColumnConverter binder,
            ExceptBiFunction<String, Map<String, String>> prepare,
            ExceptBiFunction<Object, R> post
    ) throws SQLException, IOException {

        TableDefinition tableDef =
                this.tableSchemaCache.getTableDefinition(this.connection, this.schema, tableName);
        List<ColumnDefinition> columns =
                tableDef.columnDefinitions;
        List<R> resultList =
                new ArrayList<>();

        for (Map<String, String> row : source.loadDataSource()) {

            // prepare conversion.
            Map<String, String> src = prepare.apply(this.connection, row);

            // column values.
            List<String> columnNames = this.toInsertColumnNames(binder, src);

            // gen insert sql.
            String sql = this.toInsertSql(tableDef, columnNames);

            // execution
            Map<String, Object> converted = new HashMap<>();
            try(PreparedStatement ps = this.connection.prepareStatement(sql)) {
                int idx = 1;
                for (Map.Entry<String, String> entry : src.entrySet()) {
                    Optional<ColumnDefinition> cdef = this.findColumn(columns, entry.getKey());
                    if (cdef.isPresent()) {
                        int dataType = cdef.get().dataType;
                        Object conv = binder.conversion(dataType, entry.getValue());
                        ps.setObject(idx++, conv, dataType);

                        converted.put(entry.getKey(), conv);
                    } else {
                        throw new IOException("Unknown column definition: " + entry.getKey());
                    }
                }
                ps.executeUpdate();
            }

            // cache
            resultList.add(post.apply(this.connection, converted));
        }

        return resultList;
    }

    private String toInsertSql(TableDefinition tableDef, List<String> columnNames) {
        String columnDef = String.join(", ", columnNames);
        String binds = columnNames.stream().map(c -> "?").collect(Collectors.joining(","));
        return String.format(
                "INSERT INTO %s (%s) VALUES (%s)",
                tableDef.tableName, columnDef, binds
        );
    }

    private List<String> toInsertColumnNames(ColumnConverter binder, Map<String, String> src) {
        List<String> columnNames = new ArrayList<>(src.size());

        // convert
        for (Map.Entry<String, String> entry : src.entrySet()) {
            columnNames.add(binder.columnNameWrapper(entry.getKey()));
        }
        return columnNames;
    }

    private Optional<ColumnDefinition> findColumn(List<ColumnDefinition> columns, String key) {
        for (ColumnDefinition c : columns) {
            String lowerName = c.columnName.toLowerCase();
            if (lowerName.equals(key.toLowerCase())) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }


    /**
     * API handler interface.
     *
     * @param <T> argument value type.
     * @param <R> converted value type.
     */
    public interface ExceptBiFunction<T, R> {

        /**
         * SQL value convert function.
         *
         * @param connection connection value.
         * @param src        single row value
         * @return converted value.
         * @throws SQLException throw if you use connection and failure.
         */
        R apply(Connection connection, Map<String, T> src) throws SQLException;
    }

    /**
     * NOOP function.
     *
     * @param <T> src type.
     */
    private class NoopBiFunction<T> implements ExceptBiFunction<T, Map<String, T>> {

        @Override
        public Map<String, T> apply(Connection connection, Map<String, T> src) throws SQLException {
            return src;
        }
    }
}
