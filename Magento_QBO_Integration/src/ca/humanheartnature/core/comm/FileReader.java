/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 *
 *
 * Ver  Date        Change Log
 * ---  ----------  -----------------------------------
 * 1.0  2017-06-14   Initial version
 */
package ca.humanheartnature.core.comm;

import ca.humanheartnature.abstracts.comm.DataInterface;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Generates objects from file streams
 */
public class FileReader implements DataInterface
{   
   /**
    * Create a serializable object from a file. The class of the object is determined by
    * the class of the object that the file is generated from.
    * 
    * @param fileLocation File location of the java bean file to retrieve into JVM space
    * @return Object generated from the bean file
    * @throws FileNotFoundException
    * @throws IOException
    * @throws ClassNotFoundException 
    */
   public Object readFileToBean(String fileLocation)
         throws FileNotFoundException, IOException, ClassNotFoundException
   {
      if (fileLocation == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      try(FileInputStream fileStream = new FileInputStream(fileLocation))
      {
         try(ObjectInputStream objectStream = new ObjectInputStream(fileStream))
         {
            return objectStream.readObject();
         }
      }
   }
}
