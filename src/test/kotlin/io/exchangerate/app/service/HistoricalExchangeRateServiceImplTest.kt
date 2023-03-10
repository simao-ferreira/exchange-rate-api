package io.exchangerate.app.service

import com.ninjasquad.springmockk.MockkBean
import io.exchangerate.app.controller.v1.model.DatedExchangeRateResponse
import io.exchangerate.app.controller.v1.model.ExchangeRateResponse
import io.exchangerate.app.service.ecb.EcbService
import io.exchangerate.app.service.ecb.dto.CubeDto
import io.exchangerate.app.service.ecb.dto.DailyReferenceRatesDto
import io.exchangerate.app.service.ecb.dto.EnvelopeDto
import io.exchangerate.app.service.ecb.dto.ReferenceRateDto
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
                ),
                DailyReferenceRatesDto(
                    "2023-02-01",
                    listOf(
                        ReferenceRateDto("BGN", "1.9434"),
                        ReferenceRateDto("CZK", "22.553"),
                        ReferenceRateDto("DKK", "6.4422"),
                        ReferenceRateDto("GBP", "1.88445"),
                    )
                )
            )
        )
    )

    private val datedExchangeRateResponse = listOf(
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
        ),
        DatedExchangeRateResponse(
            "2023-02-01",
            listOf(
                ExchangeRateResponse("BGN", "1.9434"),
                ExchangeRateResponse("CZK", "22.553"),
                ExchangeRateResponse("DKK", "6.4422"),
                ExchangeRateResponse("GBP", "1.88445"),
            )
        )
    )

    @BeforeEach
    fun setup() {
        every { ecbService.getHistoricalExchangeRatesResponse() } returns envelopeDto
        every { ecbService.getLast90DaysExchangeRatesResponse() } returns envelopeDto
    }

    @Test
    fun `Should successfully return envelope response for historical exchange rates request`() {
        //when
        val result = service.historicalExchangeRates()
        //then
        assertEquals(
            datedExchangeRateResponse, result
        )
    }

    @Test
    fun `Should successfully return paged response for historical exchange rates request`() {
        //given
        //when
        val result = service.pagedHistoricalExchangeRates(1, 100)
        //then
        assertEquals(
            datedExchangeRateResponse, result
        )
    }

    @Test
    fun `Should successfully return page 2 response for historical exchange rates request`() {
        //when
        val result = service.pagedHistoricalExchangeRates(2, 1)
        //then
        assertEquals(
            listOf(datedExchangeRateResponse[1]), result
        )
    }

    @Test
    fun `Should successfully return 2 page with only 1 item for historical exchange rates request`() {
        //when
        val result = service.pagedHistoricalExchangeRates(2, 2)
        //then
        assertEquals(
            1, result.size
        )
    }
    @Test
    fun `Should successfully return 1 page with only 2 items for historical exchange rates request`() {
        //when
        val result = service.pagedHistoricalExchangeRates(1, 2)
        //then
        assertEquals(
            2, result.size
        )
    }
}