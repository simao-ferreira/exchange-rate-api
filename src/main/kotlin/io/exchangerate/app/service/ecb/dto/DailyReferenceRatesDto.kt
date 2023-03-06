package io.exchangerate.app.service.ecb.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class DailyReferenceRatesDto(
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JacksonXmlProperty(localName = "time")
    val time: String,
    @JacksonXmlProperty(localName = "Cube")
    @JacksonXmlElementWrapper(useWrapping = false)
    val rates: List<ReferenceRateDto>
)