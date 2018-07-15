/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.quickbooks.comm;

import com.intuit.ipp.data.Customer;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.QueryResult;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executes QuickBooks Online queries that primarily runs against the customer table
 */
public class CustomerQuery implements QboQuery<Customer>
{
   /** Class logger */
   private static final Logger LOGGER = Logger.getLogger(CustomerQuery.class.getName());
   
   /** WHERE clause for SQL query */
   private String whereClause;
   
   /** ORDER BY clause for the SQL query */
   private String orderClause;
   
   
   /**
    * Instantiate via select method
    */
   private CustomerQuery(){}
   
   /**
    * Perform query to retrieve list of QBO customers
    * 
    * @param conn QBO connection
    * @return List of QBO customers
    * @throws FMSException 
    */
   @Override
   public List<Customer> execute(QboDataConnectionFactory conn) throws FMSException
   {
      String query =
            "SELECT * " +
            "FROM customer " +
            whereClause +
            (orderClause==null?"":orderClause);
      
      LOGGER.log(Level.FINER, "QuickBooks Online query:\n{0}", query);
         
      QueryResult queryResult = conn.getConnection().executeQuery(query);
      List<Customer> customers = (List<Customer>)queryResult.getEntities();
      LOGGER.log(Level.FINER, "Result set size: {0}", customers.size());
      return customers;
   }
   
   /**
    * Customer table columns
    */
   public enum COLUMN
   {
      DISPLAY_NAME("displayName");
      
      private final String columnName;
      
      private COLUMN(String columnName)
      {
         this.columnName = columnName;
      }
      
      @Override
      public String toString()
      {
         return columnName;
      }
    };
   
   
   /** 
    * Builder class for <code>SalesReceiptQuery</code>
    */
   public static class Builder
   { 
      /** Used as elements for WHERE IN clause */
      private Set<String> emails;
      
      private String displayName;
      
      private COLUMN highestColumn;
      
      /**
       * Privatized to force instantiation through {@link #select}
       */
      private Builder(){}
      
      /**
       * Generates an instance of this class
       * 
       * @return Instance of this class
       */
      public static Builder select()
      {
         return new Builder();
      }
   
      /**
       * <pre>
       * <code>SELECT primaryemailaddr, id
       * FROM customer
       * WHERE primaryemailaddr IN (emails)
       * </code>
       * </pre>
       * 
       * @param emails Set of emails to be used as parameters for WHERE clause
       * @return Instance of this class with WHERE clause defined
       */
      public Builder whereEmailIn(Set<String> emails)
      {
         this.emails = emails;
         return this;
      }
      
      /**
       * Limits the results to customers whose name matches the patter defined by the
       * <code>displayName</code> parameter
       * 
       * @param displayName LIKE clause expression used a key to find customers
       * @return Customers whose name correspond to the LIKE clause expression defined by
       *         the <code>displayName</code> parameter
       */
      public Builder whereDisplayNameLike(String displayName)
      {
         this.displayName = displayName.trim();
         return this;
      }
      
      /**
       * Limits the result to the entry whose value is the highest in the specified column
       * 
       * @param columnName Name of the column with which to retrieve the highest value
       *                   from
       * @return  Builder object for the purpose of method chaining
       */
      public Builder highest(COLUMN columnName)
      {
         this.highestColumn = columnName;
         return this;
      }
      
      /**
       * @return <code>CustomerQuery</code> object with SQL query initialized
       */
      public CustomerQuery build()
      {
         CustomerQuery query = new CustomerQuery();
         
         if (emails != null)
         {
            StringJoiner commaDelimitedEmails = new StringJoiner(",");
            emails.forEach(identifier ->
            {
               commaDelimitedEmails.add("'"+identifier+"'");
            });
            query.whereClause = " WHERE primaryemailaddr IN ("+commaDelimitedEmails+")";
         }
         
         if (displayName != null)
         {
            if (query.whereClause == null)
            {
               query.whereClause = " WHERE ";
            }
            else
            {
               query.whereClause += " AND ";
            }
            query.whereClause += "displayname LIKE '" + displayName.replace("'", "") + "'";
         }
         
         if (highestColumn != null)
         {
            query.orderClause = " ORDER BY " + highestColumn + " DESC MAXRESULTS 1";
         }
         
         return query;
      }
   }
   
}
