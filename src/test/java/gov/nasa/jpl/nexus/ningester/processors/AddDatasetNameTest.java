/*
 ******************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.apache.sdap.nexusproto.NexusTile;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AddDatasetNameTest {

    @Test
    public void testDatasetName() {

        String datasetName = "testDataset";

        NexusTile input = NexusTile.newBuilder().build();

        AddDatasetName processor = new AddDatasetName(datasetName);

        NexusTile result = processor.addDatasetName(input);

        assertThat(result.getSummary().getDatasetName(), is(datasetName));

    }
}