/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 */
package ca.humanheartnature.magento.struct;

import ca.humanheartnature.abstracts.struct.DatabaseAccessObject;
import java.util.Objects;

/**
 * Contains customer-specific information
 */
public class Customer implements DatabaseAccessObject
{
   private static final long serialVersionUID = 1L;
   
   /** Primary index */
   private final Integer index;
   
   /** Customer email */
   private final String email;
   
   /** Customer first name */
   private final String firstName;
   
   /** Customer middle name */
   private final String middleName;
   
   /** Customer last name */
   private final String lastName;
   
   /** Billing address */
   private final Address billingAddress;
  
   /** Shipping address */
   private final Address shippingAddress;
   
   
   
   /* -------------------- CONSTRUCTORS -------------------- */
   
   private Customer(Integer index,
                   String email,
                   String firstName,
                   String middleName,
                   String lastName,
                   Address billingAddress,
                   Address shippingAddress)
   {
      this.index = index;
      this.email = email;
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
      this.billingAddress = billingAddress;
      this.shippingAddress = shippingAddress;
   }
   
   
   
   /* -------------------- OVERRIDDEN METHODS -------------------- */
   
   /**
    * @param obj Object to compare equivalency
    * @return True if {@link #email} are equal
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof Customer)
      {
         if (email.equals(((Customer)obj).getEmail()))
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
      hash = 31 * hash + Objects.hashCode(this.email);
      return hash;
   }
   

   
   /* -------------------- ACCESSORS -------------------- */
   
   /**
    * @return Primary index
    */
   public Integer getIndex()
   {
      return index;
   }

   /**   
    * @return Customer email
    */
   public String getEmail()
   {
      return email;
   }

   /**
    * @return First name
    */
   public String getFirstName()
   {
      return firstName;
   }

   /**
    * @return Billing address
    */
   public Address getBillingAddress()
   {
      return billingAddress;
   }

   /**
    * @return Shipping address
    */
   public Address getShippingAddress()
   {
      return shippingAddress;
   }

   /**
    * @return Customer middle name
    */
   public String getMiddleName()
   {
      return middleName;
   }

   /**
    * @return Customer last name
    */
   public String getLastName()
   {
      return lastName;
   }

   /**
    * @return Customers full name in the format of [First name] [Middle name] [Last name]
    */
   public String getFullName()
   {
      return (this.firstName==null? "" : this.firstName + " ") +
             (this.middleName==null? "" : this.middleName + " ") +
             (this.lastName==null? "" : this.lastName);
   }
   
   
   
   /* -------------------- NESTED CLASSES -------------------- */
   
   /**
    * Generates an immutable <code>Customer</code> object whose fields are
    * populated through the builder's set methods
    */
   public static class Builder
   {
      /** Primary index */
      private Integer index;

      /** Customer email */
      private String email;

      /** Customer first name */
      private String firstName;

      /** Customer middle name */
      private String middleName;

      /** Customer last name */
      private String lastName;

      /** Billing address */
      private Address billingAddress;

      /** Shipping address */
      private Address shippingAddress;
      
      /**
       * @return <code>Customer</code> object whose fields are populated
       * from <code>CustomerBuilder</code> set methods
       */
      public Customer build()
      {
         return new Customer(index,
                             email,
                             firstName,
                             middleName,
                             lastName,
                             billingAddress,
                             shippingAddress);
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
       * @param email Customer email
       * @return Current object
       */
      public Builder setEmail(String email)
      {
         this.email = email;
         return this;
      }

      /**
       * @param firstName First name
       * @return Current object
       */
      public Builder setFirstName(String firstName)
      {
         this.firstName = firstName;
         return this;
      }

      /**
       * @param billingAddress Billing address
       * @return Current object
       */
      public Builder setBillingAddress(Address billingAddress)
      {
         this.billingAddress = billingAddress;
         return this;
      }

      /**
       * @param shippingAddress Shipping address
       * @return Current object
       */
      public Builder setShippingAddress(Address shippingAddress)
      {
         this.shippingAddress = shippingAddress;
         return this;
      }

      /**
       * @param middleName Customer middle name
       * @return Current object
       */
      public Builder setMiddleName(String middleName)
      {
         this.middleName = middleName;
         return this;
      }

      /**
       * @param lastName Customer last name
       * @return Current object
       */
      public Builder setLastName(String lastName)
      {
         this.lastName = lastName;
         return this;
      }
   
   }

}
