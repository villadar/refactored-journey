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
import com.intuit.ipp.data.Item;
import com.intuit.ipp.exception.FMSException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Looks up the internal index of a QBO service by using the service's SKU as the key
 */
public class ShippingIndexLookup implements LookupObject<String, Item, RuntimeException>
{
   /** Stores key-value mappings*/
   private final Map<String,Item> skuToIndexMapping;
   
   /**
    * @param qboService Used to perform SQL queries against QBO
    * @throws FMSException 
    */
   public ShippingIndexLookup(QboDataConnectionFactory qboService) throws FMSException
   {
      if (qboService == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      QboDataSource dataSource = new QboDataSource(qboService);
      List<Item> inventory = dataSource.getAllActiveServices();
      skuToIndexMapping = inventory.stream()
         .collect(Collectors.toMap(Item::getSku,
                                   shippingItem -> shippingItem,
                                   (item1, item2) -> item1));
   }
   
   /**
    * @return Contents of the map used by this class
    */
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
   
   /**
    * @param sku SKU of the shipping service to retrieve the index of
    * @return Internal QBO index of the shipping service referred to by the SKU parameter,
    * if it exists in QBO
    */
   @Override
   public Optional<Item> lookup(String sku)
   {
      return Optional.ofNullable(skuToIndexMapping.get(sku.trim()));
   }
   
   
}
