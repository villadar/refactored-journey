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

import java.sql.Connection;

/**
 * Abstraction for factories that generates new instances of {@link Connection}
 * 
 * @param <T> Connection type
 * @param <E> Exception thrown when creating connection
 */
public interface DataConnectionFactory<T, E extends Throwable> extends DataInterface
{
   /**
    * @return Connection for the purpose of transferring data
    * @throws E Checked exception thrown by the connection
    */
   public T getConnection() throws E;
}
