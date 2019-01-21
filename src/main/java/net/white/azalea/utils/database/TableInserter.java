package net.white.azalea.utils.database;


import java.sql.Connection;

/**
 * Insert to target table.
 *
 * @author S.Yoshii
 */
public class TableInserter {

    private final Connection connection;
    private final String schema;

    public TableInserter(Connection connection, String schemaName) {
        this.connection = connection;
        this.schema = schemaName;
    }
}
