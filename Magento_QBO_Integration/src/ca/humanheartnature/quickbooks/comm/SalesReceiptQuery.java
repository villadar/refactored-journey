/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 *
 *
 * Ver  Date        Change Log
 * ---  ----------  -----------------------------------
 * 1.0  2017-06-14  Initial version
 */
package ca.humanheartnature.quickbooks.comm;

import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.QueryResult;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executes QuickBooks Online queries that primarily runs against the salesreceipt table
 */
public class SalesReceiptQuery implements QboQuery<SalesReceipt>
{     
   /** Class logger */
   private static final Logger LOGGER =
         Logger.getLogger(SalesReceiptQuery.class.getName());
   
   /** WHERE clause for SQL query */
   private String whereClause;
   
   /** ORDER BY clause for the SQL query */
   private String orderbyClause;
   
   
   /**
    * Instantiate via inner builder class
    */
   private SalesReceiptQuery(){}
   
   
   @Override
   public List<SalesReceipt> execute(QboDataServiceSingleton conn) throws FMSException
   {
      try
      {
         String query =
               "SELECT *\n" + 
               "FROM salesreceipt\n" +
               whereClause + "\n" +
               orderbyClause;

         LOGGER.log(Level.FINER, "QuickBooks Online query:\n{0}", query);
      
         QueryResult queryResult = conn.getInstance().executeQuery(query);
         return (List<SalesReceipt>) queryResult.getEntities();
      }
      catch (FMSException ex)
      {
         System.out.println(ex.getMessage());
         throw ex;
      }
   }
   
   /**
    * salesreceipt columns
    */
   public enum COLUMN
   {
      ID("id");
      
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
      private String docNumberExpression;
      
      private SalesReceiptQuery.COLUMN highestColumn;
      
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
       * <code>
       * WHERE docnumber like '<docNumberLike>'
       * </code>
       * </pre>
       * 
       * @param docNumberLike Document number of rececipt
       * @return 
       */
      public Builder whereDocNumberLike(String docNumberLike)
      {
         this.docNumberExpression = docNumberLike;
         return this;
      }
      
      /**
       * Limits the result to the entry whose value is the highest in the specified column
       * 
       * @param columnName Name of the column with which to retrieve the highest value
       *                   from
       * @return Builder object for the purpose of method chaining
       */
      public Builder highest(SalesReceiptQuery.COLUMN columnName)
      {
         this.highestColumn = columnName;
         return this;
      }
      
      /**
       * @return <code>SalesReceiptQuery</code> object with SQL query initialized
       */
      public SalesReceiptQuery build()
      {
         SalesReceiptQuery query = new SalesReceiptQuery();
         
         if (docNumberExpression != null)
         {
            query.whereClause = "WHERE docnumber LIKE '" + docNumberExpression + "'";
         }
         
         if (highestColumn != null)
         {
            query.orderbyClause = " ORDER BY " + highestColumn + " DESC MAXRESULTS 1";
         }
         
         return query;
      }
   }
   
}
