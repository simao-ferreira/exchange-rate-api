package io.exchangerate.app.api.service

import io.exchangerate.app.api.controller.v1.model.DatedExchangeRateResponse

interface HistoricalExchangeRateService {
    fun historicalExchangeRates(): List<DatedExchangeRateResponse>

}