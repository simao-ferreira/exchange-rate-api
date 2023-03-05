package io.exchangerate.app.api.service

import io.exchangerate.app.api.controller.v1.model.DatedExchangeRateResponse
import io.exchangerate.app.api.controller.v1.model.ExchangeRateResponse
import io.exchangerate.app.infrastructure.model.EnvelopeDto
import org.springframework.stereotype.Service

@Service
class HistoricalExchangeRateServiceImpl(
    val ecbService: EcbService
) : HistoricalExchangeRateService {

    override fun historicalExchangeRates(): List<DatedExchangeRateResponse> {
        val response = ecbService.getHistoricalExchangeRatesResponse()
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