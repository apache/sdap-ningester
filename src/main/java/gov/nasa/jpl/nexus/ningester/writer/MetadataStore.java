/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.writer;

import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;

import java.util.List;

public interface MetadataStore {

    void saveMetadata(List<? extends NexusContent.NexusTile> nexusTiles);
}
