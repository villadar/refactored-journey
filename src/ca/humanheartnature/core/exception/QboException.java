/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.core.exception;

/**
 * Represents exceptions generated when performing authentication or CRUD operations
 * against QuickBooks Online. Created due to inability to throw the checked exception 
 * {@link com.intuit.ipp.exception.FMSException} from FunctionalInterfaces
 */
public class QboException extends RuntimeException
{
   /**
    * @see Exception#Exception()
    */
   public QboException()
   {
   }
   
   /**
    * @param msg Exception message
    * @see Exception#Exception(String) 
    */
   public QboException(String msg)
   {
      super(msg);
   }
   
   /**
    * @param ex Wrapped exception
    * @see Exception#Exception(Throwable)
    */
   public QboException(Throwable ex)
   {
      super(ex);
   }
   
   /**
    * @param msg Exception message
    * @param ex Wrapped exception
    * @see Exception#Exception(String, Throwable)
    */
   public QboException(String msg, Throwable ex)
   {
      super(msg, ex);
   }
}
