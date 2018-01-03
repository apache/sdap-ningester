/*
 ******************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.junit.Test;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestSetDatasetName {

    @Test
    public void testDatasetName() {

        String datasetName = "testDataset";

        NexusContent.NexusTile input = NexusContent.NexusTile.newBuilder().build();

        SetDatasetName processor = new SetDatasetName(datasetName);

        NexusContent.NexusTile result = processor.addDatasetName(input);

        assertThat(result.getSummary().getDatasetName(), is(datasetName));

    }
}