/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors.properties;

import java.util.HashMap;
import java.util.Map;

public class PythonProcessorModule {

    private String name;
    private Map<String, String> config = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getConfig() {
        return config;
    }
}
