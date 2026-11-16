package br.com.fiap.argos.exception;

// Nao da para apagar um registro que ainda tem "filhos" (ex.: produtor com propriedade).
// O handler devolve 409.
public class RegistroComDependentesException extends RegraNegocioException {

    public RegistroComDependentesException(String mensagem) {
        super(mensagem);
    }
}
