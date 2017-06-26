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
import java.util.Objects;

/**
 * Contains customer-specific information
 */
public class Customer extends DatabaseAccessObject
{
   private static final long serialVersionUID = 1L;
   
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
   
   
   
   /* -------------------- OVERRIDDEN METHODS -------------------- */
   
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
   

   
   /* -------------------- ACCESSORS/MUTATORS -------------------- */
   
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
    * @return Customer email
    */
   public String getEmail()
   {
      return email;
   }

   /**
    * @param email Customer email
    */
   public void setEmail(String email)
   {
      this.email = email;
   }

   /**
    * @return First name
    */
   public String getFirstName()
   {
      return firstName;
   }

   /**
    * @param firstName First name
    */
   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }

   /**
    * @return Billing address
    */
   public Address getBillingAddress()
   {
      return billingAddress;
   }

   /**
    * @param billingAddress Billing address
    */
   public void setBillingAddress(Address billingAddress)
   {
      this.billingAddress = billingAddress;
   }

   /**
    * @return Shipping address
    */
   public Address getShippingAddress()
   {
      return shippingAddress;
   }

   /**
    * @param shippingAddress Shipping address
    */
   public void setShippingAddress(Address shippingAddress)
   {
      this.shippingAddress = shippingAddress;
   }

   /**
    * @return Customer middle name
    */
   public String getMiddleName()
   {
      return middleName;
   }

   /**
    * @param middleName Customer middle name
    */
   public void setMiddleName(String middleName)
   {
      this.middleName = middleName;
   }

   /**
    * @return Customer last name
    */
   public String getLastName()
   {
      return lastName;
   }

   /**
    * @param lastName Customer last name
    */
   public void setLastName(String lastName)
   {
      this.lastName = lastName;
   }

   /**
    * @return Customers full name in the format of [First name] [Middle name] [Last name]
    */
   public String getFullName()
   {
      String firstName = (this.firstName==null? "" : this.firstName + " ");
      String middleName = (this.middleName==null? "" : this.middleName + " ");
      String lastName = (this.lastName==null? "" : this.lastName);
      
      return firstName + middleName + lastName;
   }

}
