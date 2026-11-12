package br.com.fiap.argos.controller;

import br.com.fiap.argos.dto.SaudeResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Rota de verificacao: usada para "acordar" a API no Render e conferir a conexao com o Oracle
@RestController
@RequestMapping("/saude")
public class SaudeController {

    private final DataSource dataSource;

    // O Spring entrega o DataSource pronto (montado com DB_URL, DB_USER e DB_PASSWORD)
    public SaudeController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public SaudeResponseDto verificar() {
        return new SaudeResponseDto("ok", bancoResponde() ? "ok" : "indisponivel");
    }

    // Consulta minima no Oracle. O try-with-resources fecha conexao, comando e resultado sozinho.
    private boolean bancoResponde() {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement("SELECT 1 FROM dual");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            // O motivo fica so no log do servidor; a resposta nao mostra detalhe do banco
            System.err.println("Banco indisponivel: " + e.getMessage());
            return false;
        }
    }
}
