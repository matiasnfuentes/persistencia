package ar.edu.unq.eperdemic.spring.controllers

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.RuntimeException
import java.time.LocalDateTime


@RestController
@CrossOrigin(origins = ["*"])
annotation class ServiceREST


@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(RuntimeException::class)
    fun customHandleNotFound(ex: Exception, request: WebRequest?): ResponseEntity<ErrorResponse> {
        val errors = ErrorResponse(HttpStatus.BAD_REQUEST.name, ex.message, LocalDateTime.now(), request?.contextPath)
        return ResponseEntity<ErrorResponse>(errors, HttpStatus.NOT_FOUND)
    } //...
}

class ErrorResponse(
    val status:String,
    val error: String?,
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
      val timestamp: LocalDateTime,
    val path:String?
)
