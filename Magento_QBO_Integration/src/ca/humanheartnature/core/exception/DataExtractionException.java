/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 *
 *
 * Ver  Date        Change Log
 * ---  ----------  -----------------------------------
 * 1.0  2017-06-14   Initial version
 */
package ca.humanheartnature.core.exception;

/**
 * This exception is a proxy for all possible checked exception types that could be thrown
 * during a data extraction operation. This was created since exceptions that occur during
 * a data extraction operation should be handled in higher levels of abstraction but the
 * exact type of exception cannot be elegantly passed to said levels while preserving
 * extensibility.
 */
public class DataExtractionException extends RuntimeException
{
   /**
    * @see Exception#Exception()
    */
   public DataExtractionException()
   {
   }
   
   /**
    * @param msg Exception message
    * @see Exception#Exception(String) 
    */
   public DataExtractionException(String msg)
   {
      super(msg);
   }
   
   /**
    * @param ex Wrapped exception
    * @see Exception#Exception(Throwable)
    */
   public DataExtractionException(Throwable ex)
   {
      super(ex);
   }
   
   /**
    * @param msg Exception message
    * @param ex Wrapped exception
    * @see Exception#Exception(String, Throwable)
    */
   public DataExtractionException(String msg, Throwable ex)
   {
      super(msg, ex);
   }
   
}
