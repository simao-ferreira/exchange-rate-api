package io.exchangerate.app.controller.v1.model

data class CurrencyResponse (
    val date: String,
    val currency: String,
    val rate: String
)