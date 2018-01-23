/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.sdap.ningester.configuration.properties;

import org.apache.sdap.ningester.writer.properties.CassandraStore;
import org.apache.sdap.ningester.writer.properties.DynamoStore;
import org.apache.sdap.ningester.writer.properties.S3Store;
import org.apache.sdap.ningester.writer.properties.SolrStore;
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
