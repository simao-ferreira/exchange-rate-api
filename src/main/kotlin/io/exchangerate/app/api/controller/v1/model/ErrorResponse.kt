package io.exchangerate.app.api.controller.v1.model

data class ErrorResponse(
    var status: Int,
    var message: String? = null,
)