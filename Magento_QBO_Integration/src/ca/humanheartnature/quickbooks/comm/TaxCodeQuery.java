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

import com.intuit.ipp.data.TaxCode;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.QueryResult;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executes QuickBooks Online queries that primarily runs against the taxcode table
 */
public class TaxCodeQuery implements QboQuery<TaxCode>
{
   /** Class logger */
   private static final Logger LOGGER = Logger.getLogger(TaxCodeQuery.class.getName());
   
   /** WHERE clause for SQL query */
   private String whereClause;
   
   
   /**
    * Instantiate via inner builder class
    */
   private TaxCodeQuery(){}
   
   
   @Override
   public List<TaxCode> execute(QboDataServiceSingleton conn) throws FMSException
   {
      String query =
            "SELECT name, id " +
            "FROM taxcode " +
            whereClause;
      
      LOGGER.log(Level.FINER, "QuickBooks Online query:\n{0}", query);
         
      QueryResult queryResult = conn.getInstance().executeQuery(query);
      List<TaxCode> taxCodes = (List<TaxCode>)queryResult.getEntities();
      
      LOGGER.log(Level.FINER, "Result set size: {0}", taxCodes.size());
      
      return taxCodes;
   }
   
   
   /** 
    * Builder class for <code>SalesReceiptQuery</code>
    */
   public static class Builder
   { 
      private boolean whereActive;
      
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
       * <code>SELECT name, id
       * FROM taxcode
       * WHERE active = true";
       * </code>
       * </pre>
       * 
       * @return Instance of this class with WHERE clause defined
       */
      public Builder whereActive()
      {
         this.whereActive = true;
         return this;
      }
      
      /**
       * @return <code>TaxCodeQuery</code> object with SQL query initialized
       */
      public TaxCodeQuery build()
      {
         TaxCodeQuery query = new TaxCodeQuery();
         
         if (whereActive)
         {
            query.whereClause = "WHERE active = true";
         }
         
         return query;
      }
   }
   
}
