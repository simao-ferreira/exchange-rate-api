package io.exchangerate.app.service

import io.exchangerate.app.controller.v1.model.CurrencyResponse
import io.exchangerate.app.controller.v1.model.DatedExchangeRateResponse
import io.exchangerate.app.controller.v1.model.ExchangeRateResponse
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

    override fun ecbDailyExchangeRates(): DatedExchangeRateResponse {
        val response = ecbService.getDailyExchangeRatesResponse()
        log.info { "Handling response for ECB daily exchange rates" }
        return mapDailyExchangeRatesResponse(response)
    }

    private fun mapDailyExchangeRatesResponse(envelopeDto: EnvelopeDto): DatedExchangeRateResponse {
        var date: String
        val exchangeRates = mutableListOf<ExchangeRateResponse>()
        envelopeDto.cubeDto.exchangeRates.first().let {
            date = it.time
            it.rates.forEach { exchangeRate ->
                exchangeRates.add(
                    ExchangeRateResponse(
                        exchangeRate.currency,
                        exchangeRate.rate
                    )
                )
            }
        }

        if (date.isBlank()) {
            throw CorruptedResponseException("Response has a corrupted date")
        }

        return DatedExchangeRateResponse(date, exchangeRates)
    }

    private fun mapDayAvailableCurrencies(envelopeDto: EnvelopeDto): Set<String> {
        return envelopeDto.cubeDto.exchangeRates.first().rates.map { it.currency }.toSet()
    }

    private fun mapExchangeRateForCurrencyFromResponse(
        envelopeDto: EnvelopeDto,
        currency: String
    ): CurrencyResponse {
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