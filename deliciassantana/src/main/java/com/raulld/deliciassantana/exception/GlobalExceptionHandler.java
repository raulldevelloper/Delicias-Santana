package com.raulld.deliciassantana.exception;

import com.raulld.deliciassantana.dtos.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 — recurso não encontrado (produto, cliente, pedido, categoria inexistente)
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex, WebRequest request) {
        ErroResponse erro = new ErroResponse(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                extrairCaminho(request)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // 409 — conflito: estoque insuficiente pra atender o pedido
    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ErroResponse> tratarEstoqueInsuficiente(
            EstoqueInsuficienteException ex, WebRequest request) {
        ErroResponse erro = new ErroResponse(
                HttpStatus.CONFLICT.value(),
                "Estoque insuficiente",
                ex.getMessage(),
                extrairCaminho(request)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    // 409 — conflito: email já cadastrado
    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarEmailJaCadastrado(
            EmailJaCadastradoException ex, WebRequest request) {
        ErroResponse erro = new ErroResponse(
                HttpStatus.CONFLICT.value(),
                "Email já cadastrado",
                ex.getMessage(),
                extrairCaminho(request)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    // 400 — tentativa de pular etapa no status do pedido
    @ExceptionHandler(TransicaoStatusInvalidaException.class)
    public ResponseEntity<ErroResponse> tratarTransicaoInvalida(
            TransicaoStatusInvalidaException ex, WebRequest request) {
        ErroResponse erro = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Transição de status inválida",
                ex.getMessage(),
                extrairCaminho(request)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 400 — erros de validação do @Valid nos DTOs (ex: @NotBlank, @Email, @Size)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarErroValidacao(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> camposComErro = new LinkedHashMap<>();
        for (FieldError erroCampo : ex.getBindingResult().getFieldErrors()) {
            camposComErro.put(erroCampo.getField(), erroCampo.getDefaultMessage());
        }

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", java.time.LocalDateTime.now());
        corpo.put("status", HttpStatus.BAD_REQUEST.value());
        corpo.put("erro", "Dados inválidos");
        corpo.put("campos", camposComErro);
        corpo.put("caminho", extrairCaminho(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    // 500 — qualquer coisa inesperada que não mapeamos explicitamente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroGenerico(Exception ex, WebRequest request) {
        ErroResponse erro = new ErroResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente.",
                extrairCaminho(request)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> tratarAcessoNegado(AccessDeniedException ex, WebRequest request) {
        ErroResponse erro = new ErroResponse(
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado",
                ex.getMessage(),
                extrairCaminho(request)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    private String extrairCaminho(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
