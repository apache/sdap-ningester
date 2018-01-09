/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.datatiler.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties
@Component("sliceFileByTilesDesiredProperties")
public class SliceFileByTilesDesired {
    private Integer tilesDesired;
    private List<String> dimensions = new ArrayList<>();
    private String timeDimension;

    public Integer getTilesDesired() {
        return tilesDesired;
    }

    public void setTilesDesired(Integer tilesDesired) {
        this.tilesDesired = tilesDesired;
    }

    public List<String> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<String> dimensions) {
        this.dimensions = dimensions;
    }

    public String getTimeDimension() {
        return timeDimension;
    }

    public void setTimeDimension(String timeDimension) {
        this.timeDimension = timeDimension;
    }
}
