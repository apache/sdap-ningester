/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
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
@TestPropertySource(properties = {"spring.config.location = classpath:testjobs/SmapJobTest.yml"})
@ActiveProfiles({"test", "cassandra", "solr"})
public class SmapJobTest {

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
                .addString("granule", "classpath:granules/SMAP_L2B_SSS_04892_20160101T005507_R13080.h5")
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat(jobExecution.getExitStatus().getExitCode(), is("COMPLETED"));
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getReadCount(), is(1624));
        assertThat(stepExecution.getWriteCount(), is(990));
        assertThat(stepExecution.getFilterCount(), is(634));

        assertEqualsEventually(990L,
                () -> solrTemplate.count(datasourceProperties.getSolrStore().getCollection(),
                        new SimpleQuery("dataset_s: " + applicationProperties.getAddDatasetName().getDatasetName())),
                2);

        long cassandraCount = cassandraTemplate.count(datasourceProperties.getCassandraStore().getTableName());

        assertThat(cassandraCount, is(990L));


    }

    @TestConfiguration
    static class NingesterApplicationTestsConfig {

        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }

    }
}
