package br.com.fiap.argos.exception;

// Cada telefone so pode pertencer a um produtor. O handler devolve 409.
public class TelefoneJaCadastradoException extends RegraNegocioException {

    public TelefoneJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}
