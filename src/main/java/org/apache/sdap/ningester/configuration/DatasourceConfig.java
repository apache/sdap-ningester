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
package org.apache.sdap.ningester.configuration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import org.apache.sdap.nexusproto.NexusTile;
import org.apache.sdap.ningester.configuration.properties.DatasourceProperties;
import org.apache.sdap.ningester.writer.*;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class DatasourceConfig {

    @Bean
    @Profile("default")
    public MetadataStore metadataStore() {
        return new MetadataStore() {
            @Override
            public void saveMetadata(List<? extends NexusTile> nexusTiles) {
            }

            @Override
            public void deleteMetadata(List<? extends NexusTile> nexusTiles) {
            }
        };
    }

    @Bean
    @Profile("default")
    public DataStore dataStore() {
        return nexusTiles -> {
        };
    }

    @Bean
    @Profile("embedded")
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }


    @Configuration
    @Profile("cassandra")
    @Import({CassandraDataAutoConfiguration.class, CassandraAutoConfiguration.class})
    static class CassandraConfiguration {

        @Bean
        public DataStore dataStore(CassandraTemplate cassandraTemplate) {
            return new CassandraStore(cassandraTemplate);
        }
    }

    @Configuration
    @Profile("dynamo")
    static class DynamoConfiguration {

        @Autowired
        private DatasourceProperties datasourceProperties;

        @Bean
        public AmazonDynamoDB dynamoClient() {
            AmazonDynamoDB dynamoClient = new AmazonDynamoDBClient();
            dynamoClient.setRegion(Region.getRegion(Regions.fromName(datasourceProperties.getDynamoStore().getRegion())));
            return dynamoClient;
        }

        @Bean
        public DataStore dataStore(AmazonDynamoDB dynamoClient) {
            return new DynamoStore(dynamoClient,
                    datasourceProperties.getDynamoStore().getTableName(),
                    datasourceProperties.getDynamoStore().getPrimaryKey());
        }
    }

    @Configuration
    @Profile("s3")
    static class S3Configuration {
        @Autowired
        private DatasourceProperties datasourceProperties;

        @Bean
        public AmazonS3Client s3client() {
            AmazonS3Client s3Client = new AmazonS3Client();
            s3Client.setRegion(Region.getRegion(Regions.fromName(datasourceProperties.getS3Store().getRegion())));
            return s3Client;
        }

        @Bean
        public DataStore dataStore(AmazonS3Client s3Client) {
            return new S3Store(s3Client, datasourceProperties.getS3Store().getBucketName());
        }
    }

    @Configuration
    @Profile("solr")
    @Import({SolrAutoConfiguration.class})
    static class SolrConfiguration {

        @Autowired
        private DatasourceProperties datasourceProperties;

        @Bean
        public SolrOperations solrTemplate(SolrClient solrClient) {
            return new SolrTemplate(solrClient);
        }


        @Bean
        public MetadataStore metadataStore(SolrOperations solrTemplate) {
            SolrStore solrStore = new SolrStore(solrTemplate);
            solrStore.setCollection(datasourceProperties.getSolrStore().getCollection());
            solrStore.setCommitWithin(datasourceProperties.getSolrStore().getCommitWithin());
            solrStore.setGeoPrecision(datasourceProperties.getSolrStore().getGeoPrecision());

            return solrStore;
        }
    }
}
