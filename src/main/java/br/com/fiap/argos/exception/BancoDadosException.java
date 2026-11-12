package br.com.fiap.argos.exception;

// Os DAOs embrulham o SQLException nesta excecao. O handler devolve 500 com mensagem
// generica: o detalhe do SQL fica so no log do servidor, nunca na resposta.
public class BancoDadosException extends RuntimeException {

    public BancoDadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
