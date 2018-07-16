/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.quickbooks.util;

import ca.humanheartnature.abstracts.util.LookupObject;
import ca.humanheartnature.quickbooks.comm.QboDataConnectionFactory;
import ca.humanheartnature.quickbooks.comm.QboDataSource;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.exception.FMSException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Looks up the internal index of a QBO customer by using the customer's email as the key
 */
public class CustomerEmailToIndexLookup
      implements LookupObject<String, String, RuntimeException>
{
   /** Translates customer email address to QuickBooks Online index equivalent */
   private final Map<String, String> customerEmailToIndexMapping;
   
   /**
    * @param dataService Used to retrieve key-value pairing from QBO
    * @param emails  Set of emails to be used as keys
    * @throws FMSException 
    */
   public CustomerEmailToIndexLookup(QboDataConnectionFactory dataService,
                                     Set<String> emails) throws FMSException
   {
      if (dataService == null || emails == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      QboDataSource dataSource = new QboDataSource(dataService);
      
      List<Customer> customersWithId = dataSource.getCustomersByEmail(emails);
      
      customerEmailToIndexMapping = customersWithId.stream()
         .collect(
            Collectors.toMap(cust-> cust.getPrimaryEmailAddr().getAddress().toLowerCase(),
                             cust-> cust.getId(),
                             (cust1, cust2) -> cust1));
   }
   
   /**
    * Add an entry to the look up table
    * 
    * @param customerEmail Key to the look up table
    * @param customerId Value to the lookup table
    */
   public void add(String customerEmail, String customerId)
   {
      customerEmailToIndexMapping.put(customerEmail, customerId);
   }
   
   /**
    * @param customerEmail Customer email to retrieve the index of
    * @return Internal QBO index of the customer if their email is found
    */
   @Override
   public Optional<String> lookup(String customerEmail)
   {
      return Optional.ofNullable(
            customerEmailToIndexMapping.get(customerEmail.toLowerCase()));
   }
   
}
