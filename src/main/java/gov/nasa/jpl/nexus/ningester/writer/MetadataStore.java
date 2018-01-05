/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.writer;

import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;

import java.util.Collection;

public interface MetadataStore {

    void saveMetadata(Collection<NexusContent.NexusTile> nexusTiles);
}
