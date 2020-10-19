package io.bytetrend.geo.location.source.loader.csv;

import io.bytetrend.geo.location.model.LocationType;
import io.bytetrend.geo.location.source.loader.fastfood.FastFoodFileFieldSetMapper;
import io.bytetrend.geo.location.source.loader.fastfood.FastFoodToFileLocation;
import io.bytetrend.geo.location.source.loader.starbucks.StarbucksFileFieldSetMapper;
import io.bytetrend.geo.location.source.loader.starbucks.StarbucksToFileLocation;
import io.bytetrend.geo.location.source.model.FileLocation;
import org.springframework.batch.item.file.FlatFileItemReader;

import java.util.EnumSet;

import static io.bytetrend.geo.location.source.loader.SourceSinkColumnNames.fastFoodCsvColumns;
import static io.bytetrend.geo.location.source.loader.SourceSinkColumnNames.starbucksCsvColumns;

/**
 * This Enum return readers of CVS files that reads the records in the file
 * and mapped them into FileLocation objects.
 */
public enum CsvLoaderImpl implements CsvLoader<FileLocation> {
    STARBUCKS() {
        public FlatFileItemReader<FileLocation> getReader() {
            return new CsvReader<>(
                    starbucksCsvColumns,
                    LocationType.COFFEE_SHOP,
                    new StarbucksFileFieldSetMapper<>(EnumSet.allOf(StarbucksToFileLocation.class),
                            LocationType.COFFEE_SHOP)
            );
        }
    },
    FAST_FOOD() {
        public FlatFileItemReader<FileLocation> getReader() {
            return new CsvReader<>(
                    fastFoodCsvColumns,
                    LocationType.FAST_FOOD,
                    new FastFoodFileFieldSetMapper<>(EnumSet.allOf(FastFoodToFileLocation.class),
                            LocationType.FAST_FOOD)
            );
        }
    };

    public abstract FlatFileItemReader<FileLocation> getReader();

}
