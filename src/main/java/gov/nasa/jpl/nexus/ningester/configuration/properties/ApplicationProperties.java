/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.configuration.properties;

import gov.nasa.jpl.nexus.ningester.datatiler.properties.SliceFileByDimension;
import gov.nasa.jpl.nexus.ningester.datatiler.properties.SliceFileByTilesDesired;
import gov.nasa.jpl.nexus.ningester.processors.properties.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("ningester")
@Component
public class ApplicationProperties {

    @NestedConfigurationProperty
    private final SliceFileByDimension sliceFileByDimension = new SliceFileByDimension();
    @NestedConfigurationProperty
    private final SliceFileByTilesDesired sliceFileByTilesDesired = new SliceFileByTilesDesired();
    @NestedConfigurationProperty
    private final AddDayOfYearAttribute addDayOfYearAttribute = new AddDayOfYearAttribute();
    @NestedConfigurationProperty
    private final AddTimeFromGranuleName addTimeFromGranuleName = new AddTimeFromGranuleName();
    @NestedConfigurationProperty
    private final GenerateTileId generateTileId = new GenerateTileId();
    @NestedConfigurationProperty
    private final PythonChainProcessor pythonChainProcessor = new PythonChainProcessor();
    @NestedConfigurationProperty
    private final AddDatasetName addDatasetName = new AddDatasetName();
    private String tileSlicer;
    private List<String> tileProcessors = new ArrayList<>();

    public PythonChainProcessor getPythonChainProcessor() {
        return pythonChainProcessor;
    }

    public List<String> getTileProcessors() {
        return tileProcessors;
    }

    public AddDayOfYearAttribute getAddDayOfYearAttribute() {
        return addDayOfYearAttribute;
    }

    public AddTimeFromGranuleName getAddTimeFromGranuleName() {
        return addTimeFromGranuleName;
    }

    public GenerateTileId getGenerateTileId() {
        return generateTileId;
    }

    public AddDatasetName getAddDatasetName() {
        return addDatasetName;
    }

    public String getTileSlicer() {
        return tileSlicer;
    }

    public void setTileSlicer(String tileSlicer) {
        this.tileSlicer = tileSlicer;
    }

    public SliceFileByTilesDesired getSliceFileByTilesDesired() {
        return sliceFileByTilesDesired;
    }

    public SliceFileByDimension getSliceFileByDimension() {
        return sliceFileByDimension;
    }
}
