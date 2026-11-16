package br.com.fiap.argos.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

// O que a API recebe para cadastrar ou atualizar um produtor
public class ProdutorRequestDto {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 80, message = "O nome pode ter no máximo 80 letras.")
    private String nome;

    // So numeros, com DDD: 10 ou 11 digitos
    @NotBlank(message = "O telefone é obrigatório.")
    @Pattern(regexp = "\\d{10,11}", message = "O telefone deve ter só números, com DDD (10 ou 11 dígitos).")
    private String telefone;

    // Por onde o produtor prefere receber os avisos
    @NotBlank(message = "O canal preferido é obrigatório.")
    @Pattern(regexp = "APP|AUDIO|SMS|WHATSAPP", message = "O canal deve ser APP, AUDIO, SMS ou WHATSAPP.")
    private String canalPreferido;

    @NotBlank(message = "A preferência de leitura é obrigatória.")
    @Pattern(regexp = "OUVIR|LER|AMBOS", message = "A preferência deve ser OUVIR, LER ou AMBOS.")
    private String preferenciaLeitura;

    // Opcional: 1 (usa pouco) a 3 (usa muito)
    @Min(value = 1, message = "O nível de uso do celular vai de 1 a 3.")
    @Max(value = 3, message = "O nível de uso do celular vai de 1 a 3.")
    private Integer nivelUsoCelular;

    // Opcional
    @PositiveOrZero(message = "Os anos de experiência não podem ser negativos.")
    @Max(value = 99, message = "Os anos de experiência vão até 99.")
    private Integer anosExperiencia;

    // Recebe visita de tecnico (assistencia tecnica)?
    @NotNull(message = "Informe se o produtor recebe assistência técnica.")
    private Boolean recebeAter;

    // Opcional: o cadastro foi feito por um ajudante? Sem valor, vale "nao".
    private Boolean assistido;

    // LGPD: sem o aceite do produtor o cadastro nao entra
    @NotNull(message = "O consentimento é obrigatório.")
    @AssertTrue(message = "O produtor precisa aceitar o uso dos dados para se cadastrar.")
    private Boolean consentimento;

    // Codigo do IBGE do municipio (7 digitos)
    @NotNull(message = "O município é obrigatório.")
    @Positive(message = "O código do município é inválido.")
    private Integer municipioId;

    public ProdutorRequestDto() {
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
}
