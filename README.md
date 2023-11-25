# MyInvoice

Invoicing system written in Clojure

## Tech

- Clojure
- Pedestal
- Malli


Point-of-Sale System:
## 1. Product Catalog:

Maintain a catalog of all the products available in your store.
Each product should have a unique identifier, name, price, and possibly other details like quantity in stock.

## 2. Sales Transaction:
When a customer selects a product, add it to their shopping cart or transaction.
Allow the cashier to input the quantity of each product.

## 3. Calculate Total Amount:
Calculate the total amount to be paid by summing up the prices of all the products in the cart.

## 4. Sales Receipt:
Generate a sales receipt for each transaction.
Include details such as the date, time, items purchased, quantities, prices, and the total amount paid.
Optionally, include any applicable taxes or discounts.

## 5. Payment Processing:
Accept various payment methods (cash, card, etc.).
Process the payment and update the transaction status.

## 6. End-of-Day Report:
Generate a report at the end of the day summarizing the total sales.
Include details like total revenue, number of transactions, and breakdown of sales by product or category.

## 7. Cash Register Balancing:
Provide a feature to reconcile cash transactions to ensure the cash register is balanced.
Highlight any discrepancies between expected and actual cash.

## 8. Product Management:
Allow for easy addition, modification, or removal of products from the catalog.
