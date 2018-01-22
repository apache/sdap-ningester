/*
 ******************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.apache.sdap.nexusproto.NexusTile;

public class AddDatasetName {

    String datasetName;

    public AddDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public NexusTile addDatasetName(NexusTile inputTile) {

        NexusTile.Builder outTileBuilder = NexusTile.newBuilder().mergeFrom(inputTile);

        outTileBuilder.getSummaryBuilder().setDatasetName(datasetName);

        return outTileBuilder.build();

    }
}