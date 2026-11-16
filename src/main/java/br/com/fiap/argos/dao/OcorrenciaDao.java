package br.com.fiap.argos.dao;

import br.com.fiap.argos.model.Ocorrencia;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Acesso a tb_ocorrencia. Cada relato pertence a um plantio (cd_plantio e a chave estrangeira
// do relacionamento 1:N plantio -> ocorrencias). A praga e opcional: o produtor pode nao saber qual e.
@Repository
public class OcorrenciaDao {

    // LEFT JOIN: traz o relato mesmo quando cd_praga e nulo (o nome da praga vem nulo)
    private static final String SELECT = "SELECT o.cd_ocorrencia, o.cd_plantio, o.cd_praga, o.dt_ocorrencia, "
            + "o.nv_intensidade, o.ds_observacao, o.fl_confirmada, pg.nm_praga "
            + "FROM tb_ocorrencia o LEFT JOIN tb_praga pg ON pg.cd_praga = o.cd_praga";

    private final DataSource dataSource;

    public OcorrenciaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Cadastra e devolve o id novo, que sai da sequence sq_tb_ocorrencia
    public Long inserir(Ocorrencia ocorrencia) {
        String sql = "INSERT INTO tb_ocorrencia (cd_ocorrencia, cd_plantio, cd_praga, dt_ocorrencia, "
                + "nv_intensidade, ds_observacao, fl_confirmada) VALUES (sq_tb_ocorrencia.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"cd_ocorrencia"})) {
            stmt.setLong(1, ocorrencia.getPlantioId());
            preencher(stmt, 2, ocorrencia);
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                chaves.next();
                return chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "cadastrar o relato de praga");
        }
    }

    public List<Ocorrencia> listar() {
        return consultar(SELECT + " ORDER BY o.dt_ocorrencia DESC", null);
    }

    // Rota aninhada: os relatos de um plantio
    public List<Ocorrencia> listarPorPlantio(Long plantioId) {
        return consultar(SELECT + " WHERE o.cd_plantio = ? ORDER BY o.dt_ocorrencia DESC", plantioId);
    }

    public Optional<Ocorrencia> buscarPorId(Long id) {
        List<Ocorrencia> achadas = consultar(SELECT + " WHERE o.cd_ocorrencia = ?", id);
        return achadas.isEmpty() ? Optional.empty() : Optional.of(achadas.get(0));
    }

    // O plantio (cd_plantio) nao muda na atualizacao
    public boolean atualizar(Ocorrencia ocorrencia) {
        String sql = "UPDATE tb_ocorrencia SET cd_praga = ?, dt_ocorrencia = ?, nv_intensidade = ?, "
                + "ds_observacao = ?, fl_confirmada = ? WHERE cd_ocorrencia = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencher(stmt, 1, ocorrencia);
            stmt.setLong(6, ocorrencia.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "atualizar o relato de praga");
        }
    }

    public boolean remover(Long id) {
        String sql = "DELETE FROM tb_ocorrencia WHERE cd_ocorrencia = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "remover o relato de praga");
        }
    }

    // RN05: quantos relatos o plantio tem? Com algum, ele nao pode ser removido.
    public int contarPorPlantio(Long plantioId) {
        String sql = "SELECT COUNT(*) FROM tb_ocorrencia WHERE cd_plantio = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, plantioId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "contar os relatos do plantio");
        }
    }

    private List<Ocorrencia> consultar(String sql, Long parametro) {
        List<Ocorrencia> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (parametro != null) {
                stmt.setLong(1, parametro);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "consultar os relatos de praga");
        }
    }

    // Cinco "?" seguidos, a partir da posicao dada: iguais no INSERT e no UPDATE
    private void preencher(PreparedStatement stmt, int posicao, Ocorrencia ocorrencia) throws SQLException {
        JdbcApoio.definirInteiro(stmt, posicao, ocorrencia.getPragaId());
        JdbcApoio.definirData(stmt, posicao + 1, ocorrencia.getDataOcorrencia());
        stmt.setInt(posicao + 2, ocorrencia.getIntensidade());
        stmt.setString(posicao + 3, ocorrencia.getObservacao());
        stmt.setString(posicao + 4, JdbcApoio.simNao(ocorrencia.getConfirmada()));
    }

    private Ocorrencia montar(ResultSet rs) throws SQLException {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(rs.getLong("cd_ocorrencia"));
        ocorrencia.setPlantioId(rs.getLong("cd_plantio"));
        ocorrencia.setPragaId(JdbcApoio.lerInteiro(rs, "cd_praga"));
        ocorrencia.setDataOcorrencia(JdbcApoio.lerData(rs, "dt_ocorrencia"));
        ocorrencia.setIntensidade(rs.getInt("nv_intensidade"));
        ocorrencia.setObservacao(rs.getString("ds_observacao"));
        ocorrencia.setConfirmada(JdbcApoio.ehSim(rs.getString("fl_confirmada")));
        ocorrencia.setPragaNome(rs.getString("nm_praga"));
        return ocorrencia;
    }
}
