/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.abstracts.comm;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Generates JDBC connections
 */
public interface JdbcConnectionFactory
      extends DataConnectionFactory<Connection, SQLException>
{
}
