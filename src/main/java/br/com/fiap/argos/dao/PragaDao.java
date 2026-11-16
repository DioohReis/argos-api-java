package br.com.fiap.argos.dao;

import br.com.fiap.argos.model.Praga;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Acesso a tb_praga. E uma tabela de referencia (dados da Embrapa): a API so le.
@Repository
public class PragaDao {

    private static final String COLUNAS = "cd_praga, nm_praga, nm_cientifico, ds_sintomas, ds_manejo, ds_fonte, ds_url_imagem";

    private final DataSource dataSource;

    // O Spring entrega o DataSource pelo construtor
    public PragaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Praga> listar() {
        String sql = "SELECT " + COLUNAS + " FROM tb_praga ORDER BY nm_praga";
        List<Praga> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "listar tb_praga");
        }
    }

    public Optional<Praga> buscarPorId(Integer id) {
        String sql = "SELECT " + COLUNAS + " FROM tb_praga WHERE cd_praga = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(montar(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "buscar em tb_praga");
        }
    }

    // As pragas que atacam uma cultura. A ligacao fica na tabela associativa tb_cultura_praga,
    // que tem chave composta (cd_cultura, cd_praga).
    public List<Praga> listarPorCultura(Integer culturaId) {
        String sql = "SELECT " + COLUNAS + " FROM tb_praga WHERE cd_praga IN "
                + "(SELECT cd_praga FROM tb_cultura_praga WHERE cd_cultura = ?) ORDER BY nm_praga";
        List<Praga> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, culturaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "listar pragas da cultura");
        }
    }

    // Copia uma linha do resultado para o objeto do dominio
    private Praga montar(ResultSet rs) throws SQLException {
        Praga praga = new Praga();
        praga.setId(rs.getInt("cd_praga"));
        praga.setNome(rs.getString("nm_praga"));
        praga.setNomeCientifico(rs.getString("nm_cientifico"));
        praga.setSintomas(rs.getString("ds_sintomas"));
        praga.setManejo(rs.getString("ds_manejo"));
        praga.setFonte(rs.getString("ds_fonte"));
        praga.setUrlImagem(rs.getString("ds_url_imagem"));
        return praga;
    }
}
