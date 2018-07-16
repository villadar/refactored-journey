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
   public TaxCodeLookup(QboDataConnectionFactory qboDataService) throws FMSException
   {
      if (qboDataService == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      QboDataSource dataSource = new QboDataSource(qboDataService);
      List<TaxCode> taxCodes = dataSource.getAllTaxCodes();
      
      taxCodeMap = taxCodes.stream()
         .collect(Collectors.toMap(taxCode -> taxCode.getName(),
                                   taxCode -> taxCode.getId(),
                                   (taxCode1, taxCode2) -> taxCode1));
   }
   
   /**
    * @return Contents of the map used by this class
    */
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
   
   /**
    * @param taxCode Name of the tax code to retrieve the index of
    * @return Internal QBO index of the tax code, if it exists in QBO
    */
   @Override
   public Optional<String> lookup(String taxCode)
   {
      return Optional.ofNullable(taxCodeMap.get(taxCode));
   }
}
