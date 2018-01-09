/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.writer.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("solr")
@Component("solrStoreProperties")
public class SolrStore {


    private String url;
    private String zkHost;
    private String collection;


    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
