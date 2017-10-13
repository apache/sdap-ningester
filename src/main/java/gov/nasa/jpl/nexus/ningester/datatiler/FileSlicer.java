/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.datatiler;

import java.io.File;
import java.util.List;

public interface FileSlicer {

    List<String> generateSlices(File inputfile);

}
