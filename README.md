# Hibiscus Watcher

## What does it do?

This is a tool which makes use of [Hibiscus Payment Server](https://www.willuhn.de/products/hibiscus-server/)
to get account data securely from a bank and generate a report. The report is in plain text
(ideal for mailing it) and can be configured to contain more or less details.

## Gettings started

* get the [Java Runtime Environment (JRE)](http://java.com/) in version 8 or newer
* download the [lastest release](https://codeberg.org/mathisdt/hibiscus-watcher/releases/latest)
  and unpack it
* start it using the script contained in the "bin" directory or with `java -jar hibiscus-watcher.jar`
  and append a combination of these parameters:
  * --url \<URL\>                  : URL of the XML-RPC services of Hibiscus, e.g. https://localhost:8080/xmlrpc/
  * --username \<LOGIN\>           : username to access the XML-RPC services at the given URL, defaults to 'admin'
  * --password \<PASSWORD\>        : password to access the XML-RPC services at the given URL
  * --balances                     : get the balances of all accounts
  * --single                       : get all single postings of all accounts and generate the more detailed postings report (which also contains the balances)
  * --days \<DAYS\>                : go back this amount of days for fetching the postings, e.g. 7 (the default) - this option is only effective in single postings mode!
  * --only-account \<ACCOUNT\>     : account(s) for --balances or --single (multiple times allowed)
  * --low                          : check if the balance of an account is below a minimum account
  * --low-account \<ACCOUNT\>      : account(s) for --low (multiple times allowed)
  * --low-minimum \<WHOLE NUMBER\> : minimum amount for --low
  * --no-negative                  : do not get single postings which contain a negative amount of money
  * --no-positive                  : do not get single postings which contain a positive amount of money
  * --no-sum                       : do not print a sum

## License

This project is licensed under GPL v3. If you submit or contribute changes, these are automatically licensed
under GPL v3 as well. If you don't want that, please don't submit the contribution (e.g. pull request)!
