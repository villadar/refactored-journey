/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.abstracts.comm;

import java.util.List;

/**
 * Performs data retrieval operations against DataInterface T to retrieve data in the form
 * of U
 * 
 * @param <T> DataInterface the query is ran against
 * @param <U> Container for data retrieved
 * @param <E> Exception generated during query execution
 */
public interface DataQuery<T extends DataConnectionFactory, U, E extends Throwable>
{   
   /**
    * Executes a data retrieval query
    * 
    * @param dataInterface Data source that the query is ran against
    * @return Object that contains data retrieved through the query
    * @throws E
    */
   public List<U> execute(T dataInterface) throws E;
}