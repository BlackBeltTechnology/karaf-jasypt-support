# Jasypt support for Apache Karaf

This bundle is a helper for Jasypt (http://www.jasypt.org) running on Apache Karaf (http://karaf.apache.org) OSGi environment. The following features are added:

* register default StringEncryptor service that is used also by PAX-JDBC config (https://ops4j1.jira.com/wiki/display/PAXJDBC/Pax+JDBC)
* provide Apache Karaf console command to encrypt/decrypt text values or calculate/validate digests

## Usage

* drop bundle into deploy/ directory of Apache Karaf container
* set environment variable (ENCRYPTION_PASSWORD by default) for encryption password
```
$ KARAF_HOME/bin/karaf -DENCRYPTION_PASSWORD=password
```
* configure default StringEncryptor (not created without configuration!)
  * create hu.blackbelt.karaf.jasypt.services.DefaultStringEncryptorConfig.cfg configuration file in deploy/ directory
  * or create configuration using WebConsole (all options are displayed automatically)
