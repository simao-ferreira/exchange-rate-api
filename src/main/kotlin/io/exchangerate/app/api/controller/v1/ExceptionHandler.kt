package io.exchangerate.app.api.controller.v1

import io.exchangerate.app.api.controller.v1.exceptions.CurrencyNotAvailableException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler
    fun mapUnexpectedException(ex: Exception): ResponseEntity<ErrorResponseModel> {
        return ResponseEntity(
            ErrorResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler
    fun currencyNotAvailableException(ex: CurrencyNotAvailableException): ResponseEntity<ErrorResponseModel> {
        return ResponseEntity(ErrorResponseModel(HttpStatus.NOT_FOUND.value(), ex.message), HttpStatus.NOT_FOUND)
    }
}