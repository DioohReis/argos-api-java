package br.com.fiap.argos.model;

import java.math.BigDecimal;

// Municipio atendido pelo Argos (tabela tb_municipio)
public class Municipio {

    // Codigo oficial do IBGE (7 digitos)
    private Integer id;

    private String nome;
    private String uf;
    private String regiao;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public Municipio() {
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

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}
