package br.com.fiap.argos.dao;

import br.com.fiap.argos.model.Plantio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Acesso a tb_plantio. Cada plantio pertence a uma propriedade (cd_propriedade e a chave
// estrangeira do relacionamento 1:N propriedade -> plantios).
@Repository
public class PlantioDao {

    private static final String SELECT = "SELECT pl.cd_plantio, pl.cd_propriedade, pl.cd_cultura, pl.nr_area_ha, "
            + "pl.dt_plantio, pl.dt_colheita_prevista, pl.ds_estagio, c.nm_cultura "
            + "FROM tb_plantio pl INNER JOIN tb_cultura c ON c.cd_cultura = pl.cd_cultura";

    private final DataSource dataSource;

    public PlantioDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Cadastra e devolve o id novo, que sai da sequence sq_tb_plantio
    public Long inserir(Plantio plantio) {
        String sql = "INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, "
                + "dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"cd_plantio"})) {
            stmt.setLong(1, plantio.getPropriedadeId());
            preencher(stmt, 2, plantio);
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                chaves.next();
                return chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "cadastrar o plantio");
        }
    }

    public List<Plantio> listar() {
        return consultar(SELECT + " ORDER BY pl.dt_plantio DESC", null);
    }

    // Rota aninhada: os plantios de uma propriedade
    public List<Plantio> listarPorPropriedade(Long propriedadeId) {
        return consultar(SELECT + " WHERE pl.cd_propriedade = ? ORDER BY pl.dt_plantio DESC", propriedadeId);
    }

    public Optional<Plantio> buscarPorId(Long id) {
        List<Plantio> achados = consultar(SELECT + " WHERE pl.cd_plantio = ?", id);
        return achados.isEmpty() ? Optional.empty() : Optional.of(achados.get(0));
    }

    // A propriedade (cd_propriedade) nao muda na atualizacao
    public boolean atualizar(Plantio plantio) {
        String sql = "UPDATE tb_plantio SET cd_cultura = ?, nr_area_ha = ?, dt_plantio = ?, "
                + "dt_colheita_prevista = ?, ds_estagio = ? WHERE cd_plantio = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencher(stmt, 1, plantio);
            stmt.setLong(6, plantio.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "atualizar o plantio");
        }
    }

    public boolean remover(Long id) {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement("DELETE FROM tb_plantio WHERE cd_plantio = ?")) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "remover o plantio");
        }
    }

    // RN05: quantos plantios a propriedade tem? Com algum, ela nao pode ser removida.
    public int contarPorPropriedade(Long propriedadeId) {
        String sql = "SELECT COUNT(*) FROM tb_plantio WHERE cd_propriedade = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, propriedadeId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "contar os plantios da propriedade");
        }
    }

    // RN01: soma a area dos plantios ATIVOS da propriedade (os que ainda nao passaram da data
    // prevista de colheita). Na atualizacao, o proprio plantio fica de fora da soma (ignorarId).
    // NVL troca o nulo por zero quando a propriedade ainda nao tem plantio.
    public BigDecimal somarAreaAtiva(Long propriedadeId, Long ignorarId, LocalDate hoje) {
        String sql = "SELECT NVL(SUM(nr_area_ha), 0) FROM tb_plantio "
                + "WHERE cd_propriedade = ? AND cd_plantio <> ? AND dt_colheita_prevista >= ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, propriedadeId);
            stmt.setLong(2, ignorarId == null ? -1 : ignorarId);
            stmt.setDate(3, Date.valueOf(hoje));
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "somar a área plantada");
        }
    }

    // A mesma cultura, na mesma propriedade, na mesma data, nao entra duas vezes (UNIQUE do banco)
    public boolean existeIgual(Long propriedadeId, Integer culturaId, LocalDate dataPlantio, Long ignorarId) {
        String sql = "SELECT COUNT(*) FROM tb_plantio WHERE cd_propriedade = ? AND cd_cultura = ? "
                + "AND dt_plantio = ? AND cd_plantio <> ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, propriedadeId);
            stmt.setInt(2, culturaId);
            stmt.setDate(3, Date.valueOf(dataPlantio));
            stmt.setLong(4, ignorarId == null ? -1 : ignorarId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "conferir o plantio");
        }
    }

    private List<Plantio> consultar(String sql, Long parametro) {
        List<Plantio> lista = new ArrayList<>();
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
            throw JdbcApoio.traduzir(e, "consultar os plantios");
        }
    }

    // Cinco "?" seguidos, a partir da posicao dada: iguais no INSERT e no UPDATE
    private void preencher(PreparedStatement stmt, int posicao, Plantio plantio) throws SQLException {
        stmt.setInt(posicao, plantio.getCulturaId());
        stmt.setBigDecimal(posicao + 1, plantio.getAreaHa());
        JdbcApoio.definirData(stmt, posicao + 2, plantio.getDataPlantio());
        JdbcApoio.definirData(stmt, posicao + 3, plantio.getDataColheitaPrevista());
        stmt.setString(posicao + 4, plantio.getEstagio());
    }

    private Plantio montar(ResultSet rs) throws SQLException {
        Plantio plantio = new Plantio();
        plantio.setId(rs.getLong("cd_plantio"));
        plantio.setPropriedadeId(rs.getLong("cd_propriedade"));
        plantio.setCulturaId(rs.getInt("cd_cultura"));
        plantio.setAreaHa(rs.getBigDecimal("nr_area_ha"));
        plantio.setDataPlantio(JdbcApoio.lerData(rs, "dt_plantio"));
        plantio.setDataColheitaPrevista(JdbcApoio.lerData(rs, "dt_colheita_prevista"));
        plantio.setEstagio(rs.getString("ds_estagio"));
        plantio.setCulturaNome(rs.getString("nm_cultura"));
        return plantio;
    }
}
