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
import ca.humanheartnature.quickbooks.comm.QboDataServiceSingleton;
import ca.humanheartnature.quickbooks.comm.QboDataSource;
import com.intuit.ipp.data.TaxCode;
import com.intuit.ipp.exception.FMSException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Looks up the internal index of a QBO tax code by using the tax code name as the key
 */
public class TaxCodeLookup implements LookupObject<String, String,RuntimeException>
{
   /** Stores key-value pairings */
   private final Map<String, String> taxCodeMap;
   
   /**
    * @param qboDataService
    * @throws FMSException 
    */
   public TaxCodeLookup(QboDataServiceSingleton qboDataService) throws FMSException
   {
      QboDataSource dataSource = new QboDataSource(qboDataService);
      List<TaxCode> taxCodes = dataSource.getAllTaxCodes();
      
      taxCodeMap = taxCodes.stream()
         .collect(Collectors.toMap(taxCode -> taxCode.getName(),
                                   taxCode -> taxCode.getId()));
   }
   
   @Override
   public String toString()
   {
      StringJoiner commaDelimited = new StringJoiner(", ");
      taxCodeMap.entrySet().forEach(entry ->
      {
         commaDelimited.add("["+entry.getKey()+", "+entry.getValue()+"]");
      });
      return commaDelimited.toString();
   }
   
   @Override
   public Optional<String> lookup(String taxCode)
   {
      return Optional.ofNullable(taxCodeMap.get(taxCode));
   }
}
