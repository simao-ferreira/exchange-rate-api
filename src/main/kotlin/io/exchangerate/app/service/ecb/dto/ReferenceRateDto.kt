package io.exchangerate.app.service.ecb.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class ReferenceRateDto(
    @JacksonXmlProperty(localName = "currency") val currency: String,
    @JacksonXmlProperty(localName = "rate") val rate: String
)