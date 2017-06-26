/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 *
 * Ver  Date        Change Log
 * ---  ----------  -----------------------------------
 * 1.0  2017-06-14  Initial version
 */
package ca.humanheartnature.abstracts.struct;

import java.io.Serializable;

/**
 * Subclasses of this class holds structured data and can be serialized.
 * This entity is an abstract class instead of an interface in order for extending classes
 * to meet the criteria for serializability. The criteria for serializability are as
 * follows:
 * <ol>
 * <li>Must implement the serializable interface</li>
 * <li>The first superclass in its inheritance hierarchy that does not implement
 * <code>Serializable</code> must have a no-argument constructor</li>
 * </ol>
 * Since subclasses of this class will inherit Serializable, and the constructor of this
 * classes' superclass is <code>Object</code> which has a no-argument constructor, then
 * subclasses of this class are serializable.
 */
public abstract class DataBean implements Serializable, DataStructure
{
}
