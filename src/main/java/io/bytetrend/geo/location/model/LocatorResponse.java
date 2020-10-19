package io.bytetrend.geo.location.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LocatorResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "List of point of interest",required = true)
    private List<GeoResult> locations;

    public LocatorResponse(List<GeoResult> data){
        locations = data;
    }

    public List<GeoResult> getLocations() {
        return locations;
    }

}
