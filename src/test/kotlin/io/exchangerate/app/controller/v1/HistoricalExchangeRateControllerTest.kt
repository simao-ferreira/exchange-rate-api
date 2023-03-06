package io.exchangerate.app.controller.v1

import com.ninjasquad.springmockk.MockkBean
import io.exchangerate.app.controller.v1.model.DatedExchangeRateResponse
import io.exchangerate.app.controller.v1.model.ExchangeRateResponse
import io.exchangerate.app.service.HistoricalExchangeRateServiceImpl
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@WebMvcTest(HistoricalExchangeRateController::class)
class HistoricalExchangeRateControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var service: HistoricalExchangeRateServiceImpl

    @BeforeEach
    fun setup() {
        every { service.historicalExchangeRates() } returns listOf(
            DatedExchangeRateResponse(
                "2023-03-03",
                listOf(
                    ExchangeRateResponse(
                        "USD",
                        "1.0615"
                    )
                )
            )
        )
    }

    @Test
    @DirtiesContext
    fun whenCurrenciesEndpointIsCalled_thenResultIsSuccessful() {
        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/historical/exchange-rates"))
            //then
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}