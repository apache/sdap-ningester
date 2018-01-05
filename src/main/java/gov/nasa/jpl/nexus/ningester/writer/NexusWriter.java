/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.writer;

import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;

import java.util.Collection;

public class NexusWriter {

    private MetadataStore metadataStore;
    private DataStore dataStore;

    public NexusWriter(MetadataStore metadataStore, DataStore dataStore) {
        this.metadataStore = metadataStore;
        this.dataStore = dataStore;
    }

    public void saveToNexus(Collection<NexusContent.NexusTile> nexusTiles) {
        metadataStore.saveMetadata(nexusTiles);
        dataStore.saveData(nexusTiles);
    }
}
