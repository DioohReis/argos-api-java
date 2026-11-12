package br.com.fiap.argos.dto;

// Um campo com problema na validacao (ex.: campo "telefone", mensagem "O telefone e obrigatorio")
public class CampoErroDto {

    private String campo;
    private String mensagem;

    public CampoErroDto() {
    }

    public CampoErroDto(String campo, String mensagem) {
        this.campo = campo;
        this.mensagem = mensagem;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
