package io.template.app.api.service

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import io.template.app.infrastructure.model.EnvelopeDto
import org.springframework.stereotype.Component
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET

@Component
object EcbClient {

    private const val BASE_URL = "https://www.ecb.europa.eu/"

    fun getClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create(XmlMapper()))
            .build()
    }
}

interface EcbConnector {
    @GET("stats/eurofxref/eurofxref-daily.xml")
    fun getDailyRates(): Call<EnvelopeDto>
}