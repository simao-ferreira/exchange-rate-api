package io.exchangerate.app.controller.v1

import com.ninjasquad.springmockk.MockkBean
import io.exchangerate.app.controller.v1.model.DatedExchangeRateResponse
import io.exchangerate.app.controller.v1.model.ExchangeRateResponse
import io.exchangerate.app.exceptions.CurrencyNotAvailableException
import io.exchangerate.app.exceptions.EcbConnectorException
import io.exchangerate.app.exceptions.PageNotFoundException
import io.exchangerate.app.exceptions.YearOutOfLimitsException
import io.exchangerate.app.service.HistoricalExchangeRateServiceImpl
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.io.IOException

@ExtendWith(SpringExtension::class)
@WebMvcTest(HistoricalExchangeRateController::class)
class HistoricalExchangeRateControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var service: HistoricalExchangeRateServiceImpl

    private val endpointResponse = listOf(
        DatedExchangeRateResponse(
            "2023-03-02",
            listOf(
                ExchangeRateResponse("BGN", "1.9558"),
                ExchangeRateResponse("CZK", "23.643"),
                ExchangeRateResponse("DKK", "7.4438"),
                ExchangeRateResponse("GBP", "0.88245"),
            )
        ),
        DatedExchangeRateResponse(
            "2022-03-01",
            listOf(
                ExchangeRateResponse("BGN", "1.9534"),
                ExchangeRateResponse("CZK", "23.553"),
                ExchangeRateResponse("DKK", "7.4422"),
                ExchangeRateResponse("GBP", "1.88245"),
            )
        )
    )

    private val jsonResponse = """[
                          {
                            "date": "2023-03-02",
                            "rates": [
                              {
                                "currency": "BGN",
                                "rate": "1.9558"
                              },
                              {
                                "currency": "CZK",
                                "rate": "23.643"
                              },
                              {
                                "currency": "DKK",
                                "rate": "7.4438"
                              },
                              {
                                "currency": "GBP",
                                "rate": "0.88245"
                              }
                            ]
                          },
                          {
                            "date": "2022-03-01",
                            "rates": [
                              {
                                "currency": "BGN",
                                "rate": "1.9534"
                              },
                              {
                                "currency": "CZK",
                                "rate": "23.553"
                              },
                              {
                                "currency": "DKK",
                                "rate": "7.4422"
                              },
                              {
                                "currency": "GBP",
                                "rate": "1.88245"
                              }
                            ]
                          }
                        ]"""

    @Test
    @DirtiesContext
    fun whenAllExchangeRatesEndpointIsCalled_thenResultIsSuccessful() {
        //given
        every { service.pagedHistoricalExchangeRates(1, 50) } returns endpointResponse
        //when
        mockMvc.get("/historical/all-exchange-rates") {
            param("page", "1")
            param("size", "50")
        }
            //then
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    @DirtiesContext
    fun whenAllExchangeRatesEndpointIsCalled_thenResultContainsMockResponse() {
        //given
        every { service.pagedHistoricalExchangeRates(1, 50) } returns endpointResponse
        //when
        mockMvc.get("/historical/all-exchange-rates") {
            param("page", "1")
            param("size", "50")
        }
            //then
            .andExpect {
                status { isOk() }
            }
            .andExpect {
                content {
                    json(jsonResponse)
                }
            }
    }

    @Test
    @DirtiesContext
    fun when90DaysExchangeRatesEndpointIsCalled_thenResultContainsMockResponse() {
        //given
        every { service.last90DaysExchangeRates() } returns endpointResponse
        //when
        mockMvc.get("/historical/90-days-exchange-rates")
            //then
            .andExpect {
                status { isOk() }
            }
            .andExpect {
                content {
                    json(jsonResponse)
                }
            }
    }

    @Test
    @DirtiesContext
    fun when90DaysExchangeRatesEndpointIsCalled_andSomeExceptionIsThrown_thenReturnErrorResponse() {
        //given
        every { service.last90DaysExchangeRates() } throws IOException("Some exception message")
        //when
        mockMvc.get("/historical/90-days-exchange-rates") {
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

    @Test
    @DirtiesContext
    fun whenAllExchangeRatesEndpointIsCalled_andSomeExceptionIsThrown_thenReturnErrorResponse() {
        //given
        every { service.pagedHistoricalExchangeRates(1, 50) } throws EcbConnectorException("Some exception message")
        //when
        mockMvc.get("/historical/all-exchange-rates") {
            param("page", "1")
            param("size", "50")
        }.andExpect {
            status { is5xxServerError() }
            content {
                json(
                    """{
                    "status": 503,
                    "message": "Some exception message"
                    }"""
                )
            }
        }
    }

    @Test
    @DirtiesContext
    fun whenAllExchangeRatesEndpointIsCalled_forNonExistentPage_thenReturnErrorResponse() {
        //given
        every {
            service.pagedHistoricalExchangeRates(
                5,
                50
            )
        } throws PageNotFoundException("Page 5 not found, last page is 1")
        //when
        mockMvc.get("/historical/all-exchange-rates") {
            param("page", "5")
            param("size", "50")
        }.andExpect {
            status { is4xxClientError() }
            content {
                json(
                    """{
                    "status":404,
                    "message":"Page 5 not found, last page is 1"
                    }"""
                )
            }
        }
    }


    @Test
    @DirtiesContext
    fun whenYearlyExchangeRatesEndpointIsCalled_forValidYearAndCurrency_thenResultContainsMockResponse() {
        //given
        val response = listOf(
            DatedExchangeRateResponse(
                "2022-03-01",
                listOf(
                    ExchangeRateResponse("CZK", "23.553"),
                    ExchangeRateResponse("DKK", "7.4422"),
                )
            )
        )
        every { service.yearlyExchangeRates("2022", setOf("DKK", "CZK")) } returns response
        //when
        mockMvc.get("/historical/year-exchange-rates") {
            param("year", "2022")
            param("currencies", "DKK", "CZK")
        }
            //then
            .andExpect {
                status { isOk() }
            }
            .andExpect {
                content {
                    json(
                        """[
                          {
                            "date": "2022-03-01",
                            "rates": [
                              {
                                "currency": "CZK",
                                "rate": "23.553"
                              },
                              {
                                "currency": "DKK",
                                "rate": "7.4422"
                              }
                            ]
                          }
                        ]"""
                    )
                }
            }
    }

    @Test
    @DirtiesContext
    fun whenYearlyExchangeRatesEndpointIsCalled_forInvalidCurrency_thenReturnErrorResponse() {
        //given
        every {
            service.yearlyExchangeRates(
                "2023",
                setOf("HRK")
            )
        } throws CurrencyNotAvailableException("Currency not found")
        //when
        mockMvc.get("/historical/year-exchange-rates") {
            param("year", "2023")
            param("currencies", "HRK")
        }
            //then
            .andExpect {
                status { is4xxClientError() }
            }
            .andExpect {
                content {
                    json(
                        """{
                    "status":404,
                    "message":"Currency not found"
                    }"""
                    )
                }
            }
    }

    @Test
    @DirtiesContext
    fun whenYearlyExchangeRatesEndpointIsCalled_forInvalidYear_thenReturnErrorResponse() {
        //given
        every {
            service.yearlyExchangeRates(
                "2099",
                setOf("HRK")
            )
        } throws YearOutOfLimitsException("No exchange rates for the future yet!")
        //when
        mockMvc.get("/historical/year-exchange-rates") {
            param("year", "2099")
            param("currencies", "HRK")
        }
            //then
            .andExpect {
                status { is4xxClientError() }
            }
            .andExpect {
                content {
                    json(
                        """{
                    "status":400,
                    "message":"No exchange rates for the future yet!"
                    }"""
                    )
                }
            }
    }
}