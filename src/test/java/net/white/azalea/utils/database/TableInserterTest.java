package net.white.azalea.utils.database;

import net.white.azalea.utils.database.converters.H2ColumnConverter;
import net.white.azalea.utils.database.impl.TsvDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableInserterTest {

    private Connection connection;

    @BeforeAll
    static void beforeClass() throws Exception {
        Class.forName("org.h2.Driver");
    }

    @BeforeEach
    void setUp() throws Exception {
        this.connection = DriverManager.getConnection ("jdbc:h2:mem:test", "sa","");
        Statement statement = this.connection.createStatement();

        statement.execute("CREATE TABLE TEST_SCHEMA (" +
                "id BIGINT AUTO_INCREMENT, " +
                "Column1 VARCHAR(32), " +
                "Column2 INTEGER, " +
                "Column3 CHAR(32), " +
                "Column4 BOOLEAN, " +
                "Column5 DECIMAL(10,2), " +
                "Column6 DOUBLE, " +
                "Column7 REAL, " +
                "Column8 TIME," +
                "Column9 DATE," +
                "Column10 TIMESTAMP, " +
                "PRIMARY KEY(id)" +
                ")");
    }

    @AfterEach
    void tearDown() throws Exception {
        this.connection.close();
    }

    @Test
    void insert() throws Exception {
        TableInserter inserter = new TableInserter(this.connection, "");
        inserter.insert(
                "TEST_SCHEMA",
                new TsvDataSource(Paths.get(ClassLoader.getSystemResource("TableInserterTest.tsv").toURI())),
                new H2ColumnConverter()
                );

        try (PreparedStatement st = this.connection.prepareStatement("SELECT * FROM TEST_SCHEMA")) {
            try (ResultSet rs = st.executeQuery()) {
                List<Object[]> result = new ArrayList<>();
                while (rs.next()) {
                    Object[] data = {
                            rs.getObject("ID"),
                            rs.getObject("Column1"),
                            rs.getObject("Column2"),
                            rs.getObject("Column3"),
                            rs.getObject("Column4"),
                            rs.getObject("Column5"),
                            rs.getObject("Column6"),
                            rs.getObject("Column7"),
                            rs.getObject("Column8"),
                            rs.getObject("Column9"),
                            rs.getObject("Column10")
                    };

                    result.add(data);
                }
                /*
                "Column1 VARCHAR(32), " +
                "Column2 INTEGER, " +
                "Column3 CHAR(32), " +
                "Column4 BOOLEAN, " +
                "Column5 DECIMAL(10,2), " +
                "Column6 DOUBLE, " +
                "Column7 REAL, " +
                "Column8 TIME," +
                "Column9 DATE," +
                "Column10 TIMESTAMP, " +*/
                DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                DateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                assertAll(
                        "Row1",
                        () -> assertEquals(Long.valueOf(1L), result.get(0)[0]),
                        () -> assertEquals("line1", result.get(0)[1]),
                        () -> assertEquals(Integer.valueOf(65535), result.get(0)[2]),
                        () -> assertEquals("charColumn1", result.get(0)[3]),
                        () -> assertEquals(Boolean.valueOf(true), result.get(0)[4]),
                        () -> assertEquals(BigDecimal.valueOf(62.19), result.get(0)[5]),
                        () -> assertEquals(Double.valueOf(3.1415), result.get(0)[6]),
                        () -> assertEquals(Float.valueOf(12.5f), result.get(0)[7]),
                        () -> assertEquals("13:54:22", timeFormat.format((Time) result.get(0)[8])),
                        () -> assertEquals("2019/02/01", dateFormat.format((Date) result.get(0)[9])),
                        () -> assertEquals("2019/02/25 13:11:22", datetimeFormat.format((Timestamp) result.get(0)[10]))
                );
                assertAll(
                        "Row2",
                        () -> assertEquals(Long.valueOf(2L), result.get(1)[0]),
                        () -> assertEquals("line2", result.get(1)[1]),
                        () -> assertEquals(Integer.valueOf(8192), result.get(1)[2]),
                        () -> assertEquals("charColumn2", result.get(1)[3]),
                        () -> assertEquals(Boolean.valueOf(false), result.get(1)[4]),
                        () -> assertEquals(BigDecimal.valueOf(31.14), result.get(1)[5]),
                        () -> assertEquals(Double.valueOf(1.4142), result.get(1)[6]),
                        () -> assertEquals(Float.valueOf(13.9f), result.get(1)[7]),
                        () -> assertEquals("14:11:22", timeFormat.format((Time) result.get(1)[8])),
                        () -> assertEquals("2019/03/02", dateFormat.format((Date) result.get(1)[9])),
                        () -> assertEquals("2019/03/26 14:22:33", datetimeFormat.format((Timestamp) result.get(1)[10]))
                );
            }
        }
    }
}