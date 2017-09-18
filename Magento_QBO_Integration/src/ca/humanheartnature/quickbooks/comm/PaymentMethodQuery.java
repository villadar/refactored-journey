/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.quickbooks.comm;

import com.intuit.ipp.data.PaymentMethod;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.QueryResult;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executes QuickBooks Online queries that primarily runs against the payment method table
 */
public class PaymentMethodQuery implements QboQuery<PaymentMethod>
{
   /** Class logger */
   private static final Logger LOGGER =
         Logger.getLogger(PaymentMethodQuery.class.getName());
   
   /** WHERE clause for SQL query */
   private String whereClause;
   
   
   /**
    * Instantiate via inner builder class
    */
   private PaymentMethodQuery(){}
   
   /**
    * Perform query to retrieve list of payment methods
    * @param conn QBO connection
    * @return List of QBO payment methods
    * @throws FMSException 
    */
   @Override
   public List<PaymentMethod> execute(QboDataConnectionFactory conn) throws FMSException
   {
      String query = "SELECT name, id " +
                     "FROM paymentmethod " +
                     whereClause;
      
      LOGGER.log(Level.FINER, "QuickBooks Online query:\n{0}", query);
         
      QueryResult queryResult = conn.getConnection().executeQuery(query);
      List<PaymentMethod> items = (List<PaymentMethod>) queryResult.getEntities();
      
      LOGGER.log(Level.FINER, "Result set size: {0}", items.size());
      
      return items;
   }
   
   /** 
    * Builder class for <code>PaymentMethodQuery</code>
    */
   public static class Builder
   { 
      /** Used as elements for WHERE IN clause */
      private Set<String> names;
      
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
       * WHERE name in (names)
       * </code>
       * </pre>
       * @param names  Set of SKUs to be used as parameters for WHERE clause
       * @return Instance of this class with WHERE clause defined
       */
      public Builder whereNameIn(Set<String> names)
      {
         this.names = names;
         return this;
      }
      
      /**
       * @return <code>PaymentMethodQuery</code> object with SQL query initialized
       */
      public PaymentMethodQuery build()
      {
         PaymentMethodQuery query = new PaymentMethodQuery();
         
         if (names != null)
         {
            StringJoiner commaDelimitedSkus = new StringJoiner(",");
            names.forEach(sku ->
            {
               commaDelimitedSkus.add("'"+sku+"'");
            });
            query.whereClause = " WHERE name IN ("+commaDelimitedSkus+")";
         }
         
         return query;
      }
   }
}
