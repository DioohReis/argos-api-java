package br.com.fiap.argos.dao;

import br.com.fiap.argos.model.Alerta;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Acesso a tb_alerta. E uma tabela de apoio: a API so le (os avisos chegam de orgaos oficiais).
@Repository
public class AlertaDao {

    private final DataSource dataSource;

    public AlertaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Os avisos do municipio que ainda valem na data informada, do mais grave para o mais leve
    public List<Alerta> listarValidosPorMunicipio(Integer municipioId, LocalDate hoje) {
        String sql = "SELECT cd_alerta, cd_municipio, tp_alerta, nv_severidade, ds_titulo, ds_mensagem_simples, "
                + "ds_fonte, dt_inicio, dt_fim FROM tb_alerta "
                + "WHERE cd_municipio = ? AND dt_fim >= ? ORDER BY nv_severidade DESC, dt_inicio";
        List<Alerta> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, municipioId);
            stmt.setDate(2, Date.valueOf(hoje));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Alerta alerta = new Alerta();
                    alerta.setId(rs.getLong("cd_alerta"));
                    alerta.setMunicipioId(rs.getInt("cd_municipio"));
                    alerta.setTipo(rs.getString("tp_alerta"));
                    alerta.setSeveridade(rs.getInt("nv_severidade"));
                    alerta.setTitulo(rs.getString("ds_titulo"));
                    alerta.setMensagem(rs.getString("ds_mensagem_simples"));
                    alerta.setFonte(rs.getString("ds_fonte"));
                    alerta.setDataInicio(JdbcApoio.lerData(rs, "dt_inicio"));
                    alerta.setDataFim(JdbcApoio.lerData(rs, "dt_fim"));
                    lista.add(alerta);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "listar os alertas do município");
        }
    }
}
