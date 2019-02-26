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
     * File encoding.
     */
    private final String fileEncoding;

    /**
     * Specified load target.
     * @param dataSource data source path.
     */
    public TsvDataSource(String dataSource) {
        this(Paths.get(dataSource), "UTF-8");
    }

    /**
     * Specified load target.
     * @param dataSource load target TSV path string.
     * @param encoding   source file encoding.
     */
    public TsvDataSource(String dataSource, String encoding) {
        this(Paths.get(dataSource), encoding);
    }

    /**
     * Specified load target.
     * @param path      load target path.
     * @param encoding  source file encoding.
     */
    public TsvDataSource(Path path, String encoding) {
        this.path = path;
        this.fileEncoding = encoding;
    }

    @Override
    public List<Map<String, String>> loadDataSource() throws IOException {

        // loading.
        List<String[]> rows = this.loadRows();

        // load headers.
        String[] headers = rows.get(0);

        // convert to maps.
        List<Map<String, String>> lists = new LinkedList<>();
        for (String[] strs : rows) {
            if (strs == headers) continue;

            Map<String, String> row = this.zipToMap(headers, strs);
            lists.add(row);
        }

        return lists;
    }

    /**
     * Zips the list of headers passed in the first argument
     * and the list of values ​​passed in the second argument
     * with the same index as the key.
     *
     * @param headers Map key values.
     * @param strs    Map values.
     * @return zipped map.
     * @throws IOException throw if header is null or empty.
     */
    private Map<String, String> zipToMap(String[] headers, String[] strs) throws IOException {
        HashMap<String, String> row = new HashMap<>(strs.length);
        for (int n = 0; n < strs.length; n++) {
            row.put(this.toLower(headers[n]), this.wrapAsNull(strs[n]));
        }
        return row;
    }

    /**
     * Load TSV file as TSV columns list.
     * @return Read value (as list of String[]).
     * @throws IOException When failed to access.
     */
    private List<String[]> loadRows() throws IOException {
        List<String[]> rows;
        try(BufferedReader reader = Files.newBufferedReader(this.path, Charset.forName(this.fileEncoding))) {
            Stream<String> lines = reader.lines();

            rows = lines.filter(v -> v != null && v.length() > 0)
                    .map(str -> str.split("\t"))
                    .filter(v -> v.length > 0)
                    .collect(Collectors.toList());
        }
        return rows;
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