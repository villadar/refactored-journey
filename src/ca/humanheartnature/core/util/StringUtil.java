package ca.humanheartnature.core.util;

import java.util.regex.Pattern;

public class StringUtil {
	
   /**
    * Removes trailing alphabet characters from a string
    * 
    * @param str FOO123BAR
    * @return FOO123
    */
   public static String removeTrailingAlphabets(String str)
   {
      return Pattern.compile("\\D*$").matcher(str).replaceAll("");
   }
}
