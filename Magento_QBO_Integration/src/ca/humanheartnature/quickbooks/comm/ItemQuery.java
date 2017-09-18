/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.quickbooks.comm;

import com.intuit.ipp.data.Item;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.QueryResult;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executes QuickBooks Online queries that primarily runs against the item table
 * 
 * @author villa
 */
public class ItemQuery implements QboQuery<Item>
{
   /** Class logger */
   private static final Logger LOGGER = Logger.getLogger(ItemQuery.class.getName());
   
   /** WHERE clause for SQL query */
   private String whereClause;
   
   
   /**
    * Instantiate via inner builder class
    */
   private ItemQuery(){}
   
   
   /**
    * Execute query to retrieve list of QBO inventory items
    * @param conn QBO connection
    * @return List of QBO inventory items
    * @throws FMSException 
    */
   @Override
   public List<Item> execute(QboDataConnectionFactory conn) throws FMSException
   {
      String query = "SELECT * " +
                     "FROM item " +
                     whereClause;
      
      LOGGER.log(Level.FINER, "QuickBooks Online query:\n" + query);
         
      QueryResult queryResult = conn.getConnection().executeQuery(query);
      List<Item> items = (List<Item>) queryResult.getEntities();
      
      LOGGER.log(Level.FINER, "Result set size: " + items.size());
      
      return items;
   }
   
   /** 
    * Builder class for <code>SalesReceiptQuery</code>
    */
   public static class Builder
   { 
      /** Used as elements for WHERE IN clause */
      private Set<String> skus;
      
      private boolean whereServices;
      
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
       * <code>SELECT *
       * FROM item
       * WHERE active = true
       * AND type = 'INVENTORY' AND sku IN (skus)
       * </code>
       * </pre>
       * @param skus  Set of SKUs to be used as parameters for WHERE clause
       * @return Instance of this class with WHERE clause defined
       */
      public Builder whereSkusIn(Set<String> skus)
      {
         this.skus = skus;
         return this;
      }

      /**
       * <pre>
       * <code>SELECT *
       * FROM item
       * WHERE active = true
       * AND type = 'SERVICE'
       * </code>
       * </pre>
       * @return Instance of this class with the WHERE clause defined
       */
      public Builder whereServices()
      {
         this.whereServices = true;
         return this;
      }
      
   
      /**
       * <pre>
       * <code>
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
      
      public ItemQuery build()
      {
         ItemQuery query = new ItemQuery();
         
         if (skus != null)
         {
            StringJoiner commaDelimitedSkus = new StringJoiner(",");
            skus.forEach(sku ->
            {
               commaDelimitedSkus.add("'"+sku.trim()+"'");
            });
            query.whereClause = " WHERE sku IN ("+commaDelimitedSkus+")";
         }
         
         if (whereServices)
         {
            if (query.whereClause == null)
            {
               query.whereClause = " WHERE ";
            }
            else
            {
               query.whereClause += " AND ";
            }
            query.whereClause +=  "type = 'SERVICE'";
         }
         
         if (whereActive)
         {
            if (query.whereClause == null)
            {
               query.whereClause = "WHERE ";
            }
            else
            {
               query.whereClause += " AND ";
            }
            query.whereClause += "active = true";
         }
         
         return query;
      }
   }
   
}
