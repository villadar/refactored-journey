/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.core.comm;

import ca.humanheartnature.core.exception.WriteToFileException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Serializes objects into a file in the file system
 */
public class BeanFileWriter implements Consumer<Serializable>
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
    * Serializes the parameter object to a file at the location specified in the
    * constructor
    * 
    * @param bean Object to write to file
    */
   @Override
   public void accept(Serializable bean)
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