package br.com.fiap.argos.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

// O que a API recebe para cadastrar ou atualizar um plantio
public class PlantioRequestDto {

    @NotNull(message = "A cultura é obrigatória.")
    @Positive(message = "O código da cultura é inválido.")
    private Integer culturaId;

    // Area plantada, em hectares
    @NotNull(message = "A área plantada é obrigatória.")
    @Positive(message = "A área plantada deve ser maior que zero.")
    @Digits(integer = 6, fraction = 2, message = "A área aceita até 6 dígitos e 2 casas decimais.")
    private BigDecimal areaHa;

    // Formato AAAA-MM-DD. Hoje ou no passado.
    @NotNull(message = "A data do plantio é obrigatória.")
    @PastOrPresent(message = "A data do plantio não pode ser no futuro.")
    private LocalDate dataPlantio;

    // Formato AAAA-MM-DD. Tem que ser depois da data do plantio.
    @NotNull(message = "A data prevista da colheita é obrigatória.")
    private LocalDate dataColheitaPrevista;

    @NotBlank(message = "O estágio é obrigatório.")
    @Pattern(regexp = "PREPARO|PLANTIO|CRESCIMENTO|FLORACAO|COLHEITA", message = "O estágio deve ser PREPARO, PLANTIO, CRESCIMENTO, FLORACAO ou COLHEITA.")
    private String estagio;

    public PlantioRequestDto() {
    }

    public Integer getCulturaId() {
        return culturaId;
    }

    public void setCulturaId(Integer culturaId) {
        this.culturaId = culturaId;
    }

    public BigDecimal getAreaHa() {
        return areaHa;
    }

    public void setAreaHa(BigDecimal areaHa) {
        this.areaHa = areaHa;
    }

    public LocalDate getDataPlantio() {
        return dataPlantio;
    }

    public void setDataPlantio(LocalDate dataPlantio) {
        this.dataPlantio = dataPlantio;
    }

    public LocalDate getDataColheitaPrevista() {
        return dataColheitaPrevista;
    }

    public void setDataColheitaPrevista(LocalDate dataColheitaPrevista) {
        this.dataColheitaPrevista = dataColheitaPrevista;
    }

    public String getEstagio() {
        return estagio;
    }

    public void setEstagio(String estagio) {
        this.estagio = estagio;
    }
}
