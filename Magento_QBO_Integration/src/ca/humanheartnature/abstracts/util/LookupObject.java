/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.abstracts.util;

import java.util.Optional;

/**
 * Uses a key-value pairings to look up the resultant value of an input key. The returned
 * value is of the type {@link Optional} since it may or may not be defined.
 * 
 * @param <T> Key type
 * @param <U> Value type
 * @param <E> Exception thrown during lookup
 */
public interface LookupObject<T, U, E extends Throwable>
{
   /**
    * Return value mapped to the input key
    * @param t Key used to retrieved a mapped value
    * @return Optional value
    * @throws E 
    */
   public Optional<U> lookup(T t) throws E;
}
