package io.bytetrend.geo.location.model;

import java.io.Serializable;

import static io.bytetrend.geo.location.tools.GeoDistantTool.distance;

public class GeoResult implements Serializable, Comparable<GeoResult> {

    private final PointOfInterest pointOfInterest;
    private final GeoLocation center;
    private final float distance;

    public GeoResult(PointOfInterest p, GeoLocation c) {
        if (p == null || c == null) {
            throw new IllegalArgumentException("PointOfInterest and/or GeoLocation must not be null.");
        }
        pointOfInterest = p;
        center = c;
        distance = (float) distance(p.geoLocation, c);
    }

    public PointOfInterest getPointOfInterest() {
        return pointOfInterest;
    }

    public GeoLocation getCenter() {
        return center;
    }

    public float getDistance() {
        return distance;
    }

    @Override
    public int compareTo(GeoResult o) {
        return Float.compare(distance, o.distance);
    }
}
