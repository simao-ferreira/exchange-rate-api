package io.exchangerate.app.service

import io.exchangerate.app.controller.v1.model.DatedExchangeRateResponse
import io.exchangerate.app.controller.v1.model.ExchangeRateResponse
import io.exchangerate.app.service.ecb.EcbService
import io.exchangerate.app.service.ecb.dto.EnvelopeDto
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class HistoricalExchangeRateServiceImpl(
    val ecbService: EcbService
) : HistoricalExchangeRateService {

    private val log = KotlinLogging.logger {}

    override fun historicalExchangeRates(): List<DatedExchangeRateResponse> {
        val response = ecbService.getHistoricalExchangeRatesResponse()
        log.info { "Received response for ECB historical exchange rates" }
        return mapHistoricalExchangeRatesResponse(response)
    }

    private fun mapHistoricalExchangeRatesResponse(envelopeDto: EnvelopeDto): List<DatedExchangeRateResponse> {
        val listOfExchangeRates = mutableListOf<DatedExchangeRateResponse>()
        envelopeDto.cubeDto.exchangeRates.forEach {
            val exchangeRates = mutableListOf<ExchangeRateResponse>()
            it.rates.map { xr ->
                exchangeRates.add(ExchangeRateResponse(xr.currency, xr.rate))
            }
            listOfExchangeRates.add(DatedExchangeRateResponse(it.time, exchangeRates))
        }
        return listOfExchangeRates
    }
}