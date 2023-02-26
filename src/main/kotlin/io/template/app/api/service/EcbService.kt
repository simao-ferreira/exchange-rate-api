package io.template.app.api.service

import io.template.app.infrastructure.model.EnvelopeDto
import org.springframework.stereotype.Service

@Service
class EcbService(
    ecbClient: EcbClient
) {

    private val client = ecbClient.getClient()
    private val connector = client.create(EcbConnector::class.java)

    fun getDailyExchangeRatesResponse(): EnvelopeDto {
        val ecbResponse = connector.getDailyRates().execute()

        if (!ecbResponse.isSuccessful) {
            throw Exception("Error calling ECB: ${ecbResponse.errorBody()}")
        }

        return ecbResponse.body() ?: throw Exception("Response is null!")

    }

}