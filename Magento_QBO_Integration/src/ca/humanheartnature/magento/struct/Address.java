/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.magento.struct;

import ca.humanheartnature.abstracts.struct.DatabaseAccessObject;

/**
 * Contains location details of a database entity
 */
public class Address implements DatabaseAccessObject
{
   private static final long serialVersionUID = 1L;
   
   /** Province */
   private final String province;
   
   /** Street address */
   private final String fullAddress;
   
   
   
   /* -------------------- CONSTRUCTORS -------------------- */
   
   private Address(String province, String fullAddress)
   {
      this.province = province;
      this.fullAddress = fullAddress;
   }
   
   
   
   /* -------------------- ACCESSORS -------------------- */
   
   /**
    * @return Province
    */
   public String getProvince()
   {
      return province;
   }

   /**
    * @return Street address
    */
   public String getFullAddress()
   {
      return fullAddress;
   }
   
   
   
   /* -------------------- NESTED CLASSES -------------------- */
   
   /**
    * Generates an immutable <code>Address</code> object whose fields are
    * populated through the builder's set methods
    */
   public static class Builder
   {   
      /** Province */
      private String province;
   
      /** Street address */
      private String streetAddress;
      
      /**
       * @return <code>Address</code> object whose fields are populated
       * from <code>AddressBuilder</code> set methods
       */
      public Address build()
      {
         return new Address(province, streetAddress);
      }

      /**
       * @param province Province
       * @return Current Object
       */
      public Builder setProvince(String province)
      {
         this.province = province;
         return this;
      }

      /**
       * @param streetAddress Street address
       * @return Current Object
       */
      public Builder setFullAddress(String streetAddress)
      {
         this.streetAddress = streetAddress;
         return this;
      }
      
   }
   
}
