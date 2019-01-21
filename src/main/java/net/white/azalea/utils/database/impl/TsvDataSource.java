package net.white.azalea.utils.database.impl;

import net.white.azalea.utils.database.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * From static TSV (Tab separated value) data to column stream.
 *
 * @author S.Yoshii
 */
public class TsvDataSource implements DataSource {

    /**
     * Path of TSV file.
     */
    private final Path path;

    /**
     * Specified load target.
     * @param dataSource load target TSV path string.
     */
    public TsvDataSource(String dataSource) {
        this(Paths.get(dataSource));
    }

    /**
     * Specified load target.
     * @param path load target path.
     */
    public TsvDataSource(Path path) {
        this.path = path;
    }

    @Override
    public List<Map<String, String>> loadDataSource() throws IOException {
        List<Map<String, String>> lists = new LinkedList<>();
        List<String[]> rows;

        try(BufferedReader reader = Files.newBufferedReader(this.path, Charset.forName("UTF-8"))) {
            Stream<String> lines = reader.lines();

            rows = lines.filter(v -> v != null && v.length() > 0)
                    .map(str -> str.split("\t"))
                    .filter(v -> v.length > 0)
                    .collect(Collectors.toList());
        }

        // load headers.
        String[] headers = rows.get(0);

        // convert to maps.
        for (String[] strs : rows) {
            if (strs == headers) continue;

            HashMap<String, String> row = new HashMap<>(strs.length);
            for (int n = 0; n < strs.length; n++) {
                row.put(this.toLower(headers[n]), this.wrapAsNull(strs[n]));
            }
            lists.add(row);
        }

        return lists;
    }

    /**
     * return lower case column name is not null or empty.
     * @param header source target string.
     * @return lower case column name.
     * @throws IOException Invalid column name.
     */
    private String toLower(String header) throws IOException {
        String test = this.wrapAsNull(header);
        if (test == null) throw new IOException("Cant set column name as null or empty.");
        return test.toLowerCase();
    }

    /**
     * return null if str is empty;
     * @param str check target str.
     * @return return str if not null or empty.
     */
    private String wrapAsNull(String str) {
        return str == null || str.trim().length() == 0 ? null : str;
    }
}