package gov.nasa.jpl.nexus.ningester;

import gov.nasa.jpl.nexus.ningester.configuration.BatchConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NingesterApplicationTests {

	@Configuration
	@Import({BatchConfig.class})
	static class NingesterApplicationTestsConfig{
		@Bean
		JobLauncherTestUtils jobLauncherTestUtils(){
			return new JobLauncherTestUtils();
		}

	}

	@Autowired JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testJobCompletes() throws Exception {

		JobParameters jobParameters = new JobParametersBuilder()
				.addString("granule", "classpath:granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc")
				.toJobParameters();

		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

		Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
	}

}
