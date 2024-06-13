package epc.epcsalesapi.config;

import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class HikariCPConfig {

	@Value("${EPCAPI_EPC_DB_CONN_STR}")
	private String epcConnStr;

	@Value("${EPCAPI_EPC_DB_USERNAME}")
	private String epcUsername;

	@Value("${EPCAPI_EPC_DB_PWD}")
	private String epcPwd;

	@Value("${EPCAPI_EPC_DB_DRIVER}")
	private String epcDbDriver; // oracle.jdbc.OracleDriver

	@Value("${EPCAPI_EPC_DB_POOLNAME}")
	private String epcPoolName; // epcConnPool

	//@Value("${EPCAPI_EPC_DB_MAX_CONN:10}")
	@Value("${EPCAPI_EPC_DB_MAX_CONN:3}")
	private int epcMaxNoOfConn; // max no. of conn, default = 10 conn

	@Value("${EPCAPI_FES_DB_CONN_STR}")
	private String fesConnStr;

	@Value("${EPCAPI_FES_DB_USERNAME}")
	private String fesUsername;

	@Value("${EPCAPI_FES_DB_PWD}")
	private String fesPwd;

	@Value("${EPCAPI_FES_DB_DRIVER}")
	private String fesDbDriver; // oracle.jdbc.OracleDriver

	@Value("${EPCAPI_FES_DB_POOLNAME}")
	private String fesPoolName; // epcConnPool

	//@Value("${EPCAPI_FES_DB_MAX_CONN:10}")
	@Value("${EPCAPI_FES_DB_MAX_CONN:3}")
	private int fesMaxNoOfConn; // max no. of conn, default = 10 conn

	@Value("${EPCAPI_FESCRM_DB_CONN_STR}")
	private String crmFesConnStr;

	@Value("${EPCAPI_FESCRM_DB_USERNAME}")
	private String crmFesUsername;

	@Value("${EPCAPI_FESCRM_DB_PWD}")
	private String crmFesPwd;

	@Value("${EPCAPI_FESCRM_DB_DRIVER}")
	private String crmFesDbDriver; // oracle.jdbc.OracleDriver

	@Value("${EPCAPI_FESCRM_DB_POOLNAME}")
	private String crmFesPoolName; // epcConnPool

	//@Value("${EPCAPI_FESCRM_DB_MAX_CONN:10}")
	@Value("${EPCAPI_FESCRM_DB_MAX_CONN:3}")
	private int crmFesMaxNoOfConn; // max no. of conn, default = 10 conn

	// added by Danny Chan on 2021-6-25 (Apple Care enhancement): start
	/*@Value("${EPCAPI_FESCRYPTO_DB_CONN_STR}")
	private String fescryptoConnStr;

	@Value("${EPCAPI_FESCRYPTO_DB_USERNAME}")
	private String fescryptoUsername;

	@Value("${EPCAPI_FESCRYPTO_DB_PWD}")
	private String fescryptoPwd;

	@Value("${EPCAPI_FESCRYPTO_DB_DRIVER}")
	private String fescryptoDbDriver; // oracle.jdbc.OracleDriver

	@Value("${EPCAPI_FESCRYPTO_DB_POOLNAME}")
	private String fescryptoPoolName;

	@Value("${EPCAPI_FESCRYPTO_MAX_CONN:10}")
	private int fescryptoMaxNoOfConn; // max no. of conn, default = 10 conn

        @Bean
	public DataSource fescryptoDataSource() throws SQLException {
		HikariConfig config = new HikariConfig();

                System.out.println("fescryptoConnStr = " + fescryptoConnStr);
                System.out.println("fescryptoUsername = " + fescryptoUsername);
                System.out.println("fescryptoPwd = " + fescryptoPwd);
                System.out.println("fescryptoDbDriver = " + fescryptoDbDriver);
                System.out.println("fescryptoPoolName = " + fescryptoPoolName);
                System.out.println("fescryptoMaxNoOfConn = " + fescryptoMaxNoOfConn);

		config.setJdbcUrl(fescryptoConnStr);
		config.setUsername(fescryptoUsername);
		config.setPassword(fescryptoPwd);
		config.setDriverClassName(fescryptoDbDriver);
		config.setPoolName(fescryptoPoolName);
		config.setMaximumPoolSize(fescryptoMaxNoOfConn);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setConnectionTestQuery("select 1 from dual ");

		HikariDataSource ds = new HikariDataSource(config);
		return ds;
	}*/
        // added by Danny Chan on 2021-6-25 (Apple Care enhancement): end

	@Bean
	public DataSource epcDataSource() throws SQLException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(epcConnStr);
		config.setUsername(epcUsername);
		config.setPassword(epcPwd);
		config.setDriverClassName(epcDbDriver);
		config.setPoolName(epcPoolName);
		config.setMaximumPoolSize(epcMaxNoOfConn);
		config.setLeakDetectionThreshold(10000);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setConnectionTestQuery("select 1 from dual ");

		HikariDataSource ds = new HikariDataSource(config);
		return ds;
	}

	@Bean
	public DataSource fesDataSource() throws SQLException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(fesConnStr);
		config.setUsername(fesUsername);
		config.setPassword(fesPwd);
		config.setDriverClassName(fesDbDriver);
		config.setPoolName(fesPoolName);
		config.setMaximumPoolSize(fesMaxNoOfConn);
		config.setLeakDetectionThreshold(10000);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setConnectionTestQuery("select 1 from dual ");

		HikariDataSource ds = new HikariDataSource(config);
		return ds;
	}

	@Bean
	public DataSource crmFesDataSource() throws SQLException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(crmFesConnStr);
		config.setUsername(crmFesUsername);
		config.setPassword(crmFesPwd);
		config.setDriverClassName(crmFesDbDriver);
		config.setPoolName(crmFesPoolName);
		config.setMaximumPoolSize(crmFesMaxNoOfConn);
		config.setLeakDetectionThreshold(10000);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setConnectionTestQuery("select 1 from dual ");

		HikariDataSource ds = new HikariDataSource(config);
		return ds;
	}

}