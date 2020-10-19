package io.bytetrend.geo.location.source.loader.fastfood;

import com.google.common.base.Stopwatch;
import io.bytetrend.geo.location.model.Location;
import io.bytetrend.geo.location.sink.DataSink;
import io.bytetrend.geo.location.source.FileUtils;
import io.bytetrend.geo.location.source.PointOfInterestLoader;
import io.bytetrend.geo.location.source.loader.FileLocationToLocationConverter;
import io.bytetrend.geo.location.source.loader.csv.CsvLoaderImpl;
import io.bytetrend.geo.location.source.model.FileLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static java.io.File.separatorChar;

/**
 * Load a file from source: https://www.kaggle.com/datafiniti/fast-food-restaurants
 * into a NoSQL Cassandra database.
 */
@Component
public class FastFoodLoaderToNoSqlCommand implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastFoodLoaderToNoSqlCommand.class);

    @Autowired
    PointOfInterestLoader<FileLocation, Location> pointOfInterestLoader;
    @Autowired
    DataSink<Location> noSqlLocationDao;

    @Value("${location.fastfood.staging.directory}")
    private String stagingDirectory;
    @Value("${location.fastfood.data.file}")
    private String dataFile;


    @Override
    public void run(String... args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        final String sourceFilePath = stagingDirectory + separatorChar + dataFile;
        LOGGER.info("Running loading location data file {}", sourceFilePath);
        final Resource resource = FileUtils.getResource(sourceFilePath);
        int count = pointOfInterestLoader.processLocations(
                resource,
                CsvLoaderImpl.FAST_FOOD.getReader(),
                noSqlLocationDao,
                new FileLocationToLocationConverter());
        LOGGER.info("Inserting {} locations from {} to sink took {} milliseconds.",
                count, dataFile,sw.elapsed(TimeUnit.MILLISECONDS));
    }

}
