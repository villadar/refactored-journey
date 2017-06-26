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

import ca.humanheartnature.core.exception.DataLoadingException;
import ca.humanheartnature.quickbooks.struct.QboInvoicesDto;
import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.exception.FMSException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Loads data stored inside QBO DAOs to QBO database
 */
public class QboDataLoader implements Consumer<QboInvoicesDto>
{
   /** HTTP service used to push data changes to QBO */
   private final QboDataServiceSingleton dataService;
   
   /**
    * @param dataService HTTP service used to push data changes to QBO
    */
   public QboDataLoader(QboDataServiceSingleton dataService)
   {
      this.dataService = dataService;
   }

   @Override
   public void accept(QboInvoicesDto dataStruct)
   {
      List<SalesReceipt> salesReceipts = dataStruct.getSalesReceipts();
      salesReceipts.forEach(receipt ->
      {
         try
         {
            dataService.getInstance().add(receipt);
         }
         catch(FMSException ex)
         {
            throw new DataLoadingException("QBO load error", ex);
         }
      });
   }
}
