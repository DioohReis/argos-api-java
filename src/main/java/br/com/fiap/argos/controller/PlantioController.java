package br.com.fiap.argos.controller;

import br.com.fiap.argos.dto.PlantioRequestDto;
import br.com.fiap.argos.dto.PlantioResponseDto;
import br.com.fiap.argos.dto.OcorrenciaRequestDto;
import br.com.fiap.argos.dto.OcorrenciaResponseDto;
import br.com.fiap.argos.service.PlantioService;
import br.com.fiap.argos.service.OcorrenciaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// Rotas REST de plantio. O controller so traduz HTTP: recebe o DTO, chama o service e devolve
// o status certo. As regras ficam no service e os erros viram JSON no handler global.
@RestController
@RequestMapping("/plantios")
public class PlantioController {

    private final PlantioService plantioService;
    private final OcorrenciaService ocorrenciaService;

    public PlantioController(PlantioService plantioService, OcorrenciaService ocorrenciaService) {
        this.plantioService = plantioService;
        this.ocorrenciaService = ocorrenciaService;
    }

    // GET /plantios -> 200
    @GetMapping
    public List<PlantioResponseDto> listar() {
        return plantioService.listar();
    }

    // GET /plantios/{id} -> 200 (404 se nao existe)
    @GetMapping("/{id}")
    public PlantioResponseDto buscar(@PathVariable Long id) {
        return plantioService.buscar(id);
    }

    // PUT /plantios/{id} -> 200 (400, 404 ou 409)
    @PutMapping("/{id}")
    public PlantioResponseDto atualizar(@PathVariable Long id, @Valid @RequestBody PlantioRequestDto dto) {
        return plantioService.atualizar(id, dto);
    }

    // DELETE /plantios/{id} -> 204, sem corpo (404 ou 409)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        plantioService.remover(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Rotas aninhadas do relacionamento 1:N: os relatos de praga de um plantio ----

    // GET /plantios/{id}/ocorrencias -> 200 (404 se o id da rota nao existe)
    @GetMapping("/{id}/ocorrencias")
    public List<OcorrenciaResponseDto> listarOcorrencias(@PathVariable Long id) {
        return ocorrenciaService.listarPorPlantio(id);
    }

    // POST /plantios/{id}/ocorrencias -> 201 + Location
    @PostMapping("/{id}/ocorrencias")
    public ResponseEntity<OcorrenciaResponseDto> cadastrarOcorrencia(@PathVariable Long id,
            @Valid @RequestBody OcorrenciaRequestDto dto) {
        OcorrenciaResponseDto criado = ocorrenciaService.cadastrar(id, dto);
        // O recurso novo mora em /ocorrencias/{id}, e nao dentro da rota aninhada
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/ocorrencias/{id}")
                .buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }
}
