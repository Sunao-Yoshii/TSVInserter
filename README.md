# TSV Inserter

TSV Inserter は名前の通り、TSVファイルを読み込んで指定のテーブルにインサートするだけのライブラリです。  
UT でデータベースレコードのセットアップ時に、OR マッパーがあってもめんどくさく、メンテナンスするなら Excel で書きたかったから作りました。

めんどくさいので Maven リポジトリはありません。

## 環境

* Java8+
* 任意の JDBC ライブラリ
* ApacheCommonCodec 1.+

## インストール

1. このプロジェクトを clone します
2. jar ビルドしましょう
3. jar をプロジェクトに取り込みましょう

ビルドコマンドは次の通りです。

```sh
$ ./gradlew clean jar
```


## セットアップ

1. 取り込んだらデータベースの仕様を確認しましょう。  
   具体的にJDBCのマッピング仕様に合わせて実装します。  
   例えば H2 database なら http://www.h2database.com/html/datatypes.html などです。
2. `net.white.azalea.utils.database.ColumnConverter` を実装したクラスを作成します。  
   このクラスはTSVの文字列を各カラム型に合わせて変換するものです。
```java
import net.white.azalea.utils.database.ColumnConverter;
/*中略*/
import static java.sql.Types.*;

public class H2ColumnConverter implements ColumnConverter {
    private final DateFormat dateFormatter;
    private final DateFormat timeFormatter;
    private final DateFormat datetimeFormatter;

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
    public Object conversion(int dataType, String columnType,  String value) throws IOException {
        try {
            switch (dataType) {
                case INTEGER:
                    return Integer.parseInt(value);
                case BOOLEAN:
                case BIT:
                    return Boolean.parseBoolean(value);
                /* 中略 */
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
```

### 利用

以下の様なテーブルがあったとした場合

```sql
CREATE TABLE TEST_TABLE (
  id BIGINT AUTO_INCREMENT, 
  Column1 VARCHAR(32), 
  Column2 INTEGER, 
  Column3 CHAR(32), 
  Column4 BOOLEAN, 
  Column5 DECIMAL(10,2), 
  Column6 DOUBLE, 
  Column7 REAL, 
  Column8 TIME,
  Column9 DATE,
  Column10 TIMESTAMP, 
  PRIMARY KEY(id)
)
```

次の様なTSVを記述します。  
各カラム名はテーブルのものと一致させます。

```tsv
Column1	Column2	Column3	Column4	Column5	Column6	Column7	Column8	Column9	Column10
line1	65535	charColumn1	true	62.19	3.1415	12.5	13:54:22	2019/02/01	2019/02/25 13:11:22
line2	8192	charColumn2	false	31.14	1.4142	13.9	14:11:22	2019/03/02	2019/03/26 14:22:33
```

あとはTSVを読み取って食わせるだけです。  

* `TableInserter` コンストラクタ  
  `java.sql.Connection` とスキーマ名（SQLServer であれば dbo など）を指定。
* `insert` メソッド  
  テーブル名、DataSourceインスタンス(TsvDataSourceがバンドル)、ColumnConverter を指定します。

```java
TableInserter inserter = new TableInserter(this.connection, "");
inserter.insert(
	"TEST_TABLE",
	new TsvDataSource(Paths.get(ClassLoader.getSystemResource("TableInserterTest.tsv").toURI()), "UTF-8"),
	new H2ColumnConverter()
);
```

### License

Apache2.0