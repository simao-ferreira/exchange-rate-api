package io.template.app.api.service

import org.springframework.stereotype.Service

@Service
class ExchangeRateServiceImpl : ExchangeRateService {
    override fun availableCurrencies(): Set<String> {
        return setOf("PLN", "EUR", "GBP")
    }

    override fun exchangeRateFor(currency: String): String {
        return when (currency) {
            "EUR" -> "1"
            "PLN" -> "4"
            "GBP" -> "1.2"
            else -> {
                "0"
            }
        }
    }
}