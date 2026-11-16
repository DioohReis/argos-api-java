package br.com.fiap.argos.model;

import java.time.LocalDate;

// Produtor rural atendido pelo Argos (tabela tb_produtor)
public class Produtor {

    private Long id;
    private String nome;
    private String telefone;
    private String canalPreferido;
    private String preferenciaLeitura;
    private Integer nivelUsoCelular;
    private Integer anosExperiencia;
    private Boolean recebeAter;
    private Boolean assistido;
    private Boolean consentimento;
    private Integer municipioId;
    private LocalDate dataConsentimento;
    private LocalDate dataCadastro;
    private String municipioNome;
    private String municipioUf;

    public Produtor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCanalPreferido() {
        return canalPreferido;
    }

    public void setCanalPreferido(String canalPreferido) {
        this.canalPreferido = canalPreferido;
    }

    public String getPreferenciaLeitura() {
        return preferenciaLeitura;
    }

    public void setPreferenciaLeitura(String preferenciaLeitura) {
        this.preferenciaLeitura = preferenciaLeitura;
    }

    public Integer getNivelUsoCelular() {
        return nivelUsoCelular;
    }

    public void setNivelUsoCelular(Integer nivelUsoCelular) {
        this.nivelUsoCelular = nivelUsoCelular;
    }

    public Integer getAnosExperiencia() {
        return anosExperiencia;
    }

    public void setAnosExperiencia(Integer anosExperiencia) {
        this.anosExperiencia = anosExperiencia;
    }

    public Boolean getRecebeAter() {
        return recebeAter;
    }

    public void setRecebeAter(Boolean recebeAter) {
        this.recebeAter = recebeAter;
    }

    public Boolean getAssistido() {
        return assistido;
    }

    public void setAssistido(Boolean assistido) {
        this.assistido = assistido;
    }

    public Boolean getConsentimento() {
        return consentimento;
    }

    public void setConsentimento(Boolean consentimento) {
        this.consentimento = consentimento;
    }

    public Integer getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Integer municipioId) {
        this.municipioId = municipioId;
    }

    public LocalDate getDataConsentimento() {
        return dataConsentimento;
    }

    public void setDataConsentimento(LocalDate dataConsentimento) {
        this.dataConsentimento = dataConsentimento;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
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
