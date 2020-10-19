package io.bytetrend.geo.location.model;

import java.io.Serializable;
import java.util.Objects;

public class GeoLocation implements Serializable {

    final float latitude;
    final float longitude;

    private GeoLocation(Float latitude, Float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoLocation that = (GeoLocation) o;
        return Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {

        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "GeoLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public static class Builder {
        float latitude;
        float longitude;

        public Builder setLatitude(float latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(float longitude) {
            this.longitude = longitude;
            return this;
        }

        public GeoLocation build() {
            return new GeoLocation(latitude, longitude);
        }
    }
}
