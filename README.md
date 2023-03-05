# Exchange Rate Api

Exchange rate api is just an exercise.

It uses the [European Central Bank](https://www.ecb.europa.eu/stats/eurofxref/.xml) to retrieve the most recent list of
exchange rates to Euro.

## Endpoints

**daily**

* `available-currencies`

> Returns all available currencies returned by ECB

* `ecb-exchange-rates`

> Returns a list of pairs - currency to exchange rate from ECB

* `exchange-rate/{currency}`

> Takes an ISO currency and returns an object with currency and rate

## Terminal

Can be called when running by using curl

```shell
$ curl -X 'GET' 'http://localhost:8080/daily/exchange-rate/USD'
```
