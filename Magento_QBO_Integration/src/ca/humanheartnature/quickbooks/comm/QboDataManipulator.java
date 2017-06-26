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

import com.intuit.ipp.data.Customer;
import com.intuit.ipp.exception.FMSException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Modifies persistent QBO data. This cannot be defined as a consumer since the returned
 * object of this classes' manipulation methods gets populated with internal indexes that
 * are used for further processing.
 */
public class QboDataManipulator
{
   /** Class logger */
   private static final Logger LOGGER =
         Logger.getLogger(QboDataManipulator.class.getName());
   
   /** HTTP service used to push data changes to QBO */
   private final QboDataServiceSingleton qboService;
   
   /**
    * @param qboService HTTP service used to push data changes to QBO
    */
   public QboDataManipulator(QboDataServiceSingleton qboService)
   {
      this.qboService = qboService;
   }
   
   /**
    * Adds a new customer to QBO. The customers display name must be unique so a #
    * followed by an auto-incrementing 5 digit number (padded on the left with 0s) is
    * appended to the display name.
    * 
    * @param customer Customer to add
    * @return Customer object build from the input parameter. Internal indexes of this
    * object are populated.
    * @throws FMSException 
    */
   public Customer addCustomer(Customer customer) throws FMSException
   {
      if (customer.getDisplayName() != null)
      {
         /* This exception is thrown to prevent redundant code that sets the display name
          * outside of this method */
         throw new IllegalArgumentException(
               "Display name must be null since it is set by this method");
      }
      
      QboDataSource qboDataSource = new QboDataSource(qboService);
      
      String displayNameSuffix =
            qboDataSource.getCustomerByName(customer.getFullyQualifiedName()+"%")
               .map(existingCustomer ->
               {
                  String displayName=existingCustomer.getDisplayName();
                  if (displayName.contains("#"))
                  {
                     int duplicateNameCount = Integer.parseInt(displayName.split("#")[1]);
                     duplicateNameCount++;
                     return " #" + String.format("%05d", duplicateNameCount);
                  }
                  return " #" + String.format("%05d", 1);
               })
               .orElse(" #" + String.format("%05d", 1));
      
      customer.setDisplayName(customer.getFullyQualifiedName() + displayNameSuffix);
      
      LOGGER.log(Level.FINER,
                 "Adding new customer to QuickBooks\n" +
                 "Display name: " + customer.getDisplayName() + "\n" +
                 "Email: " + customer.getPrimaryEmailAddr().getAddress());
      
      return qboService.getInstance().add(customer);
   }
         
}
