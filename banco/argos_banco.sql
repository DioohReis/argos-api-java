-- ARGOS - Copiloto do Pequeno Produtor
-- Global Solution 2026/2 - FIAP - Building Relational Database
-- Turmas: 1TDSPY (Gabriel Ricardo) e 1TDSPF (demais integrantes)
-- Integrantes:
--   ANA CLARA PEREIRA DE MAGALHAES - RM560871 - 1TDSPF
--   DIOGO GUILHERME DE ASSIS REIS  - RM573301 - 1TDSPF
--   GABRIEL RAZO DANTAS            - RM572244 - 1TDSPF
--   GABRIEL RICARDO GOMES LIMA     - RM572279 - 1TDSPY
--   PEDRO HENRIQUE DE LIMA REIS    - RM569178 - 1TDSPF
-- Banco: Oracle 19c - oracle.fiap.com.br:1521:ORCL
-- Ordem: 0) limpeza  1) tabelas  2) INSERTs  3) 10 consultas  4) UPDATE  5) DELETE

-- =====================================================================
-- 0) LIMPEZA - apaga SO as tabelas do Argos (filhas antes das maes)
-- =====================================================================
DROP TABLE tb_alerta        CASCADE CONSTRAINTS;
DROP TABLE tb_ocorrencia    CASCADE CONSTRAINTS;
DROP TABLE tb_cultura_praga CASCADE CONSTRAINTS;
DROP TABLE tb_plantio       CASCADE CONSTRAINTS;
DROP TABLE tb_praga         CASCADE CONSTRAINTS;
DROP TABLE tb_cultura       CASCADE CONSTRAINTS;
DROP TABLE tb_propriedade   CASCADE CONSTRAINTS;
DROP TABLE tb_produtor      CASCADE CONSTRAINTS;
DROP TABLE tb_municipio     CASCADE CONSTRAINTS;

DROP SEQUENCE sq_tb_alerta;
DROP SEQUENCE sq_tb_ocorrencia;
DROP SEQUENCE sq_tb_plantio;
DROP SEQUENCE sq_tb_cultura;
DROP SEQUENCE sq_tb_praga;
DROP SEQUENCE sq_tb_propriedade;
DROP SEQUENCE sq_tb_produtor;


-- =====================================================================
-- 1) TABELAS (maes antes das filhas)
-- =====================================================================

-- ---------------------------------------------------------------------
-- tb_municipio: municipios atendidos (dado real do IBGE)
-- Sem sequence: a PK e o codigo oficial do IBGE (7 digitos).
-- ---------------------------------------------------------------------
CREATE TABLE tb_municipio (
  cd_municipio  NUMBER(7)     NOT NULL,
  nm_municipio  VARCHAR2(80)  NOT NULL,
  sg_uf         CHAR(2)       NOT NULL,   
  ds_regiao     VARCHAR2(12)  NOT NULL,   
  nr_latitude   NUMBER(8,5),              
  nr_longitude  NUMBER(8,5),              
  CONSTRAINT tb_municipio_cd_municipio_pk PRIMARY KEY (cd_municipio),
  CONSTRAINT tb_municipio_ds_regiao_ck    CHECK (ds_regiao IN
    ('NORTE','NORDESTE','CENTRO-OESTE','SUDESTE','SUL'))
);
COMMENT ON TABLE tb_municipio IS 'Municipios atendidos pelo Argos (codigo oficial do IBGE)';


-- ---------------------------------------------------------------------
-- tb_produtor: quem usa o Argos (filha de tb_municipio)
-- ---------------------------------------------------------------------
CREATE SEQUENCE sq_tb_produtor START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_produtor (
  cd_produtor            NUMBER(10)    NOT NULL,
  nm_produtor            VARCHAR2(80)  NOT NULL,
  nr_telefone            VARCHAR2(11)  NOT NULL,  
  ds_canal_preferido     VARCHAR2(10)  NOT NULL,
  ds_preferencia_leitura VARCHAR2(6)   NOT NULL, 
  nv_uso_celular         NUMBER(1),                -- 1 pouco ... 3 muito
  nr_anos_experiencia    NUMBER(2),                
  fl_recebe_ater         CHAR(1)       NOT NULL,   -- recebe visita de tecnico?
  fl_assistido           CHAR(1)       NOT NULL,   -- cadastro feito por ajudante?
  fl_consentimento       CHAR(1)       NOT NULL,   -- LGPD (RN03)
  dt_consentimento       DATE          NOT NULL,
  dt_cadastro            DATE          DEFAULT SYSDATE NOT NULL,
  cd_municipio           NUMBER(7)     NOT NULL,   
  CONSTRAINT tb_produtor_cd_produtor_pk            PRIMARY KEY (cd_produtor),
  CONSTRAINT tb_produtor_nr_telefone_un            UNIQUE (nr_telefone),
  CONSTRAINT tb_produtor_ds_canal_preferido_ck     CHECK (ds_canal_preferido IN ('APP','AUDIO','SMS','WHATSAPP')),
  CONSTRAINT tb_produtor_ds_preferencia_leitura_ck CHECK (ds_preferencia_leitura IN ('OUVIR','LER','AMBOS')),
  CONSTRAINT tb_produtor_nv_uso_celular_ck         CHECK (nv_uso_celular BETWEEN 1 AND 3),
  CONSTRAINT tb_produtor_nr_anos_experiencia_ck    CHECK (nr_anos_experiencia >= 0),
  CONSTRAINT tb_produtor_fl_recebe_ater_ck         CHECK (fl_recebe_ater IN ('S','N')),
  CONSTRAINT tb_produtor_fl_assistido_ck           CHECK (fl_assistido IN ('S','N')),
  CONSTRAINT tb_produtor_fl_consentimento_ck       CHECK (fl_consentimento = 'S'),
  CONSTRAINT tb_produtor_cd_municipio_fk           FOREIGN KEY (cd_municipio)
    REFERENCES tb_municipio (cd_municipio)
);
COMMENT ON TABLE tb_produtor IS 'Produtor rural atendido pelo Argos';


-- ---------------------------------------------------------------------
-- tb_propriedade: a roca (filha de tb_produtor e de tb_municipio)
-- Um produtor pode ter 0..N propriedades (1:N).
-- ---------------------------------------------------------------------
CREATE SEQUENCE sq_tb_propriedade START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_propriedade (
  cd_propriedade    NUMBER(10)    NOT NULL,
  cd_produtor       NUMBER(10)    NOT NULL,  
  cd_municipio      NUMBER(7)     NOT NULL,   
  nm_propriedade    VARCHAR2(80)  NOT NULL,
  nr_area_ha        NUMBER(8,2)   NOT NULL,   
  ds_fonte_agua     VARCHAR2(10),            
  ds_irrigacao      VARCHAR2(12),             
  fl_area_alagavel  CHAR(1),                 
  CONSTRAINT tb_propriedade_cd_propriedade_pk   PRIMARY KEY (cd_propriedade),
  CONSTRAINT tb_propriedade_cd_produtor_fk      FOREIGN KEY (cd_produtor)
    REFERENCES tb_produtor (cd_produtor),
  CONSTRAINT tb_propriedade_cd_municipio_fk     FOREIGN KEY (cd_municipio)
    REFERENCES tb_municipio (cd_municipio),
  CONSTRAINT tb_propriedade_nr_area_ha_ck       CHECK (nr_area_ha > 0),
  CONSTRAINT tb_propriedade_ds_fonte_agua_ck    CHECK (ds_fonte_agua IN
    ('CHUVA','POCO','RIO','ACUDE','CISTERNA','REDE')),
  CONSTRAINT tb_propriedade_ds_irrigacao_ck     CHECK (ds_irrigacao IN
    ('NAO','GOTEJAMENTO','ASPERSAO','OUTRA')),
  CONSTRAINT tb_propriedade_fl_area_alagavel_ck CHECK (fl_area_alagavel IN ('S','N'))
);
COMMENT ON TABLE tb_propriedade IS 'Propriedade rural (sitio) de um produtor';

-- ---------------------------------------------------------------------
-- tb_cultura
-- ---------------------------------------------------------------------
CREATE SEQUENCE sq_tb_cultura START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_cultura (
  cd_cultura         NUMBER(5)     NOT NULL,
  nm_cultura         VARCHAR2(40)  NOT NULL,
  nr_ciclo_dias      NUMBER(3)     NOT NULL,
  nr_temp_ideal_min  NUMBER(4,1),
  nr_temp_ideal_max  NUMBER(4,1),
  nr_agua_ciclo_mm   NUMBER(5),
  CONSTRAINT tb_cultura_cd_cultura_pk    PRIMARY KEY (cd_cultura),
  CONSTRAINT tb_cultura_nm_cultura_un    UNIQUE (nm_cultura),
  CONSTRAINT tb_cultura_nr_ciclo_dias_ck CHECK (nr_ciclo_dias > 0),
  -- (acrescentado ao dicionario) CHECK entre duas colunas: max nao pode ser menor que min
  CONSTRAINT tb_cultura_temps_ck         CHECK (nr_temp_ideal_max >= nr_temp_ideal_min)
);
COMMENT ON TABLE tb_cultura IS 'Culturas agricolas acompanhadas pelo Argos';


-- ---------------------------------------------------------------------
-- tb_praga
-- ---------------------------------------------------------------------
CREATE SEQUENCE sq_tb_praga START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_praga (
  cd_praga       NUMBER(5)      NOT NULL,
  nm_praga       VARCHAR2(60)   NOT NULL,
  nm_cientifico  VARCHAR2(80),
  ds_sintomas    VARCHAR2(400)  NOT NULL,
  ds_manejo      VARCHAR2(400)  NOT NULL,
  ds_fonte       VARCHAR2(120)  NOT NULL,   
  ds_url_imagem  VARCHAR2(250),
  CONSTRAINT tb_praga_cd_praga_pk PRIMARY KEY (cd_praga),
  CONSTRAINT tb_praga_nm_praga_un UNIQUE (nm_praga)
);
COMMENT ON TABLE tb_praga IS 'Pragas e doencas, com sintomas, manejo e fonte oficial';


-- ---------------------------------------------------------------------
-- tb_plantio: associativa do N:N propriedade x cultura,
-- COM atributos proprios e chave propria (por isso tem sequence).
-- RN01 (area plantada <= area da propriedade) envolve 2 tabelas:
-- ---------------------------------------------------------------------
CREATE SEQUENCE sq_tb_plantio START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_plantio (
  cd_plantio            NUMBER(10)    NOT NULL,
  cd_propriedade        NUMBER(10)    NOT NULL,
  cd_cultura            NUMBER(5)     NOT NULL,
  nr_area_ha            NUMBER(8,2)   NOT NULL,
  dt_plantio            DATE          NOT NULL,
  dt_colheita_prevista  DATE          NOT NULL,
  ds_estagio            VARCHAR2(12)  NOT NULL,
  CONSTRAINT tb_plantio_cd_plantio_pk     PRIMARY KEY (cd_plantio),
  CONSTRAINT tb_plantio_cd_propriedade_fk FOREIGN KEY (cd_propriedade)
    REFERENCES tb_propriedade (cd_propriedade),
  CONSTRAINT tb_plantio_cd_cultura_fk     FOREIGN KEY (cd_cultura)
    REFERENCES tb_cultura (cd_cultura),
  CONSTRAINT tb_plantio_nr_area_ha_ck     CHECK (nr_area_ha > 0),
  -- CHECK de 2 colunas: so pode ser escrito no fim da tabela (nome no estilo TB_VIAGENS_DATAS_CK da aula)
  CONSTRAINT tb_plantio_datas_ck          CHECK (dt_colheita_prevista > dt_plantio),
  CONSTRAINT tb_plantio_ds_estagio_ck     CHECK (ds_estagio IN
    ('PREPARO','PLANTIO','CRESCIMENTO','FLORACAO','COLHEITA')),
  -- UNIQUE composto: a mesma cultura, na mesma roca, na mesma data, nao entra duas vezes
  CONSTRAINT tb_plantio_prop_cult_data_un UNIQUE (cd_propriedade, cd_cultura, dt_plantio)
);
COMMENT ON TABLE tb_plantio IS 'Associativa propriedade x cultura: o que foi plantado, onde, quando e quanto';


-- ---------------------------------------------------------------------
-- tb_cultura_praga: associativa PURA do N:N cultura x praga.
-- ---------------------------------------------------------------------
CREATE TABLE tb_cultura_praga (
  cd_cultura       NUMBER(5)     NOT NULL,
  cd_praga         NUMBER(5)     NOT NULL,
  ds_fase_critica  VARCHAR2(12),
  CONSTRAINT tb_cultura_praga_cd_cultura_cd_praga_pk PRIMARY KEY (cd_cultura, cd_praga),
  CONSTRAINT tb_cultura_praga_cd_cultura_fk          FOREIGN KEY (cd_cultura)
    REFERENCES tb_cultura (cd_cultura),
  CONSTRAINT tb_cultura_praga_cd_praga_fk            FOREIGN KEY (cd_praga)
    REFERENCES tb_praga (cd_praga),
  CONSTRAINT tb_cultura_praga_ds_fase_critica_ck     CHECK (ds_fase_critica IN
    ('PREPARO','PLANTIO','CRESCIMENTO','FLORACAO','COLHEITA'))
);
COMMENT ON TABLE tb_cultura_praga IS 'Associativa cultura x praga: qual praga ataca qual cultura e em que fase';


-- ---------------------------------------------------------------------
-- tb_ocorrencia: relato "vi essa praga aqui"
-- ---------------------------------------------------------------------
CREATE SEQUENCE sq_tb_ocorrencia START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_ocorrencia (
  cd_ocorrencia   NUMBER(10)     NOT NULL,
  cd_plantio      NUMBER(10)     NOT NULL,
  cd_praga        NUMBER(5),                       -- FK opcional: nulo = "nao sei qual praga e"
  dt_ocorrencia   DATE           DEFAULT SYSDATE NOT NULL,
  nv_intensidade  NUMBER(1)      NOT NULL,
  ds_observacao   VARCHAR2(300),
  fl_confirmada   CHAR(1)        DEFAULT 'N' NOT NULL,   
  CONSTRAINT tb_ocorrencia_cd_ocorrencia_pk  PRIMARY KEY (cd_ocorrencia),
  CONSTRAINT tb_ocorrencia_cd_plantio_fk     FOREIGN KEY (cd_plantio)
    REFERENCES tb_plantio (cd_plantio),
  CONSTRAINT tb_ocorrencia_cd_praga_fk       FOREIGN KEY (cd_praga)
    REFERENCES tb_praga (cd_praga),
  CONSTRAINT tb_ocorrencia_nv_intensidade_ck CHECK (nv_intensidade BETWEEN 1 AND 3),
  CONSTRAINT tb_ocorrencia_fl_confirmada_ck  CHECK (fl_confirmada IN ('S','N'))
);
COMMENT ON TABLE tb_ocorrencia IS 'Relato de praga feito pelo produtor em um plantio';


-- ---------------------------------------------------------------------
-- tb_alerta: avisos por municipio
-- ---------------------------------------------------------------------
CREATE SEQUENCE sq_tb_alerta START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_alerta (
  cd_alerta            NUMBER(10)     NOT NULL,
  cd_municipio         NUMBER(7)      NOT NULL,
  tp_alerta            VARCHAR2(12)   NOT NULL,
  nv_severidade        NUMBER(1)      NOT NULL,   -- 1 tudo certo ... 4 grande perigo
  ds_titulo            VARCHAR2(80)   NOT NULL,
  ds_mensagem_simples  VARCHAR2(300)  NOT NULL,
  ds_fonte             VARCHAR2(80)   NOT NULL,   -- RN: alerta sem fonte oficial nao entra
  dt_inicio            DATE           NOT NULL,
  dt_fim               DATE           NOT NULL,
  CONSTRAINT tb_alerta_cd_alerta_pk     PRIMARY KEY (cd_alerta),
  CONSTRAINT tb_alerta_cd_municipio_fk  FOREIGN KEY (cd_municipio)
    REFERENCES tb_municipio (cd_municipio),
  CONSTRAINT tb_alerta_tp_alerta_ck     CHECK (tp_alerta IN
    ('CHUVA_FORTE','SECA','CALOR','GEADA','ENCHENTE','PRAGA','VENTO')),
  CONSTRAINT tb_alerta_nv_severidade_ck CHECK (nv_severidade BETWEEN 1 AND 4),
  CONSTRAINT tb_alerta_datas_ck         CHECK (dt_fim >= dt_inicio)
);
COMMENT ON TABLE tb_alerta IS 'Alerta de clima ou praga por municipio, com fonte oficial e validade';

-- =====================================================================
-- 2) INSERTs - 87 registros
-- REAL: municipios (IBGE), culturas, pragas e cultura x praga (Embrapa).
-- FICTICIO: produtores, propriedades, plantios, ocorrencias e alertas.
-- =====================================================================

-- tb_municipio (REAL - codigos conferidos em ibge.gov.br/cidades-e-estados em 13/11/2026)
-- Latitude/longitude ficam NULL: serao preenchidas pela parte de IA.
INSERT INTO tb_municipio (cd_municipio, nm_municipio, sg_uf, ds_regiao) VALUES (2611101, 'Petrolina', 'PE', 'NORDESTE');
INSERT INTO tb_municipio (cd_municipio, nm_municipio, sg_uf, ds_regiao) VALUES (2918407, 'Juazeiro', 'BA', 'NORDESTE');
INSERT INTO tb_municipio (cd_municipio, nm_municipio, sg_uf, ds_regiao) VALUES (2408003, 'Mossoró', 'RN', 'NORDESTE');
INSERT INTO tb_municipio (cd_municipio, nm_municipio, sg_uf, ds_regiao) VALUES (2208007, 'Picos', 'PI', 'NORDESTE');
INSERT INTO tb_municipio (cd_municipio, nm_municipio, sg_uf, ds_regiao) VALUES (4302105, 'Bento Gonçalves', 'RS', 'SUL');
INSERT INTO tb_municipio (cd_municipio, nm_municipio, sg_uf, ds_regiao) VALUES (4314100, 'Passo Fundo', 'RS', 'SUL');
INSERT INTO tb_municipio (cd_municipio, nm_municipio, sg_uf, ds_regiao) VALUES (4204202, 'Chapecó', 'SC', 'SUL');
INSERT INTO tb_municipio (cd_municipio, nm_municipio, sg_uf, ds_regiao) VALUES (4104808, 'Cascavel', 'PR', 'SUL');

-- tb_cultura (REAL) - temperatura e agua ficam NULL: sem fonte, nao chutamos.
-- SORGO fica sem plantio de proposito (consulta 8).
-- 1 MILHO: Embrapa Milho e Sorgo, Doc. 202 (2016): ciclo precoce 120-130 dias
INSERT INTO tb_cultura (cd_cultura, nm_cultura, nr_ciclo_dias) VALUES (sq_tb_cultura.NEXTVAL, 'MILHO', 125);
-- 2 FEIJAO: Embrapa (A Granja, out/1999): ciclo do feijoeiro ao redor de 90 dias
INSERT INTO tb_cultura (cd_cultura, nm_cultura, nr_ciclo_dias) VALUES (sq_tb_cultura.NEXTVAL, 'FEIJAO', 90);
-- 3 MANDIOCA: Embrapa (folder variedades): colheita aos 12 meses
INSERT INTO tb_cultura (cd_cultura, nm_cultura, nr_ciclo_dias) VALUES (sq_tb_cultura.NEXTVAL, 'MANDIOCA', 365);
-- 4 SOJA: Embrapa Soja, Doc. 202 (2003): semiprecoce 116-125 dias
INSERT INTO tb_cultura (cd_cultura, nm_cultura, nr_ciclo_dias) VALUES (sq_tb_cultura.NEXTVAL, 'SOJA', 120);
-- 5 TRIGO: Embrapa Agropecuaria Oeste (2000): ciclo de 120 dias a partir da semeadura
INSERT INTO tb_cultura (cd_cultura, nm_cultura, nr_ciclo_dias) VALUES (sq_tb_cultura.NEXTVAL, 'TRIGO', 120);
-- 6 SORGO: Embrapa Milho e Sorgo, BRS 373: maturacao em 110 dias
INSERT INTO tb_cultura (cd_cultura, nm_cultura, nr_ciclo_dias) VALUES (sq_tb_cultura.NEXTVAL, 'SORGO', 110);

-- tb_praga (REAL - textos resumidos em linguagem simples a partir da fonte em ds_fonte)
-- 1
INSERT INTO tb_praga (cd_praga, nm_praga, nm_cientifico, ds_sintomas, ds_manejo, ds_fonte) VALUES (sq_tb_praga.NEXTVAL, 'Lagarta-do-cartucho', 'Spodoptera frugiperda',
  'Nas plantas novas raspa as folhas. Depois faz furos grandes e destrói o miolo (cartucho), que fica cheio de fezes parecidas com serragem. A lagarta é escura e tem um Y de cabeça para baixo na cabeça.',
  'Olhe a lavoura toda semana desde que o milho nasce. Proteja os inimigos naturais. Use controle biológico ou inseticida registrado só quando o técnico indicar.',
  'Embrapa Milho e Sorgo - Manejo integrado da lagarta-do-cartucho (Cruz, I.)');
-- 2
INSERT INTO tb_praga (cd_praga, nm_praga, nm_cientifico, ds_sintomas, ds_manejo, ds_fonte) VALUES (sq_tb_praga.NEXTVAL, 'Cigarrinha-do-milho', 'Dalbulus maidis',
  'Inseto pequeno (3 a 4 mm), claro, nas folhas novas. Passa os enfezamentos: na fase da espiga as folhas ficam vermelhas ou com listras claras, a planta fica baixa e as espigas ficam pequenas.',
  'O manejo é de prevenção: tire o milho que nasce sozinho (tiguera) na entressafra, use semente tolerante e proteja a lavoura nas primeiras semanas. Depois que os sinais aparecem não tem cura.',
  'Embrapa Milho e Sorgo - Comunicado Tecnico 226 (2018)');
-- 3
INSERT INTO tb_praga (cd_praga, nm_praga, nm_cientifico, ds_sintomas, ds_manejo, ds_fonte) VALUES (sq_tb_praga.NEXTVAL, 'Mosca-branca', 'Bemisia tabaci',
  'Mosquinhas brancas embaixo das folhas, que voam quando a planta balança. No feijão passa o mosaico-dourado: folhas amarelo-vivo, planta pequena e vagens tortas. A perda pode chegar a 100%.',
  'Não plante perto de lavoura velha com a praga. Respeite a época de plantio da região. Destrua restos de cultura. Use feijão resistente ao mosaico-dourado e inseticida registrado com orientação técnica.',
  'Embrapa Semiarido - Circular Tecnica 72 (2001); Embrapa Arroz e Feijao - CT 91 (2021)');
-- 4
INSERT INTO tb_praga (cd_praga, nm_praga, nm_cientifico, ds_sintomas, ds_manejo, ds_fonte) VALUES (sq_tb_praga.NEXTVAL, 'Vaquinha', 'Diabrotica speciosa',
  'Besouro pequeno verde com manchas amarelas. Os adultos furam e comem as folhas. O estrago maior é quando a planta é novinha. As larvas ficam no solo e comem as raízes.',
  'Olhe a lavoura com atenção nas primeiras semanas depois que a planta nasce. Se a folha estiver muito comida, chame o técnico para ver se precisa de inseticida registrado.',
  'Embrapa Arroz e Feijao - Manejo de pragas na cultura de feijao (Yokoyama, 1997)');
-- 5
INSERT INTO tb_praga (cd_praga, nm_praga, nm_cientifico, ds_sintomas, ds_manejo, ds_fonte) VALUES (sq_tb_praga.NEXTVAL, 'Percevejo-marrom', 'Euschistus heros',
  'Percevejo marrom que chupa as vagens e os grãos da soja. Os grãos ficam chochos e manchados. Não causa dano antes de a soja ter vagens.',
  'Use o pano de batida para contar os percevejos a partir do começo das vagens. Conte ninfas e adultos. Use inseticida registrado só quando passar do nível indicado pelo técnico.',
  'Embrapa Soja - Correa-Ferreira e Roggia (2012); Circular Tecnica 154 (2019)');
-- 6
INSERT INTO tb_praga (cd_praga, nm_praga, nm_cientifico, ds_sintomas, ds_manejo, ds_fonte) VALUES (sq_tb_praga.NEXTVAL, 'Ferrugem-asiática', 'Phakopsora pachyrhizi',
  'Doença causada por fungo. Começa com pontinhos verde-claro ou amarelo-claro nas folhas, que crescem. As folhas amarelam e caem antes da hora. A perda vai de 10% a 90%.',
  'Respeite o vazio sanitário: pelo menos 90 dias sem soja viva no campo. Arranque a soja que nasce sozinha. Plante no começo da época, com cultivar resistente. Use fungicida com orientação técnica.',
  'Embrapa Soja - Circular Tecnica 211 (2024)');
-- 7
INSERT INTO tb_praga (cd_praga, nm_praga, nm_cientifico, ds_sintomas, ds_manejo, ds_fonte) VALUES (sq_tb_praga.NEXTVAL, 'Mandarová-da-mandioca', 'Erinnyis ello',
  'Lagarta grande que come as folhas. Pode deixar a planta sem nenhuma folha e diminuir a colheita em até 64%. O ataque é pior em plantas novas, de até 5 meses.',
  'Proteja os inimigos naturais. Use o inseticida biológico feito com o vírus Baculovirus erinnyis, que pode matar até 100% das lagartas. Aplique quando as lagartas ainda são pequenas.',
  'Embrapa Mandioca e Fruticultura - Baculovirus erinnyis (Ringenberg, R.)');
-- 8
INSERT INTO tb_praga (cd_praga, nm_praga, nm_cientifico, ds_sintomas, ds_manejo, ds_fonte) VALUES (sq_tb_praga.NEXTVAL, 'Pulgão-da-aveia', 'Rhopalosiphum padi',
  'Colônias de insetos bem pequenos nas folhas e no colmo do trigo. Chupam a seiva. O pior é que passam o vírus do nanismo-amarelo, que deixa a planta baixa e amarelada.',
  'O controle é feito principalmente por inimigos naturais, como vespinhas e joaninhas. Inseticida registrado só em caso de emergência e com orientação técnica.',
  'Embrapa Trigo - Pulgoes do trigo (Salvadori et al.); Documentos 200 (2022)');

-- tb_produtor (FICTICIO) - 9 e 10 ficam sem propriedade (consulta 5). Telefones ficticios.
-- 1
INSERT INTO tb_produtor (cd_produtor, nm_produtor, nr_telefone, ds_canal_preferido, ds_preferencia_leitura, nv_uso_celular, nr_anos_experiencia, fl_recebe_ater, fl_assistido, fl_consentimento, dt_consentimento, dt_cadastro, cd_municipio)
VALUES (sq_tb_produtor.NEXTVAL, 'Dona Cida', '87990000001', 'AUDIO', 'OUVIR', 1, 30, 'N', 'S', 'S', TO_DATE('02/09/2026','DD/MM/YYYY'), TO_DATE('02/09/2026','DD/MM/YYYY'), 2611101);
-- 2
INSERT INTO tb_produtor (cd_produtor, nm_produtor, nr_telefone, ds_canal_preferido, ds_preferencia_leitura, nv_uso_celular, nr_anos_experiencia, fl_recebe_ater, fl_assistido, fl_consentimento, dt_consentimento, dt_cadastro, cd_municipio)
VALUES (sq_tb_produtor.NEXTVAL, 'Seu Zé', '87990000002', 'WHATSAPP', 'AMBOS', 2, 25, 'S', 'N', 'S', TO_DATE('03/09/2026','DD/MM/YYYY'), TO_DATE('03/09/2026','DD/MM/YYYY'), 2611101);
-- 3
INSERT INTO tb_produtor (cd_produtor, nm_produtor, nr_telefone, ds_canal_preferido, ds_preferencia_leitura, nv_uso_celular, nr_anos_experiencia, fl_recebe_ater, fl_assistido, fl_consentimento, dt_consentimento, dt_cadastro, cd_municipio)
VALUES (sq_tb_produtor.NEXTVAL, 'Francisca', '74990000003', 'AUDIO', 'OUVIR', 1, 18, 'N', 'S', 'S', TO_DATE('10/09/2026','DD/MM/YYYY'), TO_DATE('10/09/2026','DD/MM/YYYY'), 2918407);
-- 4
INSERT INTO tb_produtor (cd_produtor, nm_produtor, nr_telefone, ds_canal_preferido, ds_preferencia_leitura, nv_uso_celular, nr_anos_experiencia, fl_recebe_ater, fl_assistido, fl_consentimento, dt_consentimento, dt_cadastro, cd_municipio)
VALUES (sq_tb_produtor.NEXTVAL, 'Raimundo', '84990000004', 'SMS', 'LER', NULL, 40, 'N', 'N', 'S', TO_DATE('15/09/2026','DD/MM/YYYY'), TO_DATE('15/09/2026','DD/MM/YYYY'), 2408003);
-- 5
INSERT INTO tb_produtor (cd_produtor, nm_produtor, nr_telefone, ds_canal_preferido, ds_preferencia_leitura, nv_uso_celular, nr_anos_experiencia, fl_recebe_ater, fl_assistido, fl_consentimento, dt_consentimento, dt_cadastro, cd_municipio)
VALUES (sq_tb_produtor.NEXTVAL, 'Antônia', '89990000005', 'WHATSAPP', 'OUVIR', 1, 22, 'S', 'S', 'S', TO_DATE('20/09/2026','DD/MM/YYYY'), TO_DATE('20/09/2026','DD/MM/YYYY'), 2208007);
-- 6
INSERT INTO tb_produtor (cd_produtor, nm_produtor, nr_telefone, ds_canal_preferido, ds_preferencia_leitura, nv_uso_celular, nr_anos_experiencia, fl_recebe_ater, fl_assistido, fl_consentimento, dt_consentimento, dt_cadastro, cd_municipio)
VALUES (sq_tb_produtor.NEXTVAL, 'João Batista', '87990000006', 'APP', 'LER', 3, 10, 'S', 'N', 'S', TO_DATE('01/10/2026','DD/MM/YYYY'), TO_DATE('01/10/2026','DD/MM/YYYY'), 2611101);
-- 7
INSERT INTO tb_produtor (cd_produtor, nm_produtor, nr_telefone, ds_canal_preferido, ds_preferencia_leitura, nv_uso_celular, nr_anos_experiencia, fl_recebe_ater, fl_assistido, fl_consentimento, dt_consentimento, dt_cadastro, cd_municipio)
VALUES (sq_tb_produtor.NEXTVAL, 'Lurdes', '54990000007', 'APP', 'AMBOS', 2, 35, 'S', 'N', 'S', TO_DATE('05/10/2026','DD/MM/YYYY'), TO_DATE('05/10/2026','DD/MM/YYYY'), 4302105);
-- 8
INSERT INTO tb_produtor (cd_produtor, nm_produtor, nr_telefone, ds_canal_preferido, ds_preferencia_leitura, nv_uso_celular, nr_anos_experiencia, fl_recebe_ater, fl_assistido, fl_consentimento, dt_consentimento, dt_cadastro, cd_municipio)
VALUES (sq_tb_produtor.NEXTVAL, 'Valdir', '54990000008', 'WHATSAPP', 'LER', 3, NULL, 'S', 'N', 'S', TO_DATE('08/10/2026','DD/MM/YYYY'), TO_DATE('08/10/2026','DD/MM/YYYY'), 4314100);
-- 9
INSERT INTO tb_produtor (cd_produtor, nm_produtor, nr_telefone, ds_canal_preferido, ds_preferencia_leitura, nv_uso_celular, nr_anos_experiencia, fl_recebe_ater, fl_assistido, fl_consentimento, dt_consentimento, dt_cadastro, cd_municipio)
VALUES (sq_tb_produtor.NEXTVAL, 'Neusa', '49990000009', 'AUDIO', 'OUVIR', 1, 45, 'N', 'S', 'S', TO_DATE('03/11/2026','DD/MM/YYYY'), TO_DATE('03/11/2026','DD/MM/YYYY'), 4204202);
-- 10
INSERT INTO tb_produtor (cd_produtor, nm_produtor, nr_telefone, ds_canal_preferido, ds_preferencia_leitura, nv_uso_celular, nr_anos_experiencia, fl_recebe_ater, fl_assistido, fl_consentimento, dt_consentimento, dt_cadastro, cd_municipio)
VALUES (sq_tb_produtor.NEXTVAL, 'Edson', '45990000010', 'APP', 'LER', 3, 8, 'N', 'N', 'S', TO_DATE('10/11/2026','DD/MM/YYYY'), TO_DATE('10/11/2026','DD/MM/YYYY'), 4104808);

-- tb_propriedade (FICTICIO) - areas de 1,5 a 60 ha (consultas 3, 9 e 10)
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 1, 2611101, 'Sítio Boa Esperança', 3, 'CISTERNA', 'NAO', 'N'); -- 1
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 2, 2611101, 'Sítio Riacho Seco', 8.5, 'POCO', 'GOTEJAMENTO', 'N'); -- 2
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 2, 2918407, 'Lote 12 do Projeto Irrigado', 12, 'RIO', 'GOTEJAMENTO', 'S'); -- 3
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 3, 2918407, 'Sítio Caatinga Verde', 5, 'ACUDE', 'NAO', NULL); -- 4
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 4, 2408003, 'Sítio Três Irmãos', 20, 'POCO', 'ASPERSAO', 'N'); -- 5
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 5, 2208007, 'Sítio Baixa Funda', 1.5, 'CHUVA', 'NAO', 'S'); -- 6
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 6, 2611101, 'Sítio São João', 6, 'RIO', 'ASPERSAO', 'N'); -- 7
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 7, 4302105, 'Propriedade Santa Lúcia', 15, 'CHUVA', 'NAO', 'N'); -- 8
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 7, 4302105, 'Linha Palmeiro', 4, 'REDE', 'OUTRA', 'N'); -- 9
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 8, 4314100, 'Granja Bom Jesus', 60, 'CHUVA', 'NAO', 'N'); -- 10
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 8, 4314100, 'Lote Coxilha', 35, 'CHUVA', 'ASPERSAO', NULL); -- 11
INSERT INTO tb_propriedade (cd_propriedade, cd_produtor, cd_municipio, nm_propriedade, nr_area_ha, ds_fonte_agua, ds_irrigacao, fl_area_alagavel) VALUES (sq_tb_propriedade.NEXTVAL, 8, 4204202, 'Sítio do Lajeado', 25, 'RIO', 'NAO', 'S'); -- 12

-- tb_plantio (FICTICIO) - 1 a 4 colhem entre 04/12 e 18/12/2026 (consulta 7).
-- Soma da area plantada nunca passa da area da propriedade (RN01).
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 1, 2, 1, TO_DATE('05/09/2026','DD/MM/YYYY'), TO_DATE('04/12/2026','DD/MM/YYYY'), 'CRESCIMENTO'); -- 1
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 1, 1, 1.5, TO_DATE('15/08/2026','DD/MM/YYYY'), TO_DATE('18/12/2026','DD/MM/YYYY'), 'FLORACAO'); -- 2
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 2, 1, 5, TO_DATE('01/08/2026','DD/MM/YYYY'), TO_DATE('04/12/2026','DD/MM/YYYY'), 'FLORACAO'); -- 3
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 2, 2, 2, TO_DATE('10/09/2026','DD/MM/YYYY'), TO_DATE('09/12/2026','DD/MM/YYYY'), 'CRESCIMENTO'); -- 4
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 3, 1, 10, TO_DATE('01/10/2026','DD/MM/YYYY'), TO_DATE('03/02/2027','DD/MM/YYYY'), 'CRESCIMENTO'); -- 5
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 4, 2, 3, TO_DATE('15/10/2026','DD/MM/YYYY'), TO_DATE('13/01/2027','DD/MM/YYYY'), 'CRESCIMENTO'); -- 6
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 5, 1, 12, TO_DATE('20/10/2026','DD/MM/YYYY'), TO_DATE('22/02/2027','DD/MM/YYYY'), 'CRESCIMENTO'); -- 7
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 5, 2, 6, TO_DATE('01/11/2026','DD/MM/YYYY'), TO_DATE('30/01/2027','DD/MM/YYYY'), 'PLANTIO'); -- 8
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 6, 3, 1, TO_DATE('10/03/2026','DD/MM/YYYY'), TO_DATE('10/03/2027','DD/MM/YYYY'), 'CRESCIMENTO'); -- 9
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 7, 1, 4, TO_DATE('25/10/2026','DD/MM/YYYY'), TO_DATE('27/02/2027','DD/MM/YYYY'), 'CRESCIMENTO'); -- 10
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 8, 1, 10, TO_DATE('01/09/2026','DD/MM/YYYY'), TO_DATE('04/01/2027','DD/MM/YYYY'), 'FLORACAO'); -- 11
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 10, 4, 35, TO_DATE('20/10/2026','DD/MM/YYYY'), TO_DATE('17/02/2027','DD/MM/YYYY'), 'CRESCIMENTO'); -- 12
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 10, 5, 20, TO_DATE('01/06/2026','DD/MM/YYYY'), TO_DATE('29/09/2026','DD/MM/YYYY'), 'COLHEITA'); -- 13
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 11, 4, 30, TO_DATE('05/11/2026','DD/MM/YYYY'), TO_DATE('05/03/2027','DD/MM/YYYY'), 'PLANTIO'); -- 14
INSERT INTO tb_plantio (cd_plantio, cd_propriedade, cd_cultura, nr_area_ha, dt_plantio, dt_colheita_prevista, ds_estagio) VALUES (sq_tb_plantio.NEXTVAL, 12, 1, 20, TO_DATE('15/09/2026','DD/MM/YYYY'), TO_DATE('18/01/2027','DD/MM/YYYY'), 'CRESCIMENTO'); -- 15

-- tb_cultura_praga (REAL - mesma fonte da praga). Fase NULL quando a fonte nao diz.
-- Percevejo: ataca as vagens; FLORACAO representa a fase reprodutiva na nossa lista.
INSERT INTO tb_cultura_praga (cd_cultura, cd_praga, ds_fase_critica) VALUES (1, 1, 'CRESCIMENTO');
INSERT INTO tb_cultura_praga (cd_cultura, cd_praga, ds_fase_critica) VALUES (1, 2, 'PLANTIO');
INSERT INTO tb_cultura_praga (cd_cultura, cd_praga, ds_fase_critica) VALUES (1, 4, NULL);
INSERT INTO tb_cultura_praga (cd_cultura, cd_praga, ds_fase_critica) VALUES (2, 3, NULL);
INSERT INTO tb_cultura_praga (cd_cultura, cd_praga, ds_fase_critica) VALUES (2, 4, 'PLANTIO');
INSERT INTO tb_cultura_praga (cd_cultura, cd_praga, ds_fase_critica) VALUES (4, 5, 'FLORACAO');
INSERT INTO tb_cultura_praga (cd_cultura, cd_praga, ds_fase_critica) VALUES (4, 6, NULL);
INSERT INTO tb_cultura_praga (cd_cultura, cd_praga, ds_fase_critica) VALUES (3, 7, 'CRESCIMENTO');
INSERT INTO tb_cultura_praga (cd_cultura, cd_praga, ds_fase_critica) VALUES (5, 8, NULL);
INSERT INTO tb_cultura_praga (cd_cultura, cd_praga, ds_fase_critica) VALUES (6, 1, 'CRESCIMENTO');

-- tb_ocorrencia (FICTICIO) - Petrolina tem 4 relatos em nov/2026 (consulta 4);
-- a 4 tem praga NULL ("nao sei qual e").
INSERT INTO tb_ocorrencia (cd_ocorrencia, cd_plantio, cd_praga, dt_ocorrencia, nv_intensidade, ds_observacao, fl_confirmada) VALUES (sq_tb_ocorrencia.NEXTVAL, 2, 1, TO_DATE('03/11/2026','DD/MM/YYYY'), 2, 'Folhas raspadas e serragem no miolo do milho', 'S'); -- 1
INSERT INTO tb_ocorrencia (cd_ocorrencia, cd_plantio, cd_praga, dt_ocorrencia, nv_intensidade, ds_observacao, fl_confirmada) VALUES (sq_tb_ocorrencia.NEXTVAL, 1, 3, TO_DATE('05/11/2026','DD/MM/YYYY'), 2, 'Mosquinha branca embaixo das folhas do feijão', 'N'); -- 2
INSERT INTO tb_ocorrencia (cd_ocorrencia, cd_plantio, cd_praga, dt_ocorrencia, nv_intensidade, ds_observacao, fl_confirmada) VALUES (sq_tb_ocorrencia.NEXTVAL, 3, 2, TO_DATE('06/11/2026','DD/MM/YYYY'), 3, 'Muita cigarrinha nas folhas novas', 'S'); -- 3
INSERT INTO tb_ocorrencia (cd_ocorrencia, cd_plantio, cd_praga, dt_ocorrencia, nv_intensidade, ds_observacao, fl_confirmada) VALUES (sq_tb_ocorrencia.NEXTVAL, 10, NULL, TO_DATE('09/11/2026','DD/MM/YYYY'), 1, 'Folha com furos, não sei qual bicho é', 'N'); -- 4
INSERT INTO tb_ocorrencia (cd_ocorrencia, cd_plantio, cd_praga, dt_ocorrencia, nv_intensidade, ds_observacao, fl_confirmada) VALUES (sq_tb_ocorrencia.NEXTVAL, 5, 1, TO_DATE('07/11/2026','DD/MM/YYYY'), 2, 'Lagarta no cartucho', 'N'); -- 5
INSERT INTO tb_ocorrencia (cd_ocorrencia, cd_plantio, cd_praga, dt_ocorrencia, nv_intensidade, ds_observacao, fl_confirmada) VALUES (sq_tb_ocorrencia.NEXTVAL, 6, 4, TO_DATE('10/11/2026','DD/MM/YYYY'), 1, 'Besourinho verde comendo as folhas', 'N'); -- 6
INSERT INTO tb_ocorrencia (cd_ocorrencia, cd_plantio, cd_praga, dt_ocorrencia, nv_intensidade, ds_observacao, fl_confirmada) VALUES (sq_tb_ocorrencia.NEXTVAL, 7, 1, TO_DATE('25/10/2026','DD/MM/YYYY'), 3, 'Muitas plantas com o miolo comido', 'S'); -- 7
INSERT INTO tb_ocorrencia (cd_ocorrencia, cd_plantio, cd_praga, dt_ocorrencia, nv_intensidade, ds_observacao, fl_confirmada) VALUES (sq_tb_ocorrencia.NEXTVAL, 8, 3, TO_DATE('11/11/2026','DD/MM/YYYY'), 2, NULL, 'N'); -- 8
INSERT INTO tb_ocorrencia (cd_ocorrencia, cd_plantio, cd_praga, dt_ocorrencia, nv_intensidade, ds_observacao, fl_confirmada) VALUES (sq_tb_ocorrencia.NEXTVAL, 11, 2, TO_DATE('20/10/2026','DD/MM/YYYY'), 1, 'Poucas cigarrinhas', 'S'); -- 9
INSERT INTO tb_ocorrencia (cd_ocorrencia, cd_plantio, cd_praga, dt_ocorrencia, nv_intensidade, ds_observacao, fl_confirmada) VALUES (sq_tb_ocorrencia.NEXTVAL, 12, 4, TO_DATE('12/11/2026','DD/MM/YYYY'), 1, 'Furinhos nas folhas da soja', 'N'); -- 10

-- tb_alerta (FICTICIO, inspirado em avisos reais; ds_fonte marca 'exemplo ficticio')
-- Severidade 3-4 valida ate 15/12 ou depois em Petrolina, Juazeiro e Passo Fundo (consulta 6).
INSERT INTO tb_alerta (cd_alerta, cd_municipio, tp_alerta, nv_severidade, ds_titulo, ds_mensagem_simples, ds_fonte, dt_inicio, dt_fim) VALUES (sq_tb_alerta.NEXTVAL, 2611101, 'CALOR', 3, 'Calor forte nos próximos dias',
  'Vai fazer muito calor. Regue cedo ou no fim da tarde. Dê sombra e água para os animais.',
  'INMET (exemplo ficticio)', TO_DATE('10/11/2026','DD/MM/YYYY'), TO_DATE('20/12/2026','DD/MM/YYYY')); -- 1
INSERT INTO tb_alerta (cd_alerta, cd_municipio, tp_alerta, nv_severidade, ds_titulo, ds_mensagem_simples, ds_fonte, dt_inicio, dt_fim) VALUES (sq_tb_alerta.NEXTVAL, 2611101, 'SECA', 4, 'Seca prolongada',
  'Vai ficar muitos dias sem chuva. Guarde água. Não plante agora sem irrigação.',
  'Cemaden (exemplo ficticio)', TO_DATE('01/11/2026','DD/MM/YYYY'), TO_DATE('31/12/2026','DD/MM/YYYY')); -- 2
INSERT INTO tb_alerta (cd_alerta, cd_municipio, tp_alerta, nv_severidade, ds_titulo, ds_mensagem_simples, ds_fonte, dt_inicio, dt_fim) VALUES (sq_tb_alerta.NEXTVAL, 2918407, 'SECA', 3, 'Pouca chuva',
  'A chuva deve ficar abaixo do normal. Use a água com cuidado.',
  'Cemaden (exemplo ficticio)', TO_DATE('01/11/2026','DD/MM/YYYY'), TO_DATE('20/12/2026','DD/MM/YYYY')); -- 3
INSERT INTO tb_alerta (cd_alerta, cd_municipio, tp_alerta, nv_severidade, ds_titulo, ds_mensagem_simples, ds_fonte, dt_inicio, dt_fim) VALUES (sq_tb_alerta.NEXTVAL, 2408003, 'CALOR', 2, 'Dias quentes',
  'Os dias vão ser quentes. Beba água e evite o sol do meio-dia.',
  'INMET (exemplo ficticio)', TO_DATE('10/11/2026','DD/MM/YYYY'), TO_DATE('25/11/2026','DD/MM/YYYY')); -- 4
INSERT INTO tb_alerta (cd_alerta, cd_municipio, tp_alerta, nv_severidade, ds_titulo, ds_mensagem_simples, ds_fonte, dt_inicio, dt_fim) VALUES (sq_tb_alerta.NEXTVAL, 2208007, 'PRAGA', 2, 'Atenção à mosca-branca',
  'Foram vistas muitas mosquinhas brancas na região. Olhe embaixo das folhas do feijão.',
  'Embrapa (exemplo ficticio)', TO_DATE('05/11/2026','DD/MM/YYYY'), TO_DATE('05/12/2026','DD/MM/YYYY')); -- 5
INSERT INTO tb_alerta (cd_alerta, cd_municipio, tp_alerta, nv_severidade, ds_titulo, ds_mensagem_simples, ds_fonte, dt_inicio, dt_fim) VALUES (sq_tb_alerta.NEXTVAL, 4314100, 'CHUVA_FORTE', 3, 'Chuva forte',
  'Pode chover muito. Não deixe máquinas e animais perto de rio ou de área que alaga.',
  'Defesa Civil RS (exemplo ficticio)', TO_DATE('12/11/2026','DD/MM/YYYY'), TO_DATE('16/12/2026','DD/MM/YYYY')); -- 6
INSERT INTO tb_alerta (cd_alerta, cd_municipio, tp_alerta, nv_severidade, ds_titulo, ds_mensagem_simples, ds_fonte, dt_inicio, dt_fim) VALUES (sq_tb_alerta.NEXTVAL, 4204202, 'VENTO', 2, 'Vento forte',
  'Pode ventar forte. Prenda lonas e telhas soltas.',
  'INMET (exemplo ficticio)', TO_DATE('13/11/2026','DD/MM/YYYY'), TO_DATE('15/11/2026','DD/MM/YYYY')); -- 7
INSERT INTO tb_alerta (cd_alerta, cd_municipio, tp_alerta, nv_severidade, ds_titulo, ds_mensagem_simples, ds_fonte, dt_inicio, dt_fim) VALUES (sq_tb_alerta.NEXTVAL, 4104808, 'CHUVA_FORTE', 4, 'Temporal',
  'Pode cair temporal com granizo. Procure abrigo seguro.',
  'Defesa Civil PR (exemplo ficticio)', TO_DATE('01/10/2026','DD/MM/YYYY'), TO_DATE('10/10/2026','DD/MM/YYYY')); -- 8

COMMIT;

-- =====================================================================
-- 3) CONSULTAS - cada uma responde uma pergunta do Argos
-- =====================================================================

-- 1) Quais produtores de Petrolina recebem assistencia tecnica (ATER)?
SELECT nm_produtor, nr_telefone
  FROM tb_produtor
 WHERE cd_municipio = 2611101
   AND fl_recebe_ater = 'S'
 ORDER BY nm_produtor;

-- 2) Quanto de area esta plantada por cultura?
SELECT c.nm_cultura, SUM(pl.nr_area_ha) AS area_total_ha
  FROM tb_plantio pl
 INNER JOIN tb_cultura c ON c.cd_cultura = pl.cd_cultura
 GROUP BY c.cd_cultura, c.nm_cultura
 ORDER BY area_total_ha DESC;

-- 3) Qual a area media das propriedades por regiao?
SELECT m.ds_regiao, ROUND(AVG(pr.nr_area_ha), 2) AS area_media_ha
  FROM tb_propriedade pr
 INNER JOIN tb_municipio m ON m.cd_municipio = pr.cd_municipio
 GROUP BY m.ds_regiao;

-- 4) Quais municipios tiveram mais de 2 relatos de praga em novembro de 2026?
SELECT m.nm_municipio, COUNT(o.cd_ocorrencia) AS qtd_relatos
  FROM tb_ocorrencia o
 INNER JOIN tb_plantio pl     ON pl.cd_plantio = o.cd_plantio
 INNER JOIN tb_propriedade pr ON pr.cd_propriedade = pl.cd_propriedade
 INNER JOIN tb_municipio m    ON m.cd_municipio = pr.cd_municipio
 WHERE EXTRACT(MONTH FROM o.dt_ocorrencia) = 11
   AND EXTRACT(YEAR  FROM o.dt_ocorrencia) = 2026
 GROUP BY m.cd_municipio, m.nm_municipio
HAVING COUNT(o.cd_ocorrencia) > 2;

-- 5) Quais produtores ainda nao cadastraram propriedade?
SELECT p.nm_produtor
  FROM tb_produtor p
  LEFT JOIN tb_propriedade pr ON pr.cd_produtor = p.cd_produtor
 WHERE pr.cd_propriedade IS NULL;

-- 6) Quantos produtores estao em municipios com alerta de severidade 3 ou 4 ainda valido?
SELECT COUNT(DISTINCT p.cd_produtor) AS qtd_produtores_em_risco
  FROM tb_produtor p
 INNER JOIN tb_alerta a ON a.cd_municipio = p.cd_municipio
 WHERE a.nv_severidade >= 3
   AND a.dt_fim >= TRUNC(SYSDATE);

-- 7) Quais plantios tem colheita nos proximos 30 dias e quantos dias faltam?
SELECT pr.nm_propriedade, c.nm_cultura,
       TO_CHAR(pl.dt_colheita_prevista, 'DD/MM/YYYY') AS colheita,
       pl.dt_colheita_prevista - TRUNC(SYSDATE) AS dias_para_colher
  FROM tb_plantio pl
 INNER JOIN tb_propriedade pr ON pr.cd_propriedade = pl.cd_propriedade
 INNER JOIN tb_cultura c      ON c.cd_cultura = pl.cd_cultura
 WHERE pl.dt_colheita_prevista BETWEEN TRUNC(SYSDATE) AND TRUNC(SYSDATE) + 30
 ORDER BY dias_para_colher;

-- 8) Quais culturas ainda nao tem nenhum plantio?
SELECT c.nm_cultura
  FROM tb_plantio pl
 RIGHT JOIN tb_cultura c ON c.cd_cultura = pl.cd_cultura
 WHERE pl.cd_plantio IS NULL;

-- 9) Quais propriedades tem area acima da media geral?
SELECT nm_propriedade, nr_area_ha
  FROM tb_propriedade
 WHERE nr_area_ha > (SELECT AVG(nr_area_ha) FROM tb_propriedade)
 ORDER BY nr_area_ha DESC;

-- 10) Qual a maior e a menor propriedade por UF?
SELECT m.sg_uf, MAX(pr.nr_area_ha) AS maior_ha, MIN(pr.nr_area_ha) AS menor_ha
  FROM tb_propriedade pr
 INNER JOIN tb_municipio m ON m.cd_municipio = pr.cd_municipio
 GROUP BY m.sg_uf
 ORDER BY m.sg_uf;
 
-- =====================================================================
-- 4) UPDATE - o feijao da Dona Cida passou de CRESCIMENTO para FLORACAO
-- =====================================================================
SELECT cd_plantio, ds_estagio FROM tb_plantio WHERE cd_plantio = 1;   -- antes
UPDATE tb_plantio
   SET ds_estagio = 'FLORACAO'
 WHERE cd_plantio = 1;
SELECT cd_plantio, ds_estagio FROM tb_plantio WHERE cd_plantio = 1;   -- depois
COMMIT;

-- =====================================================================
-- 5) DELETE - relato de praga registrado por engano (sem observacao)
-- tb_ocorrencia nao tem tabela filha, entao o DELETE nao quebra FK
-- =====================================================================
SELECT cd_ocorrencia, cd_plantio, ds_observacao FROM tb_ocorrencia WHERE cd_ocorrencia = 8;  -- antes
DELETE FROM tb_ocorrencia
 WHERE cd_ocorrencia = 8;
SELECT COUNT(*) AS deve_ser_zero FROM tb_ocorrencia WHERE cd_ocorrencia = 8;                 -- depois
COMMIT;