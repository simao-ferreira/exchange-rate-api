package io.exchangerate.app.api.service

import io.exchangerate.app.api.controller.v1.exceptions.EcbConnectorException
import io.exchangerate.app.infrastructure.model.EnvelopeDto
import org.springframework.stereotype.Service

@Service
class EcbService {

    private val connector = EcbConnector.create()

    fun getDailyExchangeRatesResponse(): EnvelopeDto {
        val ecbResponse = connector.getDailyRates().execute()
        if (!ecbResponse.isSuccessful) {
            throw EcbConnectorException("ECB call was unsuccessful")
        }
        return ecbResponse.body() ?: throw EcbConnectorException("ECB response was null")
    }

    fun getHistoricalExchangeRatesResponse(): EnvelopeDto {
        val ecbResponse = connector.getLast90DaysRates().execute()
        if (!ecbResponse.isSuccessful) {
            throw EcbConnectorException("ECB call was unsuccessful")
        }
        return ecbResponse.body() ?: throw EcbConnectorException("ECB response was null")
    }
}