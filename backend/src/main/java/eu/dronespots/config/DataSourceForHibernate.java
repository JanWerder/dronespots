package eu.dronespots.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceForHibernate {

	Logger logger = LoggerFactory.getLogger(DataSourceForHibernate.class);

	@Autowired
	Environment env;

	@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();

		logger.info("DB driver name: " + env.getProperty("custom.dronespots.db.driverClassName"));
		logger.info("DB url: " + env.getProperty("custom.dronespots.db.url"));
		logger.info("DB user: " + env.getProperty("custom.dronespots.db.user"));

		dataSource.setDriverClassName(env.getProperty("custom.dronespots.db.driverClassName"));
		dataSource.setUrl(env.getProperty("custom.dronespots.db.url"));
		dataSource.setUsername(env.getProperty("custom.dronespots.db.user"));
		dataSource.setPassword(env.getProperty("custom.dronespots.db.password"));
		return dataSource;
	}
}