package br.com.fiap.argos.exception;

// Lancada quando o id pedido nao existe no banco. O handler devolve 404.
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
