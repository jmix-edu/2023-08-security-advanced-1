package com.company.jmixpm;

import com.company.jmixpm.app.RegistrationCleaner;
import com.google.common.base.Strings;
import org.quartz.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@SpringBootApplication
public class JmixPmApplication {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(JmixPmApplication.class, args);
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource")
    DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource.hikari")
    DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @EventListener
    public void printApplicationUrl(ApplicationStartedEvent event) {
        LoggerFactory.getLogger(JmixPmApplication.class).info("Application started at "
                + "http://localhost:"
                + environment.getProperty("local.server.port")
                + Strings.nullToEmpty(environment.getProperty("server.servlet.context-path")));
    }

    @Bean
    public JobDetail registrationCleaningJob() {
        return JobBuilder.newJob()
                .ofType(RegistrationCleaner.class)
                .withIdentity("registrationCleaning")
                .storeDurably() // Where or not the Job should remain
                // stored after ot is orphaned (no triggers point to it)
                .build();
    }

    @Bean
    public Trigger registrationCleaningTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("registrationCleaningTrigger")
                .forJob(registrationCleaningJob())
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?")) // every minute at 00
                .build();
    }
}
