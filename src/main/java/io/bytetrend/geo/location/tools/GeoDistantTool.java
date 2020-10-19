package io.bytetrend.geo.location.tools;

import io.bytetrend.geo.location.model.GeoLocation;

import static java.lang.Math.*;

public class GeoDistantTool {

    public static final float RADIANS_PER_MILE = 1 / 69.172F;
    public static final float EARTH_RADIUS_IN_MILES = 3958.8F;

    /**
     * Given some miles in the surface of the earth it will
     * return the corresponding radians.
     *
     * @param miles miles to convert to radians
     * @return radians equivalent to miles.
     */
    public static float milesToRadian(float miles) {
        return (miles * RADIANS_PER_MILE);
    }

    /**
     * Calculate the distance in miles between to geodesic points.
     *
     * @param loc1 first GeoLocation
     * @param loc2 second GeoLocation
     * @return a distance in miles between the two.
     */
    public static double distance(GeoLocation loc1, GeoLocation loc2) {
        double distLat = toRadians(loc2.getLatitude() - loc1.getLatitude());
        double distLon = toRadians(loc2.getLongitude() - loc1.getLongitude());
        double part1 = sin(distLat / 2) * sin(distLat / 2)
                + cos(toRadians(loc1.getLatitude())) * cos(toRadians(loc2.getLatitude()))
                *  sin(distLon / 2) * sin(distLon / 2) ;
        return 2 * atan2(sqrt(part1), sqrt(1 - part1)) * EARTH_RADIUS_IN_MILES;
    }

}
