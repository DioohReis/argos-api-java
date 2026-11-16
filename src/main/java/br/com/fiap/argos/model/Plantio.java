package br.com.fiap.argos.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// O que foi plantado numa propriedade, quando e quanto (tabela tb_plantio)
public class Plantio {

    private Long id;
    private Long propriedadeId;
    private Integer culturaId;
    private BigDecimal areaHa;
    private LocalDate dataPlantio;
    private LocalDate dataColheitaPrevista;
    private String estagio;
    private String culturaNome;

    public Plantio() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropriedadeId() {
        return propriedadeId;
    }

    public void setPropriedadeId(Long propriedadeId) {
        this.propriedadeId = propriedadeId;
    }

    public Integer getCulturaId() {
        return culturaId;
    }

    public void setCulturaId(Integer culturaId) {
        this.culturaId = culturaId;
    }

    public BigDecimal getAreaHa() {
        return areaHa;
    }

    public void setAreaHa(BigDecimal areaHa) {
        this.areaHa = areaHa;
    }

    public LocalDate getDataPlantio() {
        return dataPlantio;
    }

    public void setDataPlantio(LocalDate dataPlantio) {
        this.dataPlantio = dataPlantio;
    }

    public LocalDate getDataColheitaPrevista() {
        return dataColheitaPrevista;
    }

    public void setDataColheitaPrevista(LocalDate dataColheitaPrevista) {
        this.dataColheitaPrevista = dataColheitaPrevista;
    }

    public String getEstagio() {
        return estagio;
    }

    public void setEstagio(String estagio) {
        this.estagio = estagio;
    }

    public String getCulturaNome() {
        return culturaNome;
    }

    public void setCulturaNome(String culturaNome) {
        this.culturaNome = culturaNome;
    }
}
