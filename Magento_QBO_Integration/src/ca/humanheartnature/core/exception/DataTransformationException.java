/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.core.exception;

/**
 * This exception is a proxy for all possible checked exception types that could be thrown
 * during a data transformation operation. This was created since exceptions that occur
 * during a data transformation operation should be handled in higher levels of
 * abstraction but the exact type of exception cannot be elegantly passed to said levels.
 */
public class DataTransformationException extends RuntimeException
{
   /**
    * @see Exception#Exception()
    */
   public DataTransformationException()
   {     
   }
   
   /**
    * @param msg Exception message
    * @see Exception#Exception(String) 
    */
   public DataTransformationException(String msg)
   {
      super(msg);
   }
   
   /**
    * @param ex Wrapped exception
    * @see Exception#Exception(Throwable)
    */
   public DataTransformationException(Throwable ex)
   {
      super(ex);
   }
   
   /**
    * @param msg Exception message
    * @param ex Wrapped exception
    * @see Exception#Exception(String, Throwable)
    */
   public DataTransformationException(String msg, Throwable ex)
   {
      super(msg, ex);
   }
   
}
