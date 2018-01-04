/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.testjobs;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
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
@TestPropertySource(properties = { "spring.config.location = classpath:testjobs/SmapJobTest.yml" })
@ActiveProfiles({"test"})
public class SmapJobTest {

    @TestConfiguration
    static class NingesterApplicationTestsConfig {

        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }

    }

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;


    @Test
    public void testJobCompletes() throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("granule", "classpath:granules/SMAP_L2B_SSS_04892_20160101T005507_R13080.h5")
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }
}
