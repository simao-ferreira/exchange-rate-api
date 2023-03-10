package io.exchangerate.app.controller.v1

import com.ninjasquad.springmockk.MockkBean
import io.exchangerate.app.controller.v1.model.CurrencyResponse
import io.exchangerate.app.controller.v1.model.DatedExchangeRateResponse
import io.exchangerate.app.controller.v1.model.ExchangeRateResponse
import io.exchangerate.app.exceptions.CorruptedResponseException
import io.exchangerate.app.exceptions.CurrencyNotAvailableException
import io.exchangerate.app.service.ExchangeRateServiceImpl
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.IOException

@ExtendWith(SpringExtension::class)
@WebMvcTest(DailyExchangeRateController::class)
class DailyExchangeRateControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var service: ExchangeRateServiceImpl

    @BeforeEach
    fun setup() {
        every { service.availableCurrencies() } returns setOf("PLN", "EUR")
    }

    @Test
    @DirtiesContext
    fun whenCurrenciesEndpointIsCalled_thenResultIsSuccessful() {
        //when
        mockMvc.perform(get("/daily/available-currencies"))
            //then
            .andExpect(status().isOk)
    }

    @Test
    @DirtiesContext
    fun whenCurrenciesEndpointIsCalled_thenResultContainsMockResponse() {
        //when
        mockMvc.perform(get("/daily/available-currencies"))
            //then
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.[0]").value("PLN"))
            .andExpect(jsonPath("$.[1]").value("EUR"))
    }

    @Test
    @DirtiesContext
    fun whenExchangeRateForCurrencyEndpointIsCalled_thenResultContainsMockResponse() {
        //given
        val currency = "PLN"
        every { service.dailyExchangeRateFor(currency) } returns CurrencyResponse("2023-01-01", currency, "4")
        //when
        mockMvc.get("/daily/exchange-rate/{currency}", currency)
            .andExpect {
                status { isOk() }
                content {
                    json(
                        """{
                            "date": "2023-01-01",
                            "currency": "PLN",
                            "rate": "4"
                            }"""
                    )
                }
            }
    }

    @Test
    @DirtiesContext
    fun whenExchangeRateCurrencyEndpointIsCalled_andDataIsCorrupted_thenReturnErrorResponse() {
        //given
        val currency = "USD"
        every { service.dailyExchangeRateFor(currency) } throws
                CorruptedResponseException("Response has a corrupted value")
        //when
        mockMvc.get("/daily/exchange-rate/{currency}", currency) {
        }.andExpect {
            status { is4xxClientError() }
            content {
                json(
                    """{
                        "status": 422,
                        "message": "Response has a corrupted value"
                    }"""
                )
            }
        }
    }

    @Test
    @DirtiesContext
    fun whenExchangeRateForNonExistentCurrencyEndpointIsCalled_thenReturnErrorResponse() {
        //given
        val currency = "HRK"
        every { service.dailyExchangeRateFor(currency) } throws
                CurrencyNotAvailableException("Currency $currency not available in ECB daily exchange rate response")
        //when
        mockMvc.get("/daily/exchange-rate/{currency}", currency) {
        }.andExpect {
            status { is4xxClientError() }
            content {
                json(
                    """{
                    "status": 404,
                    "message": "Currency $currency not available in ECB daily exchange rate response"
                }"""
                )
            }
        }
    }

    @Test
    @DirtiesContext
    fun whenDailyExchangeRatesIsCalled_thenResultContainsMockResponse() {
        //given
        val datedExchangeRateResponse = DatedExchangeRateResponse(
            "2020-02-01",
            listOf(
                ExchangeRateResponse("USD", "1.0570"),
                ExchangeRateResponse("JPY", "143.55"),

                )
        )
        every { service.ecbDailyExchangeRates() } returns datedExchangeRateResponse
        //when
        mockMvc.get("/daily/ecb-exchange-rates") {
        }.andExpect {
            status { isOk() }
            content { json(
                """{
                  "date": "2020-02-01",
                  "rates": [
                    {
                      "currency": "USD",
                      "rate": "1.0570"
                    },
                    {
                      "currency": "JPY",
                      "rate": "143.55"
                    }
                  ]
                }""")
            }
        }
    }

    @Test
    @DirtiesContext
    fun whenDailyExchangeRatesIsCalled_andSomeExceptionIsThrown_thenReturnErrorResponse() {
        //given
        every { service.ecbDailyExchangeRates() } throws IOException("Some exception message")
        //when
        mockMvc.get("/daily/ecb-exchange-rates") {
        }.andExpect {
            status { is5xxServerError() }
            content {
                json(
                    """{
                    "status": 500,
                    "message": "Some exception message"
                }"""
                )
            }
        }
    }

}