/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.core.exception;

/**
 * This exception is a proxy for all possible checked exception types that could be thrown
 * during a supplier operation to read a file in the file system. This was created since
 * exception types aren't declared in {@link java.util.function.Consumer#accept}.
 */
public class FileReadException extends RuntimeException
{
   /**
    * @see Exception#Exception()
    */
   public FileReadException()
   {
   }
   
   /**
    * @param msg Exception message
    * @see Exception#Exception(String) 
    */
   public FileReadException(String msg)
   {
      super(msg);
   }
   
   /**
    * @param ex Wrapped exception
    * @see Exception#Exception(Throwable)
    */
   public FileReadException(Throwable ex)
   {
      super(ex);
   }
   
   /**
    * @param msg Exception message
    * @param ex Wrapped exception
    * @see Exception#Exception(String, Throwable)
    */
   public FileReadException(String msg, Throwable ex)
   {
      super(msg, ex);
   }
   
}
