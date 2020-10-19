package io.bytetrend.geo.location.source.model;

import io.bytetrend.geo.location.model.LocationType;

import static org.apache.commons.lang3.StringUtils.trimToNull;

public class FileLocation {


    private final String latitude;
    private final String longitude;
    private final String address;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String name;
    private final String country;
    private final String website;
    private final String keys;
    private final LocationType locationType;

    private FileLocation(String lat,
                         String lng, String add, String ci,
                         String s, String p, String nm,
                         String co, String web, String k,
                         LocationType lc) {

        this.latitude = trimToNull(lat);
        this.longitude = trimToNull(lng);
        this.address = trimToNull(add);
        this.city = trimToNull(ci);
        this.state = trimToNull(s);
        this.postalCode = trimToNull(p);
        this.name = trimToNull(nm);
        this.country = trimToNull(co);
        this.website = parseWebsite(trimToNull(web));
        this.keys = trimToNull(k);
        this.locationType = lc;
        if (latitude == null || longitude == null || name == null || address == null || locationType == null)
            throw new IllegalArgumentException("latitude, longitude, name , address, locationType can not be null");
    }

    private String parseWebsite(String w) {
        if (w != null && w.indexOf(',') > -1) {
            return w.substring(0, w.indexOf(','));
        }
        return w;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getWebsite() {
        return website;
    }

    public String getKeys() {
        return keys;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public static class Builder {
        private String latitude;
        private String longitude;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String name;
        private String country;
        private String website;
        private String keys;
        private LocationType locationType;

        public FileLocation build() {
            return new FileLocation(latitude,
                    longitude, address, city,
                    state, postalCode, name,
                    country, website, keys, locationType);
        }

        public Builder setLatitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(String longitude) {
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

        public Builder setPostalCode(String postalCode) {
            this.postalCode = postalCode;
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

        public Builder setKeys(String keys) {
            this.keys = keys;
            return this;
        }

        public Builder setLocationType(LocationType locationType) {
            this.locationType = locationType;
            return this;
        }
    }
}
