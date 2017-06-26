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

import static ca.humanheartnature.quickbooks.comm.CustomerQuery.COLUMN.DISPLAY_NAME;
import static ca.humanheartnature.quickbooks.comm.SalesReceiptQuery.COLUMN.ID;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.Deposit;
import com.intuit.ipp.data.Item;
import com.intuit.ipp.data.PaymentMethod;
import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.data.TaxCode;
import com.intuit.ipp.exception.FMSException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generates QBO DAOs by performing SQL queries against QBO
 */
public class QboDataSource
{
   /** Class logger */
   private static final Logger LOGGER = Logger.getLogger(QboDataSource.class.getName());
   
   /** Interface for QBO data */
   private final QboDataServiceSingleton qboService;
   
   /**
    * @param dataService Interface for QBO data
    */
   public QboDataSource(QboDataServiceSingleton dataService)
   {
      this.qboService = dataService;
   }
   
   /**
    * Performs a QBO query to retrieve the SalesReceipt with the latest invoice date.
    * 
    * @return SalesReceipt with the latest invoice date found
    * @throws FMSException 
    */
   public Optional<SalesReceipt> getLastExportedSalesReceipt() throws FMSException
   {
      LOGGER.log(Level.FINE,
                 "Executing QBO database query: getLastExportedSalesReceipt()");
      
      List<SalesReceipt> salesReceipts = SalesReceiptQuery.Builder
         .select()
         .whereDocNumberLike("CAN%")
         .highest(ID)
         .build()
         .execute(qboService);
      
      if (salesReceipts.isEmpty())
      {
         return Optional.empty();
      }
      return Optional.ofNullable(salesReceipts.get(0));
   }
   
   /**
    * Performs a QBO query to retrieve 0 or 1 Customer based on his name
    * 
    * @param nameExpression LIKE-clause expression for customer name
    * @return Customer
    * @throws FMSException 
    */
   public Optional<Customer> getCustomerByName(String nameExpression) throws FMSException
   {
      LOGGER.log(Level.FINE,
                 "Executing QBO database query: getCustomerByName({0})",
                 nameExpression);
      
      List<Customer> customers = CustomerQuery.Builder
         .select()
         .whereDisplayNameLike(nameExpression.toLowerCase())
         .highest(DISPLAY_NAME)
         .build()
         .execute(qboService);
      
      if (customers.isEmpty())
      {
         return Optional.empty();
      }
      return Optional.ofNullable(customers.get(0));
   }
   
   /**
    * Performs a QBO query to retrieve a QBO Class based on its name
    * 
    * @param className Name of class to use as a key
    * @return Class whose name corresponds to the className parameter
    * @throws FMSException 
    */
   public Optional<com.intuit.ipp.data.Class> getClassByName(String className)
         throws FMSException
   {
      LOGGER.log(Level.FINE,
                 "Executing QBO database query: getClassByName({0})",
                 className);
      
      List<com.intuit.ipp.data.Class> classes = ClassQuery.Builder
         .select()
         .whereNameIs(className)
         .build()
         .execute(qboService);
      
      if (classes.isEmpty())
      {
         return Optional.empty();
      }
      return Optional.ofNullable(classes.get(0));
   }
   
   /**
    * Performs a QBO query to retrieve a list of customers based on their emails
    * 
    * @param emails Email addresses used as keys to retrieve a list of customers
    * @return List of Customers whose emails corresponds to this method's parameter
    * @throws FMSException 
    */
   public List<Customer> getCustomersByEmail(Set<String> emails) throws FMSException
   {
      LOGGER.log(Level.FINE, "Executing QBO database query: getCustomersByEmail()");
      
      return CustomerQuery.Builder
         .select()
         .whereEmailIn(emails)
         .build()
         .execute(qboService);
   }
   
   /**
    * Performs a QBO query to retrieve all deposit accounts
    * 
    * @return List of all QBO deposit accounts
    * @throws FMSException 
    */
   public List<Deposit> getDepositAccounts() throws FMSException
   {
      LOGGER.log(Level.FINE, "Executing QBO database query: getDepositAccounts()");
      
      return DepositQuery.Builder
         .select()
         .build()
         .execute(qboService);
   }
   
   /**
    * Performs a QBO query to retrieve payment methods based on their names
    * 
    * @param paymentNames A set of payment method names to be used as keys for the query
    * @return List of PaymentMethods whose names corresponds the paymentNames parameter
    * @throws FMSException 
    */
   public List<PaymentMethod> getPaymentMethodsByNames(Set<String> paymentNames)
         throws FMSException
   {
      LOGGER.log(Level.FINE, "Executing QBO database query: getPaymentMethodsByNames()");
      
      return PaymentMethodQuery.Builder
         .select()
         .whereNameIn(paymentNames)
         .build()
         .execute(qboService);
   }
   
   /**
    * Performs a QBO query to retrieve sale items based on their SKUs
    * 
    * @param skus Set of SKUs used to be used as keys for the query
    * @return List of sale items whose SKUs corresponds to the skus parameter
    * @throws com.intuit.ipp.exception.FMSException
    */
   public List<Item> getSaleItemsBySkus(Set<String> skus) throws FMSException
   {
      LOGGER.log(Level.FINE, "Executing QBO database query: getSaleItemsBySkus()");
      
      return ItemQuery.Builder
         .select()
         .whereSkusIn(skus)
         .whereActive()
         .build()
         .execute(qboService);
   }
   
   /**
    * Performs a QBO query to retrieve all active services from QBO
    * 
    * @return List of all active services
    * @throws FMSException 
    */
   public List<Item> getAllActiveServices() throws FMSException
   {
      LOGGER.log(Level.FINE, "Executing QBO database query: getAllServices()");
      
      return ItemQuery.Builder
         .select()
         .whereServices()
         .whereActive()
         .build()
         .execute(qboService);
   }
   
   /**
    * Performs a QBO query to retrieve all tax codes from QBO
    * 
    * @return All tax codes defined in QBO
    * @throws FMSException 
    */
   public List<TaxCode> getAllTaxCodes() throws FMSException
   {
      LOGGER.log(Level.FINE, "Executing QBO database query: getAllTaxCodes()");
      
      return TaxCodeQuery.Builder
         .select()
         .whereActive()
         .build()
         .execute(qboService);
   }
   
}
