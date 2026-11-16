package br.com.fiap.argos.dao;

import br.com.fiap.argos.model.Propriedade;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Acesso a tb_propriedade. Cada propriedade pertence a um produtor (cd_produtor e a chave
// estrangeira do relacionamento 1:N produtor -> propriedades).
@Repository
public class PropriedadeDao {

    private static final String SELECT = "SELECT pr.cd_propriedade, pr.cd_produtor, pr.cd_municipio, "
            + "pr.nm_propriedade, pr.nr_area_ha, pr.ds_fonte_agua, pr.ds_irrigacao, pr.fl_area_alagavel, "
            + "m.nm_municipio, m.sg_uf "
            + "FROM tb_propriedade pr INNER JOIN tb_municipio m ON m.cd_municipio = pr.cd_municipio";

    private final DataSource dataSource;

    public PropriedadeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Cadastra e devolve o id novo, que sai da sequence sq_tb_propriedade
    public Long inserir(Propriedade propriedade) {
        String sql = "INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, "
                + "nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) "
                + "VALUES (sq_tb_propriedade.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"cd_propriedade"})) {
            stmt.setLong(1, propriedade.getProdutorId());
            preencher(stmt, 2, propriedade);
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                chaves.next();
                return chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "cadastrar a propriedade");
        }
    }

    public List<Propriedade> listar() {
        return consultar(SELECT + " ORDER BY pr.nm_propriedade", null);
    }

    // Rota aninhada: as propriedades de um produtor
    public List<Propriedade> listarPorProdutor(Long produtorId) {
        return consultar(SELECT + " WHERE pr.cd_produtor = ? ORDER BY pr.nm_propriedade", produtorId);
    }

    public Optional<Propriedade> buscarPorId(Long id) {
        List<Propriedade> achadas = consultar(SELECT + " WHERE pr.cd_propriedade = ?", id);
        return achadas.isEmpty() ? Optional.empty() : Optional.of(achadas.get(0));
    }

    // O dono (cd_produtor) nao muda na atualizacao
    public boolean atualizar(Propriedade propriedade) {
        String sql = "UPDATE tb_propriedade SET cd_municipio = ?, nm_propriedade = ?, nr_area_ha = ?, "
                + "ds_fonte_agua = ?, ds_irrigacao = ?, fl_area_alagavel = ? WHERE cd_propriedade = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencher(stmt, 1, propriedade);
            stmt.setLong(7, propriedade.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "atualizar a propriedade");
        }
    }

    public boolean remover(Long id) {
        String sql = "DELETE FROM tb_propriedade WHERE cd_propriedade = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "remover a propriedade");
        }
    }

    // RN05: quantas propriedades o produtor tem? Com alguma, ele nao pode ser removido.
    public int contarPorProdutor(Long produtorId) {
        String sql = "SELECT COUNT(*) FROM tb_propriedade WHERE cd_produtor = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, produtorId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "contar as propriedades do produtor");
        }
    }

    // Roda um SELECT com zero ou um parametro (o id do filtro) e devolve a lista montada
    private List<Propriedade> consultar(String sql, Long parametro) {
        List<Propriedade> lista = new ArrayList<>();
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
            throw JdbcApoio.traduzir(e, "consultar as propriedades");
        }
    }

    // Seis "?" seguidos, a partir da posicao dada: iguais no INSERT e no UPDATE
    private void preencher(PreparedStatement stmt, int posicao, Propriedade propriedade) throws SQLException {
        stmt.setInt(posicao, propriedade.getMunicipioId());
        stmt.setString(posicao + 1, propriedade.getNome());
        stmt.setBigDecimal(posicao + 2, propriedade.getAreaHa());
        stmt.setString(posicao + 3, propriedade.getFonteAgua());
        stmt.setString(posicao + 4, propriedade.getIrrigacao());
        stmt.setString(posicao + 5, propriedade.getAreaAlagavel());
    }

    private Propriedade montar(ResultSet rs) throws SQLException {
        Propriedade propriedade = new Propriedade();
        propriedade.setId(rs.getLong("cd_propriedade"));
        propriedade.setProdutorId(rs.getLong("cd_produtor"));
        propriedade.setMunicipioId(rs.getInt("cd_municipio"));
        propriedade.setNome(rs.getString("nm_propriedade"));
        propriedade.setAreaHa(rs.getBigDecimal("nr_area_ha"));
        propriedade.setFonteAgua(rs.getString("ds_fonte_agua"));
        propriedade.setIrrigacao(rs.getString("ds_irrigacao"));
        propriedade.setAreaAlagavel(rs.getString("fl_area_alagavel"));
        propriedade.setMunicipioNome(rs.getString("nm_municipio"));
        propriedade.setMunicipioUf(rs.getString("sg_uf"));
        return propriedade;
    }
}
