/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.core.enums;

/**
 * Valued enum that contains labeled date formats stored in a string 
 */
public enum DateFormatEnum
{
   /**
    * yyyy-MM-dd HH:mm:ss
    */
   ISO_8601("yyyy-MM-dd HH:mm:ss");
   
   /** Contains the value for this enum */
   private final String stringFormat;
   
   /**
    * Enables valued enum
    * 
    * @param stringDateFormat Value for this enum
    */
   private DateFormatEnum(String stringDateFormat)
   {
      this.stringFormat = stringDateFormat;
   }
   
   /**
    * Input for <code>SimpleDateFormat</code> constructor
    * @return 
    */
   public String getStringFormat()
   {
      return stringFormat;
   }
}
