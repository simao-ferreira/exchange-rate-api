package io.template.app.api.service

import io.template.app.infrastructure.model.EnvelopeDto
import org.springframework.stereotype.Service

@Service
class ExchangeRateServiceImpl(
    val ecbService: EcbService
) : ExchangeRateService {
    override fun availableCurrencies(): Set<String> {
        val response = ecbService.getDailyExchangeRatesResponse()
        return mapDayAvailableCurrencies(response)
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

    override fun ecbDailyExchangeRates(): Map<String, String> {
        val response = ecbService.getDailyExchangeRatesResponse()
        return mapExchangeRatesResponse(response)
    }

    private fun mapExchangeRatesResponse(envelopeDto: EnvelopeDto): MutableMap<String, String> {

        val exchangeRates = mutableMapOf<String, String>()

        envelopeDto.cubeDto.exchangeRates.first().rates.map {
            exchangeRates.put(it.currency, it.rate)
        }

        return exchangeRates
    }

    private fun mapDayAvailableCurrencies(envelopeDto: EnvelopeDto): Set<String> {
        return envelopeDto.cubeDto.exchangeRates.first().rates.map { it.currency }.toSet()
    }
}