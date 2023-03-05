package io.exchangerate.app.api.controller.v1

import io.exchangerate.app.api.controller.v1.model.CurrencyResponse
import io.exchangerate.app.api.controller.v1.model.ErrorResponse
import io.exchangerate.app.api.service.ExchangeRateServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/daily")
@Tag(name = "Daily Euro Exchange Rate", description = "Endpoint for retrieving daily euro exchange rate currencies")
class ExchangeRateController(
    private val exchangeRateServiceImpl: ExchangeRateServiceImpl
) {
    @Operation(
        method = "GET",
        summary = "Return daily euro exchange rate ISO currencies",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful return of daily available currencies"
            )
        ]
    )
    @GetMapping("/available-currencies")
    fun listAvailableCurrencies(): Set<String> {
        return exchangeRateServiceImpl.availableCurrencies()
    }

    @Operation(
        method = "GET",
        summary = "Returns daily euro exchange rates",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful return of available daily exchange rates"
            )
        ]
    )
    @GetMapping("/ecb-exchange-rates")
    fun getEcbExchangeRates(): Map<String, String> {
        return exchangeRateServiceImpl.ecbDailyExchangeRates()
    }

    @Operation(
        method = "GET",
        summary = "Returns daily euro exchange rate for given ISO currency",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful return of available exchange rate for currency"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Currency not found",
                content = [
                    Content(schema = Schema(implementation = ErrorResponse::class))
                ]
            ),
        ]
    )
    @GetMapping("/exchange-rate/{currency}")
    fun getExchangeRate(@PathVariable currency: String): CurrencyResponse {
        return exchangeRateServiceImpl.exchangeRateFor(currency)
    }
}