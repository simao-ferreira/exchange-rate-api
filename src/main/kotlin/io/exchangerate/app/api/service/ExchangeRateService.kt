package io.exchangerate.app.api.service

interface ExchangeRateService {

    fun availableCurrencies(): Set<String>

    fun exchangeRateFor(currency: String): String

    fun ecbDailyExchangeRates(): Map<String, String>

}