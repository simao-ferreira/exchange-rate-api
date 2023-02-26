package io.template.app.api.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.template.app.infrastructure.model.CubeDto
import io.template.app.infrastructure.model.DailyReferenceRatesDto
import io.template.app.infrastructure.model.EnvelopeDto
import io.template.app.infrastructure.model.ReferenceRateDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
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

    @Test
    fun `Available currencies should return EUR, PLN and GBP`() {
        //when
        val result = exchangeRateService.availableCurrencies()
        //then
        assertThat(result).containsExactlyInAnyOrder("EUR", "PLN", "GBP")
    }

    @TestFactory
    fun `Should return correct exchange rate`() = listOf(
        "GBP" to "1.2",
        "EUR" to "1",
        "PLN" to "4",
    )
        .map { (input, expected) ->
            DynamicTest.dynamicTest("of $expected for currency $input ") {
                assertEquals(expected, exchangeRateService.exchangeRateFor(input))
            }

        }

    @Test
    fun `Given no error happens should return a list of ecb daily exchange rates`() {
        //given
        val envelope = EnvelopeDto(
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

        val expected = mapOf(
            "BGN" to "1.9558",
            "CZK" to "23.643",
            "DKK" to "7.4438",
            "GBP" to "0.88245",
        )

        every { ecbService.getDailyExchangeRatesResponse() } returns envelope

        //when
        val result = exchangeRateService.ecbDailyExchangeRates()
        //then
        assertEquals(result, expected)
    }
}