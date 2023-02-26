package io.template.app.api.controller.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.template.app.api.service.ExchangeRateServiceImpl
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
@ApiResponse(responseCode = "500", description = "Internal server error, this should not happen")
@Tag(name = "Exchange Rate Controller", description = "Endpoint for retrieving currencies")
class ExchangeRateController(
    private val exchangeRateServiceImpl: ExchangeRateServiceImpl
) {

    @Operation(
        method = "GET",
        summary = "Returns list of available ISO currencies",
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully returned available currencies"
    )
    @GetMapping("/currencies")
    fun listAvailableCurrencies(): Set<String> {
        return exchangeRateServiceImpl.availableCurrencies()
    }

    @Operation(
        method = "GET",
        summary = "Returns exchange rate for given ISO currency",
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully returned available exchange rate"
    )
    @GetMapping("/exchange-rate/{currency}")
    fun getExchangeRate(@PathVariable currency: String): String {
        return exchangeRateServiceImpl.exchangeRateFor(currency)
    }

    @Operation(
        method = "GET",
        summary = "Returns ECB daily exchange rate",
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully returned available daily exchange rates"
    )
    @GetMapping("/ecb-exchange-rates")
    fun getEcbExchangeRates(): Map<String, String> {
        return exchangeRateServiceImpl.ecbDailyExchangeRates()
    }
}
