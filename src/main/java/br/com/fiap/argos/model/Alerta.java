package br.com.fiap.argos.model;

import java.time.LocalDate;

// Aviso de clima ou de praga para um municipio (tabela tb_alerta)
public class Alerta {

    private Long id;
    private Integer municipioId;
    // CHUVA_FORTE, SECA, CALOR, GEADA, ENCHENTE, PRAGA ou VENTO
    private String tipo;

    // 1 (tudo certo) a 4 (grande perigo)
    private Integer severidade;

    private String titulo;
    // O aviso em palavras simples
    private String mensagem;

    // Orgao oficial que deu o aviso
    private String fonte;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    public Alerta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Integer municipioId) {
        this.municipioId = municipioId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getSeveridade() {
        return severidade;
    }

    public void setSeveridade(Integer severidade) {
        this.severidade = severidade;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
}
