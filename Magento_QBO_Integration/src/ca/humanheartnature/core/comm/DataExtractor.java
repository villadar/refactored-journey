/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.core.comm;

import ca.humanheartnature.abstracts.struct.DataTransferObject;
import ca.humanheartnature.core.exception.DataExtractionException;
import java.util.function.Supplier;

/**
 * Data operation. Generates <code>DataStructure</code> objects
 * 
 * @param <T> Extracted type
 */
public class DataExtractor<T extends DataTransferObject> 
{
   /**
    * Extracts data from an external source through a
    * {@link ca.humanheartnature.abstracts.comm.DataConnectionFactory} of type T
    * 
    * @param supplier Functional interface that generates <code>DataStructure</code>s
    * @return <code>DataStructure</code> object
    * @throws DataExtractionException 
    */
   public T extract(Supplier<T> supplier)
   {
      return supplier.get();
   }
}
