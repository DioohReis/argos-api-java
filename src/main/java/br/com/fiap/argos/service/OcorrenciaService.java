package br.com.fiap.argos.service;

import br.com.fiap.argos.dao.OcorrenciaDao;
import br.com.fiap.argos.dao.PlantioDao;
import br.com.fiap.argos.dao.PragaDao;
import br.com.fiap.argos.dto.OcorrenciaRequestDto;
import br.com.fiap.argos.dto.OcorrenciaResponseDto;
import br.com.fiap.argos.exception.DadosInvalidosException;
import br.com.fiap.argos.exception.PlantioEncerradoException;
import br.com.fiap.argos.exception.RecursoNaoEncontradoException;
import br.com.fiap.argos.model.Ocorrencia;
import br.com.fiap.argos.model.Plantio;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Regras do relato de praga ("vi essa praga aqui")
@Service
public class OcorrenciaService {

    private final OcorrenciaDao ocorrenciaDao;
    private final PlantioDao plantioDao;
    private final PragaDao pragaDao;
    private final ModelMapper mapper;

    public OcorrenciaService(OcorrenciaDao ocorrenciaDao, PlantioDao plantioDao, PragaDao pragaDao,
                             ModelMapper mapper) {
        this.ocorrenciaDao = ocorrenciaDao;
        this.plantioDao = plantioDao;
        this.pragaDao = pragaDao;
        this.mapper = mapper;
    }

    // Rota aninhada POST /plantios/{id}/ocorrencias: o plantio vem da rota, nao do corpo
    public OcorrenciaResponseDto cadastrar(Long plantioId, OcorrenciaRequestDto dto) {
        Plantio plantio = encontrarPlantio(plantioId);
        // RN06: relato so em plantio ativo (que ainda nao passou da data prevista de colheita)
        if (plantio.getDataColheitaPrevista().isBefore(LocalDate.now())) {
            throw new PlantioEncerradoException(
                    "Este plantio já passou da colheita. Só dá para relatar praga em plantio ativo.");
        }

        Ocorrencia ocorrencia = preparar(dto, plantio);
        ocorrencia.setPlantioId(plantioId);
        Long id = ocorrenciaDao.inserir(ocorrencia);
        return buscar(id);
    }

    public List<OcorrenciaResponseDto> listar() {
        return ocorrenciaDao.listar().stream().map(this::paraDto).toList();
    }

    // Rota aninhada GET /plantios/{id}/ocorrencias
    public List<OcorrenciaResponseDto> listarPorPlantio(Long plantioId) {
        encontrarPlantio(plantioId);
        return ocorrenciaDao.listarPorPlantio(plantioId).stream().map(this::paraDto).toList();
    }

    public OcorrenciaResponseDto buscar(Long id) {
        return paraDto(encontrar(id));
    }

    public OcorrenciaResponseDto atualizar(Long id, OcorrenciaRequestDto dto) {
        Ocorrencia atual = encontrar(id);
        Plantio plantio = encontrarPlantio(atual.getPlantioId());

        Ocorrencia ocorrencia = preparar(dto, plantio);
        ocorrencia.setId(id);
        ocorrencia.setPlantioId(atual.getPlantioId());
        ocorrenciaDao.atualizar(ocorrencia);
        return buscar(id);
    }

    public void remover(Long id) {
        encontrar(id);
        ocorrenciaDao.remover(id);
    }

    // Confere a praga e a data e preenche o que e opcional (data de hoje, "nao confirmada")
    private Ocorrencia preparar(OcorrenciaRequestDto dto, Plantio plantio) {
        // A praga e opcional ("nao sei qual e"); mas, se veio, tem que existir
        if (dto.getPragaId() != null && pragaDao.buscarPorId(dto.getPragaId()).isEmpty()) {
            throw new RecursoNaoEncontradoException("Praga " + dto.getPragaId() + " não encontrada.");
        }
        Ocorrencia ocorrencia = mapper.map(dto, Ocorrencia.class);
        if (ocorrencia.getDataOcorrencia() == null) {
            ocorrencia.setDataOcorrencia(LocalDate.now());
        }
        if (ocorrencia.getDataOcorrencia().isBefore(plantio.getDataPlantio())) {
            throw new DadosInvalidosException("A data do relato não pode ser antes da data do plantio.");
        }
        if (ocorrencia.getConfirmada() == null) {
            ocorrencia.setConfirmada(false);
        }
        return ocorrencia;
    }

    private Ocorrencia encontrar(Long id) {
        return ocorrenciaDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relato " + id + " não encontrado."));
    }

    private Plantio encontrarPlantio(Long plantioId) {
        return plantioDao.buscarPorId(plantioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plantio " + plantioId + " não encontrado."));
    }

    private OcorrenciaResponseDto paraDto(Ocorrencia ocorrencia) {
        return mapper.map(ocorrencia, OcorrenciaResponseDto.class);
    }
}
