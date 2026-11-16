package br.com.fiap.argos.controller;

import br.com.fiap.argos.dto.ProdutorRequestDto;
import br.com.fiap.argos.dto.ProdutorResponseDto;
import br.com.fiap.argos.dto.PropriedadeRequestDto;
import br.com.fiap.argos.dto.PropriedadeResponseDto;
import br.com.fiap.argos.service.ProdutorService;
import br.com.fiap.argos.service.PropriedadeService;
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

// Rotas REST de produtor. O controller so traduz HTTP: recebe o DTO, chama o service e devolve
// o status certo. As regras ficam no service e os erros viram JSON no handler global.
@RestController
@RequestMapping("/produtores")
public class ProdutorController {

    private final ProdutorService produtorService;
    private final PropriedadeService propriedadeService;

    public ProdutorController(ProdutorService produtorService, PropriedadeService propriedadeService) {
        this.produtorService = produtorService;
        this.propriedadeService = propriedadeService;
    }

    // POST /produtores -> 201 + Location. O @Valid dispara o Bean Validation do DTO (erro = 400).
    @PostMapping
    public ResponseEntity<ProdutorResponseDto> cadastrar(@Valid @RequestBody ProdutorRequestDto dto) {
        ProdutorResponseDto criado = produtorService.cadastrar(dto);
        // Location: o endereco do recurso que acabou de ser criado
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    // GET /produtores -> 200
    @GetMapping
    public List<ProdutorResponseDto> listar() {
        return produtorService.listar();
    }

    // GET /produtores/{id} -> 200 (404 se nao existe)
    @GetMapping("/{id}")
    public ProdutorResponseDto buscar(@PathVariable Long id) {
        return produtorService.buscar(id);
    }

    // PUT /produtores/{id} -> 200 (400, 404 ou 409)
    @PutMapping("/{id}")
    public ProdutorResponseDto atualizar(@PathVariable Long id, @Valid @RequestBody ProdutorRequestDto dto) {
        return produtorService.atualizar(id, dto);
    }

    // DELETE /produtores/{id} -> 204, sem corpo (404 ou 409)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        produtorService.remover(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Rotas aninhadas do relacionamento 1:N: as propriedades de um produtor ----

    // GET /produtores/{id}/propriedades -> 200 (404 se o id da rota nao existe)
    @GetMapping("/{id}/propriedades")
    public List<PropriedadeResponseDto> listarPropriedades(@PathVariable Long id) {
        return propriedadeService.listarPorProdutor(id);
    }

    // POST /produtores/{id}/propriedades -> 201 + Location
    @PostMapping("/{id}/propriedades")
    public ResponseEntity<PropriedadeResponseDto> cadastrarPropriedade(@PathVariable Long id,
            @Valid @RequestBody PropriedadeRequestDto dto) {
        PropriedadeResponseDto criado = propriedadeService.cadastrar(id, dto);
        // O recurso novo mora em /propriedades/{id}, e nao dentro da rota aninhada
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/propriedades/{id}")
                .buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }
}
