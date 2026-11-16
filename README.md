# Argos · API Java

API REST do **Argos**, o copiloto do pequeno produtor rural (FIAP · Global Solution 2026/2 · CitizenTech).
Disciplina: Domain Driven Design Using Java.

O produtor cadastra a roça dele e o que plantou; a API guarda isso no Oracle, devolve os avisos do
município e recebe os relatos de praga. O front do grupo consome estas rotas.

## Integrantes

| Nome | RM | Turma |
|---|---|---|
| Ana Clara Pereira de Magalhães | RM560871 | 1TDSPF |
| Diogo Guilherme de Assis Reis | RM573301 | 1TDSPF |
| Gabriel Razo Dantas | RM572244 | 1TDSPF |
| Gabriel Ricardo Gomes Lima | RM572279 | 1TDSPY |
| Pedro Henrique de Lima Reis | RM569178 | 1TDSPF |

## Links

- API no Render: _preencher_
- Vídeo (YouTube não listado): _preencher_

## Tecnologias

Java 21 · Spring Boot 4 · JDBC puro (sem JPA, Hibernate ou Spring Data) · Oracle · Bean Validation · ModelMapper · Docker

## Como rodar

1. Crie as tabelas: abra `banco/argos_banco.sql` no SQL Developer, conectado no seu usuário do Oracle
   da FIAP, e rode com F5. Na primeira vez os `DROP` dão erro (as tabelas ainda não existem); é normal.

2. Defina as variáveis de ambiente (os valores nunca vão para o código nem para o GitHub):

| Variável | Para que serve |
|---|---|
| `DB_URL` | Endereço JDBC do Oracle (se faltar, usa o Oracle da FIAP) |
| `DB_USER` | Usuário do Oracle |
| `DB_PASSWORD` | Senha do Oracle |
| `CORS_ORIGINS` | Endereços do front liberados, separados por vírgula (se faltar, `http://localhost:5173`) |

3. Suba a API:

```bash
./mvnw spring-boot:run
```

4. Teste: `GET http://localhost:8080/saude` deve responder `{"status":"ok","banco":"ok"}`.

## Domínio

`Produtor` 1:N `Propriedade` 1:N `Plantio` 1:N `Ocorrencia` (o relato de praga).

As quatro têm CRUD completo. `Municipio`, `Cultura`, `Praga` e `Alerta` são tabelas de apoio, com dados
de fontes oficiais (IBGE, Embrapa, órgãos de alerta): a API só consulta.

## Rotas

Sucesso e erros esperados de cada rota. Todo erro sai no mesmo JSON (ver "Erros").

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| POST | `/produtores` | 201 + Location | 400 · 404 (município) · 409 (telefone já cadastrado) |
| GET | `/produtores` | 200 | — |
| GET | `/produtores/{id}` | 200 | 404 |
| PUT | `/produtores/{id}` | 200 | 400 · 404 · 409 |
| DELETE | `/produtores/{id}` | 204 | 404 · 409 (tem propriedades) |
| GET | `/produtores/{id}/propriedades` | 200 | 404 |
| POST | `/produtores/{id}/propriedades` | 201 + Location | 400 · 404 |
| GET | `/propriedades` | 200 | — |
| GET | `/propriedades/{id}` | 200 | 404 |
| PUT | `/propriedades/{id}` | 200 | 400 · 404 · 409 (área menor que a plantada) |
| DELETE | `/propriedades/{id}` | 204 | 404 · 409 (tem plantios) |
| GET | `/propriedades/{id}/plantios` | 200 | 404 |
| POST | `/propriedades/{id}/plantios` | 201 + Location | 400 · 404 · 409 (área excedida) |
| GET | `/plantios` | 200 | — |
| GET | `/plantios/{id}` | 200 | 404 |
| PUT | `/plantios/{id}` | 200 | 400 · 404 · 409 |
| DELETE | `/plantios/{id}` | 204 | 404 · 409 (tem relatos) |
| GET | `/plantios/{id}/ocorrencias` | 200 | 404 |
| POST | `/plantios/{id}/ocorrencias` | 201 + Location | 400 · 404 · 409 (plantio já colhido) |
| GET | `/ocorrencias` | 200 | — |
| GET | `/ocorrencias/{id}` | 200 | 404 |
| PUT | `/ocorrencias/{id}` | 200 | 400 · 404 |
| DELETE | `/ocorrencias/{id}` | 204 | 404 |

Somente consulta:

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| GET | `/municipios` · `/municipios/{cd}` | 200 | 404 |
| GET | `/municipios/{cd}/alertas` | 200 (os avisos que ainda valem hoje) | 404 |
| GET | `/culturas` · `/culturas/{id}` | 200 | 404 |
| GET | `/culturas/{id}/pragas` | 200 | 404 |
| GET | `/pragas` · `/pragas/{id}` | 200 | 404 |
| GET | `/saude` | 200 | — |

Método que a rota não aceita devolve 405. Erro inesperado devolve 500 com mensagem genérica.

## Exemplos

`POST /produtores`

```json
{
  "nome": "Cida",
  "telefone": "87999990000",
  "municipioId": 2611101,
  "canalPreferido": "AUDIO",
  "preferenciaLeitura": "OUVIR",
  "anosExperiencia": 30,
  "recebeAter": false,
  "consentimento": true
}
```

Resposta: `201`, com `Location: /produtores/11`

```json
{
  "id": 11,
  "nome": "Cida",
  "telefone": "87999990000",
  "municipio": { "id": 2611101, "nome": "Petrolina", "uf": "PE" },
  "canalPreferido": "AUDIO",
  "preferenciaLeitura": "OUVIR",
  "nivelUsoCelular": null,
  "anosExperiencia": 30,
  "recebeAter": false,
  "assistido": false,
  "consentimento": true,
  "dataConsentimento": "2026-11-16",
  "dataCadastro": "2026-11-16"
}
```

`POST /produtores/11/propriedades`

```json
{ "nome": "Sítio Boa Esperança", "municipioId": 2611101, "areaHa": 3.0,
  "fonteAgua": "CISTERNA", "irrigacao": "NAO", "areaAlagavel": "N" }
```

`POST /propriedades/12/plantios`

```json
{ "culturaId": 2, "areaHa": 1.5, "dataPlantio": "2026-10-20",
  "dataColheitaPrevista": "2027-01-18", "estagio": "CRESCIMENTO" }
```

`POST /plantios/31/ocorrencias` (a praga pode ser `null`: "não sei qual é")

```json
{ "pragaId": 4, "dataOcorrencia": "2026-11-12", "intensidade": 2, "observacao": "Folhas furadas" }
```

## Erros

Todo erro (400, 404, 405, 409, 500) sai neste formato, sem stack trace e sem SQL:

```json
{
  "timestamp": "2026-11-16T10:00:00",
  "status": 409,
  "erro": "Regra de negócio",
  "mensagem": "A área plantada passa da área da propriedade. A propriedade tem 3 ha, já tem 2.5 ha plantados e sobram 0.5 ha.",
  "caminho": "/propriedades/1/plantios",
  "campos": []
}
```

No 400 de validação, `campos` lista cada campo com problema e a mensagem para o usuário.

## Regras de negócio

| Regra | O que diz | Onde está | Status |
|---|---|---|---|
| RN01 | A soma das áreas dos plantios ativos não passa da área da propriedade. É a regra principal. | `PlantioService` · `AreaExcedidaException` | 409 |
| RN02 | Telefone único por produtor. | `ProdutorService` · `TelefoneJaCadastradoException` | 409 |
| RN03 | Sem consentimento não há cadastro. | `ProdutorRequestDto` (`@AssertTrue`) | 400 |
| RN05 | Não se remove registro com filhos. | services · `RegistroComDependentesException` | 409 |
| RN06 | Relato de praga só em plantio ativo. | `OcorrenciaService` · `PlantioEncerradoException` | 409 |

Plantio ativo é o que ainda não passou da data prevista de colheita.

## Pacotes

| Pacote | Responsabilidade |
|---|---|
| `model` | Entidades do domínio |
| `dao` | Acesso ao banco com JDBC: um DAO por entidade, DataSource pelo construtor e PreparedStatement |
| `dto` | Entrada (com Bean Validation) e saída da API |
| `service` | Regras de negócio |
| `controller` | Rotas REST e status HTTP |
| `exception` | Erros do domínio |
| `handler` | JSON de erro padronizado (`@RestControllerAdvice`) |
| `config` | CORS e ModelMapper |

O caminho de uma requisição: controller (recebe o DTO e valida com `@Valid`) → service (confere as
regras) → DAO (SQL com PreparedStatement) → Oracle. Na volta, o model vira DTO de saída pelo ModelMapper.

## Deploy

Dockerfile multi-stage, publicado no Render como Web Service. As variáveis `DB_URL`, `DB_USER`,
`DB_PASSWORD` e `CORS_ORIGINS` são definidas no painel do Render, nunca no código.

## Uso de IA

_preencher com o registro do grupo_
