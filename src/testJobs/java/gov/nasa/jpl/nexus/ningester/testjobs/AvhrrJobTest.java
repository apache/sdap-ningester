/*
 *****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.testjobs;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.config.location = classpath:testjobs/AvhrrJobTest.yml"})
@ActiveProfiles({"test"})
public class AvhrrJobTest {

    @TestConfiguration
    static class NingesterApplicationTestsConfig {

        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }

        @Bean
        ItemWriter<NexusContent.NexusTile> writer() {
            return items -> System.out.println("Wrote " + items.size() + " item(s).");
        }

    }

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;


    @Test
    public void testJobCompletes() throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("granule", "classpath:granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc")
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }
}
