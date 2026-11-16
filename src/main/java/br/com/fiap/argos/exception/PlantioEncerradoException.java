package br.com.fiap.argos.exception;

// Regra do dominio (RN06): so da para relatar praga num plantio ativo, que ainda nao passou
// da data prevista de colheita. O handler devolve 409.
public class PlantioEncerradoException extends RegraNegocioException {

    public PlantioEncerradoException(String mensagem) {
        super(mensagem);
    }
}
