package io.template.app.api.service

interface ExchangeRateService {

    fun availableCurrencies(): Set<String>

    fun exchangeRateFor(currency: String): String

}