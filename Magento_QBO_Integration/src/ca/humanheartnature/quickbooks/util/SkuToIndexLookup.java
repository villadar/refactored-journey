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
import com.intuit.ipp.data.Item;
import com.intuit.ipp.exception.FMSException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Looks up the internal index of a QBO sale item by using the sale item's SKU as the key
 */
public class SkuToIndexLookup implements LookupObject<String, String, RuntimeException>
{
   /** Stores key-value pairings */
   private final Map<String, String> skuToIndexMapping;
   
   /**
    * @param qboService Used to retrieve key-value pairing from QBO
    * @param skus Set of sale item SKUS to be used as keys
    * @throws FMSException 
    */
   public SkuToIndexLookup(QboDataServiceSingleton qboService, Set<String> skus)
         throws FMSException
   {
      QboDataSource dataSource = new QboDataSource(qboService);
      List<Item> inventory = dataSource.getSaleItemsBySkus(skus);
      skuToIndexMapping = inventory.stream()
         .collect(Collectors.toMap(Item::getSku,
                                   Item::getId,
                                   (item1, item2) -> item1));
   }
   
   @Override
   public String toString()
   {
      StringJoiner commaDelimited = new StringJoiner(", ");
      skuToIndexMapping.entrySet().forEach(entry ->
      {
         commaDelimited.add("["+entry.getKey()+", "+entry.getValue()+"]");
      });
      return commaDelimited.toString();
   }

   @Override
   public Optional<String> lookup(String sku)
   {
      return Optional.ofNullable(skuToIndexMapping.get(sku.trim()));
   }
   
}
