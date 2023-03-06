package io.exchangerate.app.controller.v1

import io.exchangerate.app.controller.v1.model.DatedExchangeRateResponse
import io.exchangerate.app.service.HistoricalExchangeRateService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/historical")
@Tag(
    name = "Historical Euro Exchange Rate",
    description = "Endpoint for retrieving historical euro exchange rate currencies"
)
class HistoricalExchangeRateController(
    private val historicalExchangeRateService: HistoricalExchangeRateService
) {

    @Operation(
        method = "GET",
        summary = "Returns last 90 days euro exchange rates",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful return of available last 90 days exchange rates"
            )
        ]
    )
    @GetMapping("/90-days-exchange-rates")
    fun getLast90DaysExchangeRates(): List<DatedExchangeRateResponse> {
        return historicalExchangeRateService.last90DaysExchangeRates()
    }

    @Operation(
        method = "GET",
        summary = "Returns historical euro exchange rates",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful return of available historical exchange rates"
            )
        ]
    )
    @GetMapping("/all-exchange-rates")
    fun getHistoricalExchangeRates(): List<DatedExchangeRateResponse> {
        return historicalExchangeRateService.historicalExchangeRates()
    }
}