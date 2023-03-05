package io.exchangerate.app.infrastructure.model

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.File


@SpringBootTest
class EnvelopeDtoUnmarshallingTest {

    fun unmarshall(): EnvelopeDto {
        val xmlMapper = XmlMapper()
        return xmlMapper.readValue(File("src/test/resources/eurofxref-daily.xml"), EnvelopeDto::class.java)
    }

    @Test
    fun `Test xml unmarshalling to envelope works accurately`() {
        //when
        val result = unmarshall()
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
}

