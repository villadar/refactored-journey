/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 *
 *
 * Ver  Date        Change Log
 * ---  ----------  -----------------------------------
 * 1.0  2017-07-23  Initial version
 */
package ca.humanheartnature.core.comm;

import ca.humanheartnature.abstracts.comm.DataInterface;
import ca.humanheartnature.abstracts.struct.DataBean;
import ca.humanheartnature.core.exception.WriteToFileException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.function.Consumer;

/**
 * Serializes DataBean objects into a file in the file system
 */
public class BeanFileWriter implements DataInterface, Consumer<DataBean>
{
   /** Location in the file system to write the file to */
   private String fileLocation;
   
   
   /**
    * @param fileLocation Location in the file system to write the file to 
    */
   public BeanFileWriter(String fileLocation)
   {
      if (fileLocation == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      this.fileLocation = fileLocation;
   }
   
   /**
    * Serializes the parameter <code>DataBean</code> to a file at the location specified
    * in the constructor
    * 
    * @param bean <code>DataBean</code> to write to file
    */
   @Override
   public void accept(DataBean bean)
   {
      if (bean == null)
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
      catch(IOException ex)
      {
         throw new WriteToFileException(
               "Error encountered when writing to " + fileLocation, ex);
      }
   }
   
}