package io.exchangerate.app.api.service

import com.ninjasquad.springmockk.MockkBean
import io.exchangerate.app.api.controller.v1.model.DatedExchangeRateResponse
import io.exchangerate.app.api.controller.v1.model.ExchangeRateResponse
import io.exchangerate.app.infrastructure.model.CubeDto
import io.exchangerate.app.infrastructure.model.DailyReferenceRatesDto
import io.exchangerate.app.infrastructure.model.EnvelopeDto
import io.exchangerate.app.infrastructure.model.ReferenceRateDto
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class HistoricalExchangeRateServiceImplTest {

    @MockkBean
    private lateinit var ecbService: EcbService

    @Autowired
    private lateinit var service: HistoricalExchangeRateService

    private val envelopeDto = EnvelopeDto(
        CubeDto(
            listOf(
                DailyReferenceRatesDto(
                    "2023-03-02",
                    listOf(
                        ReferenceRateDto("BGN", "1.9558"),
                        ReferenceRateDto("CZK", "23.643"),
                        ReferenceRateDto("DKK", "7.4438"),
                        ReferenceRateDto("GBP", "0.88245"),
                    )
                ),
                DailyReferenceRatesDto(
                    "2023-03-01",
                    listOf(
                        ReferenceRateDto("BGN", "1.9534"),
                        ReferenceRateDto("CZK", "23.553"),
                        ReferenceRateDto("DKK", "7.4422"),
                        ReferenceRateDto("GBP", "1.88245"),
                    )
                )
            )
        )
    )

    @BeforeEach
    fun setup() {
        every { ecbService.getHistoricalExchangeRatesResponse() } returns envelopeDto
    }

    @Test
    fun `Available currencies should return BGN, CZK, DKK and GBP`() {
        //when
        val result = service.historicalExchangeRates()
        //then
        assertEquals(
            listOf(
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
                    "2023-03-01",
                    listOf(
                        ExchangeRateResponse("BGN", "1.9534"),
                        ExchangeRateResponse("CZK", "23.553"),
                        ExchangeRateResponse("DKK", "7.4422"),
                        ExchangeRateResponse("GBP", "1.88245"),
                    )
                )
            ), result
        )
    }
}