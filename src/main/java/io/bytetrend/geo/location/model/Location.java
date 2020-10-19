package io.bytetrend.geo.location.model;

import io.bytetrend.geo.location.source.model.FileLocation;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {

    private final Float latitude;
    private final Float longitude;
    private final String name;
    private final Address address;
    private final String website;
    private final LocationType locationType;

    private Location(Float latitude, Float longitude,
                     String address, String city,
                     String state, String zip, String name,
                     String country, String website, LocationType locationType) {
        if (latitude == null || longitude == null || name == null || address == null || locationType == null)
            throw new IllegalArgumentException("latitude, longitude, name , address, locationType can not be null");

        this.latitude = latitude;
        this.longitude = longitude;
        this.address = new Address(address, null, city, state, zip, country);
        this.name = name;
        this.website = website;
        this.locationType = locationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return Objects.equals(latitude, location.latitude) &&
                Objects.equals(longitude, location.longitude) &&
                Objects.equals(name, location.name);
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public Address getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getWebsite() {
        return website;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, name);
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                '}';
    }

    public static class Builder {
        private Float latitude;
        private Float longitude;
        private String address;
        private String city;
        private String state;
        private String zip;
        private String name;
        private String country;
        private String website;
        private LocationType locationType;

        public Builder setLatitude(Float latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(Float longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setState(String state) {
            this.state = state;
            return this;
        }

        public Builder setZip(String zip) {
            this.zip = zip;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setWebsite(String website) {
            this.website = website;
            return this;
        }

        public Builder setLocationType(LocationType locationType) {
            this.locationType = locationType;
            return this;
        }

        public Builder convert(FileLocation loc) {
            latitude = Float.parseFloat(loc.getLatitude());
            longitude = Float.parseFloat(loc.getLongitude());
            address = loc.getAddress();
            city = loc.getCity();
            state = loc.getState();
            zip = loc.getPostalCode();
            name = loc.getName();
            country = loc.getCountry();
            website = loc.getWebsite();
            locationType = loc.getLocationType();
            return this;
        }

        public Location build() {
            return new Location(latitude, longitude, address, city,
                    state, zip, name, country, website, locationType);
        }
    }
}
