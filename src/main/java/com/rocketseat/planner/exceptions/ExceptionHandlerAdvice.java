package com.rocketseat.planner.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({ErrorDataExcpetion.class})
    ResponseEntity<ErrorDetails>errorDataFormatClient(ErrorDataExcpetion errorDataExcpetion){
        return ResponseEntity
                .status(400)
                .body(ErrorDetails.builder()
                        .codigo(HttpStatus.BAD_REQUEST.value())
                        .message(errorDataExcpetion.getMessage())
                        .time(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler({RecursoNaoEncontradoException.class})
    ResponseEntity<ErrorDetails>recursoNaoEncontrado(RecursoNaoEncontradoException recursoNaoEncontradoException){
        return ResponseEntity
                .status(404)
                .body(ErrorDetails.builder()
                        .codigo(HttpStatus.NOT_FOUND.value())
                        .message(recursoNaoEncontradoException.getMessage())
                        .time(LocalDateTime.now())
                        .build());
    }

}
