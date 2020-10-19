package io.bytetrend.geo.location.source;

import io.bytetrend.geo.location.model.Converter;
import io.bytetrend.geo.location.sink.DataSink;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.Resource;

public interface PointOfInterestLoader<T, U> {
    int processLocations(Resource resource,
                         FlatFileItemReader<T> reader,
                         DataSink<U> sink,
                         Converter<T, U> converter) throws Exception;
}
