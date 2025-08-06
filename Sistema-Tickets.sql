CREATE DATABASE IF NOT EXISTS sistema_tickets CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistema_tickets;

CREATE TABLE departamentos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('USUARIO', 'TECNICO', 'ADMIN') NOT NULL DEFAULT 'USUARIO',
    departamento_id INT,
    ativo BOOLEAN DEFAULT TRUE,
    ultimo_login TIMESTAMP NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (departamento_id) REFERENCES departamentos(id) ON DELETE SET NULL,
    INDEX idx_email (email),
    INDEX idx_perfil (perfil)
);

CREATE TABLE categorias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    cor VARCHAR(7) DEFAULT '#4ecdc4',
    ativa BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE tickets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    status ENUM('ABERTO', 'EM_ANDAMENTO', 'RESOLVIDO', 'FECHADO') DEFAULT 'ABERTO',
    prioridade ENUM('BAIXA', 'MEDIA', 'ALTA', 'CRITICA') DEFAULT 'MEDIA',
    categoria_id INT NOT NULL,
    solicitante_id INT NOT NULL,
    responsavel_id INT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    data_resolucao TIMESTAMP NULL,
    data_fechamento TIMESTAMP NULL,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    FOREIGN KEY (solicitante_id) REFERENCES usuarios(id),
    FOREIGN KEY (responsavel_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_codigo (codigo),
    INDEX idx_status (status),
    INDEX idx_prioridade (prioridade),
    INDEX idx_solicitante (solicitante_id),
    INDEX idx_responsavel (responsavel_id),
    INDEX idx_data_criacao (data_criacao)
);

CREATE TABLE comentarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    usuario_id INT NOT NULL,
    conteudo TEXT NOT NULL,
    tipo ENUM('COMENTARIO', 'RESOLUCAO', 'INTERNO') DEFAULT 'COMENTARIO',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_ticket (ticket_id),
    INDEX idx_usuario (usuario_id),
    INDEX idx_data (data_criacao)
);

CREATE TABLE anexos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    nome_original VARCHAR(255) NOT NULL,
    nome_arquivo VARCHAR(255) NOT NULL,
    tamanho INT NOT NULL,
    tipo_mime VARCHAR(100),
    caminho VARCHAR(500) NOT NULL,
    data_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_id INT NOT NULL,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_ticket (ticket_id)
);

CREATE TABLE tags (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL UNIQUE,
    cor VARCHAR(7) DEFAULT '#96ceb4',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ticket_tags (
    ticket_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (ticket_id, tag_id),
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE TABLE historico_tickets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    usuario_id INT NOT NULL,
    acao VARCHAR(100) NOT NULL,
    campo_alterado VARCHAR(50),
    valor_anterior TEXT,
    valor_novo TEXT,
    data_alteracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_ticket (ticket_id),
    INDEX idx_data (data_alteracao)
);

CREATE TABLE configuracoes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    chave VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT,
    descricao TEXT,
    tipo ENUM('STRING', 'NUMBER', 'BOOLEAN', 'JSON') DEFAULT 'STRING',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

DELIMITER //
CREATE TRIGGER atualizar_data_resolucao 
BEFORE UPDATE ON tickets
FOR EACH ROW
BEGIN
    IF NEW.status IN ('RESOLVIDO', 'FECHADO') AND OLD.status NOT IN ('RESOLVIDO', 'FECHADO') THEN
        SET NEW.data_resolucao = CURRENT_TIMESTAMP;
    END IF;
    IF NEW.status = 'FECHADO' AND OLD.status != 'FECHADO' THEN
        SET NEW.data_fechamento = CURRENT_TIMESTAMP;
    END IF;
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER registrar_historico_ticket 
AFTER UPDATE ON tickets
FOR EACH ROW
BEGIN
    DECLARE usuario_atual INT DEFAULT 1;
    IF OLD.status != NEW.status THEN
        INSERT INTO historico_tickets (ticket_id, usuario_id, acao, campo_alterado, valor_anterior, valor_novo)
        VALUES (NEW.id, usuario_atual, 'ALTERACAO_STATUS', 'status', OLD.status, NEW.status);
    END IF;
    IF OLD.prioridade != NEW.prioridade THEN
        INSERT INTO historico_tickets (ticket_id, usuario_id, acao, campo_alterado, valor_anterior, valor_novo)
        VALUES (NEW.id, usuario_atual, 'ALTERACAO_PRIORIDADE', 'prioridade', OLD.prioridade, NEW.prioridade);
    END IF;
    IF IFNULL(OLD.responsavel_id, 0) != IFNULL(NEW.responsavel_id, 0) THEN
        INSERT INTO historico_tickets (ticket_id, usuario_id, acao, campo_alterado, valor_anterior, valor_novo)
        VALUES (NEW.id, usuario_atual, 'ALTERACAO_RESPONSAVEL', 'responsavel_id', OLD.responsavel_id, NEW.responsavel_id);
    END IF;
END//
DELIMITER ;

CREATE VIEW v_tickets_completos AS
SELECT 
    t.id, t.codigo, t.titulo, t.descricao, t.status, t.prioridade,
    c.nome AS categoria, c.cor AS categoria_cor,
    s.nome AS solicitante, s.email AS solicitante_email,
    s.departamento_id AS solicitante_departamento_id,
    r.nome AS responsavel, r.email AS responsavel_email,
    t.data_criacao, t.data_atualizacao, t.data_resolucao, t.data_fechamento,
    TIMESTAMPDIFF(HOUR, t.data_criacao, COALESCE(t.data_resolucao, NOW())) AS tempo_resolucao_horas,
    (SELECT COUNT(*) FROM comentarios WHERE ticket_id = t.id) AS total_comentarios,
    (SELECT COUNT(*) FROM anexos WHERE ticket_id = t.id) AS total_anexos
FROM tickets t
JOIN categorias c ON t.categoria_id = c.id
JOIN usuarios s ON t.solicitante_id = s.id
LEFT JOIN usuarios r ON t.responsavel_id = r.id;

CREATE VIEW v_estatisticas_status AS
SELECT 
    status,
    COUNT(*) as total,
    ROUND((COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tickets)), 2) as percentual
FROM tickets 
GROUP BY status;

CREATE VIEW v_estatisticas_prioridade AS
SELECT 
    prioridade,
    COUNT(*) as total,
    ROUND((COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tickets)), 2) as percentual
FROM tickets 
GROUP BY prioridade;

CREATE VIEW v_performance_tecnicos AS
SELECT 
    u.id, u.nome, u.email,
    COUNT(t.id) as tickets_atribuidos,
    COUNT(CASE WHEN t.status = 'RESOLVIDO' THEN 1 END) as tickets_resolvidos,
    COUNT(CASE WHEN t.status = 'FECHADO' THEN 1 END) as tickets_fechados,
    ROUND(AVG(TIMESTAMPDIFF(HOUR, t.data_criacao, t.data_resolucao)), 2) as tempo_medio_resolucao,
    ROUND((COUNT(CASE WHEN t.status IN ('RESOLVIDO', 'FECHADO') THEN 1 END) * 100.0 / COUNT(t.id)), 2) as taxa_resolucao
FROM usuarios u
LEFT JOIN tickets t ON u.id = t.responsavel_id
WHERE u.perfil IN ('TECNICO', 'ADMIN')
GROUP BY u.id, u.nome, u.email;

INSERT INTO departamentos (nome, descricao) VALUES 
('TI', 'Tecnologia da Informação'),
('RH', 'Recursos Humanos'),
('Financeiro', 'Departamento Financeiro'),
('Vendas', 'Departamento de Vendas'),
('Suporte', 'Suporte ao Cliente');

INSERT INTO usuarios (nome, email, senha, perfil, departamento_id) VALUES 
('Administrador Sistema', 'admin@sistema.com', SHA2('admin123', 256), 'ADMIN', 1),
('João Silva', 'joao.silva@empresa.com', SHA2('usuario123', 256), 'USUARIO', 2),
('Maria Santos', 'maria.santos@empresa.com', SHA2('tecnico123', 256), 'TECNICO', 1),
('Pedro Costa', 'pedro.costa@empresa.com', SHA2('usuario123', 256), 'USUARIO', 3),
('Ana Oliveira', 'ana.oliveira@empresa.com', SHA2('tecnico123', 256), 'TECNICO', 1);

INSERT INTO categorias (nome, descricao, cor) VALUES 
('Hardware', 'Problemas relacionados a equipamentos físicos', '#ff6b6b'),
('Software', 'Problemas relacionados a programas e aplicações', '#4ecdc4'),
('Rede', 'Problemas de conectividade e internet', '#45b7d1'),
('Acesso', 'Problemas de login, senhas e permissões', '#feca57'),
('Email', 'Problemas relacionados ao correio eletrônico', '#ff9ff3'),
('Impressora', 'Problemas com impressoras e digitalização', '#96ceb4'),
('Telefonia', 'Problemas com telefones e ramais', '#ff7675'),
('Outros', 'Outros tipos de problemas não categorizados', '#a29bfe');

INSERT INTO tags (nome, cor) VALUES 
('Urgente', '#ff6b6b'),
('Recorrente', '#feca57'),
('Simples', '#96ceb4'),
('Complexo', '#ff9ff3'),
('Hardware', '#45b7d1'),
('Software', '#4ecdc4'),
('Treinamento', '#a29bfe');

INSERT INTO configuracoes (chave, valor, descricao, tipo) VALUES 
('sistema_nome', 'Sistema de Tickets de Suporte', 'Nome do sistema', 'STRING'),
('tickets_por_pagina', '20', 'Número de tickets por página', 'NUMBER'),
('email_notificacoes', 'true', 'Enviar notificações por email', 'BOOLEAN'),
('tempo_auto_fechamento', '72', 'Horas para fechamento automático após resolução', 'NUMBER'),
('prioridade_padrao', 'MEDIA', 'Prioridade padrão para novos tickets', 'STRING');

DELIMITER //
CREATE PROCEDURE sp_criar_ticket(
    IN p_titulo VARCHAR(255),
    IN p_descricao TEXT,
    IN p_categoria_id INT,
    IN p_solicitante_id INT,
    IN p_prioridade ENUM('BAIXA', 'MEDIA', 'ALTA', 'CRITICA')
)
BEGIN
    DECLARE v_codigo VARCHAR(20);
    DECLARE v_ticket_id INT;
    SELECT CONCAT('TK', YEAR(NOW()), LPAD(COALESCE(MAX(id), 0) + 1, 6, '0')) 
    INTO v_codigo FROM tickets;
    INSERT INTO tickets (codigo, titulo, descricao, categoria_id, solicitante_id, prioridade)
    VALUES (v_codigo, p_titulo, p_descricao, p_categoria_id, p_solicitante_id, p_prioridade);
    SET v_ticket_id = LAST_INSERT_ID();
    INSERT INTO historico_tickets (ticket_id, usuario_id, acao)
    VALUES (v_ticket_id, p_solicitante_id, 'CRIACAO');
    SELECT v_ticket_id AS ticket_id, v_codigo AS codigo;
END//
DELIMITER ;
