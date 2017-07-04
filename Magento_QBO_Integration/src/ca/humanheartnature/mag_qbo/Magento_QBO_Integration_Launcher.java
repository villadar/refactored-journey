/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 *
 *
 * Ver  Date        Change Log
 * ---  ----------  -----------------------------------
 * 1.0  2017-06-14  Initial version
 * 1.1  2017-07-03  Modified help message
 */
package ca.humanheartnature.mag_qbo;

import ca.humanheartnature.core.comm.MySqlConnectionFactory;
import ca.humanheartnature.core.util.ArgParser;
import ca.humanheartnature.mag_qbo.comm.MagentoQboDataBridge;
import ca.humanheartnature.mag_qbo.enums.MagQboPropertyKeys;
import ca.humanheartnature.quickbooks.comm.QboDataServiceSingleton;
import com.intuit.ipp.exception.FMSException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Launches the Magento_QBO_Integration program from a command line call
 * <p>
 * <b>Usage Message</b>
 * <pre>
 * Program:     Magento_QBO_Integration 1.1
 * Description: Performs ETL operations between a Magento database and QuickBooks Online
 *              (QBO)
 *
 * Ordinal arguments:
 *    arg1:             Operation to execute:
 *                      mag_to_qbo - Transfer data from Magento database to QBO
 *                      extract_mag - Extract Magento database content to the file system
 *                      load_qbo - Load from the file system to QBO
 *
 * Nominal arguments:
 *    --user, -u        Magento database user
 *    --password, -p    Password for the Magento database user
 *    --bean, -b        Location of the file to extract from or load to when performing
 *                      extract_mag or load_qbo operations
 *    --config, -c      Location of this program's configuration file; mandatory argument
 *    --logging, -l     Location of the logger configuration file
 * </pre>
 */
public final class Magento_QBO_Integration_Launcher
{
   /** Class logger */
   private static final Logger LOGGER =
         Logger.getLogger(Magento_QBO_Integration_Launcher.class.getName());
   
   
   /** Application version number */
   private static final String PROGRAM_VERSION = "1.1";
   
   /** Position of operation argument */
   private static final int OPERATION_ARG = 0;
   
   /** Positional argument 1 value for Magento to QBO transfer operation */
   private static final String MAG_TO_QBO_OP = "mag_to_qbo";
   
   /** Positional argument 1 value for Magento data extraction operation */
   private static final String EXTRACT_MAG_OP = "extract_mag";
   
   /** Positional argument 1 value for QBO data loading operation */
   private static final String LOAD_QBO_OP = "load_qbo";
   
   /** Command line argument identifier for Magento database user */
   private static final String MAG_USER_ARG = "--user";
   
   /** Command line argument identifier for Magento database password for user */
   private static final String MAG_PASSWORD_ARG = "--password";
   
   /** Command line argument identifier for configuration file location */
   private static final String CONFIG_FILE_ARG = "--config";
   
   /** Command line argument identifier for bean file location */
   private static final String BEAN_FILE_ARG = "--bean";
   
   /** Command line argument identifier for logging preference file location */
   private static final String LOGGING_FILE_ARG = "--logging";
   
   
   /**
    * Initializes and parses command line arguments for Magento_QBO_Integration program
    * 
    * @param args the command line arguments
    */
   public static void main(String[] args)
   {
      ArgParser argParser = new ArgParser();
      argParser.setUsageMessageHeader(
            "Magento_QBO_Integration",
            PROGRAM_VERSION,
            "Performs ETL operations between a Magento database and QuickBooks Online " +
            "(QBO)");
      
      argParser.addPositionalArgument(
            "Operation to execute:\n" +
            MAG_TO_QBO_OP + " - Transfer data from Magento database to QBO\n" +
            EXTRACT_MAG_OP + " - Extract Magento database content to the file system\n" +
            LOAD_QBO_OP + " - Load from the file system to QBO");
      
      argParser.addNamedArgument(
            MAG_USER_ARG, "-u", "Magento database user", true);
      argParser.addNamedArgument(
            MAG_PASSWORD_ARG, "-p",
            "Password for the Magento database user",
            true);
      argParser.addNamedArgument(
            BEAN_FILE_ARG, "-b",
            "Location of the file to extract from or load to when performing " +
            "extract_mag or load_qbo operations", true);
      argParser.addNamedArgument(
            CONFIG_FILE_ARG, "-c",
            "Location of this program's configuration file; mandatory argument", true);
      argParser.addNamedArgument(
            LOGGING_FILE_ARG, "-l", "Location of the logger configuration file", true);
      
      argParser.parseArgs(args);
      
      Properties config = new Properties();
      if (argParser.isNamedArgumentUsed(CONFIG_FILE_ARG))
      {
         String fileLocation = argParser.getNamedArgValue(CONFIG_FILE_ARG);
         try (InputStream inputStream = new FileInputStream(fileLocation))
         {
            config.load(inputStream);
         }
         catch(IOException ex)
         {
            System.out.println(
                  "Cannot access configuration properties file: " + fileLocation);
            System.exit(-1);
         }
      }
      else
      {
         System.out.println("Configuration file location must be specified\n");
         argParser.printUsageMessage();
         System.exit(-1);
      }
      
      if (argParser.isNamedArgumentUsed(LOGGING_FILE_ARG))
      {
         String fileLocation = argParser.getNamedArgValue(LOGGING_FILE_ARG);
         try (InputStream inputStream = new FileInputStream(fileLocation))
         {
            LogManager.getLogManager().readConfiguration(inputStream);
         }
         catch(IOException ex)
         {
            System.out.println("Cannot access logging properties file: " + fileLocation);
            ex.printStackTrace(System.out);
            System.exit(-1);
         }
      }      
      
      Magento_QBO_Integration_Launcher launcher = new Magento_QBO_Integration_Launcher();
      launcher.launch(argParser, config);
   }
   
   /**
    * Executes program operations based on command line argument input and configuration
    * file settings
    * 
    * @param argParser Command line argument parser for this program
    * @param config Configuration properties for this program
    */
   private void launch(ArgParser argParser, Properties config)
   {
      try
      {
         MagentoQboDataBridge dataBridge = new MagentoQboDataBridge();
         MySqlConnectionFactory magConnFactory;
         QboDataServiceSingleton qboService;

         switch(argParser.getPositionalArgValue(OPERATION_ARG))
         {
            case MAG_TO_QBO_OP:
               if (!argParser.isNamedArgumentUsed(MAG_USER_ARG))
               {
                  System.out.println("Database user must be specified\n");
                  argParser.printUsageMessage();
                  System.exit(-1);
               }
               if (!argParser.isNamedArgumentUsed(MAG_PASSWORD_ARG))
               {
                  System.out.println("Database password must be specified\n");
                  argParser.printUsageMessage();
                  System.exit(-1);
               }
      
               magConnFactory = new MySqlConnectionFactory(
                     config.getProperty(MagQboPropertyKeys.MAGENTO_HOST),
                     config.getProperty(MagQboPropertyKeys.MAGENTO_DATABASE),
                     argParser.getNamedArgValue(MAG_USER_ARG),
                     argParser.getNamedArgValue(MAG_PASSWORD_ARG));
               qboService = new QboDataServiceSingleton(
                     config.getProperty(MagQboPropertyKeys.QBO_BASE_URL),
                     config.getProperty(MagQboPropertyKeys.QBO_APP_TOKEN),
                     config.getProperty(MagQboPropertyKeys.QBO_REALM_ID),
                     config.getProperty(MagQboPropertyKeys.QBO_CONSUMER_KEY),
                     config.getProperty(MagQboPropertyKeys.QBO_CONSUMER_SECRET),
                     config.getProperty(MagQboPropertyKeys.QBO_ACCESS_TOKEN),
                     config.getProperty(MagQboPropertyKeys.QBO_ACCESS_SECRET));
               dataBridge.etlFromMagentoToQbo(magConnFactory, qboService, config);
               break;

            case EXTRACT_MAG_OP:
               if (!argParser.isNamedArgumentUsed(MAG_USER_ARG))
               {
                  System.out.println("Database user must be specified\n");
                  argParser.printUsageMessage();
                  System.exit(-1);
               }
               if (!argParser.isNamedArgumentUsed(MAG_PASSWORD_ARG))
               {
                  System.out.println("Database password must be specified\n");
                  argParser.printUsageMessage();
                  System.exit(-1);
               }
               if (!argParser.isNamedArgumentUsed(BEAN_FILE_ARG))
               {
                  System.out.println("Bean file name must be specified\n");
                  argParser.printUsageMessage();
                  System.exit(-1);
               }
               
               magConnFactory = new MySqlConnectionFactory(
                     config.getProperty(MagQboPropertyKeys.MAGENTO_HOST),
                     config.getProperty(MagQboPropertyKeys.MAGENTO_DATABASE),
                     argParser.getNamedArgValue(MAG_USER_ARG),
                     argParser.getNamedArgValue(MAG_PASSWORD_ARG));
               dataBridge.extractFromMagentoToBeanFile(
                     magConnFactory,
                     argParser.getNamedArgValue(BEAN_FILE_ARG),
                     config);
               break;

            case LOAD_QBO_OP:
               if (!argParser.isNamedArgumentUsed(BEAN_FILE_ARG))
               {
                  System.out.println("Bean file name must be specified\n");
                  argParser.printUsageMessage();
                  System.exit(-1);
               }
               
               qboService = new QboDataServiceSingleton(
                     config.getProperty(MagQboPropertyKeys.QBO_BASE_URL),
                     config.getProperty(MagQboPropertyKeys.QBO_APP_TOKEN),
                     config.getProperty(MagQboPropertyKeys.QBO_REALM_ID),
                     config.getProperty(MagQboPropertyKeys.QBO_CONSUMER_KEY),
                     config.getProperty(MagQboPropertyKeys.QBO_CONSUMER_SECRET),
                     config.getProperty(MagQboPropertyKeys.QBO_ACCESS_TOKEN),
                     config.getProperty(MagQboPropertyKeys.QBO_ACCESS_SECRET));
               dataBridge.loadFromBeanFileToQbo(argParser.getNamedArgValue(BEAN_FILE_ARG),
                                                qboService,
                                                config);
               break;
               
            default:
               // Cases where switch argument is null is handled by ArgParser.parse()
               System.out.println(argParser.getPositionalArgValue(OPERATION_ARG) +
                                  " is not a valid operation\n");
               argParser.printUsageMessage();
               System.exit(-1);
         }
      }
      catch (FMSException ex)
      {
         LOGGER.log(Level.SEVERE,
                    "Unable to connect to QuickBooks Online. Verify that the " +
                    "QuickBooks Online configuration properties are correct",
                    ex);
         System.exit(-1);
      }
   }
   
}
