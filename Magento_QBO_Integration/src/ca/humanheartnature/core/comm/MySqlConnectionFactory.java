/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.core.comm;

import ca.humanheartnature.abstracts.comm.JdbcConnectionFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generates new instances of JDBC connections to a MySQL database.
 */
public final class MySqlConnectionFactory implements JdbcConnectionFactory
{
   private static final Logger LOGGER =
         Logger.getLogger(MySqlConnectionFactory.class.getName());
   
   /** JDBC driver main class name */
   private static final String CLASS_NAME = "com.mysql.jdbc.Driver";
   
   /** MySQL connection URL */
   private static final String URL = "jdbc:mysql://%s:3306/%s?user=root&password=root";
   
   /** JDBC connection string using {@link #URL} template */
   private String connString;
   
   /** Username to connect with */
   private String user;
   
   /** Password for {@link #user} */
   private String password;
   
   
   /**
    * Stores database database information required to create a connection and tests the
    * connection
    * 
    * @param host Host of the database to connect to
    * @param database Database name to connect to
    * @param user Username to connect with
    * @param password Password of the username parameter
    */
   public MySqlConnectionFactory(String host,
                                 String database,
                                 String user,
                                 String password)
   {
      try
      {
         if (host == null ||
             database == null ||
             user == null ||
             password == null)
         {
            throw new IllegalArgumentException("Argument cannot be null");
         }

         this.connString = String.format(URL, host, database);
         this.user = user;
         this.password = password;
         
         LOGGER.log(Level.FINEST, "Connecting to DMBS using url: {0}", connString);
      
         Class.forName(CLASS_NAME).newInstance();
      }
      catch(ClassNotFoundException | InstantiationException | IllegalAccessException ex)
      {
         /* These exceptions are caused by programming error so treat them as
          * RuntimeException (quit when encountered) */
         LOGGER.log(Level.SEVERE, "JDBC connection initialization failure", ex);
         System.exit(-1);
      }
   }
   
   /**
    * Create a {@link java.sql.Connection} object to a MySQL Database on the default port
    * 3306
    * 
    * @return Connection to MySQL database
    * @throws SQLException 
    */
   @Override
   public Connection getConnection() throws SQLException
   {
      return DriverManager.getConnection(connString, user, password);
   }
   
}
