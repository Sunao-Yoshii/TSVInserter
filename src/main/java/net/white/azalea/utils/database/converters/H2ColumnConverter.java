package net.white.azalea.utils.database.converters;

import net.white.azalea.utils.database.ColumnConverter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static java.sql.Types.*;

/**
 * H2Database column converter.
 */
public class H2ColumnConverter implements ColumnConverter {
    private final DateFormat dateFormatter;
    private final DateFormat timeFormatter;
    private final DateFormat datetimeFormatter;

    public H2ColumnConverter() {
        this(
            TimeZone.getDefault(),
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd",
            "HH:mm:ss"
        );
    }

    public H2ColumnConverter(
            final TimeZone timeZone,
            final String datetimeFormatter,
            final String dateFormatter,
            final String timeFormatter
    ) {
        this.datetimeFormatter = new SimpleDateFormat(datetimeFormatter);
        this.dateFormatter = new SimpleDateFormat(dateFormatter);
        this.timeFormatter = new SimpleDateFormat(timeFormatter);
        this.datetimeFormatter.setTimeZone(timeZone);
    }

    @Override
    public Object conversion(int dataType, String value) throws IOException {
        try {
            switch (dataType) {
                case INTEGER:
                    return Integer.parseInt(value);
                case BOOLEAN:
                case BIT:
                    return Boolean.parseBoolean(value);
                case TINYINT:
                    return Byte.parseByte(value);
                case SMALLINT:
                    return Short.parseShort(value);
                case BIGINT:
                    return Long.parseLong(value);
                case NUMERIC:
                case DECIMAL:
                    return new BigDecimal(value);
                case FLOAT:
                case DOUBLE:
                    return Double.parseDouble(value);
                case REAL:
                    return Float.parseFloat(value);
                case TIME:
                    return this.timeFormatter.parse(value);
                case DATE:
                    return this.dateFormatter.parse(value);
                case TIMESTAMP:
                    return this.datetimeFormatter.parse(value);
                case BINARY:
                case VARBINARY:
                case LONGVARBINARY:
                    return Hex.decodeHex(value);
                case CHAR:
                case VARCHAR:
                case LONGVARCHAR:
                case NCHAR:
                case NVARCHAR:
                case LONGNVARCHAR:
                    return value;
                case ARRAY:  // Support string array only.
                    return value.split(",");
                case BLOB:
                case CLOB:
                case NCLOB:
                case TIMESTAMP_WITH_TIMEZONE:
                default:
                    throw new IOException("Not support!");
            }
        } catch (ParseException | DecoderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String columnNameWrapper(String columnName) {
        // NOOP.
        return columnName;
    }
}
