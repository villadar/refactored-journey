/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.quickbooks.comm;

import com.intuit.ipp.exception.FMSException;
import ca.humanheartnature.abstracts.comm.DataQuery;

/**
 * Performs data retrieval queries against QuickBooks Online
 * @param <T> Return type of {@link #execute}
 */
public interface QboQuery<T> extends DataQuery<QboDataConnectionFactory, T, FMSException>
{   
}
