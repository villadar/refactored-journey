# Magento-QuickBooksOnline Integration
Transfers sales data from a Magento-hosted, e-commerce website to a QuickBooks Online account  


## License
Copyright 2017 Paolo Villadarez

This code cannot be used, copied, or redistributed without express consent from the author.
Please contact villadarez@gmail.com for permission to use this code.


## Functional Specification

### Objective
Lab International has chosen QuickBooks Online as their primary accounting and inventory management
software. However, online sales receipts are recorded only into Magento, an e-commerce development
platform, which hosts the humanheartnature.ca website. Data integration software is needed to
reconcile online transactions made through the website with Lab International's QuickBooks Online
account.


### Functional Requirements
1. All receipts from online sales must be transferred exactly once into QuickBooks Online. An
   imported QuickBooks Online sales receipt will have the following information filled in:
   - Customer display name
   - Sales receipt number, retrieved from the Magento sales receipt identifier (unique)
   - Customer email
   - Billing address
   - Shipping address
   - Shipping method
   - Invoice timestamp
   - Payment method
   - Invoice number, retrieved from the Magento invoice identifier (unique)
   - Account to deposit to; specified through user preference
   - Product name, SKU, description, quantity, rate, amount, class, and sales tax
   - Discount amount, if provided
   - Shipping cost, if provided. The shipping amount can either be recorded into the designated
     discount field or recorded as a service, depending on user preference.
   - Total amount received

2. Imported sales receipts must be linked to a QuickBooks Online customer. If the customer that owns
   the sales receipt does not already exist in QuickBooks Online at the time of the data transfer,
   then a new customer will be created in QuickBooks Online. The customer email address will be used
   as the unique key to identify a customer. The following fields must be filled in when creating a
   new customer:
   - Display name (unique)
   - First name
   - Middle name, if provided
   - Last name
   - Email address (unique)
   - Billing address
   - Shipping address

3. The functionality to exclude export data based on a given lower date boundary for the export date
   range must be provided. This is in case the Magento database contains test data or a partial data
   transfer has already been performed.


### Design Constraints
1. The only stable hosting environment for this program is the Linux server that is hosting the
   website.

2. The program should be fully automated.

3. The program should be deterministic. The program should not rely on preserving state in between
   data transfer operations in order to function. Administrator intervention should also not be
   needed if the hosting environment goes down or if communication to either endpoint is
   interrupted.

4. The Magento code has been heavily modified and is supported by a third-party developer.
   Modification of Magento development assets for this project should be avoided in order to not
   interfere with possible Magento development in the future. Consistent behavior from the Magento
   API is not guaranteed.


### Implementation
This program will be hosted on the same Linux environment that is hosting the website due to
constraint #1. In order to meet constraint #2 and #3, the export process will be automated through
the use of Cron as opposed to implementing this program as a daemon. Once per day, a cron tab will
run the program which will perform a single bulk transfer operation. Communication to Magento will
be established through a direct database connection to the underlying MySQL database, since
constraint #4 discourages us from using the Magento API. Out of the three languages that has API
support from Intuit, Java was chosen as the development platform since it is the one most suited for
creating an executable file in Linux.

All sales receipts that have an invoice timestamp after the invoice time of the last transferred
sales receipt will be migrated during a single bulk transfer operation. A date specified through
a program configuration setting will be used as the minimum date boundary for the export date range
instead of the last transfered invoice timestamp if the provided date is later than the timestamp
(requirement #3). Each imported sales receipt in QuickBooks Online will include all information
specified in requirement #1. 

Each sales receipt will be guaranteed to have a customer email address attached to it. If an
existing customer is found in QuickBooks Online with an email address matching the one contained in
a sales receipt, then these two entities will be linked together. If an existing customer cannot be
found, then a new customer will be created using customer-specifc information specified in
requirement #2. To meet the requirement of customers having a unique display name, the display name
of a newly created customer will be set to a combination of the customer's first name, last name,
and a 5-digit number that starts with a '#' and is padded to the left with 0's (John Smith #000001).
This number will be equal to n+1, where n is the number of existing customers who share that same
first name and last name as the new customer being added and who have a number identifier in their
display name.

In order to facilitate modular testing, the following two operations will also be implemented:
1. Export of bulk sales receipts from Magento to a data transfer object that is serialized into the
   filesystem
2. Import of the contents of the file generated from the operation above into QuickBooks Online


## Technical Specification

### Prerequisites
1. Java Runtime Environment 8
3. Magento version 1.7
3. Credentials to the Magento Database
4. Access tokens for QuickBooks Online account, retrieved through
   https://appcenter.intuit.com/Playground/OAuth/IA/ 
5. The QuickBooks Online account must have:
   - A custom field for the purpose of storing the invoice timestamp toggled on
   - Shipping toggled on
   - Discount toggled on


### Usage
```
Program:     Magento_QBO_Integration 1.1
Description: Performs ETL operations between a Magento database and QuickBooks Online (QBO)

Positional arguments:
   arg1:             Operation to execute:
                     mag_to_qbo - Transfer data from Magento database to QBO
                     extract_mag - Extract Magento database content to the file system
                     load_qbo - Load from the file system to QBO

Named arguments:
   --user, -u        Magento database user
   --password, -p    Password for the Magento database user
   --bean, -b        Location of the file to extract from or load to when performing extract_mag or
                     load_qbo operations
   --config, -c      Location of this program's configuration file; mandatory argument
   --logging, -l     Location of the logger configuration file
```


### Configuration
The following configurations can be performed by modifying the following keys found in the
configuration file that would be passed into the program through the '--config' argument.
- quickbooks.depositto:          Account which the sales revenue will be deposited to
- quickbooks.invoicedatefield:   Custom field position for invoice timestamp
- quickbooks.maxlookback:        Minimum date boundary used for requirement #3
- quickbooks.classname:          Name of the class to assign Magento sales receipts to
- quickbooks.payment.creditcard: Payment type name for credit card transactions
- quickbooks.payment.paypal:     Payment type name for paypal transaction
- quickbooks.shipping.sku:       SKU for shipping service. If this is not defined, then the shipping
                                 charge will be placed in the designated shipping line.
- quickbooks.timediff:           Amount of hours to add or subtract to the invoice time to account
                                 for timezone differences
