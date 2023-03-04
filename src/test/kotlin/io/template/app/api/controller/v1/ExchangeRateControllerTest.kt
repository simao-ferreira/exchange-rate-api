package io.template.app.api.controller.v1

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.template.app.api.service.ExchangeRateServiceImpl
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

@ExtendWith(SpringExtension::class)
@WebMvcTest(ExchangeRateController::class)
class ExchangeRateControllerTest {

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
        every { service.exchangeRateFor(currency) } returns "4"
        //when
        mockMvc.get("/daily/exchange-rate/{currency}", currency) {
        }.andExpect {
            status { isOk() }
            content { json("4") }
        }
    }

    @Test
    @DirtiesContext
    fun whenDailyExchangeRatesIsCalled_thenResultContainsMockResponse() {
        //given
        every { service.ecbDailyExchangeRates() } returns mapOf(Pair("USD", "1.0570"), Pair("JPY", "143.55"))
        //when
        mockMvc.get("/daily/ecb-exchange-rates") {
        }.andExpect {
            status { isOk() }
            content { json("""{"USD": "1.0570","JPY": "143.55"}""") }
        }
    }

}