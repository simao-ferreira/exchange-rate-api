package io.exchangerate.app.api.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.exchangerate.app.api.controller.v1.exceptions.CurrencyNotAvailableException
import io.exchangerate.app.infrastructure.model.CubeDto
import io.exchangerate.app.infrastructure.model.DailyReferenceRatesDto
import io.exchangerate.app.infrastructure.model.EnvelopeDto
import io.exchangerate.app.infrastructure.model.ReferenceRateDto
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
                    "",
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
    fun `Should return a list of ecb daily exchange rates`() {
        //given
        val expected = mapOf(
            "BGN" to "1.9558",
            "CZK" to "23.643",
            "DKK" to "7.4438",
            "GBP" to "0.88245",
        )
        //when
        val result = exchangeRateService.ecbDailyExchangeRates()
        //then
        assertEquals(result, expected)
    }
}