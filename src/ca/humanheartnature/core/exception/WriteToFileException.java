/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.core.exception;

/**
 * This exception is a proxy for all possible checked exception types that could be thrown
 * during a consumer operation to write a file into the file system. This was created
 * since exception types aren't declared in {@link java.util.function.Consumer#accept}.
 */
public class WriteToFileException extends RuntimeException
{
   /**
    * @see Exception#Exception()
    */
   public WriteToFileException()
   {
   }
   
   /**
    * @param msg Exception message
    * @see Exception#Exception(String) 
    */
   public WriteToFileException(String msg)
   {
      super(msg);
   }
   
   /**
    * @param ex Wrapped exception
    * @see Exception#Exception(Throwable)
    */
   public WriteToFileException(Throwable ex)
   {
      super(ex);
   }
   
   /**
    * @param msg Exception message
    * @param ex Wrapped exception
    * @see Exception#Exception(String, Throwable)
    */
   public WriteToFileException(String msg, Throwable ex)
   {
      super(msg, ex);
   }
   
}
