package gov.nasa.jpl.nexus.ningester.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {

  @Bean
  @Profile("embedded")
  public DataSource dataSource(){
    EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
    return builder
        .setType(EmbeddedDatabaseType.H2)
        .build();
  }
}
