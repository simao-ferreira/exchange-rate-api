package io.exchangerate.app.service

import io.exchangerate.app.controller.v1.model.CurrencyResponse
import io.exchangerate.app.exceptions.CorruptedResponseException
import io.exchangerate.app.exceptions.CurrencyNotAvailableException
import io.exchangerate.app.service.ecb.EcbService
import io.exchangerate.app.service.ecb.dto.EnvelopeDto
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class ExchangeRateServiceImpl(
    val ecbService: EcbService
) : ExchangeRateService {

    private val log = KotlinLogging.logger {}

    override fun availableCurrencies(): Set<String> {
        val response = ecbService.getDailyExchangeRatesResponse()
        log.info { "Handling response for ECB daily exchange rates" }
        return mapDayAvailableCurrencies(response)
    }

    override fun dailyExchangeRateFor(currency: String): CurrencyResponse {
        val response = ecbService.getDailyExchangeRatesResponse()
        log.info { "Handling response for ECB daily exchange rates" }
        return mapExchangeRateForCurrencyFromResponse(response, currency)
    }

    override fun ecbDailyExchangeRates(): Map<String, String> {
        val response = ecbService.getDailyExchangeRatesResponse()
        log.info { "Handling response for ECB daily exchange rates" }
        return mapDailyExchangeRatesResponse(response)
    }

    private fun mapDailyExchangeRatesResponse(envelopeDto: EnvelopeDto): Map<String, String> {
        val exchangeRates = mutableMapOf<String, String>()
        envelopeDto.cubeDto.exchangeRates.first().rates.map {
            exchangeRates.put(it.currency, it.rate)
        }
        return exchangeRates
    }

    private fun mapDayAvailableCurrencies(envelopeDto: EnvelopeDto): Set<String> {
        return envelopeDto.cubeDto.exchangeRates.first().rates.map { it.currency }.toSet()
    }

    private fun mapExchangeRateForCurrencyFromResponse(envelopeDto: EnvelopeDto, currency: String): CurrencyResponse {
        var rate: String
        var date: String
        try {
            envelopeDto.cubeDto.exchangeRates.first().let { dailyRate ->
                date = dailyRate.time
                rate = dailyRate.rates.first { it.currency == currency }.rate
            }
        } catch (ex: NoSuchElementException) {
            throw CurrencyNotAvailableException("Currency $currency not available in ECB daily exchange rate response")
        }

        if (rate.isBlank() || date.isBlank()) {
            throw CorruptedResponseException("Response has a corrupted value: rate $rate and date $date")
        }

        return CurrencyResponse(
            date,
            currency,
            rate
        )
    }
}