/*
 *****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.writer.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties
@Component("solrStoreProperties")
public class SolrStore {


    private Integer commitWithin = 1000;
    private Integer geoPrecision = 3;
    private String collection = "nexustiles";


    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Integer getCommitWithin() {
        return commitWithin;
    }

    public void setCommitWithin(Integer commitWithin) {
        this.commitWithin = commitWithin;
    }

    public Integer getGeoPrecision() {
        return geoPrecision;
    }

    public void setGeoPrecision(Integer geoPrecision) {
        this.geoPrecision = geoPrecision;
    }
}
