package br.com.fiap.argos.controller;

import br.com.fiap.argos.dto.OcorrenciaRequestDto;
import br.com.fiap.argos.dto.OcorrenciaResponseDto;
import br.com.fiap.argos.service.OcorrenciaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Rotas REST de relato de praga. O controller so traduz HTTP: recebe o DTO, chama o service e devolve
// o status certo. As regras ficam no service e os erros viram JSON no handler global.
@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;

    public OcorrenciaController(OcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    // GET /ocorrencias -> 200
    @GetMapping
    public List<OcorrenciaResponseDto> listar() {
        return ocorrenciaService.listar();
    }

    // GET /ocorrencias/{id} -> 200 (404 se nao existe)
    @GetMapping("/{id}")
    public OcorrenciaResponseDto buscar(@PathVariable Long id) {
        return ocorrenciaService.buscar(id);
    }

    // PUT /ocorrencias/{id} -> 200 (400, 404 ou 409)
    @PutMapping("/{id}")
    public OcorrenciaResponseDto atualizar(@PathVariable Long id, @Valid @RequestBody OcorrenciaRequestDto dto) {
        return ocorrenciaService.atualizar(id, dto);
    }

    // DELETE /ocorrencias/{id} -> 204, sem corpo (404 ou 409)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        ocorrenciaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
