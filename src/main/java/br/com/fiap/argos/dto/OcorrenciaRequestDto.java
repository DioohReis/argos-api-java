package br.com.fiap.argos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

// O que a API recebe para cadastrar ou atualizar um relato de praga
public class OcorrenciaRequestDto {

    // Opcional: fica vazio quando o produtor nao sabe qual praga e
    @Positive(message = "O código da praga é inválido.")
    private Integer pragaId;

    // Opcional (formato AAAA-MM-DD): sem data, vale o dia de hoje
    @PastOrPresent(message = "A data do relato não pode ser no futuro.")
    private LocalDate dataOcorrencia;

    // 1 (pouco), 2 (medio) ou 3 (muito)
    @NotNull(message = "A intensidade é obrigatória.")
    @Min(value = 1, message = "A intensidade vai de 1 a 3.")
    @Max(value = 3, message = "A intensidade vai de 1 a 3.")
    private Integer intensidade;

    // Opcional
    @Size(max = 300, message = "A observação pode ter no máximo 300 letras.")
    private String observacao;

    // Opcional: um tecnico ja confirmou o relato? Sem valor, vale "nao".
    private Boolean confirmada;

    public OcorrenciaRequestDto() {
    }

    public Integer getPragaId() {
        return pragaId;
    }

    public void setPragaId(Integer pragaId) {
        this.pragaId = pragaId;
    }

    public LocalDate getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(LocalDate dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public Integer getIntensidade() {
        return intensidade;
    }

    public void setIntensidade(Integer intensidade) {
        this.intensidade = intensidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Boolean getConfirmada() {
        return confirmada;
    }

    public void setConfirmada(Boolean confirmada) {
        this.confirmada = confirmada;
    }
}
