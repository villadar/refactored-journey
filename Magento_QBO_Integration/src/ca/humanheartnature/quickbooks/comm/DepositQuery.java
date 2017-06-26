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

import com.intuit.ipp.data.Deposit;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.QueryResult;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executes QuickBooks Online queries that primarily runs against the deposit table
 */
public class DepositQuery implements QboQuery<Deposit>
{
   /** Class logger */
   private static final Logger LOGGER = Logger.getLogger(DepositQuery.class.getName());
   
   
   @Override
   public List<Deposit> execute(QboDataServiceSingleton conn) throws FMSException
   {
      String query = "SELECT * FROM deposit";
      
      LOGGER.log(Level.FINER, "QuickBooks Online query:\n{0}", query);
         
      QueryResult queryResult = conn.getInstance().executeQuery(query);
      List<Deposit> deposits = (List<Deposit>) queryResult.getEntities();
      LOGGER.log(Level.FINER, "Result set size: {0}", deposits.size());
      return deposits;
   }
   
   
   /** 
    * Builder class for <code>DepositQuery</code>
    */
   public static class Builder
   { 
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
       * @return <code>DepositQuery</code> object with SQL query initialized
       */
      public DepositQuery build()
      {
         return new DepositQuery();
      }
      
   }
   
}
