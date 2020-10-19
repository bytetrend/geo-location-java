package io.bytetrend.geo.location.source.loader;

/**
 * It maps the columns names from the source, i.e: column names
 * in a CSV file. To columns names in FileLocation canonical object.
 */
public interface SourceToFileLocation {

    /**
     *
     * @return the name of the source column corresponding to the sink column
     */
    String getSourceCol();

    /**
     *
     * @return the name of the field in FileLocation mapping to the source column.
     */
    String getCanonicalFieldName();
}
