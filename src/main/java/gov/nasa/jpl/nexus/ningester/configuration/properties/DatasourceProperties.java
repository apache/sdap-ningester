/*
 *****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.configuration.properties;

import gov.nasa.jpl.nexus.ningester.writer.properties.CassandraStore;
import gov.nasa.jpl.nexus.ningester.writer.properties.DynamoStore;
import gov.nasa.jpl.nexus.ningester.writer.properties.S3Store;
import gov.nasa.jpl.nexus.ningester.writer.properties.SolrStore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@ConfigurationProperties("datasource")
@Component
public class DatasourceProperties {

    @NestedConfigurationProperty
    private final CassandraStore cassandraStore = new CassandraStore();

    @NestedConfigurationProperty
    private final DynamoStore dynamoStore = new DynamoStore();

    @NestedConfigurationProperty
    private final S3Store s3Store = new S3Store();

    @NestedConfigurationProperty
    private final SolrStore solrStore = new SolrStore();

    public DynamoStore getDynamoStore() {
        return dynamoStore;
    }

    public S3Store getS3Store() {
        return s3Store;
    }

    public SolrStore getSolrStore() {
        return solrStore;
    }

    public CassandraStore getCassandraStore() {
        return cassandraStore;
    }
}
