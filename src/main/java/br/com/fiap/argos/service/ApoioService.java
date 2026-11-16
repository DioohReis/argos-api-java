package br.com.fiap.argos.service;

import br.com.fiap.argos.dao.AlertaDao;
import br.com.fiap.argos.dao.CulturaDao;
import br.com.fiap.argos.dao.MunicipioDao;
import br.com.fiap.argos.dao.PragaDao;
import br.com.fiap.argos.dto.AlertaResponseDto;
import br.com.fiap.argos.dto.CulturaResponseDto;
import br.com.fiap.argos.dto.MunicipioResponseDto;
import br.com.fiap.argos.dto.PragaResponseDto;
import br.com.fiap.argos.exception.RecursoNaoEncontradoException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Consultas das tabelas de apoio (municipio, cultura, praga e alerta). Sao somente leitura:
// os dados vem de fontes oficiais (IBGE, Embrapa, orgaos de alerta) e entram pelo script do banco.
@Service
public class ApoioService {

    private final MunicipioDao municipioDao;
    private final CulturaDao culturaDao;
    private final PragaDao pragaDao;
    private final AlertaDao alertaDao;
    private final ModelMapper mapper;

    public ApoioService(MunicipioDao municipioDao, CulturaDao culturaDao, PragaDao pragaDao, AlertaDao alertaDao,
                        ModelMapper mapper) {
        this.municipioDao = municipioDao;
        this.culturaDao = culturaDao;
        this.pragaDao = pragaDao;
        this.alertaDao = alertaDao;
        this.mapper = mapper;
    }

    public List<MunicipioResponseDto> listarMunicipios() {
        return municipioDao.listar().stream().map(m -> mapper.map(m, MunicipioResponseDto.class)).toList();
    }

    public MunicipioResponseDto buscarMunicipio(Integer id) {
        return municipioDao.buscarPorId(id).map(m -> mapper.map(m, MunicipioResponseDto.class))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Município " + id + " não encontrado."));
    }

    // Os avisos do municipio que ainda estao valendo hoje
    public List<AlertaResponseDto> listarAlertas(Integer municipioId) {
        buscarMunicipio(municipioId);
        return alertaDao.listarValidosPorMunicipio(municipioId, LocalDate.now()).stream()
                .map(a -> mapper.map(a, AlertaResponseDto.class)).toList();
    }

    public List<CulturaResponseDto> listarCulturas() {
        return culturaDao.listar().stream().map(c -> mapper.map(c, CulturaResponseDto.class)).toList();
    }

    public CulturaResponseDto buscarCultura(Integer id) {
        return culturaDao.buscarPorId(id).map(c -> mapper.map(c, CulturaResponseDto.class))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cultura " + id + " não encontrada."));
    }

    // As pragas que atacam uma cultura
    public List<PragaResponseDto> listarPragasDaCultura(Integer culturaId) {
        buscarCultura(culturaId);
        return pragaDao.listarPorCultura(culturaId).stream().map(p -> mapper.map(p, PragaResponseDto.class)).toList();
    }

    public List<PragaResponseDto> listarPragas() {
        return pragaDao.listar().stream().map(p -> mapper.map(p, PragaResponseDto.class)).toList();
    }

    public PragaResponseDto buscarPraga(Integer id) {
        return pragaDao.buscarPorId(id).map(p -> mapper.map(p, PragaResponseDto.class))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Praga " + id + " não encontrada."));
    }
}
