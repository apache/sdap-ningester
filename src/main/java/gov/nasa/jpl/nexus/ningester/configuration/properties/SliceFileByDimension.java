/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties
@Component("sliceFileByDimensionProperties")
public class SliceFileByDimension {

    private String sliceByDimension;
    private List<String> dimensions;
    private String dimensionNamePrefix = "";

    public String getSliceByDimension() {
        return sliceByDimension;
    }

    public void setSliceByDimension(String sliceByDimension) {
        this.sliceByDimension = sliceByDimension;
    }

    public List<String> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<String> dimensions) {
        this.dimensions = dimensions;
    }

    public String getDimensionNamePrefix() {
        return dimensionNamePrefix;
    }

    public void setDimensionNamePrefix(String dimensionNamePrefix) {
        this.dimensionNamePrefix = dimensionNamePrefix;
    }
}
