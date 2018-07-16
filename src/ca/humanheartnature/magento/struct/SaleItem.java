/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.magento.struct;

import ca.humanheartnature.abstracts.struct.DatabaseAccessObject;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents an inventory item type. A quantity attribute is provided if this class were
 * to represent a single or set of items
 */
public class SaleItem implements DatabaseAccessObject
{
   private static final long serialVersionUID = 1L;
   
   /** Total price with tax */
   private final BigDecimal total_price_with_tax;
   
   /** Stock keeping unit identifier */
   private final String SKU;
   
   /** Name displayed to customer */
   private final String displayName;
   
   /** Quantity of items of the same type */
   private final int quantity;
   
   /** Price per single unit of the product */
   private final BigDecimal unitPrice;
   
   /** Code for tax applied on product */
   private final String taxCode;
   
   
   
   /* -------------------- CONSTRUCTORS -------------------- */
   
   /**
    * @param total_price_with_tax Total price with tax
    * @param SKU Stock keeping unit identifier
    * @param displayName Name displayed to customer
    * @param quantity Quantity of items of the same type
    * @param unitPrice Price per single unit of the product
    * @param taxCode Code for tax applied on product
    */
   private SaleItem(BigDecimal total_price_with_tax,
                   String SKU,
                   String displayName,
                   int quantity,
                   BigDecimal unitPrice,
                   String taxCode)
   {
      this.total_price_with_tax = total_price_with_tax;
      this.SKU = SKU;
      this.displayName = displayName;
      this.quantity = quantity;
      this.unitPrice = unitPrice;
      this.taxCode = taxCode;
   }

   
   
   /* -------------------- OVERRIDDEN METHODS -------------------- */
   
   /**
    * @param obj Object to compare equivalency
    * @return True if {@link #SKU} are equal
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof SaleItem)
      {
         if (SKU.equals(((SaleItem)obj).getSKU()))
         {
            return true;
         }
      }
      return false;
   }
   
   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = 47 * hash + Objects.hashCode(this.SKU);
      return hash;
   }

   
   
   /* -------------------- ACCESSORS -------------------- */
   
   /**
    * @return Total price with tax
    */
   public BigDecimal getTotalPriceWithTax()
   {
      return total_price_with_tax;
   }

   /**
    * @return Stock keeping unit identifier
    */
   public String getSKU()
   {
      return SKU;
   }

   /**
    * @return Name displayed to customer
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * @return {@link ProductSet} can only contain unique products so define quantity at
    * here
    */
   public int getQuantity()
   {
      return quantity;
   }

   /**
    * @return Code for tax applied on product
    */
   public String getTaxCode()
   {
      return taxCode;
   }

   /**
    * @return Price per single unit of the product
    */
   public BigDecimal getUnitPrice()
   {
      return unitPrice;
   }
   
   
   
   /* -------------------- NESTED CLASSES -------------------- */
   
   /**
    * Generates an immutable <code>SaleItem</code> object whose fields are
    * populated through the builder's set methods
    */
   public static class Builder
   {    
      /** Total price with tax */
      private BigDecimal total_price_with_tax;

      /** Stock keeping unit identifier */
      private String SKU;

      /** Name displayed to customer */
      private String displayName;

      /** {@link ProductSet} can only contain unique products so define quantity at here */
      private int quantity;

      /** Price per single unit of the product */
      private BigDecimal unitPrice;

      /** Code for tax applied on product */
      private String taxCode;
      
      /**
       * @return <code>SaleItem</code> object whose fields are populated from
       * <code>SaleItemBuilder</code> set methods
       */
      public SaleItem build()
      {
         return new SaleItem(total_price_with_tax,
                             SKU,
                             displayName,
                             quantity,
                             unitPrice,
                             taxCode);
      }
       
      /**
       * @param totalPrice Total price with tax
       * @return Current object
       */
      public Builder setTotalPriceWithTax(BigDecimal totalPrice)
      {
         this.total_price_with_tax = totalPrice;
         return this;
      }

      /**
       * @param SKU Stock keeping unit identifier
       * @return Current object
       */
      public Builder setSKU(String SKU)
      {
         this.SKU = SKU;
         return this;
      }

      /**
       * @param displayName Name displayed to customer
       * @return Current object
       */
      public Builder setDisplayName(String displayName)
      {
         this.displayName = displayName;
         return this;
      }

      /**
       * @param quantity {@link ProductSet} can only contain unique products so define
       * quantity at here
       * @return Current object
       */
      public Builder setQuantity(int quantity)
      {
         this.quantity = quantity;
         return this;
      }

      /**
       * @param taxCode Code for tax applied on product
       * @return Current object
       */
      public Builder setTaxCode(String taxCode)
      {
         this.taxCode = taxCode;
         return this;
      }

      /**
       * @param unitPrice Price per single unit of the product
       * @return Current object
       */
      public Builder setUnitPrice(BigDecimal unitPrice)
      {
         this.unitPrice = unitPrice;
         return this;
      }
      
   }
   
}
