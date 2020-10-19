package io.bytetrend.geo.location.source.loader;

import io.bytetrend.geo.location.model.Converter;
import io.bytetrend.geo.location.model.Location;
import io.bytetrend.geo.location.source.model.FileLocation;

public class FileLocationToLocationConverter implements Converter<FileLocation, Location> {

    final Location.Builder builder = new Location.Builder();

    @Override
    public Location convertTo(FileLocation fileLocation) {
        return builder.convert(fileLocation).build();
    }

}
