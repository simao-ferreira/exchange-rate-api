package io.exchangerate.app.api.service

import io.exchangerate.app.api.controller.v1.exceptions.CurrencyNotAvailableException
import io.exchangerate.app.api.controller.v1.model.CurrencyResponse
import io.exchangerate.app.infrastructure.model.EnvelopeDto
import org.springframework.stereotype.Service

@Service
class ExchangeRateServiceImpl(
    val ecbService: EcbService
) : ExchangeRateService {
    override fun availableCurrencies(): Set<String> {
        val response = ecbService.getDailyExchangeRatesResponse()
        return mapDayAvailableCurrencies(response)
    }

    override fun dailyExchangeRateFor(currency: String): CurrencyResponse {
        val response = ecbService.getDailyExchangeRatesResponse()
        val exchangeRate = mapExchangeRateForCurrencyFromResponse(response, currency)
        return CurrencyResponse(currency, exchangeRate)
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