# Magento-QuickBooksOnline Integration
Performs data transfer operations from a Magento 1.7 database to Intuit's QuickBooks Online. 


## License
Copyright 2017 Paolo Villadarez

This code cannot be used, copied, or redistributed without express consent from the author.
Please contact villadarez@gmail.com for permission to use this code.


## Functional Specification

### Overview
QuickBooks has been chosen as the primary accounting and inventory-management software. However,
online sales data (item sold, payment confirmation, etc.) is recorded only in Magento's SQL database
which is located in the Linux server hosting the humanheartnature.ca website. An automated data
exporter is needed to reconcile QuickBooks data with transactions made through the website.


### Design Criteria
1. The only stable hosting environment for this program is the same one that is hosting the website.

2. The program must be fully automated.

3. There are no direct system administrators supporting this program. Administrator intervention
   should not be needed if the hosting server goes down or communication to QuickBook online is
   briefly interrupted.

4. The option to exclude export data based on a given date boundary should be provided in case the
   Magento database contains test data or data that has already been exported to Magento.

5. Sales receipts must be linked to a QuickBooks Online customer that will be uniquely identified by
   their email address.

6. Customers automatically imported to QuickBooks Online must have a unique display name.

7. All shipping, billing, sale item, tax, invoice timestamp, and discount fields in the QuickBooks
   sales receipt page must be filled in.


### Prerequisites
1. Credentials to Magento Database
2. Access tokens for QuickBooks Online account
3. The QuickBooks Online account must have:
   - A custom field toggled on, for the purpose of storing the invoice timestamp
   - Shipping toggled on
   - Discount toggled on


### Implementation
Criteria #1 constrains this program to be hosted on the same Linux server that is hosting the
website. In order to meet criteria #2 and #3, the export process will be automated through the use
of a Cron Job that would run this program through a shell call. A single bulk transfer operation
will be performed per call, as opposed to this program running as a daemon. Java has been chosen as
the development platform because, out of the three languages that has API support from Intuit, it is
the most suited to create an executable file in Linux.

When the program is run, it exports all sales receipts after either the invoice timestamp of the
last exported sales receipt or a time specified through the configuration file (criteria #4).
Shipping, billing, sale item, tax, invoice timestamp, and discount fields are populated in the
QuickBooks Online sales receipts using corresponding fields from its Magento invoice equivalent.
Since Quickbooks Online sales receipts cannot normally store timestamps to the exact second, a
custom field must be created to store the invoice timestamp.

Each sales receipt will be guaranteed to have a customer email address attached to it. If an
existing customer is found in QuickBooks Online with an email address matching the one contained in
a sales receipt, then that customer will be linked to the sales receipt (criteria #5). However, if a
customer cannot be found with an email address matching the one attached to a sales receipt, then a
new customer will be created using the customer name, billing details, shipping details, and email
address that is contained within the sales receipt. According to criteria #6, the display name for a
customer in Quickbooks Online must be unique. To meet this criteria, the display name of a newly
created customer will be set to a combination of the customer's first name, last name, and a 5-digit
number that starts with a '#' and is padded to the left with 0's (John Smith #000001). This number
will be equal to n+1, where n is the number of existing customers who shares that same first name
and last name as the new customer being added.

In addition to this program being able to transfer sales receipts straight from Magento to
QuickBooks Online, an option to transfer sales receipt data to and from a data transfer object that
is serialized to a file will also be provided.

### Usage
```
Program:     Magento_QBO_Integration 1.0
Description: Performs ETL operations between a Magento database and QuickBooks Online

Positional arguments:
   arg1:             Operation to execute:
                     mag_to_qbo - Transfer data from Magento database to QBO
                     extract_mag - Extract Magento database content to file
                     load_qbo - Load from file to QBO

Named arguments:
   --user, -u        Magento database user
   --password, -p    Password for the database user specified by the --user argument
   --bean, -b        Location of the bean file to extract or load
   --config, -c      Location of this program's configuration file; mandatory argument
   --logging, -l     Location of the logger configuration file
```


### Configurations
The following configurations can be performed by modifying the following keys found in the
configuration file that would be passed in to the program through the --config argument.

- quickbooks.depositto:          Account which the sales revenue will be deposited to
- quickbooks.invoicedatefield:   Custom field position for invoice timestamp
- quickbooks.maxlookback:        Minimum date boundary used for criteria #4
- quickbooks.classname:          Name of the class to assign Magento sales receipts to
- quickbooks.payment.creditcard: Payment type name for credit card transactions
- quickbooks.payment.paypal:     Payment type name for paypal transaction
- quickbooks.shipping.sku:       SKU for shipping service. If excluded, the shipping charge will be
                                 placed in the designated shipping line.
