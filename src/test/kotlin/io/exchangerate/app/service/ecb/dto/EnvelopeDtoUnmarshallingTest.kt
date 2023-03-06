package io.exchangerate.app.service.ecb.dto

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.File


@SpringBootTest
class EnvelopeDtoUnmarshallingTest {

    fun unmarshall(filename: String): EnvelopeDto {
        val xmlMapper = XmlMapper()
        return xmlMapper.readValue(File(filename), EnvelopeDto::class.java)
    }

    @Test
    fun `Unmarshalling daily exchange rates to envelope works accurately`() {
        //when
        val result = unmarshall("src/test/resources/eurofxref-daily.xml")
        //then
        assertNotNull(result)
        assertEquals(1, result.cubeDto.exchangeRates.size)
        assertEquals("2023-03-03", result.cubeDto.exchangeRates[0].time)
        assertEquals(30, result.cubeDto.exchangeRates[0].rates.size)
        //USD index 0
        assertEquals("USD", result.cubeDto.exchangeRates[0].rates[0].currency)
        assertEquals("1.0615", result.cubeDto.exchangeRates[0].rates[0].rate)
        //ISK index 12
        assertEquals("ISK", result.cubeDto.exchangeRates[0].rates[11].currency)
        assertEquals("150.30", result.cubeDto.exchangeRates[0].rates[11].rate)
        //ZAR index 29
        assertEquals("ZAR", result.cubeDto.exchangeRates[0].rates[29].currency)
        assertEquals("19.2837", result.cubeDto.exchangeRates[0].rates[29].rate)
    }

    @Test
    fun `Unmarshalling historical exchange rates to envelope works accurately`() {
        //when
        val result = unmarshall("src/test/resources/eurofxref-hist-90d.xml")
        //then
        assertNotNull(result)
        assertEquals(64, result.cubeDto.exchangeRates.size)

        assertEquals("2023-03-03", result.cubeDto.exchangeRates[0].time)
        assertEquals(30, result.cubeDto.exchangeRates[0].rates.size)

        assertEquals("USD", result.cubeDto.exchangeRates[0].rates[0].currency)
        assertEquals("1.0615", result.cubeDto.exchangeRates[0].rates[0].rate)

        assertEquals("2023-02-28", result.cubeDto.exchangeRates[3].time)
        assertEquals(30, result.cubeDto.exchangeRates[3].rates.size)

        assertEquals("CZK", result.cubeDto.exchangeRates[3].rates[3].currency)
        assertEquals("23.497", result.cubeDto.exchangeRates[3].rates[3].rate)

        assertEquals("2022-12-05", result.cubeDto.exchangeRates[63].time)
        assertEquals(31, result.cubeDto.exchangeRates[63].rates.size)

        assertEquals("HRK", result.cubeDto.exchangeRates[63].rates[13].currency)
        assertEquals("7.551", result.cubeDto.exchangeRates[63].rates[13].rate)

    }
}

