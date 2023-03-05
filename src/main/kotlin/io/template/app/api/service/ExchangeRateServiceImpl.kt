package io.template.app.api.service

import io.template.app.api.controller.v1.exceptions.CurrencyNotAvailableException
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
        val response = ecbService.getDailyExchangeRatesResponse()
        return mapExchangeRateForCurrencyFromResponse(response, currency)
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

    private fun mapExchangeRateForCurrencyFromResponse(envelopeDto: EnvelopeDto, currency: String): String {
        return try {
            envelopeDto.cubeDto.exchangeRates.first().rates.first { it.currency == currency }.rate
        } catch (ex: NoSuchElementException) {
            throw CurrencyNotAvailableException("Currency $currency not available in ECB daily exchange rate response")
        }
    }
}