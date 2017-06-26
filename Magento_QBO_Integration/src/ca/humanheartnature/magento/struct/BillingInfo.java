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

/**
 * Contains details regarding the billing information for a customer or company
 */
public class BillingInfo extends DatabaseAccessObject
{
   private static final long serialVersionUID = 1L;
   
   /** Shipping address */
   private String address;
   
   /** Province */
   private String province;
   
   /**
    * @return Shipping address
    */
   public String getAddress()
   {
      return address;
   }

   /**
    * @param address Shipping address
    */
   public void setAddress(String address)
   {
      this.address = address;
   }

   /**
    * @return Province
    */
   public String getProvince()
   {
      return province;
   }

   /**
    * @param province Province
    */
   public void setProvince(String province)
   {
      this.province = province;
   }
   
}
