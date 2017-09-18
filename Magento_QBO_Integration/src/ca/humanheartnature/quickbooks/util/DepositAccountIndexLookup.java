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
import com.intuit.ipp.data.Deposit;
import com.intuit.ipp.exception.FMSException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Looks up the internal index of a QBO deposit account by using the deposit account name
 * as the key
 */
public class DepositAccountIndexLookup
      implements LookupObject<String, String, RuntimeException>
{
   /** Stores key-value pairings */
   private final Map<String, String> depositAccountMap;
   
   /**
    * @param qboDataService Used to retrieve key-value pairing from QBO
    * @throws FMSException 
    */
   public DepositAccountIndexLookup(QboDataConnectionFactory qboDataService)
         throws FMSException
   {
      if (qboDataService == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      QboDataSource dataSource = new QboDataSource(qboDataService);
      List<Deposit> depositAccounts = dataSource.getDepositAccounts();
      
      depositAccountMap = depositAccounts.stream()
         .map(deposit -> deposit.getDepositToAccountRef())
         .collect(Collectors.toMap(ref -> ref.getName(),
                                   ref -> ref.getValue(),
                                   (ref1, ref2) -> ref1));
   }
   
   /**
    * @return Contents of the lookup table
    */
   @Override
   public String toString()
   {
      StringJoiner commaDelimited = new StringJoiner(", ");
      depositAccountMap.entrySet().forEach(entry ->
      {
         commaDelimited.add("["+entry.getKey()+", "+entry.getValue()+"]");
      });
      return commaDelimited.toString();
   }
   
   /**
    * @param depositAccount Deposit account to retrieve the index of
    * @return Internal QBO index of the deposit account, if it exists in QBO
    */
   @Override
   public Optional<String> lookup(String depositAccount)
   {
      return Optional.ofNullable(depositAccountMap.get(depositAccount));
   }
}
