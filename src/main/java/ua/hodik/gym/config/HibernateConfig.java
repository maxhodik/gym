package ua.hodik.gym.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "ua.hodik.gym")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ua.hodik.gym")
public class HibernateConfig {
    @Value("${hibernate.driver-class}")
    private String hibernateDriverClass;
    @Value("${hibernate.connection.url}")
    private String hibernateConnectionUrl;

    @Value("${hibernate.username}")
    private String userName;
    @Value("${hibernate.password}")
    private String password;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Value("${hibernate.show_sql}")
    private String showSql;
    @Value("${hibernate.hbm2ddl.auto}")
    private String ddl;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(hibernateDriverClass);
        dataSource.setUrl((hibernateConnectionUrl));
        dataSource.setUsername((userName));
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan("ua.hodik.gym.model");
        sessionFactoryBean.setHibernateProperties(hibernateProperties());
        return sessionFactoryBean;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.hbm2ddl.auto", ddl);

        return properties;
    }
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}