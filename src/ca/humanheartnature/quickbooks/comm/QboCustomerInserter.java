/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.quickbooks.comm;

import ca.humanheartnature.core.exception.QboException;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.exception.FMSException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Inserts a customer entry to QBO. 
 */
public class QboCustomerInserter implements Function<Customer, Customer>
{
   /** Class logger */
   private static final Logger LOGGER =
         Logger.getLogger(QboCustomerInserter.class.getName());
   
   /** HTTP service used to push data changes to QBO */
   private final QboDataConnectionFactory qboService;
   
   /**
    * @param qboService HTTP service used to push data changes to QBO
    */
   public QboCustomerInserter(QboDataConnectionFactory qboService)
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
    */
   @Override
   public Customer apply(Customer customer)
   {
      if (customer == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      QboDataSource qboDataSource = new QboDataSource(qboService);
      
      try
      {
         String displayNameSuffix =
               qboDataSource.getCustomerByName(customer.getFullyQualifiedName()+"%") // % is a wildcard
                  .map(existingCustomer ->
                  {
                     String displayName=existingCustomer.getDisplayName();
                     if (displayName.contains("#"))
                     {
                        int duplicateNameCount =
                              Integer.parseInt(displayName.split("#")[1]);
                        duplicateNameCount++;
                        return " #" + String.format("%05d", duplicateNameCount);
                     }
                     return " #" + String.format("%05d", 1);
                  })
                  .orElse(" #" + String.format("%05d", 1));
         
         customer.setDisplayName(customer.getFullyQualifiedName() + displayNameSuffix);
      }
      catch(FMSException ex)
      {
         throw new QboException("Error occured when retrieving customer " +
                                customer.getFullyQualifiedName()+"%",
                                ex);
      }
      
      
      LOGGER.log(Level.FINER,
            "Adding new customer to QuickBooks\n" +
            "Display name: {0}\n" +
            "Email: {1}",
            new Object[] {customer.getDisplayName(),
                          customer.getPrimaryEmailAddr().getAddress()});
      
      try
      {
         return qboService.getConnection().add(customer);
      }
      catch (FMSException ex)
      {
         throw new QboException("Error occured when adding new customer to QBO", ex);
      }
   }
         
}
