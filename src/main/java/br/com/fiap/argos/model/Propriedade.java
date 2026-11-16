package br.com.fiap.argos.model;

import java.math.BigDecimal;

// Propriedade rural (sitio) de um produtor (tabela tb_propriedade)
public class Propriedade {

    private Long id;
    private Long produtorId;
    private Integer municipioId;
    private String nome;
    private BigDecimal areaHa;
    private String fonteAgua;
    private String irrigacao;
    private String areaAlagavel;
    private String municipioNome;
    private String municipioUf;

    public Propriedade() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProdutorId() {
        return produtorId;
    }

    public void setProdutorId(Long produtorId) {
        this.produtorId = produtorId;
    }

    public Integer getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Integer municipioId) {
        this.municipioId = municipioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getAreaHa() {
        return areaHa;
    }

    public void setAreaHa(BigDecimal areaHa) {
        this.areaHa = areaHa;
    }

    public String getFonteAgua() {
        return fonteAgua;
    }

    public void setFonteAgua(String fonteAgua) {
        this.fonteAgua = fonteAgua;
    }

    public String getIrrigacao() {
        return irrigacao;
    }

    public void setIrrigacao(String irrigacao) {
        this.irrigacao = irrigacao;
    }

    public String getAreaAlagavel() {
        return areaAlagavel;
    }

    public void setAreaAlagavel(String areaAlagavel) {
        this.areaAlagavel = areaAlagavel;
    }

    public String getMunicipioNome() {
        return municipioNome;
    }

    public void setMunicipioNome(String municipioNome) {
        this.municipioNome = municipioNome;
    }

    public String getMunicipioUf() {
        return municipioUf;
    }

    public void setMunicipioUf(String municipioUf) {
        this.municipioUf = municipioUf;
    }
}
