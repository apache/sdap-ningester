/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("ningester")
@Component
public class ApplicationProperties {

    private List<String> tileProcessors = new ArrayList<>();

    @NestedConfigurationProperty
    private final AddTimeToSectionSpec addTimeToSectionSpec = new AddTimeToSectionSpec();

    @NestedConfigurationProperty
    private final PythonChainProperties pythonChainProperties = new PythonChainProperties();

    private final Ningesterpy ningesterPy = new Ningesterpy();

    public Ningesterpy getNingesterpy() {
        return ningesterPy;
    }

    public PythonChainProperties getPythonChainProperties() {
        return pythonChainProperties;
    }

    public List<String> getTileProcessors() {
        return tileProcessors;
    }

    public AddTimeToSectionSpec getAddTimeToSectionSpec() {
        return addTimeToSectionSpec;
    }


    public static class Ningesterpy {

        private URL baseUrl;

        public URL getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(URL baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

}
