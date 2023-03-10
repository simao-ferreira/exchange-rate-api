package io.exchangerate.app.service

import io.exchangerate.app.controller.v1.model.DatedExchangeRateResponse
import io.exchangerate.app.controller.v1.model.ExchangeRateResponse
import io.exchangerate.app.exceptions.CurrencyNotAvailableException
import io.exchangerate.app.exceptions.PageNotFoundException
import io.exchangerate.app.exceptions.YearOutOfLimitsException
import io.exchangerate.app.service.ecb.EcbService
import io.exchangerate.app.service.ecb.dto.EnvelopeDto
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.Year

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

    override fun pagedHistoricalExchangeRates(page: Int, size: Int): List<DatedExchangeRateResponse> {
        val response = ecbService.getHistoricalExchangeRatesResponse()
        log.info { "Received response for ECB historical exchange rates" }

        val rates = mapHistoricalExchangeRatesResponse(response).chunked(size)

        if (page - 1 > rates.size) {
            throw PageNotFoundException("Page $page not found, last page is ${rates.size}")
        }

        return rates[page - 1]
    }

    override fun yearlyExchangeRates(year: String, currencies: Set<String>): List<DatedExchangeRateResponse> {

        if (year.toInt() > Year.now().value) {
            throw YearOutOfLimitsException("Requested year $year is in the future")
        }

        val response = ecbService.getHistoricalExchangeRatesResponse()
        log.info { "Received response for ECB historical exchange rates" }

        val yearlyResponse = mutableListOf<DatedExchangeRateResponse>()

        mapHistoricalExchangeRatesResponse(response)
            .filter { it.date.contains(year) }
            .map {
                yearlyResponse.add(DatedExchangeRateResponse(
                    it.date,
                    it.rates.filter { c -> currencies.contains(c.currency) }
                ))
            }

        if (yearlyResponse.none { it.rates.isNotEmpty() }) {
            throw CurrencyNotAvailableException("No currencies from $currencies not present in the response")
        }

        return yearlyResponse
    }

    override fun last90DaysExchangeRates(): List<DatedExchangeRateResponse> {
        val response = ecbService.getLast90DaysExchangeRatesResponse()
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