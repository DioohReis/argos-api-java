# Argos · API Java

API REST do **Argos**, o copiloto do pequeno produtor rural (FIAP · Global Solution 2026/2 · CitizenTech).
Disciplina: Domain Driven Design Using Java.

## Integrantes

| Nome | RM |
|---|---|
| Gabriel Ricardo | _preencher_ |
| Diogo Guilherme | _preencher_ |
| Pedro Henrique | _preencher_ |
| Gabriel Raso | _preencher_ |
| Ana Magalhães | _preencher_ |

## Links

- API no Render: _preencher_
- Vídeo (YouTube não listado): _preencher_

## Tecnologias

Java 21 · Spring Boot 4 · JDBC (sem JPA) · Oracle · Bean Validation · ModelMapper · Docker

## Como rodar

1. Defina as variáveis de ambiente (os valores nunca vão para o código nem para o GitHub):

| Variável | Para que serve |
|---|---|
| `DB_URL` | Endereço JDBC do Oracle (se faltar, usa o Oracle da FIAP) |
| `DB_USER` | Usuário do Oracle |
| `DB_PASSWORD` | Senha do Oracle |
| `CORS_ORIGINS` | Endereços do front liberados, separados por vírgula |

2. Suba a API:

```bash
./mvnw spring-boot:run
```

3. Teste: `GET http://localhost:8080/saude` deve responder `{"status":"ok","banco":"ok"}`.

## Pacotes

| Pacote | Responsabilidade |
|---|---|
| `model` | Entidades do domínio |
| `dao` | Acesso ao banco com JDBC |
| `dto` | Entrada e saída da API |
| `service` | Regras de negócio |
| `controller` | Rotas REST e status HTTP |
| `exception` | Erros do domínio |
| `handler` | JSON de erro padronizado |
| `config` | CORS e ModelMapper |

## Uso de IA

_preencher com o registro do grupo_
