package br.com.fiap.argos.exception;

// Regra do dominio (RN01): a soma das areas plantadas nao pode passar da area da propriedade.
// O handler devolve 409.
public class AreaExcedidaException extends RegraNegocioException {

    public AreaExcedidaException(String mensagem) {
        super(mensagem);
    }
}
