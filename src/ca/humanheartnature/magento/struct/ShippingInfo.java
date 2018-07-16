/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.magento.struct;

import ca.humanheartnature.abstracts.struct.DatabaseAccessObject;
import java.math.BigDecimal;

/**
 * Contains details regarding the shipping information for a customer or company
 */
public class ShippingInfo implements DatabaseAccessObject
{
   private static final long serialVersionUID = 1L;
   
   /** Shipping address */
   private final String address;
   
   /** Shipping designation */
   private final String designation;
   
   /** Shipping cost */
   private final BigDecimal cost;
   
   
   
   /* -------------------- CONSTRUCTORS -------------------- */
   
   /**
    * @param address Shipping address
    * @param designation Shipping designation
    * @param cost Shipping cost
    */
   private ShippingInfo(String address, String designation, BigDecimal cost)
   {
      this.address = address;
      this.designation = designation;
      this.cost = cost;
   }

   
   
   /* -------------------- ACCESSORS -------------------- */
   
   /**
    * @return Shipping address 
    */
   public String getAddress()
   {
      return address;
   }

   /**
    * @return Shipping designation
    */
   public String getDesignation()
   {
      return designation;
   }

   /**
    * @return Shipping cost
    */
   public BigDecimal getCost()
   {
      return cost;
   }
   
   
   
   /* -------------------- NESTED CLASSES -------------------- */
   
   /**
    * Generates an immutable <code>ShippingInfo</code> object whose fields are
    * populated through the builder's set methods
    */
   public static class Builder
   {
      /** Shipping address */
      private String address;

      /** Shipping designation */
      private String designation;

      /** Shipping cost */
      private BigDecimal cost;
      
      /**
       * @return <code>ShippingInfo</code> object whose fields are populated
       * from <code>ShippingInfoBuilder</code> set methods
       */
      public ShippingInfo build()
      {
         return new ShippingInfo(address, designation, cost);
      }

      /**
       * @param address Shipping address
       * @return Current object
       */
      public Builder setAddress(String address)
      {
         this.address = address;
         return this;
      }

      /**
       * @param name Shipping designation
       * @return Current object
       */
      public Builder setDesignation(String name)
      {
         this.designation = name;
         return this;
      }

      /**
       * @param cost Shipping cost
       * @return Current object
       */
      public Builder setCost(BigDecimal cost)
      {
         this.cost = cost;
         return this;
      }
   }
   
}
