package br.com.fiap.argos.controller;

import br.com.fiap.argos.dto.PragaResponseDto;
import br.com.fiap.argos.service.ApoioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Catalogo de pragas, com sintomas, manejo e fonte. Tabela de apoio: somente consulta (GET).
@RestController
@RequestMapping("/pragas")
public class PragaController {

    private final ApoioService apoioService;

    public PragaController(ApoioService apoioService) {
        this.apoioService = apoioService;
    }

    // GET /pragas -> 200
    @GetMapping
    public List<PragaResponseDto> listar() {
        return apoioService.listarPragas();
    }

    // GET /pragas/{id} -> 200 (404 se nao existe)
    @GetMapping("/{id}")
    public PragaResponseDto buscar(@PathVariable Integer id) {
        return apoioService.buscarPraga(id);
    }
}
