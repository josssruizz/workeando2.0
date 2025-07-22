package Workeando20.erp.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "Workeando20.erp.dominio.repository",
    entityManagerFactoryRef = "dominioEntityManagerFactory",
    transactionManagerRef = "dominioTransactionManager"
)
public class DataSourceDominioConfig {

    @Bean(name = "dominioDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.dominio")
    public DataSource dominioDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dominioEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dominioEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dominioDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");

        return builder
                .dataSource(dataSource)
                .packages("Workeando20.erp.dominio.model")
                .persistenceUnit("dominio")
                .properties(properties)
                .build();
    }

    @Bean(name = "dominioTransactionManager")
    public PlatformTransactionManager dominioTransactionManager(
            @Qualifier("dominioEntityManagerFactory") EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}
