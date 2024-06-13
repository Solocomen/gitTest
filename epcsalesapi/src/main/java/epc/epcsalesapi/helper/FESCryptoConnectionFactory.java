/* FESCryptoConectionFactory.java (Apple Care enhancement): created by Danny Chan on 2021-6-25 */
package epc.epcsalesapi.helper;
import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

public class FESCryptoConnectionFactory extends ConnectionFactory {
  private static FESCryptoConnectionFactory factory = new FESCryptoConnectionFactory();
 
  @Autowired
  private DataSource fescryptoDataSource;  
  
  @Autowired
  private DataSource fesDataSource;  
  
  public FESCryptoConnectionFactory()
  {
  }
  
  public static ConnectionFactory getInstance() {
        return factory;
  }
  
  public Connection createConnection() throws SQLException {
        System.out.println("fescryptoDataSource = " + fescryptoDataSource);        
        System.out.println("fesDataSource = " + fesDataSource);        
        Connection conn = fescryptoDataSource.getConnection();
        System.out.println("fescryptoDataSource conn = " + conn);
        return conn;
  }
  
}