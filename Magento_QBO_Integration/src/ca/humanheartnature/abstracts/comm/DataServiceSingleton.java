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
package ca.humanheartnature.abstracts.comm;

/**
 * Performs data transfer operation between the JVM and an external data
 * resource/destination through a single instance of a data service object
 * 
 * @param <T> Type of data service
 */
public interface DataServiceSingleton<T> extends DataInterface
{
   /**
    * @return Data service object
    */
   public T getInstance();
}
