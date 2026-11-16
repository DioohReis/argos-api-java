package br.com.fiap.argos.controller;

import br.com.fiap.argos.dto.CulturaResponseDto;
import br.com.fiap.argos.dto.PragaResponseDto;
import br.com.fiap.argos.service.ApoioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Culturas e as pragas que atacam cada uma. Tabelas de apoio: somente consulta (GET).
@RestController
@RequestMapping("/culturas")
public class CulturaController {

    private final ApoioService apoioService;

    public CulturaController(ApoioService apoioService) {
        this.apoioService = apoioService;
    }

    // GET /culturas -> 200
    @GetMapping
    public List<CulturaResponseDto> listar() {
        return apoioService.listarCulturas();
    }

    // GET /culturas/{id} -> 200 (404 se nao existe)
    @GetMapping("/{id}")
    public CulturaResponseDto buscar(@PathVariable Integer id) {
        return apoioService.buscarCultura(id);
    }

    // GET /culturas/{id}/pragas -> 200 (404 se a cultura nao existe)
    @GetMapping("/{id}/pragas")
    public List<PragaResponseDto> listarPragas(@PathVariable Integer id) {
        return apoioService.listarPragasDaCultura(id);
    }
}
