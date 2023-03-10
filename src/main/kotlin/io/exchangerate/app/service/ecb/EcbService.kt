package io.exchangerate.app.service.ecb

import io.exchangerate.app.exceptions.EcbConnectorException
import io.exchangerate.app.service.ecb.dto.EnvelopeDto
import mu.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import retrofit2.Call

@Service
class EcbService(val ecbConnector: EcbConnector) {

    private val log = KotlinLogging.logger {}

    @Cacheable(value = ["dailyExchangeRates"])
    fun getDailyExchangeRatesResponse(): EnvelopeDto {
        log.debug { "Calling ECB daily exchange rates" }
        return envelopeResponse(ecbConnector.getDailyRates())
    }

    @Cacheable(value = ["last90DaysExchangeRates"])
    fun getLast90DaysExchangeRatesResponse(): EnvelopeDto {
        log.info { "Calling ECB 90 days exchange rates" }
        return envelopeResponse(ecbConnector.getLast90DaysRates())
    }

    @Cacheable(value = ["historicalExchangeRates"])
    fun getHistoricalExchangeRatesResponse(): EnvelopeDto {
        log.info { "Calling ECB historical exchange rates" }
        return envelopeResponse(ecbConnector.getAllHistoricalRates())
    }

    private fun envelopeResponse(call: Call<EnvelopeDto>): EnvelopeDto {
        val response = call.execute()
        if (!response.isSuccessful) {
            log.warn("ECB call was unsuccessful: {}", response.errorBody())
            throw EcbConnectorException("ECB call was unsuccessful")
        }
        return response.body() ?: throw EcbConnectorException("ECB response was null")
    }
}