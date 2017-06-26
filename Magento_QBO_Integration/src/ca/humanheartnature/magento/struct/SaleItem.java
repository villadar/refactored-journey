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
import java.util.Objects;

/**
 * Represents an inventory item type. A quantity attribute is provided if this class were
 * to represent a single or set of items
 */
public class SaleItem extends DatabaseAccessObject
{
   private static final long serialVersionUID = 1L;
   
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

   
   
   /* -------------------- OVERRIDDEN METHODS -------------------- */
   
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

   
   
   /* -------------------- ACCESSORS/MUTATORS -------------------- */
   
   /**
    * @return Total price with tax
    */
   public BigDecimal getTotalPriceWithTax()
   {
      return total_price_with_tax;
   }

   /**
    * @param totalPrice Total price with tax
    */
   public void setTotalPriceWithTax(BigDecimal totalPrice)
   {
      this.total_price_with_tax = totalPrice;
   }

   /**
    * @return Stock keeping unit identifier
    */
   public String getSKU()
   {
      return SKU;
   }

   /**
    * @param SKU Stock keeping unit identifier
    */
   public void setSKU(String SKU)
   {
      this.SKU = SKU;
   }

   /**
    * @return Name displayed to customer
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * @param displayName Name displayed to customer
    */
   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
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
    * @param quantity {@link ProductSet} can only contain unique products so define
    * quantity at here
    */
   public void setQuantity(int quantity)
   {
      this.quantity = quantity;
   }

   /**
    * @return Code for tax applied on product
    */
   public String getTaxCode()
   {
      return taxCode;
   }

   /**
    * @param taxCode Code for tax applied on product
    */
   public void setTaxCode(String taxCode)
   {
      this.taxCode = taxCode;
   }

   /**
    * @return Price per single unit of the product
    */
   public BigDecimal getUnitPrice()
   {
      return unitPrice;
   }

   /**
    * @param unitPrice Price per single unit of the product
    */
   public void setUnitPrice(BigDecimal unitPrice)
   {
      this.unitPrice = unitPrice;
   }
   
}
