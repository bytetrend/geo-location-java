package io.bytetrend.geo.location.source.loader.fastfood;

import io.bytetrend.geo.location.source.loader.SourceToFileLocation;

import static io.bytetrend.geo.location.source.loader.SourceSinkColumnNames.fastFoodCsvColumns;
import static io.bytetrend.geo.location.source.loader.SourceSinkColumnNames.fileLocationFieldNames;

/**
 * This Enum maps the column names from an Starbucks location file
 * to a FileLocation field names.
 */
public enum FastFoodToFileLocation implements SourceToFileLocation {
    ADDRESS(fastFoodCsvColumns[0], fileLocationFieldNames[0]),
    CITY(fastFoodCsvColumns[1], fileLocationFieldNames[1]),
    COUNTRY(fastFoodCsvColumns[2], fileLocationFieldNames[2]),
    LATITUDE(fastFoodCsvColumns[4], fileLocationFieldNames[3]),
    LONGITUDE(fastFoodCsvColumns[5], fileLocationFieldNames[4]),
    NAME(fastFoodCsvColumns[6], fileLocationFieldNames[5]),
    POSTALCODE(fastFoodCsvColumns[7], fileLocationFieldNames[6]),
    STATE(fastFoodCsvColumns[8], fileLocationFieldNames[7]),
    WEBSITE(fastFoodCsvColumns[9], fileLocationFieldNames[8]),
    CATEGORY(null, fileLocationFieldNames[9]);

    private final String fileCol;
    private final String canonicalFieldName;

    FastFoodToFileLocation(String file, String table) {
        fileCol = file;
        canonicalFieldName = table;
    }

    public String getSourceCol() {
        return fileCol;
    }

    public String getCanonicalFieldName() {
        return canonicalFieldName;
    }

}
