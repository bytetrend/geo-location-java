package io.bytetrend.geo.location.source.loader.starbucks;

import io.bytetrend.geo.location.model.LocationType;
import io.bytetrend.geo.location.source.model.FileLocation;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.util.EnumSet;

/**
 * This class reads fields (columns) from a flat file and
 * for each it returns a canonical item of type FileLocation.
 * The file specifically loads Starbucks locations.
 *
 * @param <T> is an Enum that has the columns names
 *           that appear in the file.
 */
public class StarbucksFileFieldSetMapper<T extends Enum<T>> implements FieldSetMapper<FileLocation> {
    private final EnumSet<T> tableColumnNameSet;
    private final LocationType locationType;

    public StarbucksFileFieldSetMapper(EnumSet<T> e, LocationType lt) {
        tableColumnNameSet = e;
        locationType = lt;
    }

    @Override
    public FileLocation mapFieldSet(FieldSet fieldSet) {

        FileLocation.Builder builder = new FileLocation.Builder();
        for (Enum<T> e : tableColumnNameSet) {
            switch (e.name()) {
                case "ADDRESS":
                    builder.setAddress(fieldSet.readString(StarbucksToFileLocation.ADDRESS.getSourceCol()));
                    break;
                case "CITY":
                    builder.setCity(fieldSet.readString(StarbucksToFileLocation.CITY.getSourceCol()));
                    break;
                case "COUNTRY":
                    builder.setCountry(fieldSet.readString(StarbucksToFileLocation.COUNTRY.getSourceCol()));
                    break;
                case "LATITUDE":
                    builder.setLatitude(fieldSet.readString(StarbucksToFileLocation.LATITUDE.getSourceCol()));
                    break;
                case "LONGITUDE":
                    builder.setLongitude(fieldSet.readString(StarbucksToFileLocation.LONGITUDE.getSourceCol()));
                    break;
                case "NAME":
                    builder.setName(fieldSet.readString(StarbucksToFileLocation.NAME.getSourceCol()));
                    break;
                case "POSTALCODE":
                    builder.setPostalCode(fieldSet.readString(StarbucksToFileLocation.POSTALCODE.getSourceCol()));
                    break;
                case "STATE":
                    builder.setState(fieldSet.readString(StarbucksToFileLocation.STATE.getSourceCol()));
                    break;
                case "WEBSITE":
                    builder.setWebsite("http://www.starbucks.com");
                    break;
                case "CATEGORY":
                    builder.setLocationType(locationType);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid table column name found " + e.name());
            }
        }
        return builder.build();
    }
}