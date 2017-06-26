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

import ca.humanheartnature.abstracts.struct.DatabaseAccessObject;
import java.sql.SQLException;

/**
 * Performs SQL queries against a JDBC connection
 * 
 * @param <T> Return type of a JDBC query
 */
public interface JdbcQuery<T extends DatabaseAccessObject>
      extends Query<JdbcConnectionFactory, T, SQLException>
{   
}
