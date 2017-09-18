/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.core.util;

import ca.humanheartnature.core.enums.DateFormatEnum;
import java.text.SimpleDateFormat;

/**
 * Generates <code>SimpleDateFormat</code> values from <code>DateFormatEnum</code> keys
 */
public class DateFormatFactory
{
   /**
    * @param dateFormatEnum Key to corresponding <code>SimpleDateFormat</code>
    * @return Date parser/formatter
    */
   public static SimpleDateFormat getDateFormat(DateFormatEnum dateFormatEnum)
   {
      return new SimpleDateFormat(dateFormatEnum.getStringFormat());
   }
}
