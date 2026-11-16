package br.com.fiap.argos.dao;

import br.com.fiap.argos.model.Municipio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Acesso a tb_municipio. E uma tabela de referencia (dados do IBGE): a API so le.
// Nao tem sequence: a chave e o proprio codigo do IBGE.
@Repository
public class MunicipioDao {

    private static final String COLUNAS = "cd_municipio, nm_municipio, sg_uf, ds_regiao, nr_latitude, nr_longitude";

    private final DataSource dataSource;

    // O Spring entrega o DataSource pelo construtor
    public MunicipioDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Municipio> listar() {
        String sql = "SELECT " + COLUNAS + " FROM tb_municipio ORDER BY nm_municipio";
        List<Municipio> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "listar tb_municipio");
        }
    }

    public Optional<Municipio> buscarPorId(Integer id) {
        String sql = "SELECT " + COLUNAS + " FROM tb_municipio WHERE cd_municipio = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(montar(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "buscar em tb_municipio");
        }
    }

    // Copia uma linha do resultado para o objeto do dominio
    private Municipio montar(ResultSet rs) throws SQLException {
        Municipio municipio = new Municipio();
        municipio.setId(rs.getInt("cd_municipio"));
        municipio.setNome(rs.getString("nm_municipio"));
        municipio.setUf(rs.getString("sg_uf"));
        municipio.setRegiao(rs.getString("ds_regiao"));
        municipio.setLatitude(rs.getBigDecimal("nr_latitude"));
        municipio.setLongitude(rs.getBigDecimal("nr_longitude"));
        return municipio;
    }
}
