package io.template.app.api.service

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
class ExchangeRateServiceImplTest(
    @Autowired private val exchangeRateService: ExchangeRateServiceImpl
) {
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

}