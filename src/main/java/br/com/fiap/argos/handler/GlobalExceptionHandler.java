package br.com.fiap.argos.handler;

import br.com.fiap.argos.dto.CampoErroDto;
import br.com.fiap.argos.dto.ErroResponseDto;
import br.com.fiap.argos.exception.BancoDadosException;
import br.com.fiap.argos.exception.RecursoNaoEncontradoException;
import br.com.fiap.argos.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// Captura as excecoes de todos os controllers e devolve sempre o mesmo JSON de erro,
// sem stack trace e sem detalhe de SQL.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: o DTO de entrada nao passou no Bean Validation (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDto> validacao(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<CampoErroDto> campos = new ArrayList<>();
        e.getBindingResult().getFieldErrors()
                .forEach(erro -> campos.add(new CampoErroDto(erro.getField(), erro.getDefaultMessage())));
        return montar(HttpStatus.BAD_REQUEST, "Dados inválidos", "Confira os campos informados.", req, campos);
    }

    // 400: o JSON veio quebrado ou com um tipo errado (texto onde era numero, data fora do formato)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDto> jsonInvalido(HttpMessageNotReadableException e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "Dados inválidos",
                "O corpo da requisição não é um JSON válido ou tem um campo com o tipo errado.", req, new ArrayList<>());
    }

    // 400: o id da rota nao e um numero (ex.: /produtores/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponseDto> parametroInvalido(MethodArgumentTypeMismatchException e,
                                                            HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "Dados inválidos",
                "O valor informado em '" + e.getName() + "' é inválido.", req, new ArrayList<>());
    }

    // 404: o id pedido nao existe no banco
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDto> naoEncontrado(RecursoNaoEncontradoException e, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, "Não encontrado", e.getMessage(), req, new ArrayList<>());
    }

    // 404: a rota nao existe na API
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponseDto> rotaInexistente(NoResourceFoundException e, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, "Não encontrado", "Esta rota não existe.", req, new ArrayList<>());
    }

    // 405: a rota existe, mas nao aceita este metodo (ex.: DELETE em /saude)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponseDto> metodoNaoSuportado(HttpRequestMethodNotSupportedException e,
                                                             HttpServletRequest req) {
        return montar(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido",
                "Esta rota não aceita o método " + e.getMethod() + ".", req, new ArrayList<>());
    }

    // 409: regra de negocio violada ou remocao de registro com filhos
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponseDto> regraNegocio(RegraNegocioException e, HttpServletRequest req) {
        return montar(HttpStatus.CONFLICT, "Regra de negócio", e.getMessage(), req, new ArrayList<>());
    }

    // 500: falha no banco. O motivo real vai para o log; a resposta e generica.
    @ExceptionHandler(BancoDadosException.class)
    public ResponseEntity<ErroResponseDto> banco(BancoDadosException e, HttpServletRequest req) {
        System.err.println("Erro de banco em " + req.getRequestURI() + ": " + e.getMessage()
                + " | causa: " + e.getCause());
        return erroInterno(req);
    }

    // 500: qualquer erro que ninguem previu
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDto> inesperado(Exception e, HttpServletRequest req) {
        System.err.println("Erro inesperado em " + req.getRequestURI() + ": " + e);
        return erroInterno(req);
    }

    private ResponseEntity<ErroResponseDto> erroInterno(HttpServletRequest req) {
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Não foi possível concluir a operação. Tente de novo em instantes.", req, new ArrayList<>());
    }

    // Monta o JSON padrao: timestamp, status, erro, mensagem, caminho e campos
    private ResponseEntity<ErroResponseDto> montar(HttpStatus status, String erro, String mensagem,
                                                   HttpServletRequest req, List<CampoErroDto> campos) {
        String agora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ErroResponseDto corpo = new ErroResponseDto(agora, status.value(), erro, mensagem, req.getRequestURI(), campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
