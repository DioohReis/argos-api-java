package br.com.fiap.argos.service;

import br.com.fiap.argos.dao.MunicipioDao;
import br.com.fiap.argos.dao.ProdutorDao;
import br.com.fiap.argos.dao.PropriedadeDao;
import br.com.fiap.argos.dto.MunicipioResumoDto;
import br.com.fiap.argos.dto.ProdutorRequestDto;
import br.com.fiap.argos.dto.ProdutorResponseDto;
import br.com.fiap.argos.exception.RecursoNaoEncontradoException;
import br.com.fiap.argos.exception.RegistroComDependentesException;
import br.com.fiap.argos.exception.TelefoneJaCadastradoException;
import br.com.fiap.argos.model.Produtor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Regras do produtor. O controller so recebe e devolve; o DAO so fala com o banco;
// quem decide o que pode e o que nao pode e esta classe.
@Service
public class ProdutorService {

    private final ProdutorDao produtorDao;
    private final PropriedadeDao propriedadeDao;
    private final MunicipioDao municipioDao;
    private final ModelMapper mapper;

    public ProdutorService(ProdutorDao produtorDao, PropriedadeDao propriedadeDao, MunicipioDao municipioDao,
                           ModelMapper mapper) {
        this.produtorDao = produtorDao;
        this.propriedadeDao = propriedadeDao;
        this.municipioDao = municipioDao;
        this.mapper = mapper;
    }

    public ProdutorResponseDto cadastrar(ProdutorRequestDto dto) {
        conferirMunicipio(dto.getMunicipioId());
        conferirTelefone(dto.getTelefone(), null);

        // O ModelMapper copia os campos de mesmo nome do DTO para o model
        Produtor produtor = mapper.map(dto, Produtor.class);
        if (produtor.getAssistido() == null) {
            produtor.setAssistido(false);
        }
        // RN03: o aceite (consentimento) ja foi exigido na validacao do DTO; aqui fica a data dele
        produtor.setDataConsentimento(LocalDate.now());

        Long id = produtorDao.inserir(produtor);
        return buscar(id);
    }

    public List<ProdutorResponseDto> listar() {
        return produtorDao.listar().stream().map(this::paraDto).toList();
    }

    public ProdutorResponseDto buscar(Long id) {
        return paraDto(encontrar(id));
    }

    public ProdutorResponseDto atualizar(Long id, ProdutorRequestDto dto) {
        Produtor atual = encontrar(id);
        conferirMunicipio(dto.getMunicipioId());
        conferirTelefone(dto.getTelefone(), id);

        Produtor produtor = mapper.map(dto, Produtor.class);
        produtor.setId(id);
        if (produtor.getAssistido() == null) {
            produtor.setAssistido(atual.getAssistido());
        }
        produtorDao.atualizar(produtor);
        return buscar(id);
    }

    public void remover(Long id) {
        encontrar(id);
        // RN05: produtor com propriedade nao pode ser removido
        if (propriedadeDao.contarPorProdutor(id) > 0) {
            throw new RegistroComDependentesException(
                    "Este produtor tem propriedades cadastradas. Remova as propriedades antes.");
        }
        produtorDao.remover(id);
    }

    // Devolve o produtor ou lanca a excecao que vira 404
    private Produtor encontrar(Long id) {
        return produtorDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produtor " + id + " não encontrado."));
    }

    private void conferirMunicipio(Integer municipioId) {
        if (municipioDao.buscarPorId(municipioId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Município " + municipioId + " não encontrado.");
        }
    }

    // RN02: telefone unico por produtor
    private void conferirTelefone(String telefone, Long ignorarId) {
        if (produtorDao.existeTelefone(telefone, ignorarId)) {
            throw new TelefoneJaCadastradoException("Este telefone já está cadastrado para outro produtor.");
        }
    }

    // Model -> DTO de saida. O municipio sai como um objeto: "municipio": { id, nome, uf }
    private ProdutorResponseDto paraDto(Produtor produtor) {
        ProdutorResponseDto dto = mapper.map(produtor, ProdutorResponseDto.class);
        MunicipioResumoDto municipio = new MunicipioResumoDto();
        municipio.setId(produtor.getMunicipioId());
        municipio.setNome(produtor.getMunicipioNome());
        municipio.setUf(produtor.getMunicipioUf());
        dto.setMunicipio(municipio);
        return dto;
    }
}
