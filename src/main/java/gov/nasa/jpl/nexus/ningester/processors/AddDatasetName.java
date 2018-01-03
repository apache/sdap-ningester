/*
 ******************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;


public class AddDatasetName {

    String datasetName;

    public AddDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public NexusContent.NexusTile addDatasetName(NexusContent.NexusTile inputTile) {

        NexusContent.NexusTile.Builder outTileBuilder = NexusContent.NexusTile.newBuilder().mergeFrom(inputTile);

        outTileBuilder.getSummaryBuilder().setDatasetName(datasetName);

        return outTileBuilder.build();

    }
}