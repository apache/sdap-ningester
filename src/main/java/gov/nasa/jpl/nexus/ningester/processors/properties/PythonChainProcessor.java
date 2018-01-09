/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties
@Component("pythonChainProcessorProperties")
public class PythonChainProcessor {
    private URL baseUrl;
    private String uriPath;

    private List<PythonProcessorModule> processorList = new ArrayList<>();

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public List<PythonProcessorModule> getProcessorList() {
        return processorList;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }
}
