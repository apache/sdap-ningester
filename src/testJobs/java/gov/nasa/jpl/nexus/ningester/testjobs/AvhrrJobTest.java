/*
 *****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.testjobs;

import gov.nasa.jpl.nexus.ningester.configuration.properties.ApplicationProperties;
import gov.nasa.jpl.nexus.ningester.configuration.properties.DatasourceProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static gov.nasa.jpl.nexus.ningester.testjobs.TestUtils.assertEqualsEventually;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.config.location = classpath:testjobs/AvhrrJobTest.yml"})
@ActiveProfiles({"test", "cassandra", "solr"})
public class AvhrrJobTest {

    @TestConfiguration
    static class NingesterApplicationTestsConfig {

        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }

    }

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    CassandraTemplate cassandraTemplate;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    DatasourceProperties datasourceProperties;

    @Autowired
    ApplicationProperties applicationProperties;

    @Before
    public void emptyDatabase() {
        solrTemplate.delete(datasourceProperties.getSolrStore().getCollection(), new SimpleQuery("*:*"));
        cassandraTemplate.truncate(datasourceProperties.getCassandraStore().getTableName());
    }


    @Test
    public void testJobCompletes() throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("granule", "classpath:granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc")
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat(jobExecution.getExitStatus().getExitCode(), is("COMPLETED"));
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getReadCount(), is(5184));
        assertThat(stepExecution.getWriteCount(), is(3904));
        assertThat(stepExecution.getFilterCount(), is(1280));

        assertEqualsEventually(3904L,
                () -> solrTemplate.count(datasourceProperties.getSolrStore().getCollection(),
                        new SimpleQuery("dataset_s: " + applicationProperties.getAddDatasetName().getDatasetName())),
                3);

        long cassandraCount = cassandraTemplate.count(datasourceProperties.getCassandraStore().getTableName());

        assertThat(cassandraCount, is(3904L));
    }


}
