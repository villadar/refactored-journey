/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 *
 *
 * Ver  Date        Change Log
 * ---  ----------  ----------------------------------------------------------------
 * 1.0  2017-06-14  Initial version
 * 1.1  2017-07-03  - Class is attached to item instead of entire transaction
 *                  - Modifed inovice timestamp based on quickbooks.timediff property
 */
package ca.humanheartnature.mag_qbo.comm;

import static ca.humanheartnature.core.enums.DateFormatEnum.ISO_8601;
import ca.humanheartnature.core.exception.DataTransformationException;
import ca.humanheartnature.core.util.DateFormatFactory;
import ca.humanheartnature.mag_qbo.enums.MagQboPropertyKeys;
import ca.humanheartnature.magento.struct.Customer;
import ca.humanheartnature.magento.struct.MagentoInvoicesDto;
import ca.humanheartnature.magento.struct.SaleItem;
import ca.humanheartnature.magento.struct.SalesInvoice;
import ca.humanheartnature.quickbooks.comm.QboDataManipulator;
import ca.humanheartnature.quickbooks.comm.QboDataServiceSingleton;
import ca.humanheartnature.quickbooks.struct.QboInvoicesDto;
import ca.humanheartnature.quickbooks.util.ClassIndexLookup;
import ca.humanheartnature.quickbooks.util.CustomerEmailToIndexLookup;
import ca.humanheartnature.quickbooks.util.DepositAccountIndexLookup;
import ca.humanheartnature.quickbooks.util.PaymentMethodIndexLookup;
import ca.humanheartnature.quickbooks.util.ShippingIndexLookup;
import ca.humanheartnature.quickbooks.util.SkuToIndexLookup;
import ca.humanheartnature.quickbooks.util.TaxCodeLookup;
import com.intuit.ipp.data.CustomField;
import com.intuit.ipp.data.CustomFieldTypeEnum;
import com.intuit.ipp.data.DiscountLineDetail;
import com.intuit.ipp.data.EmailAddress;
import com.intuit.ipp.data.Item;
import com.intuit.ipp.data.Line;
import com.intuit.ipp.data.LineDetailTypeEnum;
import com.intuit.ipp.data.PhysicalAddress;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.SalesItemLineDetail;
import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.data.TxnTaxDetail;
import com.intuit.ipp.exception.FMSException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Transforms data from Magento DAO to QBO API
 */
public final class MagentoToQuickBooksTransformer
      implements Function<MagentoInvoicesDto, QboInvoicesDto>
{   
   /** Class logger */
   private static final Logger LOGGER =
         Logger.getLogger(MagentoToQuickBooksTransformer.class.getName());
   
   /** Connection to QuickBooksOnline */
   private final QboDataServiceSingleton qboService;
   
   /** Field position for the invoice date custom field */
   private final String invoiceDateCustomField;
   
   /** QuickBooks class name to set to */
   private final String className;
   
   /** Name of account to deposit sales revenue to */
   private final String depositToAccountName;
   
   /** Name of the payment method in QuickBooks for credit card payments */
   private String creditCardMethodName;
   
   /** Name of the payment method in QuickBooks for paypal payments */
   private String paypalMethodName;
   
   /** Sku of the shipping service */
   private String shippingIndexSku;
   
   /** Amount of time to add/subtract to invoice timestamp */
   private String timeDiff;
   
   
   
   /* -------------------- CONSTRUCTORS -------------------- */
   
   /**
    * @param qboService Communication interface to QBO
    * @param config Configuration properties file
    */
   public MagentoToQuickBooksTransformer(QboDataServiceSingleton qboService,
                                         Properties config)
   {
      if (qboService == null || config == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      this.invoiceDateCustomField = config.getProperty(
            MagQboPropertyKeys.QBO_INVOICE_DATE_FIELD);
      this.depositToAccountName = config.getProperty(
            MagQboPropertyKeys.QBO_DEPOSIT_ACCOUNT);
      this.className = config.getProperty(
            MagQboPropertyKeys.QBO_CLASS);
      this.creditCardMethodName = config.getProperty(
            MagQboPropertyKeys.QBO_CREDIT_CARD_PAYMENT_METHOD);
      this.paypalMethodName = config.getProperty(
            MagQboPropertyKeys.QBO_PAYPAL_PAYMENT_METHOD);
      this.shippingIndexSku = config.getProperty(
            MagQboPropertyKeys.QBO_SHIPPING_SKU);
      this.timeDiff = config.getProperty(
            MagQboPropertyKeys.QBO_TIME_DIFF);
      
      this.qboService = qboService;
   }
   
   
   
   /* -------------------- OVERRIDDEN METHODS -------------------- */
   
   @Override
   public QboInvoicesDto apply(MagentoInvoicesDto salesDataTransfer)
   {
      try
      {
         if (salesDataTransfer == null)
         {
            throw new IllegalArgumentException("Argument cannot be null");
         }

         if (salesDataTransfer.getSalesInvoices().isEmpty())
         {
            LOGGER.log(Level.FINE, "No sales receipts in input");
            return new QboInvoicesDto(new ArrayList<>());
         }
         
         // Initialize lookup objects
         // <editor-fold defaultstate="collapsed" desc="Lookup object initialization">
         ClassIndexLookup classLookup = new ClassIndexLookup(qboService);

         Set<Customer> customers = salesDataTransfer.getCustomers();
         Set<String> emails = customers.stream()
            .map(customer -> customer.getEmail())
            .collect(Collectors.toSet());
         CustomerEmailToIndexLookup customerLookup =
               new CustomerEmailToIndexLookup(qboService, emails);
      
         DepositAccountIndexLookup depositAccountLookup =
               new DepositAccountIndexLookup(qboService);
         
         PaymentMethodIndexLookup paymentMethodLookup =
               new PaymentMethodIndexLookup(qboService,
                                            creditCardMethodName,
                                            paypalMethodName);
         
         Set<SaleItem> saleItems = salesDataTransfer.getSaleItems();
         Set<String> skus = saleItems.stream()
            .map(product -> product.getSKU())
            .collect(Collectors.toSet());
         SkuToIndexLookup skuLookup = new SkuToIndexLookup(qboService,skus);
         
         TaxCodeLookup taxCodeLookup = new TaxCodeLookup(qboService);
         
         ShippingIndexLookup shippingLookup = new ShippingIndexLookup(qboService);
         // </editor-fold>
      
         List<SalesInvoice> magentoInvoices = salesDataTransfer.getSalesInvoices();
         List<SalesReceipt> qboReceipts = magentoInvoices.stream()
            .map(magInvoice ->
            {
               try
               {
                  SalesReceipt qboSalesReceipt = new SalesReceipt();

                  List<Line> items = magInvoice.getSaleItems().stream()
                     .map(magItem ->
                        transformItemDetails(magItem,
                                             taxCodeLookup,
                                             classLookup,
                                             skuLookup))
                     .collect(Collectors.toList());
                  qboSalesReceipt.setLine(items);
                  
                  qboSalesReceipt.setDocNumber(magInvoice.getOrderName());
                  
                  Calendar transactionDate = Calendar.getInstance();
                  transactionDate.setTime((magInvoice.getTransactionDate()));
                  if (timeDiff != null)
                  {
                     transactionDate.add(Calendar.HOUR_OF_DAY, Integer.parseInt(timeDiff));
                  }
                  qboSalesReceipt.setTxnDate(transactionDate.getTime());
                  
                  qboSalesReceipt = addQboInvoiceDateField(qboSalesReceipt, magInvoice);
                  qboSalesReceipt = addQboClass(qboSalesReceipt, classLookup);
                  
                  ReferenceType taxCodeRef = getTaxCodeReference(magInvoice,
                                                                 taxCodeLookup);

                  qboSalesReceipt = transformPaymentDetails(qboSalesReceipt,
                                                            magInvoice,
                                                            paymentMethodLookup);
                  qboSalesReceipt = transformBillingDetails(qboSalesReceipt,
                                                            magInvoice,
                                                            depositAccountLookup,
                                                            taxCodeRef);
                  qboSalesReceipt = transformDiscountDetails(qboSalesReceipt, magInvoice);
                  qboSalesReceipt = transformShippingDetails(qboSalesReceipt,
                                                             magInvoice,
                                                             shippingLookup,
                                                             classLookup,
                                                             taxCodeRef);
                  qboSalesReceipt = transformCustomerDetails(qboSalesReceipt,
                                                             magInvoice,
                                                             customerLookup);

                  return qboSalesReceipt;
               }
               catch (FMSException ex)
               {
                  throw new DataTransformationException(
                                 "Failed to connect to QuickBooks Online", ex);
               }
            })
            .collect(Collectors.toList());

            return new QboInvoicesDto(qboReceipts);
         }
      catch(FMSException ex)
      {
         throw new DataTransformationException(
               "Failed to transform Magento DAO to QBO", ex);
      }
   }
   
   
   
   /* -------------------- PRIVATE METHODS -------------------- */
   
   /**
    * Adds a new customer to QBO. The display name of the customer must be unique so a his
    * full name is appended with a # and an auto-incrementing 5 digit number that is
    * padded to the left with zeros
    * 
    * @param magCustomer Magento customer DAO whose attributes are used to populate QBO
    *                    API customer
    * @return New QBO customer, whose internal index attributes are filled in
    */
   private com.intuit.ipp.data.Customer addNewCustomerToQbo(Customer magCustomer)
   {
      try
      {
         com.intuit.ipp.data.Customer newCustomer = new com.intuit.ipp.data.Customer();
         
         newCustomer.setFullyQualifiedName(magCustomer.getFullName());
         newCustomer.setGivenName(magCustomer.getFirstName());
         newCustomer.setMiddleName(magCustomer.getMiddleName());
         newCustomer.setFamilyName(magCustomer.getLastName());

         EmailAddress customerEmailAddr = new EmailAddress();
         customerEmailAddr.setAddress(magCustomer.getEmail());
         newCustomer.setPrimaryEmailAddr(customerEmailAddr);

         PhysicalAddress billAddress = new PhysicalAddress();
         billAddress.setLine1(magCustomer.getBillingAddress().getFullAddress());
         newCustomer.setBillAddr(billAddress);

         PhysicalAddress shipAddress = new PhysicalAddress();
         shipAddress.setLine1(magCustomer.getShippingAddress().getFullAddress());

         newCustomer.setShipAddr(shipAddress);

         QboDataManipulator dataInserter = new QboDataManipulator(qboService);
         newCustomer =  dataInserter.addCustomer(newCustomer);

         return newCustomer;
      }
      catch(FMSException ex)
      {
         throw new DataTransformationException(
               "Failed to connect to QuickBooks Online", ex);
      }
   }
   
   /**
    * Transform payment details from Magento DAO to QBO
    * 
    * @param qboSalesReceipt QBO receipt to append the payment details to
    * @param magentoSalesInvoice Contains Magento data to transform
    * @param paymentLookup Lookup table used to resolve QBO API internal indeces
    * @return QBO receipt with payment details appended to it
    * @throws FMSException 
    */
   private SalesReceipt transformPaymentDetails(SalesReceipt qboSalesReceipt,
                                                SalesInvoice magentoSalesInvoice,
                                                PaymentMethodIndexLookup paymentLookup)
                                                throws FMSException
   {
      String paymentMethodIndex =
            paymentLookup.lookup(magentoSalesInvoice.getPaymentMethod())
               .orElseThrow(() ->
                  new DataTransformationException(
                        "Failed to look up payment method type: " +
                        magentoSalesInvoice.getPaymentMethod().name() +
                        "\nPayment method lookup table content: " + paymentLookup));

      ReferenceType paymentMethodRef = new ReferenceType();
      paymentMethodRef.setValue(paymentMethodIndex);

      qboSalesReceipt.setPaymentMethodRef(paymentMethodRef);
      
      return qboSalesReceipt;
   }
   
   /**
    * Add invoice date to QBO invoice. A number of hours is added or subtracted based on
    * <code>timeDiff</code>
    * 
    * @param qboSalesReceipt QBO receipt to append the invoice date to
    * @param magentoSalesInvoice Contains Magento data to transform
    * @return QBO receipt with invoice date appended to it
    * @throws FMSException 
    */
   private SalesReceipt addQboInvoiceDateField(SalesReceipt qboSalesReceipt,
                                               SalesInvoice magentoSalesInvoice)
   {
      Calendar transactionDate = Calendar.getInstance();
      transactionDate.setTime((magentoSalesInvoice.getTransactionDate()));
      if (timeDiff != null)
      {
         transactionDate.add(Calendar.HOUR_OF_DAY, Integer.parseInt(timeDiff));
      }
      
      CustomField field = new CustomField();
      if (invoiceDateCustomField == null)
      {
         throw new DataTransformationException(
               "Custom field position for invoice date field was not specified");
      }
      
      field.setDefinitionId(invoiceDateCustomField);
      field.setStringValue(
            DateFormatFactory.getDateFormat(ISO_8601).format(transactionDate.getTime()));
      field.setType(CustomFieldTypeEnum.STRING_TYPE);

      qboSalesReceipt.setCustomField(Arrays.asList(field));
      
      return qboSalesReceipt;
   }  
   
   /**
    * Add class to QBO invoice
    * 
    * @param qboSalesReceipt QBO receipt to append the class to
    * @param classLookup Used to resolve internal index of QBO class
    * @return QBO receipt with invoice date appended to it
    * @throws FMSException 
    */
   private SalesReceipt addQboClass(SalesReceipt qboSalesReceipt,
                                    ClassIndexLookup classLookup) throws FMSException
   {
      String classId = classLookup.lookup(className)
         .orElseThrow(() ->
            new DataTransformationException(
                  "Failed to look up transaction class: " + className));

      ReferenceType classRef = new ReferenceType();
      classRef.setValue(classId);
      qboSalesReceipt.setClassRef(classRef);
      
      return qboSalesReceipt;
   }
   
   /**
    * Transform a single sale item line from Magento DAO to QBO
    * 
    * @param magentoSaleItem Contains Magento data to transform
    * @param taxLookup Lookup table used to resolve QBO tax code indexes
    * @param skuLookup Lookup table used to resolve QBO sale item indexes
    * @return QBO sale item line
    * @throws FMSException 
    */
   private Line transformItemDetails(SaleItem magentoSaleItem,
                                     TaxCodeLookup taxLookup,
                                     ClassIndexLookup classLookup,
                                     SkuToIndexLookup skuLookup)
   {
      String inventoryIndex = skuLookup.lookup(magentoSaleItem.getSKU())
         .orElseThrow(() -> 
            new DataTransformationException(
                  "Failed to look up sku: " + magentoSaleItem.getSKU() +
                  "\nSKU lookup table content: " + skuLookup));

      ReferenceType productLineRef = new ReferenceType();
      productLineRef.setValue(inventoryIndex);

      String taxCodeIndex = taxLookup.lookup(magentoSaleItem.getTaxCode())
         .orElseThrow(() -> 
            new DataTransformationException(
                  "Failed to look up tax code: " + magentoSaleItem.getTaxCode() +
                  "\nPayment method lookup table content: " + taxLookup));

      ReferenceType taxCodeRef = new ReferenceType();
      taxCodeRef.setValue(taxCodeIndex);
      
      String classId;
      try
      {
         classId = classLookup.lookup(className)
            .orElseThrow(() ->
               new DataTransformationException(
                     "Failed to look up transaction class: " + className));
      }
      catch(FMSException ex)
      {
         throw new DataTransformationException(
               "Failed to lookup class index: " + className);
      }

      ReferenceType classRef = new ReferenceType();
      classRef.setValue(classId);

      SalesItemLineDetail saleItemDetail = new SalesItemLineDetail();
      saleItemDetail.setItemRef(productLineRef);
      saleItemDetail.setTaxCodeRef(taxCodeRef);
      saleItemDetail.setQty(new BigDecimal(magentoSaleItem.getQuantity()));
      saleItemDetail.setUnitPrice(magentoSaleItem.getUnitPrice());
      saleItemDetail.setClassRef(classRef);

      Line qboSaleItem = new Line();
      qboSaleItem.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);
      qboSaleItem.setSalesItemLineDetail(saleItemDetail);
      qboSaleItem.setAmount(magentoSaleItem.getTotalPriceWithTax());
      qboSaleItem.setDescription(magentoSaleItem.getDisplayName());

      return qboSaleItem;
   }
   
   /**
    * Transform billing details from Magento DAO to QBO
    * 
    * @param qboSalesReceipt QBO receipt to append the billing details to
    * @param magentoSalesInvoice Contains Magento data to transform
    * @param accountLookup Lookup table used to resolve QBO internal account indexes
    * @param taxCodeRef Reference to QBO tax code
    * @return QBO receipt with payment details appended to it
    * @throws FMSException 
    */
   private SalesReceipt transformBillingDetails(SalesReceipt qboSalesReceipt,
                                                SalesInvoice magentoSalesInvoice,
                                                DepositAccountIndexLookup accountLookup,
                                                ReferenceType taxCodeRef)
   {
      PhysicalAddress billAddr = new PhysicalAddress();
      billAddr.setLine1(
            magentoSalesInvoice.getCustomer().getBillingAddress().getFullAddress());

      EmailAddress email = new EmailAddress();
      email.setAddress(magentoSalesInvoice.getCustomer().getEmail());

      String depositToAccountId = accountLookup.lookup(depositToAccountName)
         .orElseThrow(() ->
            new DataTransformationException(
                  "Failed to look up deposit account: " + depositToAccountName +
                  "\nPayment method lookup table content: " +
                  accountLookup));

      ReferenceType depositTo = new ReferenceType();
      depositTo.setValue(depositToAccountId);
      
      TxnTaxDetail taxDetail = new TxnTaxDetail();
      taxDetail.setTxnTaxCodeRef(taxCodeRef);

      qboSalesReceipt.setPaymentRefNum(magentoSalesInvoice.getInvoiceName());
      qboSalesReceipt.setBillAddr(billAddr);
      qboSalesReceipt.setBillEmail(email);
      qboSalesReceipt.setDepositToAccountRef(depositTo);
      qboSalesReceipt.setTxnTaxDetail(taxDetail);
                  
      return qboSalesReceipt;
   }
   
   /**
    * Transform discount details from Magento DAO to QBO
    * 
    * @param qboSalesReceipt QBO receipt to append the discount details to
    * @param magentoSalesInvoice Contains Magento data to transform
    * @return QBO receipt with discount details appended to it
    * @throws FMSException 
    */
   private SalesReceipt transformDiscountDetails(SalesReceipt qboSalesReceipt,
                                                 SalesInvoice magentoSalesInvoice)
   {
      DiscountLineDetail discountDetail = new DiscountLineDetail();
      discountDetail.setPercentBased(false);

      Line discount = new Line();
      discount.setDiscountLineDetail(discountDetail);
      discount.setDetailType(LineDetailTypeEnum.DISCOUNT_LINE_DETAIL);
      discount.setAmount(magentoSalesInvoice.getTotalDiscount().abs());
      
      qboSalesReceipt.getLine().add(discount);
      
      return qboSalesReceipt;
   }
   
   /**
    * Transform shipping details from Magento DAO to QBO. If
    * {@link MagQboPropertyKeys.QBO_SHIPPING_SKU} is defined, then Shipping is recorded as
    * a service instead of being recorded in designated shipping line.
    * 
    * @param qboSalesReceipt QBO receipt to append the shipping details to
    * @param magentoSalesInvoice Contains Magento data to transform
    * @param transactionTaxCodeRef QBO tax code reference
    * @return QBO receipt with shipping details appended to it
    * @throws FMSException 
    */
   private SalesReceipt transformShippingDetails(SalesReceipt qboSalesReceipt,
                                                 SalesInvoice magentoSalesInvoice,
                                                 ShippingIndexLookup shippingLookup,
                                                 ClassIndexLookup classLookup,
                                                 ReferenceType transactionTaxCodeRef)
   {
      ReferenceType shippingItemRef = new ReferenceType();

      SalesItemLineDetail saleItemDetail = new SalesItemLineDetail();
      saleItemDetail.setItemRef(shippingItemRef);
      saleItemDetail.setTaxCodeRef(transactionTaxCodeRef);

      Line shipping = new Line();
      shipping.setSalesItemLineDetail(saleItemDetail);
      shipping.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);
      shipping.setAmount(magentoSalesInvoice.getShippingInfo().getCost());

      if (shippingIndexSku != null)
      {
         Item shippingItem = shippingLookup.lookup(shippingIndexSku)
            .orElseThrow(() -> 
               new DataTransformationException(
                     "Failed to look up sku: " + shippingIndexSku +
                     "\nSKU lookup table content: " + shippingLookup));
         
         String classId;
         try
         {
            classId = classLookup.lookup(className)
               .orElseThrow(() ->
                  new DataTransformationException(
                        "Failed to look up transaction class: " + className));
         }
         catch(FMSException ex)
         {
            throw new DataTransformationException(
                  "Failed to lookup class index: " + className);
         }

         ReferenceType classRef = new ReferenceType();
         classRef.setValue(classId);
         
         shippingItemRef.setValue(shippingItem.getId());
         saleItemDetail.setQty(new BigDecimal("1"));
         saleItemDetail.setUnitPrice(magentoSalesInvoice.getShippingInfo().getCost());
         saleItemDetail.setClassRef(classRef);
         shipping.setDescription(shippingItem.getName());
      }
      else
      {
         shippingItemRef.setValue("SHIPPING_ITEM_ID");
      }

      PhysicalAddress shipAddr = new PhysicalAddress();
      shipAddr.setLine1(magentoSalesInvoice.getShippingInfo().getAddress());

      ReferenceType shippingRef = new ReferenceType();
      shippingRef.setValue(magentoSalesInvoice.getShippingInfo().getDesignation());

      if (shipping.getAmount().compareTo(BigDecimal.ZERO) != 0)
      {
         qboSalesReceipt.getLine().add(shipping);
      }
      qboSalesReceipt.setShipAddr(shipAddr);
      qboSalesReceipt.setShipMethodRef(shippingRef);
      
      return qboSalesReceipt;
   }
   
   /**
    * Transform customer details from Magento DAO to QBO
    * 
    * @param qboSalesReceipt QBO receipt to append the customer details to
    * @param magentoSalesInvoice Contains Magento data to transform
    * @param custLookup Lookup object used to append internal customer indexes
    * @return QBO receipt with customer details appended to it
    * @throws FMSException 
    */
   private SalesReceipt transformCustomerDetails(SalesReceipt qboSalesReceipt,
                                                 SalesInvoice magentoSalesInvoice,
                                                 CustomerEmailToIndexLookup custLookup)
   {
      String customerId = custLookup.lookup(magentoSalesInvoice.getCustomer().getEmail())
         .orElseGet(() ->
         {
            com.intuit.ipp.data.Customer newCustomer =
                  addNewCustomerToQbo(magentoSalesInvoice.getCustomer()); 
            custLookup.add(newCustomer.getPrimaryEmailAddr().getAddress(),
                           newCustomer.getId());
            return newCustomer.getId();
         });

      ReferenceType customerRef = new ReferenceType();
      customerRef.setValue(customerId);

      qboSalesReceipt.setCustomerRef(customerRef);
      
      return qboSalesReceipt;
   }
   
   /**
    * Generates a tax code reference
    * 
    * @param magentoSalesInvoice Contains Magento data to transform
    * @param taxCodeLookup Used to populate the internal index of the qbo tax code
    * @return QBO tax code reference
    * @throws FMSException 
    */
   private ReferenceType getTaxCodeReference(SalesInvoice magentoSalesInvoice,
                                             TaxCodeLookup taxCodeLookup)
   {
      SaleItem saleItem = magentoSalesInvoice.getSaleItems().stream()
         .findAny()
         .orElseThrow(() ->
            new DataTransformationException(
                  "Failed to retrieve tax code reference ID:\n" +
                  "Cannot find sale item in receipt"));
            
      String taxCodeIndex = taxCodeLookup.lookup(saleItem.getTaxCode())
         .orElseThrow(() ->
            new DataTransformationException(
                  "Failed to look up tax code: " + saleItem.getTaxCode() +
                  "\nTax code lookup table content: " + taxCodeLookup));
            
      ReferenceType taxCodeRef = new ReferenceType();
      taxCodeRef.setValue(taxCodeIndex);
      
      return taxCodeRef;
   }
                     
}
