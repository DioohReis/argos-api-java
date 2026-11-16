package br.com.fiap.argos.model;

import java.math.BigDecimal;

// Cultura agricola acompanhada pelo Argos (tabela tb_cultura)
public class Cultura {

    private Integer id;
    private String nome;
    // Dias do plantio a colheita
    private Integer cicloDias;

    private BigDecimal tempIdealMin;
    private BigDecimal tempIdealMax;
    private Integer aguaCicloMm;

    public Cultura() {
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
