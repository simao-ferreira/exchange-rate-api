package io.exchangerate.app.service

import io.exchangerate.app.controller.v1.model.CurrencyResponse

interface ExchangeRateService {

    fun availableCurrencies(): Set<String>

    fun dailyExchangeRateFor(currency: String): CurrencyResponse

    fun ecbDailyExchangeRates(): Map<String, String>

}