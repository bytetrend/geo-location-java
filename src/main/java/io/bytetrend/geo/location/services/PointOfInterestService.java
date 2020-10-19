package io.bytetrend.geo.location.services;

import io.bytetrend.geo.location.model.GeoLocationRequest;
import io.bytetrend.geo.location.model.GeoResult;
import io.bytetrend.geo.location.services.exception.ServiceException;
import io.bytetrend.geo.location.sink.dao.LocationDaoException;

import java.util.List;

public interface PointOfInterestService {

    List<GeoResult> searchLocations(GeoLocationRequest request) throws ServiceException, LocationDaoException;

    int loadLocation() throws ServiceException;

}
