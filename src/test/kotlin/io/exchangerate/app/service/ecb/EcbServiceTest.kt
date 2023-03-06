package io.exchangerate.app.service.ecb

import com.ninjasquad.springmockk.MockkBean
import io.exchangerate.app.exceptions.EcbConnectorException
import io.exchangerate.app.service.ecb.dto.CubeDto
import io.exchangerate.app.service.ecb.dto.DailyReferenceRatesDto
import io.exchangerate.app.service.ecb.dto.EnvelopeDto
import io.exchangerate.app.service.ecb.dto.ReferenceRateDto
import io.mockk.every
import io.mockk.mockk
import okhttp3.ResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import retrofit2.Response

@SpringBootTest
@ExtendWith(SpringExtension::class)
class EcbServiceTest {

    @MockkBean
    private lateinit var connector: EcbConnector

    @Autowired
    private lateinit var service: EcbService

    @Test
    @DirtiesContext
    fun `Should successfully map last 90 days rates`() {
        //given
        val envelopeDto = EnvelopeDto(
            CubeDto(
                listOf(
                    DailyReferenceRatesDto(
                        "2023-01-01",
                        listOf(
                            ReferenceRateDto("BGN", "1.9558"),
                            ReferenceRateDto("CZK", "23.643"),
                            ReferenceRateDto("DKK", "7.4438"),
                            ReferenceRateDto("GBP", "0.88245"),
                        )
                    ),
                    DailyReferenceRatesDto(
                        "2023-01-02",
                        listOf(
                            ReferenceRateDto("BGN", "1.9658"),
                            ReferenceRateDto("CZK", "23.765"),
                            ReferenceRateDto("DKK", "7.4438"),
                            ReferenceRateDto("GBP", "1.88245"),
                        )
                    )
                )
            )
        )
        every { connector.getLast90DaysRates().execute() } returns Response.success(envelopeDto)
        //when
        val sut = service.getLast90DaysExchangeRatesResponse()
        //then
        assertEquals(envelopeDto.cubeDto.exchangeRates.size, sut.cubeDto.exchangeRates.size)
        assertEquals("2023-01-01", sut.cubeDto.exchangeRates[0].time)
        assertEquals("BGN", sut.cubeDto.exchangeRates[0].rates[0].currency)
        assertEquals("0.88245", sut.cubeDto.exchangeRates[0].rates[3].rate)
        assertEquals("2023-01-02", sut.cubeDto.exchangeRates[1].time)
        assertEquals("CZK", sut.cubeDto.exchangeRates[1].rates[1].currency)
        assertEquals("7.4438", sut.cubeDto.exchangeRates[1].rates[2].rate)
    }

    @Test
    @DirtiesContext
    fun `Should successfully map historical rates`() {
        //given
        val envelopeDto = EnvelopeDto(
            CubeDto(
                listOf(
                    DailyReferenceRatesDto(
                        "2023-01-01",
                        listOf(
                            ReferenceRateDto("BGN", "1.9558"),
                            ReferenceRateDto("CZK", "23.643"),
                            ReferenceRateDto("DKK", "7.4438"),
                            ReferenceRateDto("GBP", "0.88245"),
                        )
                    ),
                    DailyReferenceRatesDto(
                        "2023-01-02",
                        listOf(
                            ReferenceRateDto("BGN", "1.9658"),
                            ReferenceRateDto("CZK", "23.765"),
                            ReferenceRateDto("DKK", "7.4438"),
                            ReferenceRateDto("GBP", "1.88245"),
                        )
                    ),
                    DailyReferenceRatesDto(
                        "1999-01-10",
                        listOf(
                            ReferenceRateDto("BGN", "0.9658"),
                            ReferenceRateDto("CZK", "21.765"),
                        )
                    )
                )
            )
        )
        every { connector.getAllHistoricalRates().execute() } returns Response.success(envelopeDto)
        //when
        val sut = service.getHistoricalExchangeRatesResponse()
        //then
        assertEquals(envelopeDto.cubeDto.exchangeRates.size, sut.cubeDto.exchangeRates.size)
        assertEquals("2023-01-01", sut.cubeDto.exchangeRates[0].time)
        assertEquals("BGN", sut.cubeDto.exchangeRates[0].rates[0].currency)
        assertEquals("0.88245", sut.cubeDto.exchangeRates[0].rates[3].rate)
        assertEquals("2023-01-02", sut.cubeDto.exchangeRates[1].time)
        assertEquals("CZK", sut.cubeDto.exchangeRates[1].rates[1].currency)
        assertEquals("7.4438", sut.cubeDto.exchangeRates[1].rates[2].rate)
        assertEquals("1999-01-10", sut.cubeDto.exchangeRates[2].time)
        assertEquals("CZK", sut.cubeDto.exchangeRates[2].rates[1].currency)
        assertEquals("21.765", sut.cubeDto.exchangeRates[2].rates[1].rate)
    }

    @Test
    @DirtiesContext
    fun `Should successfully map daily rates`() {
        //given
        val envelopeDto = EnvelopeDto(
            CubeDto(
                listOf(
                    DailyReferenceRatesDto(
                        "2023-01-05",
                        listOf(
                            ReferenceRateDto("TRY", "20.0628"),
                            ReferenceRateDto("AUD", "1.5728"),
                        )
                    )
                )
            )
        )
        every { connector.getDailyRates().execute() } returns Response.success(envelopeDto)
        //when
        val sut = service.getDailyExchangeRatesResponse()
        //then
        assertEquals(envelopeDto.cubeDto.exchangeRates.size, sut.cubeDto.exchangeRates.size)
        assertEquals("2023-01-05", sut.cubeDto.exchangeRates[0].time)
        assertEquals("TRY", sut.cubeDto.exchangeRates[0].rates[0].currency)
        assertEquals("1.5728", sut.cubeDto.exchangeRates[0].rates[1].rate)
    }

    @Test
    @DirtiesContext
    fun `Should fail when response body is null for daily rates`() {
        //given
        every { connector.getDailyRates().execute() } returns Response.success(null)
        //when
        val result: () -> Unit = { service.getDailyExchangeRatesResponse() }
        //then
        assertThrows<EcbConnectorException>(result)
    }

    @Test
    @DirtiesContext
    fun `Should fail when response is not successful for historical rates`() {
        //given
        val mockkResponseBody = mockk<ResponseBody>(relaxed = true)
        every { connector.getAllHistoricalRates().execute() } returns Response.error(404, mockkResponseBody)
        //when
        val result: () -> Unit = { service.getHistoricalExchangeRatesResponse() }
        //then
        assertThrows<EcbConnectorException>(result)
    }

    @Test
    @DirtiesContext
    fun `Should fail when response is not successful for last 90 days rates`() {
        assertThrows<EcbConnectorException> {
            //given
            val mockkResponseBody = mockk<ResponseBody>(relaxed = true)
            every { connector.getLast90DaysRates().execute() } returns Response.error(404, mockkResponseBody)
            //when
            service.getLast90DaysExchangeRatesResponse()
        }
    }

}