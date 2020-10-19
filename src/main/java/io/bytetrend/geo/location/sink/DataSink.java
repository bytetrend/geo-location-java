package io.bytetrend.geo.location.sink;

import io.bytetrend.geo.location.sink.dao.LocationDaoException;

import java.util.List;

public interface DataSink<T> {

    int save(List<T> data) throws SinkException, LocationDaoException;
}
