/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.writer;

import org.apache.sdap.nexusproto.NexusTile;

import java.util.List;

public interface MetadataStore {

    void saveMetadata(List<? extends NexusTile> nexusTiles);

    void deleteMetadata(List<? extends NexusTile> nexusTiles);
}
