-- SafeStadium schema (Supabase/PostgreSQL)
-- Atualizado para refletir relacionamentos entre as entidades

-- Tabela de Administradores
CREATE TABLE IF NOT EXISTS admins (
  id SERIAL PRIMARY KEY,
  login VARCHAR(50) NOT NULL UNIQUE,
  senha VARCHAR(255) NOT NULL,
  nome VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Tabela de Usuários (Torcedores)
CREATE TABLE IF NOT EXISTS usuarios (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  cpf VARCHAR(14) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL,
  telefone VARCHAR(20) NOT NULL,
  data_nascimento DATE,
  clube_coracao VARCHAR(100),
  foto TEXT,
  cadastrado_em TIMESTAMP DEFAULT NOW()
);

-- Tabela de Eventos
CREATE TABLE IF NOT EXISTS eventos (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(200) NOT NULL,
  data DATE NOT NULL,
  horario TIME,
  local VARCHAR(100) NOT NULL,
  descricao TEXT,
  criado_por_admin INT,
  created_at TIMESTAMP DEFAULT NOW(),
  CONSTRAINT fk_eventos_admin
    FOREIGN KEY (criado_por_admin) REFERENCES admins(id)
    ON DELETE SET NULL
);

-- Tabela de Movimentações (Entrada/Saída)
CREATE TABLE IF NOT EXISTS movimentacoes (
  id SERIAL PRIMARY KEY,
  tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('entrada', 'saida')),
  portao VARCHAR(50) NOT NULL,
  cpf VARCHAR(14) NOT NULL,
  nome VARCHAR(100) NOT NULL,
  data VARCHAR(10) NOT NULL,
  hora VARCHAR(8) NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  usuario_id INT,
  evento_id INT,
  created_at TIMESTAMP DEFAULT NOW(),
  CONSTRAINT fk_movimentacoes_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    ON DELETE SET NULL,
  CONSTRAINT fk_movimentacoes_evento
    FOREIGN KEY (evento_id) REFERENCES eventos(id)
    ON DELETE SET NULL
);

-- Tabela de Incidentes
CREATE TABLE IF NOT EXISTS incidentes (
  id SERIAL PRIMARY KEY,
  data DATE NOT NULL,
  horario TIME,
  setor VARCHAR(100),
  tipo VARCHAR(100),
  descricao TEXT,
  nivel VARCHAR(50),
  policiamento BOOLEAN DEFAULT FALSE,
  resolvido BOOLEAN DEFAULT FALSE,
  usuario_id INT,
  evento_id INT,
  created_at TIMESTAMP DEFAULT NOW(),
  CONSTRAINT fk_incidentes_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    ON DELETE SET NULL,
  CONSTRAINT fk_incidentes_evento
    FOREIGN KEY (evento_id) REFERENCES eventos(id)
    ON DELETE SET NULL
);

-- Dados de teste -----------------------------------------------------------

-- Administradores
INSERT INTO admins (login, senha, nome, email) VALUES
('admin', '123', 'Administrador do Sistema', 'admin@safestadium.com'),
('operador', '123', 'Operador SafeStadium', 'operador@safestadium.com')
ON CONFLICT (email) DO NOTHING;

-- Usuários
INSERT INTO usuarios (nome, cpf, email, telefone, data_nascimento, clube_coracao, cadastrado_em) VALUES
('Joao Silva Santos', '111.222.333-44', 'joao@email.com', '(11) 98765-4321', '1990-05-15', 'Atletico-MG', '2025-09-25T10:00:00Z'),
('Maria Oliveira Costa', '555.666.777-88', 'maria@email.com', '(11) 99999-8888', '1985-12-22', 'Cruzeiro', '2025-09-25T11:30:00Z'),
('Pedro Santos Oliveira', '999.888.777-66', 'pedro@email.com', '(31) 97777-5555', '1992-08-10', 'Flamengo', '2025-09-25T14:15:00Z')
ON CONFLICT (cpf) DO NOTHING;

-- Eventos
INSERT INTO eventos (nome, data, horario, local, descricao, criado_por_admin) VALUES
('Atletico-MG vs Flamengo', '2025-12-15', '16:00', 'Arena MRV', 'Partida valida pela 15a rodada do Campeonato Brasileiro. Classico nacional entre duas das maiores torcidas do pais.', 1),
('Show Nacional - Anitta', '2025-12-20', '20:00', 'Arena de Shows', 'Show da turne Funk Generation com participacoes especiais. Ingressos disponiveis na bilheteria oficial.', 1),
('Cruzeiro vs America-MG', '2025-12-22', '18:30', 'Estadio Mineirao', 'Classico mineiro valido pelo Campeonato Estadual. Derby tradicional entre as duas maiores equipes de Minas Gerais.', 2),
('Festival de Musica Eletronica', '2025-12-28', '22:00', 'Arena Principal', 'Noite especial com os melhores DJs nacionais e internacionais. Evento para maiores de 18 anos.', 2)
ON CONFLICT DO NOTHING;

-- Movimentações
INSERT INTO movimentacoes (tipo, portao, cpf, nome, data, hora, timestamp, usuario_id, evento_id) VALUES
('entrada', 'Portao Principal', '11122233344', 'Joao Silva Santos', '25/09/2025', '14:30', '2025-09-25T17:30:00.000Z', 1, 1),
('entrada', 'Portao Sul', '55566677788', 'Maria Oliveira Costa', '25/09/2025', '14:35', '2025-09-25T17:35:00.000Z', 2, 2),
('saida', 'Portao Principal', '11122233344', 'Joao Silva Santos', '25/09/2025', '18:15', '2025-09-25T21:15:00.000Z', 1, 1)
ON CONFLICT DO NOTHING;

-- Incidentes
INSERT INTO incidentes (data, horario, setor, tipo, descricao, nivel, policiamento, resolvido, usuario_id, evento_id) VALUES
('2025-09-25', '16:40', 'Setor Norte', 'Conflito entre torcedores', 'Equipe de seguranca acionada para conter conflito no Setor Norte.', 'Alto', TRUE, FALSE, 1, 1),
('2025-09-25', '15:10', 'Estacionamento', 'Furto', 'Torcedor reportou furto de objetos pessoais no estacionamento externo.', 'Medio', FALSE, TRUE, 2, 2),
('2025-09-26', '18:20', 'Setor VIP', 'Atendimento medico', 'Atendimento medico realizado apos principio de desmaio de torcedor.', 'Baixo', TRUE, TRUE, 3, 3)
ON CONFLICT DO NOTHING;
