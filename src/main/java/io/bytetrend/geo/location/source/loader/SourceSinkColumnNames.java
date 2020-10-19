package io.bytetrend.geo.location.source.loader;

public final class SourceSinkColumnNames {

    public static final String[] fastFoodCsvColumns = new String[]{
            "address",
            "city",
            "country",
            "keys",
            "latitude",
            "longitude",
            "name",
            "postalCode",
            "province",
            "websites"

    };

    public static final String[] starbucksCsvColumns = new String[]{
            "Brand",
            "Store Number",
            "Store Name",
            "Ownership Type",
            "Street Address",
            "City",
            "State/Province",
            "Country",
            "Postcode",
            "Phone Number",
            "Timezone",
            "Longitude",
            "Latitude"
    };

    public static final String[] fileLocationFieldNames = new String[]{
            "address",
            "city",
            "country",
            "latitude",
            "longitude",
            "name",
            "postalcode",
            "state",
            "website",
            "category"
    };

}
