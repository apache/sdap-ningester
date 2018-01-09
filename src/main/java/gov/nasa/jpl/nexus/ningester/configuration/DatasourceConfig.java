package gov.nasa.jpl.nexus.ningester.configuration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import gov.nasa.jpl.nexus.ningester.configuration.properties.DatasourceProperties;
import gov.nasa.jpl.nexus.ningester.writer.*;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {

    private final DatasourceProperties datasourceProperties;

    @Autowired
    public DatasourceConfig(DatasourceProperties datasourceProperties) {
        this.datasourceProperties = datasourceProperties;
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
    class CassandraConfiguration {
        @Bean
        public CassandraClusterFactoryBean cluster() {

            CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
            cluster.setContactPoints(datasourceProperties.getCassandraStore().getContactPoints());
            cluster.setPort(datasourceProperties.getCassandraStore().getPort());

            return cluster;
        }

        @Bean
        public CassandraMappingContext mappingContext() {
            return new BasicCassandraMappingContext();
        }

        @Bean
        public CassandraConverter converter() {
            return new MappingCassandraConverter(mappingContext());
        }

        @Bean
        public CassandraSessionFactoryBean session(){

            CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
            session.setCluster(cluster().getObject());
            session.setKeyspaceName(datasourceProperties.getCassandraStore().getKeyspace());
            session.setConverter(converter());
            session.setSchemaAction(SchemaAction.NONE);

            return session;
        }

        @Bean
        public CassandraOperations cassandraTemplate(){
            return new CassandraTemplate(session().getObject());
        }

        @Bean
        public DataStore dataStore(CassandraOperations cassandraTemplate) {
            return new CassandraStore(cassandraTemplate);
        }
    }

    @Configuration
    @Profile("dynamo")
    class DynamoConfiguration {
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
    class S3Configuration {
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
    @Profile("solr-standalone")
    class SolrConfiguration {
        @Bean
        public SolrClient solrClient(){ return new HttpSolrClient(datasourceProperties.getSolrStore().getUrl() + datasourceProperties.getSolrStore().getCollection());}

        @Bean
        public SolrOperations solrTemplate(SolrClient solrClient) {
            return new SolrTemplate(solrClient);
        }

        @Bean
        public MetadataStore metadataStore(SolrOperations solrTemplate) {
            return new SolrStore(solrTemplate);
        }
    }

    @Configuration
    @Profile("solr-cloud")
    class SolrCloudConfiguration {
        @Bean
        public SolrClient solrClient(){
            CloudSolrClient client = new CloudSolrClient(datasourceProperties.getSolrStore().getZkHost());
            client.setDefaultCollection(datasourceProperties.getSolrStore().getCollection());

            return client;
        }

        @Bean
        public SolrOperations solrTemplate(SolrClient solrClient) {
            return new SolrTemplate(solrClient);
        }

        @Bean
        public MetadataStore metadataStore(SolrOperations solrTemplate) {
            return new SolrStore(solrTemplate);
        }
    }
}
