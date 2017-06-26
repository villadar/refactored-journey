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
package ca.humanheartnature.quickbooks.comm;

import ca.humanheartnature.abstracts.comm.Query;
import com.intuit.ipp.exception.FMSException;

/**
 * Performs data retrieval queries against QuickBooks Online
 * @param <T> Return type of {@link #execute}
 */
public interface QboQuery<T> extends Query<QboDataServiceSingleton, T, FMSException>
{   
}
