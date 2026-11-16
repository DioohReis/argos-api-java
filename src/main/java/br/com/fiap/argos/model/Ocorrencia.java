package br.com.fiap.argos.model;

import java.time.LocalDate;

// Relato "vi essa praga aqui", feito pelo produtor num plantio (tabela tb_ocorrencia)
public class Ocorrencia {

    private Long id;
    private Long plantioId;
    private Integer pragaId;
    private LocalDate dataOcorrencia;
    private Integer intensidade;
    private String observacao;
    private Boolean confirmada;
    private String pragaNome;

    public Ocorrencia() {
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
