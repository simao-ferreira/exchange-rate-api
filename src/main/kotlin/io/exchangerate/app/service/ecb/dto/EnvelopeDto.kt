package io.exchangerate.app.service.ecb.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "Envelope")
@JsonIgnoreProperties(value = ["Sender", "subject"])
data class EnvelopeDto(
    @JacksonXmlProperty(localName = "Cube")
    val cubeDto: CubeDto
)
