package io.exchangerate.app.config

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import io.exchangerate.app.service.ecb.EcbConnector
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
class EcbConnectorConfig {

    @Value("\${ecb.url}")
    private lateinit var ecbUrl: String

    @Bean
    fun ecbConnector(): EcbConnector {
        return Retrofit.Builder()
            .baseUrl(ecbUrl)
            .addConverterFactory(JacksonConverterFactory.create(XmlMapper()))
            .build()
            .create(EcbConnector::class.java)
    }
}