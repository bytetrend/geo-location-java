package io.bytetrend.geo.location.source.loader.fastfood;

import io.bytetrend.geo.location.model.LocationType;
import io.bytetrend.geo.location.source.model.FileLocation;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.util.EnumSet;

/**
 * This class maps a records from a specific source, fast food csv file
 * to a canonical entities of type FileLocation.
 *
 * @param <T>
 */
public class FastFoodFileFieldSetMapper <T extends Enum<T>> implements FieldSetMapper<FileLocation> {
    /**
     * A set of columns that map to a FileLocation from the source.
     */
    private final EnumSet<T> tableColumnNameSet;
    /**
     * The type of location contained in the file.
     */
    private final LocationType locationType;

    /**
     *
     * @param e an EnumSet containing the columns that will be processed from the file. If it is
     *          not in the set it will be discarded.
     * @param lt The type of location contained in the source, i.e: fast food, coffee shop.
     */
    public FastFoodFileFieldSetMapper(EnumSet<T> e, LocationType lt) {
        tableColumnNameSet = e;
        locationType = lt;
    }

    /**
     * Each record read from the source will be map to a set of fields.
     * Those fields will be used to create a FileLocation entity.
     *
     * @param fieldSet set with field specifications and values
     * @return a FileLocation from the fieldSet
     */
    @Override
    public FileLocation mapFieldSet(FieldSet fieldSet) {

        FileLocation.Builder builder = new FileLocation.Builder();
        for (Enum<T> e : tableColumnNameSet) {
            switch (e.name()) {
                case "ADDRESS":
                    builder.setAddress(fieldSet.readString(FastFoodToFileLocation.ADDRESS.getSourceCol()));
                    break;
                case "CITY":
                    builder.setCity(fieldSet.readString(FastFoodToFileLocation.CITY.getSourceCol()));
                    break;
                case "COUNTRY":
                    builder.setCountry(fieldSet.readString(FastFoodToFileLocation.COUNTRY.getSourceCol()));
                    break;
                case "LATITUDE":
                    builder.setLatitude(fieldSet.readString(FastFoodToFileLocation.LATITUDE.getSourceCol()));
                    break;
                case "LONGITUDE":
                    builder.setLongitude(fieldSet.readString(FastFoodToFileLocation.LONGITUDE.getSourceCol()));
                    break;
                case "NAME":
                    builder.setName(fieldSet.readString(FastFoodToFileLocation.NAME.getSourceCol()));
                    break;
                case "POSTALCODE":
                    builder.setPostalCode(fieldSet.readString(FastFoodToFileLocation.POSTALCODE.getSourceCol()));
                    break;
                case "STATE":
                    builder.setState(fieldSet.readString(FastFoodToFileLocation.STATE.getSourceCol()));
                    break;
                case "WEBSITE":
                    builder.setWebsite(fieldSet.readString(FastFoodToFileLocation.WEBSITE.getSourceCol()));
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