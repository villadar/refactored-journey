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
package ca.humanheartnature.quickbooks.util;

import ca.humanheartnature.abstracts.util.LookupObject;
import ca.humanheartnature.magento.struct.SalesInvoice.PaymentMethodEnum;
import static ca.humanheartnature.magento.struct.SalesInvoice.PaymentMethodEnum.PAYPAL;
import static ca.humanheartnature.magento.struct.SalesInvoice.PaymentMethodEnum.SQUARE;
import ca.humanheartnature.quickbooks.comm.QboDataServiceSingleton;
import ca.humanheartnature.quickbooks.comm.QboDataSource;
import com.intuit.ipp.data.PaymentMethod;
import com.intuit.ipp.exception.FMSException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Looks up the internal index of a QBO payment method by using the PaymentMethodEnum
 * that corresponds to a payment method name as the key
 */
public class PaymentMethodIndexLookup
      implements LookupObject<PaymentMethodEnum, String, RuntimeException>
{
   /** Stores key-value pairings */
   private Map<PaymentMethodEnum, String> paymentMethodTypeMap;
   
   /**
    * @param qboDataService Used to retrieve key-value pairing from QBO
    * @param creditCardMethod Payment method name that corresponds to the credit card
    *                         payment method
    * @param paypalMethod Payment method name that corresponds to the paypal payment
    *                     method
    * @throws FMSException 
    */
   public PaymentMethodIndexLookup(QboDataServiceSingleton qboDataService,
                                   String creditCardMethod,
                                   String paypalMethod) throws FMSException
   {
      if (qboDataService == null ||
          creditCardMethod == null ||
          paypalMethod == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      Set<String> paymentMethodNames = new HashSet<>();
      paymentMethodNames.add(creditCardMethod);
      paymentMethodNames.add(paypalMethod);
         
      QboDataSource dataSource = new QboDataSource(qboDataService);
      List<PaymentMethod> paymentMethods =
            dataSource.getPaymentMethodsByNames(paymentMethodNames);

      paymentMethodTypeMap = new HashMap<>();
      paymentMethods.stream()
            .forEach(method ->
            {
               if (method.getName().equals(creditCardMethod))
               {
                  paymentMethodTypeMap.put(SQUARE, method.getId());
               }
               else if(method.getName().equals(paypalMethod))
               {
                  paymentMethodTypeMap.put(PAYPAL, method.getId());
               }
            });
   }
   
   @Override
   public String toString()
   {
      StringJoiner commaDelimited = new StringJoiner(", ");
      paymentMethodTypeMap.entrySet().forEach(entry ->
      {
         commaDelimited.add("["+entry.getKey()+", "+entry.getValue()+"]");
      });
      return commaDelimited.toString();
   }
   
   @Override
   public Optional<String> lookup(PaymentMethodEnum paymentMethodType)
   {
      return Optional.ofNullable(paymentMethodTypeMap.get(paymentMethodType));
   }
   
}
