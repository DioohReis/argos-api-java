package br.com.fiap.argos.dto;

import java.math.BigDecimal;

// O que a API devolve sobre uma propriedade
public class PropriedadeResponseDto {

    private Long id;
    private Long produtorId;
    private String nome;
    private BigDecimal areaHa;
    private String fonteAgua;
    private String irrigacao;
    private String areaAlagavel;
    private MunicipioResumoDto municipio;

    public PropriedadeResponseDto() {
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

    public MunicipioResumoDto getMunicipio() {
        return municipio;
    }

    public void setMunicipio(MunicipioResumoDto municipio) {
        this.municipio = municipio;
    }
}
