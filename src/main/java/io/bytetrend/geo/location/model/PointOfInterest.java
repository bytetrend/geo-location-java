package io.bytetrend.geo.location.model;

import java.io.Serializable;
import java.util.Objects;

public class PointOfInterest implements Serializable {
    final String name;
    final Address address;
    final GeoLocation geoLocation;
    final LocationType type;

    public PointOfInterest(Location location, LocationType locType) {
        name = location.getName();
        address = location.getAddress();

        geoLocation = new GeoLocation.Builder()
                .setLatitude(location.getLatitude())
                .setLongitude(location.getLongitude()).build();
        type = locType;
    }

    public PointOfInterest(String name, Address address,
                           GeoLocation geoLocation, LocationType type) {
        this.name = name;
        this.address = address;
        this.geoLocation = geoLocation;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public LocationType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointOfInterest)) return false;
        PointOfInterest that = (PointOfInterest) o;
        return name.equals(that.name) &&
                address.equals(that.address) &&
                geoLocation.equals(that.geoLocation) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, geoLocation, type);
    }

    @Override
    public String toString() {
        return "PointOfInterest{" +
                "name='" + name + '\'' +
                ", address=" + address +
                ", geoLocation=" + geoLocation +
                ", type=" + type +
                '}';
    }
}
