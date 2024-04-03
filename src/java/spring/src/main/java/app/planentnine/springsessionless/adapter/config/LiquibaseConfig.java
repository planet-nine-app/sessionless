package app.planentnine.springsessionless.adapter.config;

import liquibase.integration.spring.SpringLiquibase;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {
    
    @Bean
    public SpringLiquibase liquibase(@Qualifier("dataSource") DataSource dataSource, DataSourceProperties dataSourceProperties) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(dataSourceProperties.getChangeLog());
        liquibase.setShouldRun(dataSourceProperties.isEnabled());
        return liquibase;
    }
    
    @Configuration("liquibaseProperties")
    @ConfigurationProperties(prefix = "spring.liquibase")
    @Data
    public static class DataSourceProperties {
        
        private String changeLog;
        private boolean enabled;
    }
}
