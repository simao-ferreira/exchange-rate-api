package io.exchangerate.app.controller.v1.model

data class DatedExchangeRateResponse(
    val date: String,
    val rates: List<ExchangeRateResponse>,
)

data class ExchangeRateResponse(
    val currency: String,
    val rate: String,
)
