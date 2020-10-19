package io.bytetrend.geo.location.source.loader.csv;

import io.bytetrend.geo.location.model.LocationType;
import io.bytetrend.geo.location.source.FileParsingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;

import javax.annotation.Nonnull;

public class CsvReader<T> extends FlatFileItemReader<T> {

    CsvReader(final String[] csvColumnNames, final LocationType locationType, final FieldSetMapper<T> fieldSetMapper) {
        //Skip the header line
        setLinesToSkip(1);
        setSkippedLinesCallback(new LocationLineCallbackHandler(csvColumnNames));
        setLineMapper(new DefaultLineMapper<T>() {
            {
                DelimitedLineTokenizer lt = new DelimitedLineTokenizer();
                lt.setNames(csvColumnNames);
                setLineTokenizer(lt);

                //Set values in LocationFile class
                setFieldSetMapper(fieldSetMapper);

            }
        });
    }

    static class LocationLineCallbackHandler implements LineCallbackHandler {
        final private String[] columnNames;

        public LocationLineCallbackHandler(String[] columnNames) {
            this.columnNames = columnNames;
        }

        @Override
        public void handleLine(@Nonnull String line) {
            if (!StringUtils.trimToEmpty(line).isEmpty()) {
                LineTokenizer lineTokenizer = new DelimitedLineTokenizer();
                FieldSet fieldSet = lineTokenizer.tokenize(line);
                if (fieldSet.getFieldCount() != columnNames.length) {
                    throw new FileParsingException(String.format("Line count %d is different than expected %d",
                            fieldSet.getFieldCount(), columnNames.length));
                }
                String[] csvColumns = fieldSet.getValues();

                //Using indexOf in case the column name has quotes in the file.
                for (int i = 0; i < columnNames.length; i++) {
                    if (!columnNames[i].contains(csvColumns[i]))
                        throw new FileParsingException("column name mismatch " + columnNames[i] + " <> " + csvColumns[i]);
                }
            }
        }
    }
}

