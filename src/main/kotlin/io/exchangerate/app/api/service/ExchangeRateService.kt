package io.exchangerate.app.api.service

import io.exchangerate.app.api.controller.v1.model.CurrencyResponse

interface ExchangeRateService {

    fun availableCurrencies(): Set<String>

    fun dailyExchangeRateFor(currency: String): CurrencyResponse

    fun ecbDailyExchangeRates(): Map<String, String>

}