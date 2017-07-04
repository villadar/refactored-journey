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
 * 1.1  2017-07-03  Added QBO_TIME_DIFF
 */
package ca.humanheartnature.mag_qbo.enums;

/**
 * Contains values to be used as keys for the config properites file for the
 * Magneto_QBO_Integration program
 */
public class MagQboPropertyKeys
{
   /** Name of the Magento database */
   public static final String MAGENTO_DATABASE = "magento.database";
   
   /** Host name or IP address of the maching hosting the Magento database */
   public static final String MAGENTO_HOST = "magento.host";
   
   /** Access token for third leg of QBO authentication */
   public static final String QBO_ACCESS_TOKEN = "quickbooks.accesstoken";
   
   /** Second factor authentication token corresponding to <code>QBO_ACCESS_TOKEN</code>*/
   public static final String QBO_ACCESS_SECRET = "quickbooks.accesssecret";
   
   /** QBO application identifier */
   public static final String QBO_APP_TOKEN = "quickbooks.apptoken";
   
   /** URL to QBO or sandbox */
   public static final String QBO_BASE_URL = "quickbooks.baseurl";
   
   /** Access token for QBO authentication */
   public static final String QBO_CONSUMER_KEY = "quickbooks.consumerkey";
   
   /** Second factor authentication token corresponding to <code>QBO_CONSUMER_KEY</code>*/
   public static final String QBO_CONSUMER_SECRET = "quickbooks.consumersecret";
   
   /** QBO application identifier */
   public static final String QBO_REALM_ID = "quickbooks.realmid";
   
   /** QBO account name to deposit to */
   public static final String QBO_DEPOSIT_ACCOUNT = "quickbooks.depositto";
   
   /** QBO field position that stores the invoice date */
   public static final String QBO_INVOICE_DATE_FIELD = "quickbooks.invoicedatefield";
   
   /** The minimum date boundary for invoice dates of invoices to export */
   public static final String QBO_MIN_DATE_BOUNDARY = "quickbooks.maxlookback";
   
   /** Name of QBO class for sales receipts from Magento */
   public static final String QBO_CLASS = "quickbooks.classname";
   
   /** Name of payment method for credit cards */
   public static final String QBO_CREDIT_CARD_PAYMENT_METHOD =
         "quickbooks.payment.creditcard";
   
   /** Name of payment method for paypal */
   public static final String QBO_PAYPAL_PAYMENT_METHOD = "quickbooks.payment.paypal";
   
   /** SKU for shipping service */
   public static final String QBO_SHIPPING_SKU = "quickbooks.shipping.sku";
   
   /** Amount of hours to add or subtract to the invoice time to account for timezone
    *  differences */
   public static final String QBO_TIME_DIFF = "quickbooks.timediff";
   
}
