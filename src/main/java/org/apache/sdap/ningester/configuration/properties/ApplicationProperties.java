/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.sdap.ningester.configuration.properties;

import org.apache.sdap.ningester.datatiler.properties.SliceFileByDimension;
import org.apache.sdap.ningester.datatiler.properties.SliceFileByTilesDesired;
import org.apache.sdap.ningester.processors.properties.*;
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
