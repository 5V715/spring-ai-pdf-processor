# Sample Spring AI Project Extracting Invoice Information from Pdf

## Needs

* Maven in PATH
* docker compose
* mistral ai api key [here](https://auth.mistral.ai/ui/registration)

## Run

```shell
MISTRALAI_API_KEY=key-goes-here mvn spring-boot:run
```
then go [here](http://localhost:8080/)

please find some sample invoices in /sample-invoices

changing the `InvoiceInformation` record allows to extract different information.
