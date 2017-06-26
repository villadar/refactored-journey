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
import ca.humanheartnature.abstracts.struct.DataBean;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Writes files to the file system
 */
public class FileWriter implements DataInterface
{
   /**
    * Creates a Java object binary file on the file system by using the object's
    * <code>Serializable</code> interface.
    * 
    * @param bean Java object to write into a file
    * @param fileLocation Full path of the file destination
    * @throws FileNotFoundException
    * @throws IOException 
    */
   public void writeBeanToFile(DataBean bean, String fileLocation)
         throws FileNotFoundException, IOException
   {
      if (bean == null || fileLocation == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      try(FileOutputStream fileStream = new FileOutputStream(fileLocation))
      {
         try(ObjectOutputStream objectStream = new ObjectOutputStream(fileStream))
         {
            objectStream.writeObject(bean);
         }
      }
   }
}