/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.magento.struct;

import ca.humanheartnature.abstracts.struct.DatabaseAccessObject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A single sale transaction of one or more products
 */
public class SalesInvoice implements DatabaseAccessObject
{
   private static final long serialVersionUID = 1L;
   
   /** Primary index */
   private final Integer index;
   
   /** Display name for order in Magento backend */
   private final String orderName;
   
   /** Display name for invoice in Magento backend */
   private final String invoiceName;
   
   /** Products sold in the transaction */
   private final List<SaleItem> saleItems;
   
   /** Customer whom this transaction belongs to */
   private final Customer customer;
   
   /** Date and time the sale was made */
   private final Date transactionDate;
   
   /** Total discount amount */
   private final BigDecimal totalDiscount;
   
   /** Total amount paid */
   private final BigDecimal grandTotal;
   
   /** ShippingInfo information */
   private final ShippingInfo shippingInfo;
   
   /** Payment method (Cash, Credit Card,...) */
   private final PaymentMethodEnum paymentMethod;
   
   /** Online payment type, either SQUARE or PAYPAL */
   public enum PaymentMethodEnum{SQUARE, PAYPAL}
   
   
   
   /* -------------------- CONSTRUCTOR -------------------- */
   
   /**
    * @param index Primary index
    * @param orderName Display name for order in Magento backend
    * @param invoiceName Display name for invoice in Magento backend
    * @param saleItems Products sold in the transaction
    * @param customer Customer whom this transaction belongs to
    * @param transactionDate Date and time the sale was made
    * @param totalDiscount Total discount amount
    * @param grandTotal Total amount paid
    * @param shippingInfo ShippingInfo information
    * @param paymentMethod Payment method (Cash, Credit Card,...)
    */
   private SalesInvoice(Integer index,
                        String orderName,
                        String invoiceName,
                        List<SaleItem> saleItems,
                        Customer customer,
                        Date transactionDate,
                        BigDecimal totalDiscount,
                        BigDecimal grandTotal,
                        ShippingInfo shippingInfo,
                        PaymentMethodEnum paymentMethod)
   {
      this.index = index;
      this.orderName = orderName;
      this.invoiceName = invoiceName;
      this.saleItems = saleItems;
      this.customer = customer;
      this.transactionDate = transactionDate;
      this.totalDiscount = totalDiscount;
      this.grandTotal = grandTotal;
      this.shippingInfo = shippingInfo;
      this.paymentMethod = paymentMethod;
   }

   
   
   /* -------------------- OVERRIDDEN METHODS -------------------- */
   
   /**
    * @param obj Object to compare equivalency
    * @return True if {@link #index} are equal
    */
   @Override
   public boolean equals(Object obj)
   {
      if(obj instanceof SalesInvoice)
      {
         if (index.equals(((SalesInvoice)obj).getIndex()))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public int hashCode()
   {
      int hash = 3;
      hash = 53 * hash + Objects.hashCode(this.index);
      return hash;
   }

   
   
   /* -------------------- ACCESSORS -------------------- */
   
   /**
    * @return Products sold in the transaction
    */
   public List<SaleItem> getSaleItems()
   {
      return saleItems;
   }

   /**
    * @return Customer whom this transaction belongs to
    */
   public Customer getCustomer()
   {
      return customer;
   }

   /**
    * @return Date and time the sale was made
    */
   public Date getTransactionDate()
   {
      return transactionDate;
   }

   /**
    * @return Total amount paid
    */
   public BigDecimal getGrandTotal()
   {
      return grandTotal;
   }

   /**
    * @return Primary index
    */
   public Integer getIndex()
   {
      return index;
   }

   /**
    * @return ShippingInfo information
    */
   public ShippingInfo getShippingInfo()
   {
      return shippingInfo;
   }

   /**
    * @return Display name for order in Magento backend
    */
   public String getOrderName()
   {
      return orderName;
   }

   /**
    * @return Display name for invoice in Magento backend
    */
   public String getInvoiceName()
   {
      return invoiceName;
   }

   /**
    * @return Total discount amount
    */
   public BigDecimal getTotalDiscount()
   {
      return totalDiscount;
   }

   /**
    * @return Payment method (Cash, Credit Card,...)
    */
   public PaymentMethodEnum getPaymentMethod()
   {
      return paymentMethod;
   }
   
   
   
   /* -------------------- NESTED CLASSES -------------------- */
   
   /**
    * Generates an immutable <code>SaleInvoice</code> object whose fields are
    * populated through the builder's set methods
    */
   public static class Builder
   {  
      /** Primary index */
      private Integer index;

      /** Display name for order in Magento backend */
      private String orderName;

      /** Display name for invoice in Magento backend */
      private String invoiceName;

      /** Products sold in the transaction */
      private List<SaleItem> saleItems = new LinkedList<>();

      /** Customer whom this transaction belongs to */
      private Customer customer;

      /** Date and time the sale was made */
      private Date transactionDate;

      /** Total discount amount */
      private BigDecimal totalDiscount;

      /** Total amount paid */
      private BigDecimal grandTotal;

      /** ShippingInfo information */
      private ShippingInfo shippingInfo;

      /** Payment method (Cash, Credit Card,...) */
      private PaymentMethodEnum paymentMethod;
      
      
      /**
       * @return <code>SalesInvoice</code> object whose fields are populated
       * from <code>SalesInvoiceBuilder</code> set methods
       */
      public SalesInvoice build()
      {
         return new SalesInvoice(index,
                                 orderName,
                                 invoiceName,
                                 saleItems,
                                 customer,
                                 transactionDate,
                                 totalDiscount,
                                 grandTotal,
                                 shippingInfo,
                                 paymentMethod);
      }

      /**
       * @param salesItems Products sold in the transaction
       * @return Current object
       */
      public Builder setSaleItems(List<SaleItem> salesItems)
      {
         this.saleItems = salesItems;
         return this;
      }

      /**
       * @param customer Customer whom this transaction belongs to
       * @return Current object
       */
      public Builder setCustomer(Customer customer)
      {
         this.customer = customer;
         return this;
      }

      /**
       * @param transactionDate Date and time the sale was made
       * @return Current object
       */
      public Builder setTransactionDate(Date transactionDate)
      {
         this.transactionDate = transactionDate;
         return this;
      }

      /**
       * @param total Total amount paid
       * @return Current object
       */
      public Builder setGrandTotal(BigDecimal total)
      {
         this.grandTotal = total;
         return this;
      }

      /**
       * @param index Primary index
       * @return Current object
       */
      public Builder setIndex(Integer index)
      {
         this.index = index;
         return this;
      } 

      /**
       * @param shippingInfo ShippingInfo information
       * @return Current object
       */
      public Builder setShippingInfo(ShippingInfo shippingInfo)
      {
         this.shippingInfo = shippingInfo;
         return this;
      }

      /**
       * @param name Display name for order in Magento backend
       * @return Current object
       */
      public Builder setOrderName(String name)
      {
         this.orderName = name;
         return this;
      }

      /**
       * @param name Display name for invoice in Magento backend
       * @return Current object
       */
      public Builder setInvoiceName(String name)
      {
         this.invoiceName = name;
         return this;
      }

      /**
       * @param totalDiscount Total discount amount
       * @return Current object
       */
      public Builder setTotalDiscount(BigDecimal totalDiscount)
      {
         this.totalDiscount = totalDiscount;
         return this;
      }

      /**
       * @param paymentMethod Payment method (Cash, Credit Card,...)
       * @return Current object
       */
      public Builder setPaymentMethod(PaymentMethodEnum paymentMethod)
      {
         this.paymentMethod = paymentMethod;
         return this;
      }

   }
   
}
