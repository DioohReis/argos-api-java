package br.com.fiap.argos.dao;

import br.com.fiap.argos.exception.BancoDadosException;
import br.com.fiap.argos.exception.DadosInvalidosException;
import br.com.fiap.argos.exception.RecursoNaoEncontradoException;
import br.com.fiap.argos.exception.RegistroComDependentesException;
import br.com.fiap.argos.exception.RegraNegocioException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

// Pequenas ajudas usadas por todos os DAOs: conversoes entre Java e Oracle
// e a traducao dos erros do banco para as excecoes da API.
final class JdbcApoio {

    private JdbcApoio() {
    }

    // No banco as flags sao CHAR(1): 'S' ou 'N'. No Java sao Boolean.
    static String simNao(Boolean valor) {
        if (valor == null) {
            return null;
        }
        return valor ? "S" : "N";
    }

    static Boolean ehSim(String valor) {
        return valor == null ? null : "S".equals(valor);
    }

    // setInt nao aceita nulo: para coluna opcional e preciso avisar o tipo com setNull
    static void definirInteiro(PreparedStatement stmt, int posicao, Integer valor) throws SQLException {
        if (valor == null) {
            stmt.setNull(posicao, Types.INTEGER);
        } else {
            stmt.setInt(posicao, valor);
        }
    }

    static void definirData(PreparedStatement stmt, int posicao, LocalDate valor) throws SQLException {
        if (valor == null) {
            stmt.setNull(posicao, Types.DATE);
        } else {
            stmt.setDate(posicao, Date.valueOf(valor));
        }
    }

    // getInt devolve 0 quando a coluna e nula: wasNull() diz se era nula de verdade
    static Integer lerInteiro(ResultSet rs, String coluna) throws SQLException {
        int valor = rs.getInt(coluna);
        return rs.wasNull() ? null : valor;
    }

    static LocalDate lerData(ResultSet rs, String coluna) throws SQLException {
        Date valor = rs.getDate(coluna);
        return valor == null ? null : valor.toLocalDate();
    }

    // Traduz o erro do Oracle para a excecao certa da API. O texto do SQL nunca vai para a resposta.
    //   ORA-00001 (valor repetido em coluna UNIQUE)  -> 409
    //   ORA-02290 (CHECK violado)                    -> 400
    //   ORA-02291 (FK aponta para quem nao existe)   -> 404
    //   ORA-02292 (apagar quem tem filhos)           -> 409
    //   qualquer outro                               -> 500
    static RuntimeException traduzir(SQLException erro, String operacao) {
        switch (erro.getErrorCode()) {
            case 1:
                return new RegraNegocioException("Já existe um registro com esses dados.");
            case 2290:
                return new DadosInvalidosException("Algum valor informado não é aceito.");
            case 2291:
                return new RecursoNaoEncontradoException("Um dos registros informados não existe.");
            case 2292:
                return new RegistroComDependentesException(
                        "Não dá para apagar: existem registros ligados a este.");
            default:
                return new BancoDadosException("Falha ao " + operacao, erro);
        }
    }
}
