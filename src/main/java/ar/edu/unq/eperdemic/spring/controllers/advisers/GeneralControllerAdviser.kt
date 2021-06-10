package ar.edu.unq.eperdemic.spring.controllers.advisers

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GeneralControllerAdviser {
    val LOGGER = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(RuntimeException::class)
    fun handleRunTimeException(e: RuntimeException): ResponseEntity<String> {
        return this.error(INTERNAL_SERVER_ERROR, e)
    }

    protected fun error(status: HttpStatus, e: RuntimeException): ResponseEntity<String> {
        LOGGER.error(e.message, e)
        return ResponseEntity.status(status).body(e.message)
    }
}