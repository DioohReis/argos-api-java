package br.com.fiap.argos.controller;

import br.com.fiap.argos.dto.PropriedadeRequestDto;
import br.com.fiap.argos.dto.PropriedadeResponseDto;
import br.com.fiap.argos.dto.PlantioRequestDto;
import br.com.fiap.argos.dto.PlantioResponseDto;
import br.com.fiap.argos.service.PropriedadeService;
import br.com.fiap.argos.service.PlantioService;
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

// Rotas REST de propriedade. O controller so traduz HTTP: recebe o DTO, chama o service e devolve
// o status certo. As regras ficam no service e os erros viram JSON no handler global.
@RestController
@RequestMapping("/propriedades")
public class PropriedadeController {

    private final PropriedadeService propriedadeService;
    private final PlantioService plantioService;

    public PropriedadeController(PropriedadeService propriedadeService, PlantioService plantioService) {
        this.propriedadeService = propriedadeService;
        this.plantioService = plantioService;
    }

    // GET /propriedades -> 200
    @GetMapping
    public List<PropriedadeResponseDto> listar() {
        return propriedadeService.listar();
    }

    // GET /propriedades/{id} -> 200 (404 se nao existe)
    @GetMapping("/{id}")
    public PropriedadeResponseDto buscar(@PathVariable Long id) {
        return propriedadeService.buscar(id);
    }

    // PUT /propriedades/{id} -> 200 (400, 404 ou 409)
    @PutMapping("/{id}")
    public PropriedadeResponseDto atualizar(@PathVariable Long id, @Valid @RequestBody PropriedadeRequestDto dto) {
        return propriedadeService.atualizar(id, dto);
    }

    // DELETE /propriedades/{id} -> 204, sem corpo (404 ou 409)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        propriedadeService.remover(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Rotas aninhadas do relacionamento 1:N: os plantios de uma propriedade ----

    // GET /propriedades/{id}/plantios -> 200 (404 se o id da rota nao existe)
    @GetMapping("/{id}/plantios")
    public List<PlantioResponseDto> listarPlantios(@PathVariable Long id) {
        return plantioService.listarPorPropriedade(id);
    }

    // POST /propriedades/{id}/plantios -> 201 + Location
    @PostMapping("/{id}/plantios")
    public ResponseEntity<PlantioResponseDto> cadastrarPlantio(@PathVariable Long id,
            @Valid @RequestBody PlantioRequestDto dto) {
        PlantioResponseDto criado = plantioService.cadastrar(id, dto);
        // O recurso novo mora em /plantios/{id}, e nao dentro da rota aninhada
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/plantios/{id}")
                .buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }
}
