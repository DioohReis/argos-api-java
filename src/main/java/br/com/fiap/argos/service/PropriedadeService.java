package br.com.fiap.argos.service;

import br.com.fiap.argos.dao.MunicipioDao;
import br.com.fiap.argos.dao.PlantioDao;
import br.com.fiap.argos.dao.ProdutorDao;
import br.com.fiap.argos.dao.PropriedadeDao;
import br.com.fiap.argos.dto.MunicipioResumoDto;
import br.com.fiap.argos.dto.PropriedadeRequestDto;
import br.com.fiap.argos.dto.PropriedadeResponseDto;
import br.com.fiap.argos.exception.AreaExcedidaException;
import br.com.fiap.argos.exception.RecursoNaoEncontradoException;
import br.com.fiap.argos.exception.RegistroComDependentesException;
import br.com.fiap.argos.model.Propriedade;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Regras da propriedade (o sitio do produtor)
@Service
public class PropriedadeService {

    private final PropriedadeDao propriedadeDao;
    private final ProdutorDao produtorDao;
    private final PlantioDao plantioDao;
    private final MunicipioDao municipioDao;
    private final ModelMapper mapper;

    public PropriedadeService(PropriedadeDao propriedadeDao, ProdutorDao produtorDao, PlantioDao plantioDao,
                              MunicipioDao municipioDao, ModelMapper mapper) {
        this.propriedadeDao = propriedadeDao;
        this.produtorDao = produtorDao;
        this.plantioDao = plantioDao;
        this.municipioDao = municipioDao;
        this.mapper = mapper;
    }

    // Rota aninhada POST /produtores/{id}/propriedades: o dono vem da rota, nao do corpo
    public PropriedadeResponseDto cadastrar(Long produtorId, PropriedadeRequestDto dto) {
        conferirProdutor(produtorId);
        conferirMunicipio(dto.getMunicipioId());

        Propriedade propriedade = mapper.map(dto, Propriedade.class);
        propriedade.setProdutorId(produtorId);
        Long id = propriedadeDao.inserir(propriedade);
        return buscar(id);
    }

    public List<PropriedadeResponseDto> listar() {
        return propriedadeDao.listar().stream().map(this::paraDto).toList();
    }

    // Rota aninhada GET /produtores/{id}/propriedades
    public List<PropriedadeResponseDto> listarPorProdutor(Long produtorId) {
        conferirProdutor(produtorId);
        return propriedadeDao.listarPorProdutor(produtorId).stream().map(this::paraDto).toList();
    }

    public PropriedadeResponseDto buscar(Long id) {
        return paraDto(encontrar(id));
    }

    public PropriedadeResponseDto atualizar(Long id, PropriedadeRequestDto dto) {
        Propriedade atual = encontrar(id);
        conferirMunicipio(dto.getMunicipioId());

        // RN01 vista pelo outro lado: a area nova nao pode ficar menor que o que ja esta plantado
        BigDecimal plantado = plantioDao.somarAreaAtiva(id, null, LocalDate.now());
        if (dto.getAreaHa().compareTo(plantado) < 0) {
            throw new AreaExcedidaException("A propriedade já tem " + plantado
                    + " ha plantados. A área não pode ficar menor que isso.");
        }

        Propriedade propriedade = mapper.map(dto, Propriedade.class);
        propriedade.setId(id);
        propriedade.setProdutorId(atual.getProdutorId());
        propriedadeDao.atualizar(propriedade);
        return buscar(id);
    }

    public void remover(Long id) {
        encontrar(id);
        // RN05: propriedade com plantio nao pode ser removida
        if (plantioDao.contarPorPropriedade(id) > 0) {
            throw new RegistroComDependentesException(
                    "Esta propriedade tem plantios cadastrados. Remova os plantios antes.");
        }
        propriedadeDao.remover(id);
    }

    private Propriedade encontrar(Long id) {
        return propriedadeDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Propriedade " + id + " não encontrada."));
    }

    private void conferirProdutor(Long produtorId) {
        if (produtorDao.buscarPorId(produtorId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Produtor " + produtorId + " não encontrado.");
        }
    }

    private void conferirMunicipio(Integer municipioId) {
        if (municipioDao.buscarPorId(municipioId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Município " + municipioId + " não encontrado.");
        }
    }

    private PropriedadeResponseDto paraDto(Propriedade propriedade) {
        PropriedadeResponseDto dto = mapper.map(propriedade, PropriedadeResponseDto.class);
        MunicipioResumoDto municipio = new MunicipioResumoDto();
        municipio.setId(propriedade.getMunicipioId());
        municipio.setNome(propriedade.getMunicipioNome());
        municipio.setUf(propriedade.getMunicipioUf());
        dto.setMunicipio(municipio);
        return dto;
    }
}
