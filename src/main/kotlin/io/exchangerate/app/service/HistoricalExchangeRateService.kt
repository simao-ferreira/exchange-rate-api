package io.exchangerate.app.service

import io.exchangerate.app.controller.v1.model.DatedExchangeRateResponse

interface HistoricalExchangeRateService {

    fun historicalExchangeRates(): List<DatedExchangeRateResponse>

    fun last90DaysExchangeRates(): List<DatedExchangeRateResponse>

}