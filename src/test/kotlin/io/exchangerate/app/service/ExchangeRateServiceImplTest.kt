package io.exchangerate.app.service

import com.ninjasquad.springmockk.MockkBean
import io.exchangerate.app.controller.v1.model.DatedExchangeRateResponse
import io.exchangerate.app.controller.v1.model.ExchangeRateResponse
import io.exchangerate.app.exceptions.CorruptedResponseException
import io.exchangerate.app.exceptions.CurrencyNotAvailableException
import io.exchangerate.app.service.ecb.EcbService
import io.exchangerate.app.service.ecb.dto.CubeDto
import io.exchangerate.app.service.ecb.dto.DailyReferenceRatesDto
import io.exchangerate.app.service.ecb.dto.EnvelopeDto
import io.exchangerate.app.service.ecb.dto.ReferenceRateDto
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class ExchangeRateServiceImplTest {

    @MockkBean
    private lateinit var ecbService: EcbService

    @Autowired
    private lateinit var exchangeRateService: ExchangeRateServiceImpl

    private val envelopeDto = EnvelopeDto(
        CubeDto(
            listOf(
                DailyReferenceRatesDto(
                    "2021-01-01",
                    listOf(
                        ReferenceRateDto("BGN", "1.9558"),
                        ReferenceRateDto("CZK", "23.643"),
                        ReferenceRateDto("DKK", "7.4438"),
                        ReferenceRateDto("GBP", "0.88245"),
                    )
                )
            )
        )
    )

    @BeforeEach
    fun setup() {
        every { ecbService.getDailyExchangeRatesResponse() } returns envelopeDto
    }

    @Test
    fun `Available currencies should return BGN, CZK, DKK and GBP`() {
        //when
        val result = exchangeRateService.availableCurrencies()
        //then
        assertEquals(setOf("BGN", "CZK", "DKK", "GBP"), result)
    }

    @TestFactory
    fun `Should return correct exchange rate`() = listOf(
        "BGN" to "1.9558",
        "CZK" to "23.643",
        "DKK" to "7.4438",
        "GBP" to "0.88245",
    )
        .map { (input, expected) ->
            DynamicTest.dynamicTest("of $expected for currency $input ") {
                assertEquals(expected, exchangeRateService.dailyExchangeRateFor(input).rate)
            }
        }

    @Test
    fun `When non existent currency is requested should throw exception`() {
        //given
        val currency = "ESC"
        //when
        val result: () -> Unit = { exchangeRateService.dailyExchangeRateFor(currency) }
        //then
        assertThrows<CurrencyNotAvailableException>(result)
    }

    @Test
    fun `When currency exchange rate envelope contains corrupted date should throw exception`() {
        //given
        val currency = "BGN"
        val corruptedEnvelopeDto = EnvelopeDto(
            CubeDto(
                listOf(
                    DailyReferenceRatesDto(
                        "",
                        listOf(
                            ReferenceRateDto(currency, "1.9558"),
                        )
                    )
                )
            )
        )
        every { ecbService.getDailyExchangeRatesResponse() } returns corruptedEnvelopeDto
        //when
        val result: () -> Unit = { exchangeRateService.dailyExchangeRateFor(currency) }
        //then
        assertThrows<CorruptedResponseException>(result)
    }
    @Test
    fun `When daily exchange rate response envelope contains corrupted date should throw exception`() {
        //given
        val currency = "BGN"
        val corruptedEnvelopeDto = EnvelopeDto(
            CubeDto(
                listOf(
                    DailyReferenceRatesDto(
                        "",
                        listOf(
                            ReferenceRateDto(currency, "1.9558"),
                        )
                    )
                )
            )
        )
        every { ecbService.getDailyExchangeRatesResponse() } returns corruptedEnvelopeDto
        //when
        val result: () -> Unit = { exchangeRateService.ecbDailyExchangeRates() }
        //then
        assertThrows<CorruptedResponseException>(result)
    }

    @Test
    fun `When envelope contains corrupted rate should throw exception`() {
        //given
        val currency = "BGN"
        val corruptedEnvelopeDto = EnvelopeDto(
            CubeDto(
                listOf(
                    DailyReferenceRatesDto(
                        "2023-01-01",
                        listOf(
                            ReferenceRateDto(currency, ""),
                        )
                    )
                )
            )
        )
        every { ecbService.getDailyExchangeRatesResponse() } returns corruptedEnvelopeDto
        //when
        val result: () -> Unit = { exchangeRateService.dailyExchangeRateFor(currency) }
        //then
        assertThrows<CorruptedResponseException>(result)
    }

    @Test
    fun `Should return a list of ecb daily exchange rates`() {
        //given
        val expected = DatedExchangeRateResponse(
            "2021-01-01",
            listOf(
                ExchangeRateResponse("BGN", "1.9558"),
                ExchangeRateResponse("CZK", "23.643"),
                ExchangeRateResponse("DKK", "7.4438"),
                ExchangeRateResponse("GBP", "0.88245")
            )
        )
        //when
        val result = exchangeRateService.ecbDailyExchangeRates()
        //then
        assertEquals(expected, result)
    }
}