package io.bytetrend.geo.location.source;

import io.bytetrend.geo.location.model.Converter;
import io.bytetrend.geo.location.sink.DataSink;
import io.bytetrend.geo.location.source.loader.FileLoadingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlatFileLoaderImpl<T, U> implements PointOfInterestLoader<T, U> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlatFileLoaderImpl.class);
    static private final ExecutionContext executionContext = new ExecutionContext();
    /**
     * The maximum quantity of records that should be saved at one time.
     * Some sinks can only store certain amount of records at one time.
     * It also allows to read very large files without running out of memory.
     */
    @Value("${location.loader.file.read.max.records}")
    protected int maxRecordsToSink;

    /**
     * This method reads records from a flat file given by resource
     * and using the reader to parse each record into a type T.
     * It then will covert records of type T to type U so that they
     * can be save in a storage medium handle by a DataSink.
     *
     * @param resource the file containing the records as a Resource
     * @param reader    The record reader that will parse lines from resource
     *                  converting them in to objects of type T.
     * @param sink      The medium to store the object parsed from the resource.
     * @param converter It converts object from type T to type U.
     * @return  The number of records saved to the sink.
     * @throws FileLoadingException any exception caught while processing records.
     */
    @Override
    public int processLocations(final Resource resource,
                                final FlatFileItemReader<T> reader,
                                final DataSink<U> sink,
                                final Converter<T, U> converter) throws FileLoadingException {
        if (resource == null || !resource.exists()) {
            LOGGER.error("Resource does not exist or it is null {}", resource == null ? "null" : resource.getFilename());
            return 0;
        }
        reader.setResource(resource);
        reader.open(executionContext);
        int createdCount = 0;
        int insertedCount = 0;
        int sourceCount = 0;
        boolean hasNext = true;
        final List<T> locations = new ArrayList<>();
        try {
            while (hasNext) {
                try {
                    sourceCount++;
                    T item = reader.read();
                    if (item == null) {
                        sourceCount--;
                        LOGGER.info("Found file end, read so far: {} ", createdCount);
                        hasNext = false;
                    } else {
                        locations.add(item);
                        createdCount++;
                    }
                } catch (UnexpectedInputException | FlatFileParseException u) {
                    LOGGER.error("Resource {} has invalid data at line {} problem:{}",
                            resource.getFilename(),sourceCount, u.getMessage());
                }
                if ((!hasNext && locations.size() > 0) || locations.size() >= maxRecordsToSink) {
                    insertedCount += sink.save(convert(locations, converter));
                    locations.clear();
                }
            }
            LOGGER.info("Records in file: {}, records created: {}, records save to sink: {}, file: {}.",
                    sourceCount,createdCount, insertedCount, resource.getFilename());
            if (createdCount != insertedCount || createdCount != sourceCount) {
                LOGGER.error("Records in file: {}, records created: {}, records save to sink: {}, file: {}.",
                        sourceCount,createdCount, insertedCount, resource.getFilename());
            }
        } catch (Exception e) {
            LOGGER.error("Error loading file {} reason : {}", resource.getFilename(), e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            throw new FileLoadingException(String.format("Error parsing file %s ", resource.getFilename()), e);
        }
        return insertedCount;
    }

    private List<U> convert(final List<T> fileLocations, Converter<T, U> converter) {
        final List<U> result = new ArrayList<>(fileLocations.size());
        for (T fl : fileLocations) {
            result.add(converter.convertTo(fl));
        }
        return result;
    }
}
