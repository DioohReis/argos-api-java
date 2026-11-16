package br.com.fiap.argos.dto;

import java.time.LocalDate;

// O que a API devolve sobre um relato de praga
public class OcorrenciaResponseDto {

    private Long id;
    private Long plantioId;
    private Integer pragaId;
    private LocalDate dataOcorrencia;
    private Integer intensidade;
    private String observacao;
    private Boolean confirmada;
    private String pragaNome;

    public OcorrenciaResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlantioId() {
        return plantioId;
    }

    public void setPlantioId(Long plantioId) {
        this.plantioId = plantioId;
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

    public String getPragaNome() {
        return pragaNome;
    }

    public void setPragaNome(String pragaNome) {
        this.pragaNome = pragaNome;
    }
}
