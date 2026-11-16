package br.com.fiap.argos.model;

// Praga ou doenca, com sintomas, manejo e fonte oficial (tabela tb_praga)
public class Praga {

    private Integer id;
    private String nome;
    private String nomeCientifico;
    private String sintomas;
    private String manejo;
    // De onde veio a informacao (publicacao da Embrapa)
    private String fonte;

    private String urlImagem;

    public Praga() {
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

    public String getNomeCientifico() {
        return nomeCientifico;
    }

    public void setNomeCientifico(String nomeCientifico) {
        this.nomeCientifico = nomeCientifico;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getManejo() {
        return manejo;
    }

    public void setManejo(String manejo) {
        this.manejo = manejo;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }
}
