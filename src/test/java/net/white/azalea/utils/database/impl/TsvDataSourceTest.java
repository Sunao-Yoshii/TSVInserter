package net.white.azalea.utils.database.impl;

import net.white.azalea.utils.database.DataSource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TsvDataSourceTest {

    @Test
    void loadDataSource() throws IOException {

        DataSource src = new TsvDataSource("src/test/resources/TsvDataSource.tsv");
        List<Map<String, String>> result = src.loadDataSource();

        assertEquals(result.size(), 2);

        assertAll(
                () -> assertEquals(result.get(0).get("column1"), "value1"),
                () -> assertEquals(result.get(0).get("column2"), "11"),
                () -> assertEquals(result.get(0).get("column3"), "3.14159"),
                () -> assertEquals(result.get(1).get("column1"), "value2"),
                () -> assertEquals(result.get(1).get("column2"), "222"),
                () -> assertEquals(result.get(1).get("column3"), "1.41421356")
        );
    }
}