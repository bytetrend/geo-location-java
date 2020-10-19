package io.bytetrend.geo.location.rest;

import com.google.common.base.Stopwatch;
import io.bytetrend.geo.location.model.RestResponse;
import io.bytetrend.geo.location.model.GeoLocationRequest;
import io.bytetrend.geo.location.model.GeoResult;
import io.bytetrend.geo.location.model.LocatorResponse;
import io.bytetrend.geo.location.services.PointOfInterestService;
import io.bytetrend.geo.location.services.exception.ServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.bytetrend.geo.location.model.RestResponse.ErrorCode.UNKNOWN_ERROR;

@RestController
@Api("Point Of Interest Service")
public class PointOfInterestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PointOfInterestController.class);

    @Value("${location.search.default.miles.radius}")
    private int searchMilesRadius;
    @Value("${location.search.maximum.addresses}")
    private int maximumAddresses;

    @Autowired
    PointOfInterestService pointOfInterestService;

    private String webpage;

    @ExceptionHandler({Exception.class})
    public ResponseEntity<RestResponse> handleAll(Exception e, WebRequest r) {
        return new ResponseEntity<>(
                new RestResponse(e.getMessage(), UNKNOWN_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String welcomePage() {
        if (webpage == null) {
            try {
                File page = ResourceUtils.getFile("classpath:index.html");
                UrlResource urlResource = new UrlResource(page.toURI());
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlResource.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                webpage = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return webpage;
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public  String update() {
        try {
            int count = pointOfInterestService.loadLocation();
            return String.format("Loaded %d locations ", count);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getMessage(e));
        }
        return "Failure loading location data";
    }

    /**
     * Returns a JSON array with the matching locations example:
     * [
     * {
     * "pointOfInterest": {
     * "name": "Burger King",
     * "address": {
     * "address1": "Cc Campanar Valencia 3, Manuel De Falla,13",
     * "address2": null,
     * "city": "Anderson",
     * "state": "IN",
     * "zipCode": "46015",
     * "country": "US"
     * },
     * "geoLocation": {
     * "latitude": 39.476562,
     * "longitude": -0.40550736
     * },
     * "type": "ALL"
     * },
     * "center": {
     * "latitude": 39.47656,
     * "longitude": -0.405507
     * },
     * "distance": 0.0002561061
     * }
     * ]
     *
     * @param latitude         center latitude for search
     * @param longitude        center longitude for search
     * @param radius           radius of the search from center point.
     * @param maxLocationCount maximum number of addresses to return.
     * @param locationType     Filter by location type
     * @param sourceType       what kind of underlying source to search i.e: NOSQL, SQL, MEMCACHE
     * @return a JSON array of addresses/names
     * @throws ServiceException when an exception occurs
     */
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = LocatorResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = LocatorResponse.class),
            @ApiResponse(code = 401, message = "Authorization Failure", response = LocatorResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = LocatorResponse.class),
            @ApiResponse(code = 405, message = "Method Not Allowed", response = LocatorResponse.class),
            @ApiResponse(code = 409, message = "Request could not be completed."),
            @ApiResponse(code = 500, message = "Internal Server Error.")
    }
    )
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LocatorResponse> search(
            @ApiParam(required = true) @RequestParam("latitude") Float latitude,
            @ApiParam(required = true) @RequestParam("longitude") Float longitude,
            @ApiParam(required = false) @RequestParam(value = "radius", required = false, defaultValue = "30") Integer radius,
            @ApiParam(required = false) @RequestParam(value = "maxLocationCount", required = false, defaultValue = "20") Integer maxLocationCount,
            @ApiParam(required = false) @RequestParam(value = "locationType", required = false, defaultValue = "ALL") String locationType,
            @ApiParam(required = false) @RequestParam(value = "sourceType", required = false, defaultValue = "NO_SQL") String sourceType) throws ServiceException {
        LocatorResponse resp = null;
        Stopwatch sw = Stopwatch.createStarted();
        try {
            if (maxLocationCount < 1) {
                maxLocationCount = maximumAddresses;
            }
            if (radius < 0) {
                radius = searchMilesRadius;
            }
            GeoLocationRequest.Builder builder = new GeoLocationRequest.Builder();
            GeoLocationRequest request = builder.setLatitude(latitude).setLongitude(longitude).setRadius(radius)
                    .setMaxItems(maxLocationCount).setLocationType(locationType)
                    .setSourceType(sourceType).build();
            List<GeoResult> list = pointOfInterestService.searchLocations(request);
            //result = JSONMapper.getJsonFromBean(list);
            resp = new LocatorResponse(list);
            LOGGER.info("Search returned {}, locations from latitude {}, " +
                            "longitude {}, radius {}, max count {}, " +
                            "location type {}, source type {}", list.size(), latitude, longitude,
                    radius, maxLocationCount, locationType, sourceType);
        } catch (Exception e) {
            LOGGER.error("Exception fetching data from service with parameters " +
                            "latitude {}, " +
                            "longitude {}, radius {}, max count {}, " +
                            "location type {}, source type {}, reason: {}", latitude, longitude,
                    radius, maxLocationCount, locationType, sourceType, ExceptionUtils.getMessage(e));
        }
        LOGGER.info("Search took {} milliseconds ", sw.elapsed(TimeUnit.MILLISECONDS));
        LOGGER.debug("Returning response {}", resp);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
