package com.yhl.integrate.componet.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Configuration
@Component
@AutoConfigureAfter(DatasourceConfigration.class)
public class HiveConfig {

    @Bean(name = "hiveDruidTemplate")
    public JdbcTemplate hiveDruidTemplate(@Qualifier("datasource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
