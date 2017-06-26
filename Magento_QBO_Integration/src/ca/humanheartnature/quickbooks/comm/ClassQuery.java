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

import ca.humanheartnature.quickbooks.comm.ClassQuery.Builder;
import com.intuit.ipp.data.Class;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.QueryResult;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Queries QBO for class ids
 */
public class ClassQuery implements QboQuery<Class>
{
   /** Class logger */
   private static final Logger LOGGER = Logger.getLogger(ClassQuery.class.getName());
   
   private String whereClause;
   
   
   /* -------------------- OVERRIDDEN METHODS -------------------- */
   
   @Override
   public List<Class> execute(QboDataServiceSingleton conn) throws FMSException
   {
      String query = "SELECT id FROM class " + whereClause;
      
      LOGGER.log(Level.FINER, "QuickBooks Online query:\n{0}", query);
         
      QueryResult queryResult = conn.getInstance().executeQuery(query);
      List<Class> classes = (List<Class>) queryResult.getEntities();
      LOGGER.log(Level.FINER, "Result set size: {0}", classes.size());
      return classes;
   }
   
   
   /* -------------------- PUBLIC METHODS -------------------- */
   
   /** 
    * Buildable class for <code>SalesReceiptQuery</code>
    */
   public static class Builder
   { 
      private String className;
      
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
       * <code>SELECT id
       * FROM class
       * WHERE name = 'class'
       * </code>
       * </pre>
       * 
       * @param className Name of class
       * @return Instance of this class with WHERE clause defined
       */
      public Builder whereNameIs(String className)
      {
         this.className = className;
         return this;
      }
   
      public ClassQuery build()
      {
         ClassQuery query = new ClassQuery();
         
         if (className != null)
         {
            query.whereClause = " WHERE name = '"+className+"'";
         }
         
         return query;
      }
      
   }
   
}
