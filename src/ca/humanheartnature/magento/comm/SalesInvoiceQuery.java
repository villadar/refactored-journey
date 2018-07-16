/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.magento.comm;

import ca.humanheartnature.abstracts.comm.JdbcConnectionFactory;
import ca.humanheartnature.abstracts.comm.JdbcQuery;
import ca.humanheartnature.magento.struct.Address;
import ca.humanheartnature.magento.struct.Customer;
import ca.humanheartnature.magento.struct.SaleItem;
import ca.humanheartnature.magento.struct.SalesInvoice;
import static ca.humanheartnature.magento.struct.SalesInvoice.PaymentMethodEnum.PAYPAL;
import static ca.humanheartnature.magento.struct.SalesInvoice.PaymentMethodEnum.SQUARE;
import ca.humanheartnature.magento.struct.ShippingInfo;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performs queries against Magento's MySQL database and returns a list of
 * {@link SalesInvoice}s
 */
public class SalesInvoiceQuery implements JdbcQuery<SalesInvoice>
{
   private static final Logger LOGGER =
         Logger.getLogger(SalesInvoiceQuery.class.getName());
   
   private static final int INVALID_ORDER_ID = -1;
   
   /** SELECT clause of SQL query */
   private String selectClause;
   
   /** Additional joins of SQL query */
   private String joinClause;
   
   /** WHERE clause of SQL query */
   private String whereClause;
   
   /** If true, then the poductsSold attribute is populated */
   private boolean willJoinProductsSold;
   
   /** WHERE clause parameter that would limit invoices received to after this date.
     * If this is null, then the corresponding WHERE condition is excluded */
   private Timestamp invoiceDateLowerLimit;
   
   
   
   /* -------------------- CONSTRUCTORS -------------------- */
   
   /** Instantiated through the nested builder class */
   private SalesInvoiceQuery(){}
   
   
   
   /* -------------------- OVERRIDDEN METHODS -------------------- */
   
   /**
    * Execute a SQL query to retrieve sales receipts
    * @param dbConn JDBCConnection used to execute the query
    * @return List of sales invoices
    * @throws SQLException 
    */
   @Override
   public List<SalesInvoice> execute(JdbcConnectionFactory dbConn) throws SQLException
   {
      if (dbConn == null)
      {
         throw new IllegalArgumentException("Argument cannot be null");
      }
      
      try(Connection conn = dbConn.getConnection())
      {
         PreparedStatement statement = conn.prepareStatement(
               "SELECT sales_flat_order.entity_id, "
               + "sales_flat_order.increment_id AS ORDER_NAME, "
               + "sales_flat_invoice.increment_id AS INVOICE_NAME, "
               + "sales_flat_invoice.created_at, "
               + "sales_flat_order.discount_amount, "
               + "sales_flat_order_payment.method, "
               + "sales_flat_order.customer_firstname, "
               + "sales_flat_order.customer_middlename, "
               + "sales_flat_order.customer_lastname, "
               + "sales_flat_order.shipping_description, "
               + "sales_flat_order.shipping_amount, "
               + "sales_flat_order.customer_email, "
               + "BILLING_ADDR.region, "
               + "BILLING_ADDR.street AS BILLING_STREET, "
               + "SHIPPING_ADDR.street AS SHIPPING_STREET "
               + selectClause + "\n" +
               "FROM sales_flat_order\n" +
               "INNER JOIN sales_flat_invoice "
               + "ON sales_flat_invoice.order_id = sales_flat_order.entity_id\n" +
               "INNER JOIN sales_flat_order_payment "
               + "ON sales_flat_order_payment.parent_id = sales_flat_order.entity_id\n" +
               "INNER JOIN sales_flat_order_address BILLING_ADDR "
               + "ON BILLING_ADDR.entity_id = sales_flat_order.billing_address_id\n" +
               "INNER JOIN sales_flat_order_address SHIPPING_ADDR "
               + "ON SHIPPING_ADDR.entity_id = sales_flat_order.shipping_address_id\n"
               + joinClause
               + whereClause);
         
         int paramIndex = 1;
         if (invoiceDateLowerLimit != null)
         {
            statement.setTimestamp(paramIndex++, invoiceDateLowerLimit);
         }
         
         LOGGER.log(Level.FINER, "Magento database query:\n{0}", statement.toString());
         
         try(ResultSet rs = statement.executeQuery())
         {
            List<SalesInvoice> salesInvoices = new LinkedList<>();
            int lastId = INVALID_ORDER_ID;
            while(rs.next())
            {
               // Check if row is unique due to potential duplicates from possible joins
               if (lastId != rs.getInt("entity_id")) 
               {
                  Address.Builder billingAddressBuilder = new Address.Builder();
                  billingAddressBuilder
                     .setProvince(rs.getString("region"))
                     .setFullAddress(rs.getString("BILLING_STREET"));
                  
                  Address.Builder shippingAddressBuilder = new Address.Builder();
                  shippingAddressBuilder.setFullAddress(rs.getString("SHIPPING_STREET"));
                  
                  Customer.Builder custBuilder = new Customer.Builder();
                  custBuilder
                     .setFirstName(rs.getString("customer_firstname"))
                     .setMiddleName(rs.getString("customer_middlename"))
                     .setLastName(rs.getString("customer_lastname"))
                     .setEmail(rs.getString("customer_email"));
                  
                  ShippingInfo.Builder shippingInfoBuilder = new ShippingInfo.Builder();
                  shippingInfoBuilder
                     .setDesignation(rs.getString("shipping_description"))
                     .setCost(new BigDecimal(rs.getString("shipping_amount")))
                     .setAddress(rs.getString("SHIPPING_STREET"));
                  
                  SalesInvoice.Builder invoiceBuilder = new SalesInvoice.Builder();
                  invoiceBuilder
                     .setIndex(rs.getInt("entity_id"))
                     .setOrderName(rs.getString("ORDER_NAME"))
                     .setInvoiceName(rs.getString("INVOICE_NAME"))
                     .setTransactionDate(rs.getTimestamp("created_at"))
                     .setTotalDiscount(rs.getBigDecimal("discount_amount"));
                  String paymentMethod = rs.getString("method").toUpperCase();
                  if (paymentMethod.contains("SQUARE"))
                  {
                     invoiceBuilder.setPaymentMethod(SQUARE);
                  }
                  else if(paymentMethod.contains("PAYPAL"))
                  {
                     invoiceBuilder.setPaymentMethod(PAYPAL);
                  }
                  
                  custBuilder.setBillingAddress(billingAddressBuilder.build());
                  custBuilder.setShippingAddress(shippingAddressBuilder.build());
                  invoiceBuilder.setCustomer(custBuilder.build());
                  invoiceBuilder.setShippingInfo(shippingInfoBuilder.build());
                  salesInvoices.add(invoiceBuilder.build());
                  
                  lastId = rs.getInt("entity_id");
               }
               
               if (willJoinProductsSold)
               {
                  SaleItem.Builder saleItemBuilder = new SaleItem.Builder();
                  saleItemBuilder
                     .setDisplayName(rs.getString("name"))
                     .setSKU(rs.getString("sku"))
                     .setQuantity(rs.getInt("qty_invoiced"))
                     .setUnitPrice(rs.getBigDecimal("UNIT_PRICE"))
                     .setTotalPriceWithTax(
                           new BigDecimal(Float.toString(rs.getFloat("LINE_PRICE"))))
                     .setTaxCode(rs.getString("code"));
                  
                  salesInvoices.get(salesInvoices.size()-1).getSaleItems().add(
                        saleItemBuilder.build());
               }
            }
            
            LOGGER.log(Level.FINER, "Result set size: {0}", salesInvoices.size());
            
            return salesInvoices;
         }
         
      }
   }
   
   
   
   /* -------------------- NESTED CLASS -------------------- */
   
   /** 
    * Builder class for <code>SalesInvoiceQuery</code>
    */
   public static class Builder
   {      
      /** <code>SalesInvoiceQuery</code> object initialized by this builder class */
      private final SalesInvoiceQuery query = new SalesInvoiceQuery();
      
      /**
       * Privatized to force instantiation through {@link #select}
       */
      private Builder(){}
      
      /**
       * Generates an instance of this class
       * 
       * @return Instance of this class
       */
      public static Builder select()
      {
         return new Builder();
      }
      
      /**
       * Populates the productsSold attribute of each {@link SalesInvoice}
       * 
       * @return Instance of this class with product information retrieval enabled
       */
      public Builder joinProductsSold()
      {
         query.willJoinProductsSold = true;
         return this;
      }

      /**
       * Retrieves a range of sales invoices whose transaction date is after the parameter
       * date time
       * <pre>
       * <code>WHERE sales_flat_invoice.created_at > ?
       * </code>
       * </pre>
       * 
       * @param transactionDateTime Lower boundary of the transaction date time range
       * @return Instance of this class with the WHERE clause defined
       */
      public Builder whereAfterDateTime(Date transactionDateTime)
      {
         if (transactionDateTime == null)
         {
            throw new IllegalArgumentException("Argument cannot be null");
         }

         query.invoiceDateLowerLimit = new Timestamp(transactionDateTime.getTime());
         return this;
      }
   
      /**
       * Build the SQL query
       * 
       * @return <code>SalesInvoiceQuery</code> with query fields initialized
       */
      public SalesInvoiceQuery build()
      {
         if (query.willJoinProductsSold)
         {
            query.selectClause = 
                  ", catalog_product_flat_1.name, "
                  + "catalog_product_flat_1.sku, "
                  + "sales_flat_order_item.base_price AS UNIT_PRICE, "
                  + "sales_flat_order_item.qty_invoiced, "
                  + "sales_flat_order_item.row_total AS LINE_PRICE, "
                  + "sales_order_tax.code";
            
            query.joinClause = 
                  "INNER JOIN sales_flat_order_item "
                  + "ON sales_flat_order_item.order_id = sales_flat_order.entity_id\n" +
                  "INNER JOIN sales_order_tax_item "
                  + "ON sales_order_tax_item.item_id = sales_flat_order_item.item_id\n" +
                  "INNER JOIN sales_order_tax "
                  + "ON sales_order_tax.order_id = sales_flat_order.entity_id "
                  + "AND sales_order_tax.tax_id = sales_order_tax_item.tax_id\n" +
                  "INNER JOIN catalog_product_flat_1 "
                  + "ON catalog_product_flat_1.entity_id = "
                  + "sales_flat_order_item.product_id\n";
         }
         
         if (query.invoiceDateLowerLimit != null)
         {
            query.whereClause =
                  "WHERE sales_flat_invoice.created_at > ?";
         }
         
         return query;
      }
   }
   
}
