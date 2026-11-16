package br.com.fiap.argos.controller;

import br.com.fiap.argos.dto.AlertaResponseDto;
import br.com.fiap.argos.dto.MunicipioResponseDto;
import br.com.fiap.argos.service.ApoioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Municipios e os alertas de cada um. Tabelas de apoio: somente consulta (GET).
// O {cd} da rota e o codigo do IBGE (ex.: 2611101 = Petrolina).
@RestController
@RequestMapping("/municipios")
public class MunicipioController {

    private final ApoioService apoioService;

    public MunicipioController(ApoioService apoioService) {
        this.apoioService = apoioService;
    }

    // GET /municipios -> 200
    @GetMapping
    public List<MunicipioResponseDto> listar() {
        return apoioService.listarMunicipios();
    }

    // GET /municipios/{cd} -> 200 (404 se nao existe)
    @GetMapping("/{cd}")
    public MunicipioResponseDto buscar(@PathVariable Integer cd) {
        return apoioService.buscarMunicipio(cd);
    }

    // GET /municipios/{cd}/alertas -> 200: os avisos que ainda valem hoje (404 se o municipio nao existe)
    @GetMapping("/{cd}/alertas")
    public List<AlertaResponseDto> listarAlertas(@PathVariable Integer cd) {
        return apoioService.listarAlertas(cd);
    }
}
