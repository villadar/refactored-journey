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
 * Contains location details of a database entity
 */
public class Address extends DatabaseAccessObject
{
   private static final long serialVersionUID = 1L;
   
   /** Province */
   private String province;
   
   /** Street address */
   private String fullAddress;
   
   
   /* -------------------- ACCESSORS/MUTATORS -------------------- */
   
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

   /**
    * @return Street address
    */
   public String getFullAddress()
   {
      return fullAddress;
   }

   /**
    * @param streetAddress Street address
    */
   public void setFullAddress(String streetAddress)
   {
      this.fullAddress = streetAddress;
   }
   
}
