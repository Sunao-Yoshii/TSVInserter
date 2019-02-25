package net.white.azalea.utils.database;

import net.white.azalea.utils.database.schema.ColumnDefinition;
import net.white.azalea.utils.database.schema.TableDefinition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableSchemaCacheTest {

    private Connection connection;

    @BeforeAll
    public static void beforeClass() throws Exception {
        Class.forName("org.h2.Driver");
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.connection = DriverManager.getConnection ("jdbc:h2:mem:test", "sa","");
        Statement statement = this.connection.createStatement();

        statement.execute("CREATE TABLE TEST_SCHEMA (" +
                "id BIGINT AUTO_INCREMENT, " +
                "Column1 VARCHAR(32), " +
                "Column2 INTEGER, " +
                "Column3 CHAR(32), " +
                "PRIMARY KEY(id)" +
                ")");
    }

    @AfterEach
    public void cleanup() throws Exception {
        this.connection.close();
    }

    @Test
    void getTableDefinition() throws Exception {
        TableDefinition definition =
            TableSchemaCache.getInstance()
                    .getTableDefinition(this.connection, "", "TEST_SCHEMA");

        assertAll(
                () -> assertEquals("TEST_SCHEMA", definition.tableName),
                () -> assertEquals(4, definition.columnDefinitions.size())
        );

        List<ColumnDefinition> columns = definition.columnDefinitions;
        assertAll(
                () -> assertEquals("ID", columns.get(0).columnName),
                () -> assertEquals("BIGINT", columns.get(0).columnType),
                () -> assertEquals(19, columns.get(0).columnSize),
                () -> assertEquals(-5, columns.get(0).dataType)
        );
    }
}