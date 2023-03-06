package io.exchangerate.app.api.service

import io.exchangerate.app.service.ecb.dto.EnvelopeDto
import org.springframework.stereotype.Component
import retrofit2.Call
import retrofit2.http.GET

@Component
interface EcbConnector {

    @GET("stats/eurofxref/eurofxref-daily.xml")
    fun getDailyRates(): Call<EnvelopeDto>

    @GET("stats/eurofxref/eurofxref-hist-90d.xml")
    fun getLast90DaysRates(): Call<EnvelopeDto>

    @GET("stats/eurofxref/eurofxref-hist.xml")
    fun getAllHistoricalRates(): Call<EnvelopeDto>
}