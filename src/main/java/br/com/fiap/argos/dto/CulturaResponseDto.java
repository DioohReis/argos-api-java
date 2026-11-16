package br.com.fiap.argos.dto;

import java.math.BigDecimal;

// O que a API devolve sobre uma cultura
public class CulturaResponseDto {

    private Integer id;
    private String nome;
    private Integer cicloDias;
    private BigDecimal tempIdealMin;
    private BigDecimal tempIdealMax;
    private Integer aguaCicloMm;

    public CulturaResponseDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCicloDias() {
        return cicloDias;
    }

    public void setCicloDias(Integer cicloDias) {
        this.cicloDias = cicloDias;
    }

    public BigDecimal getTempIdealMin() {
        return tempIdealMin;
    }

    public void setTempIdealMin(BigDecimal tempIdealMin) {
        this.tempIdealMin = tempIdealMin;
    }

    public BigDecimal getTempIdealMax() {
        return tempIdealMax;
    }

    public void setTempIdealMax(BigDecimal tempIdealMax) {
        this.tempIdealMax = tempIdealMax;
    }

    public Integer getAguaCicloMm() {
        return aguaCicloMm;
    }

    public void setAguaCicloMm(Integer aguaCicloMm) {
        this.aguaCicloMm = aguaCicloMm;
    }
}
