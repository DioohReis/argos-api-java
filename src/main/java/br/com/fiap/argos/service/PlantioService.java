package br.com.fiap.argos.service;

import br.com.fiap.argos.dao.CulturaDao;
import br.com.fiap.argos.dao.OcorrenciaDao;
import br.com.fiap.argos.dao.PlantioDao;
import br.com.fiap.argos.dao.PropriedadeDao;
import br.com.fiap.argos.dto.PlantioRequestDto;
import br.com.fiap.argos.dto.PlantioResponseDto;
import br.com.fiap.argos.exception.AreaExcedidaException;
import br.com.fiap.argos.exception.DadosInvalidosException;
import br.com.fiap.argos.exception.RecursoNaoEncontradoException;
import br.com.fiap.argos.exception.RegistroComDependentesException;
import br.com.fiap.argos.exception.RegraNegocioException;
import br.com.fiap.argos.model.Plantio;
import br.com.fiap.argos.model.Propriedade;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Regras do plantio. Aqui mora a regra principal do Argos (RN01): a soma das areas dos plantios
// ativos nao pode passar da area da propriedade. O banco nao consegue garantir isso sozinho,
// porque um CHECK nao enxerga outra tabela.
@Service
public class PlantioService {

    private final PlantioDao plantioDao;
    private final PropriedadeDao propriedadeDao;
    private final CulturaDao culturaDao;
    private final OcorrenciaDao ocorrenciaDao;
    private final ModelMapper mapper;

    public PlantioService(PlantioDao plantioDao, PropriedadeDao propriedadeDao, CulturaDao culturaDao,
                          OcorrenciaDao ocorrenciaDao, ModelMapper mapper) {
        this.plantioDao = plantioDao;
        this.propriedadeDao = propriedadeDao;
        this.culturaDao = culturaDao;
        this.ocorrenciaDao = ocorrenciaDao;
        this.mapper = mapper;
    }

    // Rota aninhada POST /propriedades/{id}/plantios: a propriedade vem da rota, nao do corpo
    public PlantioResponseDto cadastrar(Long propriedadeId, PlantioRequestDto dto) {
        Propriedade propriedade = encontrarPropriedade(propriedadeId);
        conferirRegras(propriedade, dto, null);

        Plantio plantio = mapper.map(dto, Plantio.class);
        plantio.setPropriedadeId(propriedadeId);
        Long id = plantioDao.inserir(plantio);
        return buscar(id);
    }

    public List<PlantioResponseDto> listar() {
        return plantioDao.listar().stream().map(this::paraDto).toList();
    }

    // Rota aninhada GET /propriedades/{id}/plantios
    public List<PlantioResponseDto> listarPorPropriedade(Long propriedadeId) {
        encontrarPropriedade(propriedadeId);
        return plantioDao.listarPorPropriedade(propriedadeId).stream().map(this::paraDto).toList();
    }

    public PlantioResponseDto buscar(Long id) {
        return paraDto(encontrar(id));
    }

    public PlantioResponseDto atualizar(Long id, PlantioRequestDto dto) {
        Plantio atual = encontrar(id);
        Propriedade propriedade = encontrarPropriedade(atual.getPropriedadeId());
        conferirRegras(propriedade, dto, id);

        Plantio plantio = mapper.map(dto, Plantio.class);
        plantio.setId(id);
        plantio.setPropriedadeId(atual.getPropriedadeId());
        plantioDao.atualizar(plantio);
        return buscar(id);
    }

    public void remover(Long id) {
        encontrar(id);
        // RN05: plantio com relato de praga nao pode ser removido
        if (ocorrenciaDao.contarPorPlantio(id) > 0) {
            throw new RegistroComDependentesException(
                    "Este plantio tem relatos de praga. Remova os relatos antes.");
        }
        plantioDao.remover(id);
    }

    // As conferencias valem igual no cadastro e na atualizacao.
    // ignorarId e o proprio plantio na atualizacao (nulo no cadastro).
    private void conferirRegras(Propriedade propriedade, PlantioRequestDto dto, Long ignorarId) {
        if (culturaDao.buscarPorId(dto.getCulturaId()).isEmpty()) {
            throw new RecursoNaoEncontradoException("Cultura " + dto.getCulturaId() + " não encontrada.");
        }
        // Sao duas datas: a validacao de um campo so nao resolve, entao a regra fica aqui (400)
        if (!dto.getDataColheitaPrevista().isAfter(dto.getDataPlantio())) {
            throw new DadosInvalidosException("A colheita prevista tem que ser depois da data do plantio.");
        }
        if (plantioDao.existeIgual(propriedade.getId(), dto.getCulturaId(), dto.getDataPlantio(), ignorarId)) {
            throw new RegraNegocioException(
                    "Esta cultura já foi cadastrada nesta propriedade com a mesma data de plantio.");
        }

        // RN01. Plantio ativo = ainda nao passou da data prevista de colheita.
        // Um plantio que ja terminou nao ocupa mais a terra, entao nao entra na conta.
        LocalDate hoje = LocalDate.now();
        boolean novoEstaAtivo = !dto.getDataColheitaPrevista().isBefore(hoje);
        if (novoEstaAtivo) {
            BigDecimal jaPlantado = plantioDao.somarAreaAtiva(propriedade.getId(), ignorarId, hoje);
            BigDecimal total = jaPlantado.add(dto.getAreaHa());
            if (total.compareTo(propriedade.getAreaHa()) > 0) {
                BigDecimal livre = propriedade.getAreaHa().subtract(jaPlantado);
                throw new AreaExcedidaException("A área plantada passa da área da propriedade. A propriedade tem "
                        + propriedade.getAreaHa() + " ha, já tem " + jaPlantado + " ha plantados e sobram "
                        + livre + " ha.");
            }
        }
    }

    private Plantio encontrar(Long id) {
        return plantioDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plantio " + id + " não encontrado."));
    }

    private Propriedade encontrarPropriedade(Long propriedadeId) {
        return propriedadeDao.buscarPorId(propriedadeId).orElseThrow(
                () -> new RecursoNaoEncontradoException("Propriedade " + propriedadeId + " não encontrada."));
    }

    private PlantioResponseDto paraDto(Plantio plantio) {
        return mapper.map(plantio, PlantioResponseDto.class);
    }
}
