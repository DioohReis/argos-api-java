package br.com.fiap.argos.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

// O que a API recebe para cadastrar ou atualizar uma propriedade
public class PropriedadeRequestDto {

    // Codigo do IBGE do municipio (7 digitos)
    @NotNull(message = "O município é obrigatório.")
    @Positive(message = "O código do município é inválido.")
    private Integer municipioId;

    @NotBlank(message = "O nome da propriedade é obrigatório.")
    @Size(max = 80, message = "O nome pode ter no máximo 80 letras.")
    private String nome;

    // Area total, em hectares
    @NotNull(message = "A área é obrigatória.")
    @Positive(message = "A área deve ser maior que zero.")
    @Digits(integer = 6, fraction = 2, message = "A área aceita até 6 dígitos e 2 casas decimais.")
    private BigDecimal areaHa;

    // Opcional
    @Pattern(regexp = "CHUVA|POCO|RIO|ACUDE|CISTERNA|REDE", message = "A fonte de água deve ser CHUVA, POCO, RIO, ACUDE, CISTERNA ou REDE.")
    private String fonteAgua;

    // Opcional
    @Pattern(regexp = "NAO|GOTEJAMENTO|ASPERSAO|OUTRA", message = "A irrigação deve ser NAO, GOTEJAMENTO, ASPERSAO ou OUTRA.")
    private String irrigacao;

    // Opcional: a area costuma alagar? "S" ou "N"
    @Pattern(regexp = "S|N", message = "Informe S ou N para a área alagável.")
    private String areaAlagavel;

    public PropriedadeRequestDto() {
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
}
