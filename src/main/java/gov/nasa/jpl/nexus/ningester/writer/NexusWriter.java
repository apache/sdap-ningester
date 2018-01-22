/*
 ****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.writer;

import org.apache.sdap.nexusproto.NexusTile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NexusWriter {

    private static final Logger log = LoggerFactory.getLogger(NexusWriter.class);
    private MetadataStore metadataStore;
    private DataStore dataStore;

    public NexusWriter(MetadataStore metadataStore, DataStore dataStore) {
        this.metadataStore = metadataStore;
        this.dataStore = dataStore;
    }

    public void saveToNexus(List<? extends NexusTile> nexusTiles) {
        if (nexusTiles.size() > 0) {
            metadataStore.saveMetadata(nexusTiles);

            try {
                dataStore.saveData(nexusTiles);

            } catch (RuntimeException e) {
                try {
                    metadataStore.deleteMetadata(nexusTiles);
                } catch (RuntimeException e2) {
                    log.error("During exception while saving data, could not rollback metadata", e2);
                }
                throw e;
            }
        }
    }

}
