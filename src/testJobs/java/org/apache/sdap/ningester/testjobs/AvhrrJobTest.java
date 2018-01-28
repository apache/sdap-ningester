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


package org.apache.sdap.ningester.testjobs;

import org.apache.sdap.ningester.configuration.properties.ApplicationProperties;
import org.apache.sdap.ningester.configuration.properties.DatasourceProperties;
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

import static org.apache.sdap.ningester.testjobs.TestUtils.assertEqualsEventually;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.config.location = classpath:testjobs/AvhrrJobTest.yml"})
@ActiveProfiles({"test", "cassandra", "solr"})
public class AvhrrJobTest {

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
        assertThat(stepExecution.getReadCount(), is(1296));
        assertThat(stepExecution.getWriteCount(), is(1053));
        assertThat(stepExecution.getFilterCount(), is(243));

        assertEqualsEventually(1053L,
                () -> solrTemplate.count(datasourceProperties.getSolrStore().getCollection(),
                        new SimpleQuery("dataset_s: " + applicationProperties.getAddDatasetName().getDatasetName())),
                3);

        long cassandraCount = cassandraTemplate.count(datasourceProperties.getCassandraStore().getTableName());

        assertThat(cassandraCount, is(1053L));
    }

    @TestConfiguration
    static class NingesterApplicationTestsConfig {

        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }

    }


}
