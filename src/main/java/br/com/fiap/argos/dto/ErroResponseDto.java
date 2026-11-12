package br.com.fiap.argos.dto;

import java.util.ArrayList;
import java.util.List;

// JSON de erro padronizado: todo erro da API (400, 404, 405, 409, 500) sai neste formato
public class ErroResponseDto {

    private String timestamp;
    private int status;
    private String erro;
    private String mensagem;
    private String caminho;
    private List<CampoErroDto> campos = new ArrayList<>();

    public ErroResponseDto() {
    }

    public ErroResponseDto(String timestamp, int status, String erro, String mensagem, String caminho,
                           List<CampoErroDto> campos) {
        this.timestamp = timestamp;
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.caminho = caminho;
        this.campos = campos;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public List<CampoErroDto> getCampos() {
        return campos;
    }

    public void setCampos(List<CampoErroDto> campos) {
        this.campos = campos;
    }
}
