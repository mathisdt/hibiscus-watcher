![license](https://img.shields.io/github/license/mathisdt/hibiscus-watcher.svg?style=flat) [![Build](https://github.com/mathisdt/hibiscus-watcher/actions/workflows/build.yaml/badge.svg)](https://github.com/mathisdt/hibiscus-watcher/actions) [![last released](https://img.shields.io/github/release-date/mathisdt/hibiscus-watcher.svg?label=last%20released&style=flat)](https://github.com/mathisdt/hibiscus-watcher/releases)

# Hibiscus Watcher

## What does it do?

This is a tool which makes use of [Hibiscus Payment Server](https://www.willuhn.de/products/hibiscus-server/)
to get account data securely from a bank and generate a report. The report is in plain text
(ideal for mailing it) and can be configured to contain more or less details.

## Gettings started

* get the [Java Runtime Environment (JRE)](http://java.com/) in version 8 or newer
* download the [lastest release](https://github.com/mathisdt/hibiscus-watcher/releases/latest)
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

## Build using Earthly

The CI build of this project uses [Earthly](https://docs.earthly.dev/), which in turn uses
container virtualization (e.g. Docker or Podman). You can also run the build locally (if you
have Earthly as well as an OCI compatible container engine installed) by executing
`earthly +build`. This will create a container with everything needed for the build,
create the package inside it and then copy the results to the directory `target` for you.
