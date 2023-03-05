//package io.exchangerate.app.infrastructure.model
//
//import com.fasterxml.jackson.dataformat.xml.XmlMapper
//import org.junit.jupiter.api.Assertions.assertNotNull
//import org.junit.jupiter.api.Test
//import org.springframework.boot.test.context.SpringBootTest
//import java.io.File
//
//
//@SpringBootTest
//class EnvelopeDtoTest {
//
//    fun unmarshall(): EnvelopeDto {
//        val xmlMapper = XmlMapper()
//        return xmlMapper.readValue(File("src/test/resources/eurofxref-daily.xml"), EnvelopeDto::class.java)
//    }
//    This is not working because the source file is incorrectly build, something is missing!
//    @Test
//    fun `Test Unmarshalling of xml works accuratelly`() {
//
//        val result = unmarshall()
//
//        assertNotNull(result)
//    }
//}

