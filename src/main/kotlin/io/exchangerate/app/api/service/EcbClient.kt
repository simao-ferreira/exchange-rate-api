package io.exchangerate.app.api.service

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import io.exchangerate.app.infrastructure.model.EnvelopeDto
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET

interface EcbConnector {

    @GET("stats/eurofxref/eurofxref-daily.xml")
    fun getDailyRates(): Call<EnvelopeDto>

    @GET("stats/eurofxref/eurofxref-hist-90d.xml")
    fun getLast90DaysRates(): Call<EnvelopeDto>

    @GET("stats/eurofxref/eurofxref-hist.xml")
    fun getAllHistoricalRates(): Call<EnvelopeDto>

    companion object {
        private const val BASE_URL = "https://www.ecb.europa.eu/"

        fun create(): EcbConnector {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(XmlMapper()))
                .build()
                .create(EcbConnector::class.java)
        }
    }
}