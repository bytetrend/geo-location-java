package io.bytetrend.geo.location.source.loader.starbucks;

import io.bytetrend.geo.location.source.loader.SourceToFileLocation;

import static io.bytetrend.geo.location.source.loader.SourceSinkColumnNames.fileLocationFieldNames;
import static io.bytetrend.geo.location.source.loader.SourceSinkColumnNames.starbucksCsvColumns;

/**
 * This Enum maps the column names from an Starbucks location file
 * to a NoSQL table columns names.
 * The source file is from: https://www.kaggle.com/starbucks/store-locations
 */
public enum StarbucksToFileLocation implements SourceToFileLocation {
    ADDRESS(starbucksCsvColumns[4], fileLocationFieldNames[0]),
    CITY(starbucksCsvColumns[5], fileLocationFieldNames[1]),
    COUNTRY(starbucksCsvColumns[7], fileLocationFieldNames[2]),
    LATITUDE(starbucksCsvColumns[12], fileLocationFieldNames[3]),
    LONGITUDE(starbucksCsvColumns[11], fileLocationFieldNames[4]),
    NAME(starbucksCsvColumns[0], fileLocationFieldNames[5]),
    POSTALCODE(starbucksCsvColumns[8], fileLocationFieldNames[6]),
    STATE(starbucksCsvColumns[6], fileLocationFieldNames[7]),
    WEBSITE(null, fileLocationFieldNames[8]),
    CATEGORY(null, fileLocationFieldNames[9]);

    private final String fileCol;
    private final String canonicalFieldName;

    StarbucksToFileLocation(String file, String table) {
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
