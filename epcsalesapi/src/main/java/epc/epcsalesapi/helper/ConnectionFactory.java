package epc.epcsalesapi.helper;

import java.sql.*;
import javax.sql.*; // TESTING
import javax.naming.*; // TESTING

public abstract class ConnectionFactory {
    /**
     * <p>
     *  Returns an instance of the factory.
     * <p>
     *
     * @return an instance
     */
    public static ConnectionFactory getInstance() {
        return null;
    }

    /**
     * <p>
     *  Creates a connection with the specified connection parameters.  This method is a helper method
     *  that is typically called by the parameterless createConnection() method.
     * </p>
     *
     * @param host the host name to connect
     * @param port the port number to connect
     * @param database the database name
     * @param server the database server where the database resides
     * @param user the user name
     * @param password the password
     * @return a new connection
     * @SQLException when a database error occurs
     */

	 // OC4J START
    protected static Connection createConnection(
        String dataSourceName
    ) throws SQLException {
      Context envCtx=null;
      DataSource ds=null;
      try  { 
        envCtx = new InitialContext();         
        ds  =  (DataSource) envCtx.lookup(dataSourceName);    
      } catch(Exception e) {
        String errMsg = new String (e.getMessage());
        System.out.println("ConnectionFactory Exception " + errMsg);
      }    
      if (ds != null) {
          try 
          {
            return ds.getConnection();          
          } catch(SQLException e) {
            String errMsg = new String (e.getMessage());
            System.out.println("ConnectionPool Exception " + errMsg);            
            return null;
          }                
      } else {
          return null;
      }      
    }    

    /**
     * <p>
     *  Creates a new connection.  This method should not be called by the user.
     * </p>
     *
     * @return a new connection
     * @SQLException when a database error occurs
     */
    protected abstract Connection createConnection() throws SQLException;

    /**
     * <p>
     *  Returns a connection.
     * </p>
     *
     * @return a connection
     * @SQLException when a database error occurs
     */
    public Connection getConnection() throws SQLException {
        return createConnection();
    }

    /**
     * <p>
     *  Receives notification when the connection is no longer needed by the caller.
     *  The default implementation is to close the connection.
     * </p>
     *
     * @param conn the connection
     */
    public void freeConnection(Connection conn) {
        try {
            conn.close();
        } catch (Exception ignored) {}
    }
    
}