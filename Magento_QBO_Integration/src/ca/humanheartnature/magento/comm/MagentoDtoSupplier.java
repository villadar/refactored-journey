/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.magento.comm;

import ca.humanheartnature.abstracts.comm.JdbcConnectionFactory;
import ca.humanheartnature.core.exception.DataExtractionException;
import ca.humanheartnature.magento.struct.Customer;
import ca.humanheartnature.magento.struct.MagentoInvoicesDto;
import ca.humanheartnature.magento.struct.SaleItem;
import ca.humanheartnature.magento.struct.SalesInvoice;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Generates a data transfer object containing a list of sales invoices, the set of
 * invoiced customers, and the set of sold items.
 */
public class MagentoDtoSupplier implements Supplier<MagentoInvoicesDto>
{
   /** Class logger */
   private static final Logger LOGGER =
         Logger.getLogger(MagentoDtoSupplier.class.getName());
   
   /** Generates <code>Connection</code>s to database */
   private final JdbcConnectionFactory connFactory;
   
   /** Id of the last transmitted sales invoice */
   private final Date minimumDateBoundary;
   
   /**
    * @param connFactory Generates <code>Connection</code>s to database
    * @param minimumDateBoundary Datetime of the last exported sales invoice
    */
   public MagentoDtoSupplier(JdbcConnectionFactory connFactory, Date minimumDateBoundary)
   {
      if (minimumDateBoundary == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      this.connFactory = connFactory;
      this.minimumDateBoundary = minimumDateBoundary;
   }
   
   /**
    * Retrieves a list of {@link SalesInvoice}s and its associated list of
    * {@link Customer}s and {@link SaleItem}s
    * 
    * @return DTO containing collections of <code>salesInvoice</code>s,
    * <code>Customer</code>s, and <code>SaleItem</code>s
    * @throws DataExtractionException 
    */
   @Override
   public MagentoInvoicesDto get() throws DataExtractionException
   {
      try
      {
         if (connFactory == null)
         {
            throw new IllegalArgumentException("Argument cannot be null");
         }
         
         LOGGER.log(Level.FINER,
                    "Exporting invoices with invoice date after {0}",
                    minimumDateBoundary);
         
         MagentoDataSource magDataSource = new MagentoDataSource(connFactory);
         List<SalesInvoice> salesInvoices =
               magDataSource.getSalesInvoicesAfterDate(minimumDateBoundary);
         
         Set<Customer> customers = salesInvoices.stream()
               .map(invoice -> invoice.getCustomer())
               .collect(Collectors.toSet());
         
         Set<SaleItem> products = salesInvoices.stream()
               .flatMap(invoice -> invoice.getSaleItems().stream())
               .collect(Collectors.toSet());
               
         return new MagentoInvoicesDto(salesInvoices, customers, products);
      }
      catch(SQLException ex)
      {
         throw new DataExtractionException("Failed to connect to Magento Database", ex);
      }
   }
   
}