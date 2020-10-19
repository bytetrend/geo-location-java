package io.bytetrend.geo.location.model;

import io.bytetrend.geo.location.tools.GeoDistantTool;

import java.io.Serializable;
import java.util.Objects;

public class GeoLocationRequest implements Serializable {

    private final GeoLocation geoLocation;
    private final float radius;
    private final int maxItems;
    private final LocationType locationType;
    private final SourceType sourceType;
    private final GeoLocationBounds geoLocationBounds;

    private GeoLocationRequest(GeoLocation geoLocation, float radius,
                               int maxItems, LocationType locationType, SourceType sourceType) {
        this.geoLocation = geoLocation;
        this.radius = radius;
        this.maxItems = maxItems;
        this.locationType = locationType;
        this.sourceType = sourceType;
        this.geoLocationBounds = new GeoLocationBounds(getMinLatitude(), getMaxLatitude(),
                getMinLongitude(), getMaxLongitude());
    }

    private float getMinLatitude() {
        return geoLocation.latitude - GeoDistantTool.milesToRadian(radius);
    }

    private float getMaxLatitude() {
        return geoLocation.latitude + GeoDistantTool.milesToRadian(radius);
    }

    private float getMinLongitude() {
        return geoLocation.longitude - GeoDistantTool.milesToRadian(radius);
    }

    private float getMaxLongitude() {
        return geoLocation.longitude + GeoDistantTool.milesToRadian(radius);
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public float getRadius() {
        return radius;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public GeoLocationBounds getGeoLocationBounds() {
        return geoLocationBounds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoLocationRequest)) return false;
        GeoLocationRequest that = (GeoLocationRequest) o;
        return Float.compare(that.radius, radius) == 0 &&
                maxItems == that.maxItems &&
                geoLocation.equals(that.geoLocation) &&
                locationType == that.locationType &&
                sourceType == that.sourceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(geoLocation, radius, maxItems, locationType, sourceType);
    }

    @Override
    public String toString() {
        return "GeoLocationRequest{" +
                "geoLocation=" + geoLocation +
                ", radius=" + radius +
                ", maxItems=" + maxItems +
                ", locationType=" + locationType +
                ", sourceType=" + sourceType +
                '}';
    }

    public static class Builder {
        private float latitude;
        private float longitude;
        private float radius;
        private int maxItems;
        private String locationType;
        private String sourceType;

        public Builder setLatitude(float latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(float longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setRadius(float radius) {
            this.radius = radius;
            return this;
        }

        public Builder setMaxItems(int maxItems) {
            this.maxItems = maxItems;
            return this;
        }

        public Builder setLocationType(String locationType) {
            this.locationType = locationType;
            return this;
        }

        public Builder setSourceType(String sourceType) {
            this.sourceType = sourceType;
            return this;
        }

        public GeoLocationRequest build() {

            return new GeoLocationRequest(new GeoLocation.Builder()
                    .setLatitude(latitude).setLongitude(longitude).build(),
                    radius, maxItems, LocationType.valueOf(locationType),
                    SourceType.valueOf(sourceType));
        }
    }

    static public class GeoLocationBounds {
        final private float minLatitude;
        final private float maxLatitude;
        final private float minLongitude;
        final private float maxLongitude;

        private GeoLocationBounds(float minLatitude, float maxLatitude, float minLongitude, float maxLongitude) {
            this.minLatitude = minLatitude;
            this.maxLatitude = maxLatitude;
            this.minLongitude = minLongitude;
            this.maxLongitude = maxLongitude;
        }

        public float getMinLatitude() {
            return minLatitude;
        }

        public float getMaxLatitude() {
            return maxLatitude;
        }

        public float getMinLongitude() {
            return minLongitude;
        }

        public float getMaxLongitude() {
            return maxLongitude;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GeoLocationBounds)) return false;
            GeoLocationBounds that = (GeoLocationBounds) o;
            return Float.compare(that.minLatitude, minLatitude) == 0 &&
                    Float.compare(that.maxLatitude, maxLatitude) == 0 &&
                    Float.compare(that.minLongitude, minLongitude) == 0 &&
                    Float.compare(that.maxLongitude, maxLongitude) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(minLatitude, maxLatitude, minLongitude, maxLongitude);
        }

        @Override
        public String toString() {
            return "GeoLocationBounds{" +
                    "minLatitude=" + minLatitude +
                    ", maxLatitude=" + maxLatitude +
                    ", minLongitude=" + minLongitude +
                    ", maxLongitude=" + maxLongitude +
                    '}';
        }

    }

}
