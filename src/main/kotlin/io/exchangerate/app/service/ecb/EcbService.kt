package io.exchangerate.app.service.ecb

import io.exchangerate.app.exceptions.EcbConnectorException
import io.exchangerate.app.api.service.EcbConnector
import io.exchangerate.app.service.ecb.dto.EnvelopeDto
import org.springframework.stereotype.Service
import retrofit2.Call

@Service
class EcbService(val ecbConnector: EcbConnector) {

    fun getDailyExchangeRatesResponse(): EnvelopeDto {
        return envelopeResponse(ecbConnector.getDailyRates())
    }

    fun getLast90DaysExchangeRatesResponse(): EnvelopeDto {
        return envelopeResponse(ecbConnector.getLast90DaysRates())
    }

    fun getHistoricalExchangeRatesResponse(): EnvelopeDto {
        return envelopeResponse(ecbConnector.getAllHistoricalRates())
    }

    private fun envelopeResponse(call: Call<EnvelopeDto>): EnvelopeDto {
        val response = call.execute()
        if (!response.isSuccessful) {
            throw EcbConnectorException("ECB call was unsuccessful")
        }
        return response.body() ?: throw EcbConnectorException("ECB response was null")
    }
}