package br.com.fiap.argos.dao;

import br.com.fiap.argos.model.Produtor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Acesso a tb_produtor com JDBC puro: SQL escrito a mao e PreparedStatement.
// O "?" do PreparedStatement evita SQL injection: o valor nunca e colado no texto do comando.
@Repository
public class ProdutorDao {

    // O JOIN traz o nome e a UF do municipio junto, para a resposta nao mostrar so o codigo
    private static final String SELECT = "SELECT p.cd_produtor, p.nm_produtor, p.nr_telefone, p.ds_canal_preferido, "
            + "p.ds_preferencia_leitura, p.nv_uso_celular, p.nr_anos_experiencia, p.fl_recebe_ater, p.fl_assistido, "
            + "p.fl_consentimento, p.dt_consentimento, p.dt_cadastro, p.cd_municipio, m.nm_municipio, m.sg_uf "
            + "FROM tb_produtor p INNER JOIN tb_municipio m ON m.cd_municipio = p.cd_municipio";

    private final DataSource dataSource;

    // O Spring entrega o DataSource pelo construtor
    public ProdutorDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Cadastra e devolve o id novo, que sai da sequence sq_tb_produtor.
    // dt_cadastro fica de fora do INSERT: o banco preenche sozinho com a data de hoje (DEFAULT SYSDATE).
    public Long inserir(Produtor produtor) {
        String sql = "INSERT INTO tb_produtor (cd_produtor, nm_produtor, nr_telefone, ds_canal_preferido, "
                + "ds_preferencia_leitura, nv_uso_celular, nr_anos_experiencia, fl_recebe_ater, fl_assistido, "
                + "fl_consentimento, dt_consentimento, cd_municipio) "
                + "VALUES (sq_tb_produtor.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // O segundo parametro pede ao Oracle que devolva o valor gerado para a coluna cd_produtor
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"cd_produtor"})) {
            preencher(stmt, produtor);
            JdbcApoio.definirData(stmt, 10, produtor.getDataConsentimento());
            stmt.setInt(11, produtor.getMunicipioId());
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                chaves.next();
                return chaves.getLong(1);
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "cadastrar o produtor");
        }
    }

    public List<Produtor> listar() {
        List<Produtor> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT + " ORDER BY p.nm_produtor");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "listar os produtores");
        }
    }

    public Optional<Produtor> buscarPorId(Long id) {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT + " WHERE p.cd_produtor = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(montar(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "buscar o produtor");
        }
    }

    // Devolve true se alguma linha foi alterada (false = o id nao existe).
    // A data do consentimento e a do cadastro nao mudam na atualizacao.
    public boolean atualizar(Produtor produtor) {
        String sql = "UPDATE tb_produtor SET nm_produtor = ?, nr_telefone = ?, ds_canal_preferido = ?, "
                + "ds_preferencia_leitura = ?, nv_uso_celular = ?, nr_anos_experiencia = ?, fl_recebe_ater = ?, "
                + "fl_assistido = ?, fl_consentimento = ?, cd_municipio = ? WHERE cd_produtor = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencher(stmt, produtor);
            stmt.setInt(10, produtor.getMunicipioId());
            stmt.setLong(11, produtor.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "atualizar o produtor");
        }
    }

    public boolean remover(Long id) {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement("DELETE FROM tb_produtor WHERE cd_produtor = ?")) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "remover o produtor");
        }
    }

    // RN02: o telefone ja pertence a OUTRO produtor? Na atualizacao, o proprio produtor nao conta
    // (ignorarId); no cadastro, ignorarId vem nulo.
    public boolean existeTelefone(String telefone, Long ignorarId) {
        String sql = "SELECT COUNT(*) FROM tb_produtor WHERE nr_telefone = ? AND cd_produtor <> ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, telefone);
            stmt.setLong(2, ignorarId == null ? -1 : ignorarId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw JdbcApoio.traduzir(e, "conferir o telefone");
        }
    }

    // Os nove primeiros "?" sao iguais no INSERT e no UPDATE
    private void preencher(PreparedStatement stmt, Produtor produtor) throws SQLException {
        stmt.setString(1, produtor.getNome());
        stmt.setString(2, produtor.getTelefone());
        stmt.setString(3, produtor.getCanalPreferido());
        stmt.setString(4, produtor.getPreferenciaLeitura());
        JdbcApoio.definirInteiro(stmt, 5, produtor.getNivelUsoCelular());
        JdbcApoio.definirInteiro(stmt, 6, produtor.getAnosExperiencia());
        stmt.setString(7, JdbcApoio.simNao(produtor.getRecebeAter()));
        stmt.setString(8, JdbcApoio.simNao(produtor.getAssistido()));
        stmt.setString(9, JdbcApoio.simNao(produtor.getConsentimento()));
    }

    // Copia uma linha do resultado para o objeto do dominio
    private Produtor montar(ResultSet rs) throws SQLException {
        Produtor produtor = new Produtor();
        produtor.setId(rs.getLong("cd_produtor"));
        produtor.setNome(rs.getString("nm_produtor"));
        produtor.setTelefone(rs.getString("nr_telefone"));
        produtor.setCanalPreferido(rs.getString("ds_canal_preferido"));
        produtor.setPreferenciaLeitura(rs.getString("ds_preferencia_leitura"));
        produtor.setNivelUsoCelular(JdbcApoio.lerInteiro(rs, "nv_uso_celular"));
        produtor.setAnosExperiencia(JdbcApoio.lerInteiro(rs, "nr_anos_experiencia"));
        produtor.setRecebeAter(JdbcApoio.ehSim(rs.getString("fl_recebe_ater")));
        produtor.setAssistido(JdbcApoio.ehSim(rs.getString("fl_assistido")));
        produtor.setConsentimento(JdbcApoio.ehSim(rs.getString("fl_consentimento")));
        produtor.setDataConsentimento(JdbcApoio.lerData(rs, "dt_consentimento"));
        produtor.setDataCadastro(JdbcApoio.lerData(rs, "dt_cadastro"));
        produtor.setMunicipioId(rs.getInt("cd_municipio"));
        produtor.setMunicipioNome(rs.getString("nm_municipio"));
        produtor.setMunicipioUf(rs.getString("sg_uf"));
        return produtor;
    }
}
