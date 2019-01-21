package net.white.azalea.utils.database;


import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface of DataSource.
 *
 * Load column values as java.utl.Map stream.
 *
 * @author S.Yoshii
 */
public interface DataSource {

    /**
     * Load insertion data source iterator.
     *
     * @return Column,Value Mapping data source stream.
     * @exception IOException if cannot load.
     */
    List<Map<String, String>> loadDataSource() throws IOException;
}
