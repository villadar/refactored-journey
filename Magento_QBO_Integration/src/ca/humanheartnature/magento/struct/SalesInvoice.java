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
package ca.humanheartnature.magento.struct;

import ca.humanheartnature.abstracts.struct.DatabaseAccessObject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A single sale transaction of one or more products
 */
public class SalesInvoice extends DatabaseAccessObject
{
   private static final long serialVersionUID = 1L;
   
   /** Primary index */
   private Integer index;
   
   /** Display name for order in Magento backend */
   private String orderName;
   
   /** Display name for invoice in Magento backend */
   private String invoiceName;
   
   /** Products sold in the transaction */
   private List<SaleItem> saleItems;
   
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
   
   public enum PaymentMethodEnum{SQUARE, PAYPAL}
   
   /** Payment method (Cash, Credit Card,...) */
   private PaymentMethodEnum paymentMethod;

   
   
   /* -------------------- OVERRIDDEN METHODS -------------------- */
   
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

   
   
   /* -------------------- ACCESSORS/MUTATORS -------------------- */
   
   /**
    * @return Products sold in the transaction
    */
   public List<SaleItem> getSaleItems()
   {
      return saleItems;
   }

   /**
    * @param salesItems Products sold in the transaction
    */
   public void setSaleItems(List<SaleItem> salesItems)
   {
      this.saleItems = salesItems;
   }

   /**
    * @return Customer whom this transaction belongs to
    */
   public Customer getCustomer()
   {
      return customer;
   }

   /**
    * @param customer Customer whom this transaction belongs to
    */
   public void setCustomer(Customer customer)
   {
      this.customer = customer;
   }

   /**
    * @return Date and time the sale was made
    */
   public Date getTransactionDate()
   {
      return transactionDate;
   }

   /**
    * @param transactionDate Date and time the sale was made
    */
   public void setTransactionDate(Date transactionDate)
   {
      this.transactionDate = transactionDate;
   }

   /**
    * @return Total amount paid
    */
   public BigDecimal getGrandTotal()
   {
      return grandTotal;
   }

   /**
    * @param total Total amount paid
    */
   public void setGrandTotal(BigDecimal total)
   {
      this.grandTotal = total;
   }

   /**
    * @return Primary index
    */
   public Integer getIndex()
   {
      return index;
   }

   /**
    * @param index Primary index
    */
   public void setIndex(Integer index)
   {
      this.index = index;
   }

   /**
    * @return ShippingInfo information
    */
   public ShippingInfo getShippingInfo()
   {
      return shippingInfo;
   }

   /**
    * @param shippingInfo ShippingInfo information
    */
   public void setShippingInfo(ShippingInfo shippingInfo)
   {
      this.shippingInfo = shippingInfo;
   }

   /**
    * @return Display name for order in Magento backend
    */
   public String getOrderName()
   {
      return orderName;
   }

   /**
    * @param name Display name for order in Magento backend
    */
   public void setOrderName(String name)
   {
      this.orderName = name;
   }

   /**
    * @return Display name for invoice in Magento backend
    */
   public String getInvoiceName()
   {
      return invoiceName;
   }

   /**
    * @param name Display name for invoice in Magento backend
    */
   public void setInvoiceName(String name)
   {
      this.invoiceName = name;
   }

   /**
    * @return Total discount amount
    */
   public BigDecimal getTotalDiscount()
   {
      return totalDiscount;
   }

   /**
    * @param totalDiscount Total discount amount
    */
   public void setTotalDiscount(BigDecimal totalDiscount)
   {
      this.totalDiscount = totalDiscount;
   }

   /**
    * @return Payment method (Cash, Credit Card,...)
    */
   public PaymentMethodEnum getPaymentMethod()
   {
      return paymentMethod;
   }

   /**
    * @param paymentMethod Payment method (Cash, Credit Card,...)
    */
   public void setPaymentMethod(PaymentMethodEnum paymentMethod)
   {
      this.paymentMethod = paymentMethod;
   }
   
}
