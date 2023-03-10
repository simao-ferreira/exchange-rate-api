package io.exchangerate.app.controller.v1

import io.exchangerate.app.controller.v1.model.ErrorResponse
import io.exchangerate.app.exceptions.CorruptedResponseException
import io.exchangerate.app.exceptions.CurrencyNotAvailableException
import io.exchangerate.app.exceptions.EcbConnectorException
import io.exchangerate.app.exceptions.PageNotFoundException
import io.exchangerate.app.exceptions.YearOutOfLimitsException
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected Error",
        content = [
            Content(schema = Schema(implementation = ErrorResponse::class))
        ]
    )
    fun mapUnexpectedException(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler
    fun currencyNotAvailableException(ex: CurrencyNotAvailableException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun pageNotFoundException(ex: PageNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun yearOutOfLimitsException(ex: YearOutOfLimitsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler
    fun corruptedResponseException(ex: CorruptedResponseException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.message),
            HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    @ExceptionHandler
    @ApiResponse(
        responseCode = "503",
        description = "Error while connecting to ECB",
        content = [
            Content(schema = Schema(implementation = ErrorResponse::class))
        ]
    )
    fun ecbConnectorException(ex: EcbConnectorException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), ex.message),
            HttpStatus.SERVICE_UNAVAILABLE
        )
    }
}