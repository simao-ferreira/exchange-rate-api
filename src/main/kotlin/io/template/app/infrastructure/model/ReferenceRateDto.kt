package io.template.app.infrastructure.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class ReferenceRateDto(
    @JacksonXmlProperty(isAttribute = true, localName = "currency")
    val currency: String = "",
    @JacksonXmlProperty(isAttribute = true, localName = "rate")
    val rate: String = ""
)