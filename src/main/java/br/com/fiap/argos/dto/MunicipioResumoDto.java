package br.com.fiap.argos.dto;

// O municipio em poucas palavras, dentro da resposta de produtor e de propriedade
public class MunicipioResumoDto {

    // Codigo do IBGE
    private Integer id;

    private String nome;
    private String uf;

    public MunicipioResumoDto() {
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
}
