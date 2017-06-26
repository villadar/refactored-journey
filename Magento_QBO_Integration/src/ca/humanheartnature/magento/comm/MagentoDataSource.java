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
package ca.humanheartnature.magento.comm;

import ca.humanheartnature.abstracts.comm.JdbcConnectionFactory;
import ca.humanheartnature.magento.struct.SalesInvoice;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Extracts data from a Magneto database by running SQL queries through a JDBC driver
 */
public class MagentoDataSource
{
   /** Generates JDBC connections */
   private final JdbcConnectionFactory magConnFactory;
   
   /**
    * @param magConn Generates JDBC connections
    */
   public MagentoDataSource(JdbcConnectionFactory magConn)
   {
      this.magConnFactory = magConn;
   }
   
   /**
    * Retrieves a list of sales invoices whose transaction date is after the
    * <code>transactionDate</code> parameter
    * @param transactionDate Lower date boundary for salesInvoices to retrieve
    * @return
    * @throws SQLException 
    */
   public List<SalesInvoice> getSalesInvoicesAfterDate(Date transactionDate)
         throws SQLException
   {
      return SalesInvoiceQuery.Builder
         .select()
         .joinProductsSold()
         .whereAfterDateTime(transactionDate)
         .build()
         .execute(magConnFactory);
   }
   
}
