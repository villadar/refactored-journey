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
package ca.humanheartnature.abstracts.struct;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Container for a batch of <code>DataStructure</code>s for the purpose of batch transfer
 * operations
 */
public abstract class DataTransferObject extends DataBean
{
   /**
    * Transforms this object so that it can be compatible with another data interface for
    * loading
    * 
    * @param function Function in which this object will be the input and the returned
    * object will be the function output
    * output will be returned
    * @return Transformed data transfer object
    */
   public DataTransferObject transform(Function function)
   {
      return (DataTransferObject) function.apply(this);
   }
   
   /**
    * Load the data transfer object through to a data interface
    * 
    * @param consumer Performs the loading operation
    */
   public void load(Consumer consumer)
   {
      consumer.accept(this);
   }
}
