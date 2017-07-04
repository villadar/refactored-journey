/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 *
 *
 * Ver  Date        Change Log
 * ---  ----------  --------------------------------------
 * 1.0  2017-06-14  Initial version
 * 1.1  2017-07-03  Fixed formatting of help usage message
 */
package ca.humanheartnature.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This module parses command line arguments that are passed in through the main method's
 * parameter.
 * <p>
 * Positional arguments are defined through {@link #addPositionalArgument} whereas named
 * arguments are defined through {@link #addNamedArgument}. When {@link #parseArgs} is
 * called, the contents of the command-line argument <code>String</code> array are
 * categorized into positional arguments accessed through {@link #getPositionalArgValue},
 * and named arguments that are accessed through {@link #getNamedArgValue} and
 * {@link #isNamedArgumentUsed}. If a "-h" or "--help" is detected, then a formatted
 * usage instruction message will be printed. Error messages are printed into
 * <code>stdout</code> and the program is exited if:
 * <ul>
 *    <li> insufficient or too many positional arguments are passed in
 *    <li> unexpected named arguments are passed in
 *    <li> named arguments expecting a value are not assigned one
 *    <li> named arguments not expecting a value are assigned one
 *    <li> a named argument is defined more than once
 * </ul>
 * Supported delimiters between named arguments and their values are whitespaces or '='.
 * No delimiter between a named argument and its value is also supported. Named
 * command line arguments with a multiplicity greater than one are not supported.
 */
public final class ArgParser
{
   /** Ordered list of named command line arguments. A list is used so that the order that
     * the arguments are defined is reflected in usage message */
   private final List<NamedArgument> namedArgList = new ArrayList<>();
   
   /** Left margin of argument description used during printing of the usage message */
   public static final String ARG_DESC_LEFT_MARGIN = "                     ";
   
   /** Ordered list of positional command line arguments that are read during program
     * execution */
   private final List<String> posArgList = new ArrayList<>();
   
   /** Ordered list of descriptions for positional arguments. Used to construct usage
     * instruction message */
   private final List<String> posArgDescList = new ArrayList<>();
         
   /** Name of the program */
   private String programName;
   
   /** Version of the current program */
   private String programVersion;
   
   /** A description of the current program */
   private String programDesc;
   
   
   /**
    * Sets the header for the usage instructions message that is printed when "-h" or
    * "--help" is found in the command line argument list or when an illegal list of
    * arguments is passed in
    * 
    * @param programName Name of the current program
    * @param programVersion Version of current program
    * @param programDescription A description of the current program
    */
   public void setUsageMessageHeader(String programName,
                                     String programVersion,
                                     String programDescription)
   {
      if (programDescription == null ||
          programVersion == null ||
          programDescription == null)
      {
         throw new IllegalArgumentException("Parameters cannot be null");
      }
      
      this.programName = programName;
      this.programVersion = programVersion;
      this.programDesc = programDescription;
   }
   
   
   /**
    * Increments the number of expected positional arguments by one and adds a description
    * for the new positional argument
    * 
    * @param description Description of positional argument
    */
   public void addPositionalArgument(String description)
   {
      if (description == null)
      {
         throw new IllegalArgumentException("Parameter cannot be null");
      }
      
      posArgDescList.add(description);
   }
   
   
   /**
    * Adds an argument to the named argument list that the current program can handle.
    * Either one of shortFormId or longFormId can be null but not both.
    * 
    * @param longFormId Multi-character identifier of the named argument to add.
    *                   Must be prefixed with "--". <i>Nullable</i>
    * @param shortFormId Single character identifier of the named argument to
    *                    add with a "-" prefix. <i>Nullable</i>
    * @param description Description of the named argument to add 
    * @param isExpectingValue If TRUE, then print an error message and exit if no value
    *                         for the named argument is provided. If FALSE, then print
    *                         an error message and exit if a value is provided
    */
   public void addNamedArgument(String longFormId,
                                String shortFormId,
                                String description,
                                boolean isExpectingValue)
   {
      if (shortFormId == null && longFormId == null)
      {
         throw new IllegalArgumentException(
               "At least one of the shortFormId or longFormId must not be null");
      }
      if (shortFormId != null
            && (shortFormId.length() != 2 ||
                shortFormId.charAt(0) != '-' ||
                !Character.isLowerCase(shortFormId.charAt(1))))
      {
         throw new IllegalArgumentException(
               "Short form named argument identifier must consist of a single dash " +
               "and an alphabet character");
      }
      if (longFormId!=null && !longFormId.substring(0, 2).equals("--"))
      {
         throw new IllegalArgumentException(
               "Long form of named argument identifer must be prefixed with a double " +
               "dash");
      }
      if (description == null)
      {
         throw new IllegalArgumentException("Description cannot be null");
      }
      
      // Make sure both shortForm and longForm has not been defined yet
      /* Can't use Set to ensure uniqueness due to multiple, nullable keys (shortFormId
       * and longFormId) */
      if (namedArgList.stream().anyMatch(namedArg ->
            namedArg.shortFormId != null && namedArg.shortFormId.equals(shortFormId) ||
            namedArg.longFormId != null && namedArg.longFormId.equals(longFormId)))
      {
         throw new IllegalArgumentException(
               "Either shortFormId or longFormId has already been defined");
      }                 
      
      namedArgList.add(new NamedArgument(longFormId,
                                         shortFormId,
                                         description,
                                         isExpectingValue));
   }
  
   
   /**
    * Parses command line arguments retrieved from the main method. If a command line
    * switch is "-h" or "--help", then print the usage message to <code>stdout</code>.
    * 
    * @param args Arguments retrieved from the main method
    */
   public void parseArgs(String[] args)
   {
      if (args == null)
      {
         throw new IllegalArgumentException("Parameter cannot be null");
      }
      
      
      for (int i=0; i<args.length; i++)
      {
         if (!args[i].startsWith("-"))
         {
            posArgList.add(args[i]);
         }
         else
         {
            if (args[i].equals("-h") || args[i].equals("--help"))
            {
               printUsageMessage();
               System.exit(0);
            }
            
            
            /* Extract the named command line argument identifier and its value to argId
             * and argValue respectively */
            final String argId;
            String argValue = null;
            if (args[i].contains("="))
            {
               argId = args[i].split("=", 2)[0];
               argValue = args[i].split("=", 2)[1];
            }
            else if (args[i].charAt(1)!='-' && args[i].length()>2)
            {
               argId = args[i].substring(0,2);
               argValue = args[i].substring(2);
            }
            else
            {
               argId = args[i];
            }
            
            if (!namedArgList.stream()
                  .flatMap(namedArg ->
                     Stream.of(namedArg.longFormId, namedArg.shortFormId))
                  .anyMatch(id ->
                     argId.equals(id)))
            {
               System.out.println(
                     "Error parsing command line parameters: " +
                     "Unexpected token: "+argId+"\n");
               printUsageMessage();
               System.exit(-1);
            }
            
            NamedArgument namedArg = getNamedArg(argId);
            
            if (namedArg.isExpectingValue && argValue == null)
            {
               if (args.length > i+1 && !args[i+1].startsWith("-"))
               {
                  argValue = args[++i];
               }
               else
               {
                  System.out.println(
                        "Error parsing command line parameters: Named argument " +
                        argId + " is used but no value is provided\n");
                  printUsageMessage();
                  System.exit(-1);
               }
            }
            else if (!namedArg.isExpectingValue && argValue != null)
            {
               System.out.println(
                     "Command line switch " + argId + " cannot be assigned a value " +
                     argValue + "\n");
               printUsageMessage();
               System.exit(-1);
            }
            
            // Only named arguments with a multiplicity of one or zero is supported
            if (namedArg.count > 0)
            {
               System.out.println(
                     "Error parsing command line parameters: " + argId + " has already " +
                     "been specified\n");
               printUsageMessage();
               System.exit(-1);
            }
            
            namedArg.count++;
            namedArg.value = argValue;
         }
      }
      
      if (posArgList.size() != posArgDescList.size())
      {
         System.out.println(
               "Error parsing command line parameters: Invalid number of positional " +
               "arguments. Expected " + posArgDescList.size() + " but found " +
               posArgList.size() + "\n");
         printUsageMessage();
         System.exit(-1);
      }
   }
   
   
   /**
    * Prints the usage message of the application to <code>stdout</code>:
    * <p>
    * <pre>
    * Program:     Program_name 1.0
    * Description: Description goes here
    * 
    * Positional arguments:
    *    arg1:            Description
    * 
    * Named arguments:
    *   --id, -l          Description
    *   --id2             Description
    *   -m                Description
    * </pre>
    */
   public void printUsageMessage()
   {
      String usageMessage = "Program:     " + programName + " " + programVersion + "\n" +
                            "Description: " + programDesc + "\n\n";
      
      usageMessage += "Positional arguments:\n";
      int i = 1;
      for (String posArgDesc : posArgDescList)
      {
         usageMessage +=  "   arg" + i++ + ":             " +
               posArgDesc.replace("\n", "\n" + ArgParser.ARG_DESC_LEFT_MARGIN) + "\n";
      }
      
      usageMessage +=  "\nNamed arguments:\n";
      for (NamedArgument namedArg : namedArgList)
      {
         int totalIdentifierLength = 0;
         usageMessage += "   ";
         
         if (namedArg.shortFormId != null)
         {
            usageMessage += namedArg.shortFormId;
            totalIdentifierLength += namedArg.shortFormId.length();
         }
         
         if (namedArg.shortFormId != null && namedArg.longFormId != null)
         {
            usageMessage += ", ";
            totalIdentifierLength += 2;
         }
         
         if (namedArg.longFormId != null)
         {
            usageMessage += namedArg.longFormId;
            totalIdentifierLength += namedArg.longFormId.length();
         }
         
         String spacer = "                ";
         if (totalIdentifierLength <= spacer.length())
         {
            usageMessage += spacer.substring(totalIdentifierLength);
         }
         usageMessage += "  " +
               namedArg.description.replace("\n", "\n" + ArgParser.ARG_DESC_LEFT_MARGIN) +
               "\n";
      }
      
      System.out.print(usageMessage);
   }
   
   /**
    * @param identifier Either the long-form or short-form named argument identifier
    * @return True if the named argument identifier was used in the command line
    */
   public boolean isNamedArgumentUsed(String identifier)
   {
      if (identifier == null)
      {
         throw new IllegalArgumentException("Parameter cannot be null");
      }
      
      return getNamedArg(identifier).count > 0;
   }
   
   /**
    * Only usable for named arguments that are expecting values
    * 
    * @param identifier Either the long-form or short-form named argument identifier
    * @return Value of named argument
    */
   public String getNamedArgValue(String identifier)
   {
      if (identifier == null)
      {
         throw new IllegalArgumentException("Parameter cannot be null");
      }
      
      NamedArgument namedArg = getNamedArg(identifier);
      
      if (namedArg.count == 0)
      {
         System.out.println(identifier+" was not used in the command line");
         printUsageMessage();
         System.exit(-1);
      }
      if (!namedArg.isExpectingValue)
      {
         throw new IllegalStateException("This method can only be used for named " +
                                         "arguments that are expecting values");
      }
      
      return namedArg.value;
   }
   
   
   /**
    * @param i Index of the positional argument
    * @return Ordered list of positional command line arguments
    */
   public String getPositionalArgValue(int i)
   {
      return posArgList.get(i);
   }
   
   
   
   /* -------------------- PRIVATE METHODS -------------------- */
   
   /**
    * Retrieve named argument 
    * 
    * @param identifier Either the long-form or short-form named argument identifier
    * @return <Code>NamedArgument</code> object identified by the identifier
    */
   private NamedArgument getNamedArg(String identifier)
   {
      return namedArgList.stream()
         .filter(namedArg ->
            identifier.equals(namedArg.shortFormId) ||
            identifier.equals(namedArg.longFormId))
         .findFirst()
         .orElseThrow(() ->
            new IllegalArgumentException("Named argument identifier " + identifier +
                                         " has not been defined"));
   }
   
   
   
   /* -------------------- NESTED CLASS -------------------- */
   
   /**
    * Struct for named argument
    */
   private final class NamedArgument
   {      
      /** Short form of named argument consisting of a single dash and a lowercase
        * character */
      private final String shortFormId;
      
      /** Long form of named argument prefixed by double dash */
      private final String longFormId;
      
      /** Description of named argument */
      private final String description;
      
      /** True if named argument value is expecting a value */
      private final boolean isExpectingValue;
      
      /** The value assigned */
      private String value;
      
      /** The number of times this named argument is used in the command line  */
      private int count = 0;
      
      
      /**
       * At least one of the shortFormId or longFormId must not be null
       * 
       * @param shortFormId Short form of named argument consisting of a single dash
       *                    and a lowercase character
       * @param longFormId Long form of named argument prefixed by double dash
       * @param description Description of named argument
       * @param isExpectingValue True if named argument value is expecting a value
       */
      private NamedArgument(String shortFormId,
                            String longFormId,
                            String description,
                            boolean isExpectingValue)
      {
         this.shortFormId = shortFormId;
         this.longFormId = longFormId;
         this.description = description;
         this.isExpectingValue = isExpectingValue;
      }
   }
   
}
