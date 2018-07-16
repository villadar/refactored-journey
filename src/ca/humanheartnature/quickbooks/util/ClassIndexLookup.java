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
import com.intuit.ipp.exception.FMSException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Looks up the internal index of a QBO class by using the class name as the key
 */
public class ClassIndexLookup implements LookupObject<String, String, FMSException>
{
   /** Connection to QuickBooksOnline */
   private final QboDataConnectionFactory qboDataService;
   
   /** Contains key-value pairing */
   private final Map<String, String> indexMap = new HashMap<>();
   
   /**
    * @param dataService Used to retrieve key-value pairing from QBO
    */
   public ClassIndexLookup(QboDataConnectionFactory dataService)
   {
      if (dataService == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      this.qboDataService = dataService;
   }
   
   /**
    * @param className Class name to retrieve the index of
    * @return Internal QBO index of the class if it is found
    * @throws FMSException 
    */
   @Override
   public Optional<String> lookup(String className) throws FMSException
   {
      if (!indexMap.containsKey(className))
      {
         QboDataSource dataSource = new QboDataSource(qboDataService);
         dataSource.getClassByName(className)
            .ifPresent(qboClass ->
            {
               indexMap.put(className, qboClass.getId());
            });
      }
      
      return Optional.ofNullable(indexMap.get(className));
   }
}