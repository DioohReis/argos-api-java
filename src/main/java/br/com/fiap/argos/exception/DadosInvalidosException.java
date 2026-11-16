package br.com.fiap.argos.exception;

// Lancada quando os dados chegam no formato certo, mas nao fazem sentido juntos
// (ex.: colheita antes do plantio). O handler devolve 400.
public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
