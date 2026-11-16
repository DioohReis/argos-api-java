package br.com.fiap.argos.dao;

import br.com.fiap.argos.model.Cultura;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Acesso a tb_cultura. E uma tabela de referencia (dados da Embrapa): a API so le.
@Repository
public class CulturaDao {

    private static final String COLUNAS = "cd_cultura, nm_cultura, nr_ciclo_dias, nr_temp_ideal_min, nr_temp_ideal_max, nr_agua_ciclo_mm";

    private final DataSource dataSource;

    // O Spring entrega o DataSource pelo construtor
    public CulturaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Cultura> listar() {
        String sql = "SELECT " + COLUNAS + " FROM tb_cultura ORDER BY nm_cultura";
        List<Cultura> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "listar tb_cultura");
        }
    }

    public Optional<Cultura> buscarPorId(Integer id) {
        String sql = "SELECT " + COLUNAS + " FROM tb_cultura WHERE cd_cultura = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(montar(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "buscar em tb_cultura");
        }
    }

    // Copia uma linha do resultado para o objeto do dominio
    private Cultura montar(ResultSet rs) throws SQLException {
        Cultura cultura = new Cultura();
        cultura.setId(rs.getInt("cd_cultura"));
        cultura.setNome(rs.getString("nm_cultura"));
        cultura.setCicloDias(JdbcApoio.lerInteiro(rs, "nr_ciclo_dias"));
        cultura.setTempIdealMin(rs.getBigDecimal("nr_temp_ideal_min"));
        cultura.setTempIdealMax(rs.getBigDecimal("nr_temp_ideal_max"));
        cultura.setAguaCicloMm(JdbcApoio.lerInteiro(rs, "nr_agua_ciclo_mm"));
        return cultura;
    }
}
