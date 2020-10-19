package io.bytetrend.geo.location.source.loader.csv;

import org.springframework.batch.item.file.FlatFileItemReader;

public interface CsvLoader<T> {
    FlatFileItemReader<T> getReader();
}
