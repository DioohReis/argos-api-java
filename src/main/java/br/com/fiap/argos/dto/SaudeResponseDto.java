package br.com.fiap.argos.dto;

// Resposta do GET /saude: diz se a API esta no ar e se consegue falar com o Oracle
public class SaudeResponseDto {

    private String status;
    private String banco;

    public SaudeResponseDto() {
    }

    public SaudeResponseDto(String status, String banco) {
        this.status = status;
        this.banco = banco;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }
}
