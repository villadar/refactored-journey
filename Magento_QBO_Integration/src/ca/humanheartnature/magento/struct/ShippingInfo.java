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

/**
 * Contains details regarding the shipping information for a customer or company
 */
public class ShippingInfo extends DatabaseAccessObject
{
   private static final long serialVersionUID = 1L;
   
   /** Shipping address */
   private String address;
   
   /** Shipping designation */
   private String designation;
   
   /** Shipping cost */
   private BigDecimal cost;

   
   
   /* -------------------- ACCESSORS/MUTATORS -------------------- */
   
   /**
    * @return ShippingInfo address
    */
   public String getAddress()
   {
      return address;
   }

   /**
    * @param address ShippingInfo address
    */
   public void setAddress(String address)
   {
      this.address = address;
   }

   /**
    * @return ShippingInfo designation
    */
   public String getDesignation()
   {
      return designation;
   }

   /**
    * @param name ShippingInfo designation
    */
   public void setDesignation(String name)
   {
      this.designation = name;
   }

   /**
    * @return ShippingInfo cost
    */
   public BigDecimal getCost()
   {
      return cost;
   }

   /**
    * @param cost ShippingInfo cost
    */
   public void setCost(BigDecimal cost)
   {
      this.cost = cost;
   }
   
}
