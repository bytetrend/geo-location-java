package io.bytetrend.geo.location.sink.dao;

import io.bytetrend.geo.location.model.GeoLocationRequest;
import io.bytetrend.geo.location.model.Location;
import io.bytetrend.geo.location.sink.DataSink;

import java.util.List;
import java.util.Optional;

public interface NoSqlLocationDao extends DataSink<Location> {

    Optional<List<Location>> findLocations(GeoLocationRequest request) throws LocationDaoException;


    int insertLocations(List<Location> locations) throws LocationDaoException;

    Optional<List<Location>> fetchAll() throws LocationDaoException;
}
