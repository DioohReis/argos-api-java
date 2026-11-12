package br.com.fiap.argos.exception;

// Lancada quando uma regra do dominio e violada (telefone repetido, registro com filhos,
// area excedida...). O handler devolve 409. As regras especificas herdam desta classe.
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
