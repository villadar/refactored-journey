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
package ca.humanheartnature.magento.struct;

import ca.humanheartnature.abstracts.struct.DataTransferObject;
import java.util.List;
import java.util.Set;

/**
 * Data transport object from Magento database to QuickBooks Online. Contains a list of
 * {@link SalesInvoice}s and the related {@link Customer}s and {@link SaleItem}s
 * 
 */
public class MagentoInvoicesDto extends DataTransferObject
{
   private static final long serialVersionUID = 1L;
   
   /** Set of sales salesInvoices */
   private final List<SalesInvoice> salesInvoices;
   
   /** *  Set of customers that can be found in {@link #salesInvoices} */
   private final Set<Customer> customers;
   
   /** *  Set of unique products found in {@link #salesInvoices} */
   private final Set<SaleItem> products;
   
   
   /* -------------------- CONSTRUCTORS -------------------- */
   
   public MagentoInvoicesDto(List<SalesInvoice> salesInvoices,
                        Set<Customer> customers,
                        Set<SaleItem> products)
   {      
      this.salesInvoices = salesInvoices;
      this.customers = customers;
      this.products = products;
   }
   
   
   
   /* -------------------- ACCESSORS/MUTATORS -------------------- */
   
   /**
    * @return Set of sales salesInvoices
    */
   public List<SalesInvoice> getSalesInvoices()
   {
      return salesInvoices;
   }

   /**
    * @return Set of customers that can be found in {@link #salesInvoices}
    */
   public Set<Customer> getCustomers()
   {
      return customers;
   }
   
   /**
    * @return Set of unique products found in {@link #salesInvoices}
    */
   public Set<SaleItem> getSaleItems()
   {
      return products;
   }
   
}
