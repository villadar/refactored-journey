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
 */
package ca.humanheartnature.mag_qbo.comm;

import ca.humanheartnature.core.comm.DataExtractor;
import ca.humanheartnature.core.comm.FileReader;
import ca.humanheartnature.core.comm.FileWriter;
import ca.humanheartnature.core.comm.MySqlConnectionFactory;
import static ca.humanheartnature.core.enums.DateFormatEnum.ISO_8601;
import ca.humanheartnature.core.exception.DataExtractionException;
import ca.humanheartnature.core.exception.DataLoadingException;
import ca.humanheartnature.core.exception.DataTransformationException;
import ca.humanheartnature.core.util.DateFormatFactory;
import ca.humanheartnature.mag_qbo.enums.MagQboPropertyKeys;
import ca.humanheartnature.magento.comm.MagentoDtoSupplier;
import ca.humanheartnature.magento.struct.MagentoInvoicesDto;
import ca.humanheartnature.quickbooks.comm.QboDataLoader;
import ca.humanheartnature.quickbooks.comm.QboDataServiceSingleton;
import ca.humanheartnature.quickbooks.comm.QboDataSource;
import com.intuit.ipp.exception.FMSException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data bridge between Magento database and QuickBooks Online. Responsible for
 * transferring and translating data between these two systems.
 */
public final class MagentoQboDataBridge
{
   /** Class logger */
   private static final Logger LOGGER =
         Logger.getLogger(MagentoQboDataBridge.class.getName());
   
   /**
    * Perform a full ETL operation from Magento to QBO
    * 
    * @param magentoConnFactory Generates connection to Magento MySQL database
    * @param qboService Generates connection to QuickBooks Online
    * @param config Configuration properties
    */
   public void etlFromMagentoToQbo(MySqlConnectionFactory magentoConnFactory,
                                   QboDataServiceSingleton qboService,
                                   Properties config)
   {
      try
      {
         if (magentoConnFactory == null || qboService == null || config == null)
         {
            throw new IllegalArgumentException("Argument cannot be null");
         }

         LOGGER.log(Level.INFO, "Transfering data from Magento to QuickBooks Online...");

         Date minDateBoundary = getMinimumDateBoundary(qboService, config);
         
         DataExtractor<MagentoInvoicesDto> magentoReceiptExtractor = new DataExtractor();
         
         magentoReceiptExtractor
            .extract(new MagentoDtoSupplier(magentoConnFactory, minDateBoundary)) 
            .transform(new MagentoToQuickBooksTransformer(qboService, config))
            .load(new QboDataLoader(qboService));
      
         LOGGER.log(Level.INFO, "Data transfer finished");
      }
      catch(FMSException |
            DataExtractionException |
            DataTransformationException |
            DataLoadingException ex)
      {
         LOGGER.log(Level.SEVERE, "Data transfer operation failed", ex);
         System.exit(-1);
      }
   }
   
   /**
    * Transfer data from {@link MagentoInvoicesDto} bean file to QBO. Used in
    * conjunction with {@link #extractFromMagentoToBeanFile} to perform a full ETL
    * operation from Magento to QBO. This method exists for cases where the system running
    * this program has access to QBO but not Magento database.
    * 
    * @param beanFileLoc Location of bean file to load to QBO
    * @param qboService Connection factory to QBO
    * @param config MagentoQboDataBridge configuration file
    */
   public void loadFromBeanFileToQbo(String beanFileLoc,
                                     QboDataServiceSingleton qboService,
                                     Properties config)
   {
      try
      {
         if (beanFileLoc == null || qboService == null || config == null)
         {
            throw new IllegalArgumentException("Argument cannot be null");
         }

         LOGGER.log(Level.INFO,
                    "Loading data from {0} to QuickBooks Online...",
                    beanFileLoc);
         
         DataExtractor<MagentoInvoicesDto> extractor = new DataExtractor();
         extractor
            .extract(() ->
            {
               try
               {
                  FileReader supplier = new FileReader();
                  
                  MagentoInvoicesDto magDto =
                        (MagentoInvoicesDto) supplier.readFileToBean(beanFileLoc);
                  
                  LOGGER.log(Level.FINEST,
                             "Data transfer object content:\n" +
                             "Receipt count: {0}\n" +
                             "Customer count: {1}\n" +
                             "Item count: {2}",
                             new Object[]{magDto.getSalesInvoices().size(),
                                          magDto.getCustomers().size(),
                                          magDto.getSaleItems().size()});
                  
                  return magDto;
               }
               catch(ClassNotFoundException | IOException ex)
               {
                  throw new DataExtractionException(
                        "Failed to extract data from Magento database", ex);
               }
            })
            .transform(new MagentoToQuickBooksTransformer(qboService, config))
            .load(new QboDataLoader(qboService));
      
         LOGGER.log(Level.INFO, "Loading finished");
      }
      catch(DataExtractionException |
            DataTransformationException |
            DataLoadingException ex)
      {
         LOGGER.log(Level.SEVERE, "Data migration operation failed", ex);
         System.exit(-1);
      }
   }
   
   /**
    * Transfer data from Magento database to a {@link MagentoInvoicesDto} bean file. Used
    * in conjunction with {@link #loadFromBeanFileToQbo} to perform a full ETL operation
    * from Magento to QBO. This method exists for cases where the system running this
    * program has access to a Magento database but not QBO.
    * 
    * @param magentoConnFactory
    * @param beanFileLoc
    * @param config
    */
   public void extractFromMagentoToBeanFile(MySqlConnectionFactory magentoConnFactory,
                                            String beanFileLoc,
                                            Properties config)
   {
      if (magentoConnFactory == null || beanFileLoc == null || config == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      LOGGER.log(Level.INFO, "Extracting data from Magento DB to {0}...", beanFileLoc);

      Date minDateBoundary = getMinimumDateBoundary(config);

      DataExtractor<MagentoInvoicesDto> magentoReceiptExtractor = new DataExtractor<>();
      magentoReceiptExtractor
         .extract(new MagentoDtoSupplier(magentoConnFactory, minDateBoundary))
         .load(dataStruct ->
         {
            MagentoInvoicesDto magDto = (MagentoInvoicesDto) dataStruct;
            try
            {
               LOGGER.log(Level.FINEST,
                          "Data transfer object content:\n" +
                          "Receipt count: {0}\n" +
                          "Customer count: {1}\n" +
                          "Item count: {2}",
                          new Object[]{magDto.getSalesInvoices().size(),
                                       magDto.getCustomers().size(),
                                       magDto.getSaleItems().size()});

               FileWriter loader = new FileWriter();
               loader.writeBeanToFile(magDto, beanFileLoc);
            }
            catch(IOException ex)
            {
               throw new DataLoadingException("Failed to write bean content to file",
                                              ex);
            }
         });

      LOGGER.log(Level.INFO, "Extraction to bean file finished");
   }
   
   
   
   /* -------------------- PRIVATE METHODS -------------------- */
   
   /**
    * Get the date to be used as the minimum invoice date boundary during export.
    * Retrieved from configuration properties.
    * 
    * @param config Program configuration properties
    * @return Minimum export date boundary
    */
   private Date getMinimumDateBoundary(Properties config)
   {
      try
      {
         LOGGER.log(Level.INFO,
                    "Cannot find existing sales receipts exported from Magento.\n" +
                    "Using quickbooks.maxlookback in configuration file for maximum " +
                    "lookback date");
         return DateFormatFactory.getDateFormat(ISO_8601).parse(config.getProperty(
               MagQboPropertyKeys.QBO_MIN_DATE_BOUNDARY,""));
      }
      catch(ParseException ex)
      {
         throw new DataExtractionException(
               "Failed to retrieve value for quckbooks.lookbackdate from the " +
               "configuration file");
      }
   }
   
   /**
    * Get the date to be used as the minimum invoice date boundary during export.
    * Determined by the earliest date between the date of the last exported receipt and
    * the specified minimum look back date from the configuration properties.
    * 
    * @param qboService Enables connection to QBO
    * @param config Program configuration properties
    * @return Minimum export date boundary
    * @throws FMSException 
    */
   private Date getMinimumDateBoundary(QboDataServiceSingleton qboService,
                                       Properties config) throws FMSException
   {
      QboDataSource qboDataSource = new QboDataSource(qboService);
      
      return qboDataSource.getLastExportedSalesReceipt()
         .map(receipt ->
         {
            try
            {
               if (!config.containsKey("quickbooks.invoicedatefield"))
               {
                  // Return the value of orElseGet() method below
                  return null;
               }
               
               /* 0-based counting for invoice date custom field position during reads,
                * 1-based during writes */
               int invoiceDateFieldPos = Integer.valueOf(config.getProperty(
                     MagQboPropertyKeys.QBO_INVOICE_DATE_FIELD))-1;
               if (receipt.getCustomField().size() <= invoiceDateFieldPos)
               {
                  throw new DataExtractionException(
                        "quickbooks.invoicedatefield = " + invoiceDateFieldPos +
                        " is out of bounds");
               }
               
               Date lastInvoiceDate = DateFormatFactory.getDateFormat(ISO_8601).parse(
                     receipt.getCustomField().get(invoiceDateFieldPos).getStringValue());
               
               LOGGER.log(Level.INFO,
                          "Timestamp of last exported invoice: {0}. Exporting sales " +
                          "receipts after this timestamp",
                          lastInvoiceDate);
               
               return lastInvoiceDate;
            }
            catch(ParseException ex)
            {
               throw new DataExtractionException(
                     "Unable to parse Magento invoice date from QuickBooks online " +
                     "custom field", ex);
            }
         })
         .orElseGet(() ->
         {
            try
            {
               LOGGER.log(Level.INFO,
                          "Cannot find existing sales receipts exported from Magento.\n" +
                          "Using quickbooks.maxlookback in configuration file for " +
                          "maximum lookback date");
               return DateFormatFactory.getDateFormat(ISO_8601).parse(config.getProperty(
                     MagQboPropertyKeys.QBO_MIN_DATE_BOUNDARY,""));
            }
            catch(ParseException ex)
            {
               throw new DataExtractionException(
                     "Failed to retrieve value for quickbooks.maxlookback from " +
                     "configuration file");
            }
         });
   }
   
}
