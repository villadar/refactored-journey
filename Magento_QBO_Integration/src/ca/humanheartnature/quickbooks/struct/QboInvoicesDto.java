/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 *
 * Ver  Date        Change Log
 * ---  ----------  -----------------------------------
 * 1.0  2017-06-14  Initial version
 */
package ca.humanheartnature.quickbooks.struct;

import ca.humanheartnature.abstracts.struct.DataTransferObject;
import com.intuit.ipp.data.SalesReceipt;
import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object used for performing ETL batch operations against QBO data
 */
public class QboInvoicesDto extends DataTransferObject
{   
   private static final long serialVersionUID = 1L;
   
   /** List of sales */
   private List<SalesReceipt> salesReceipts = new ArrayList<>();
   
   
   /**
    * @param sales List of sales to export
    */
   public QboInvoicesDto(List<SalesReceipt> sales)
   {
      if (sales == null)
      {
         throw new IllegalArgumentException("Arguments cannot be null");
      }
      
      this.salesReceipts = sales;
   }
   
   /**
    * @return Bulk list of <code>SalesReceipt</code> objects
    */
   public List<SalesReceipt> getSalesReceipts()
   {
      return this.salesReceipts;
   }
   
}
