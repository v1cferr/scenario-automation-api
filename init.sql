-- Script de inicialização do banco de dados
-- Este arquivo será executado automaticamente quando o container PostgreSQL for criado

-- Criar extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Inserir dados de exemplo (opcional)
-- Estes dados serão inseridos apenas se as tabelas não existirem

-- Nota: As tabelas serão criadas automaticamente pelo Hibernate
-- quando a aplicação Spring Boot iniciar com spring.jpa.hibernate.ddl-auto=update

-- Comentários sobre a estrutura esperada:

-- Tabela environments:
-- id (bigserial primary key)
-- name (varchar(100) not null)
-- description (varchar(500))
-- image_url (varchar)
-- created_at (timestamp)
-- updated_at (timestamp)

-- Tabela luminaires:
-- id (bigserial primary key)
-- environment_id (bigint references environments(id))
-- name (varchar(100) not null)
-- type (varchar(50) not null)
-- status (boolean default false)
-- brightness (integer default 0)
-- color (varchar(7) default '#FFFFFF')
-- position_x (decimal default 0.0)
-- position_y (decimal default 0.0)
-- created_at (timestamp)
-- updated_at (timestamp)

-- Exemplo de inserção de dados de teste (descomente se necessário):
/*
INSERT INTO environments (name, description, created_at, updated_at) VALUES 
('Sala de Estar', 'Ambiente principal da casa', NOW(), NOW()),
('Quarto', 'Quarto de dormir', NOW(), NOW()),
('Cozinha', 'Área de preparo de alimentos', NOW(), NOW());

INSERT INTO luminaires (environment_id, name, type, status, brightness, color, position_x, position_y, created_at, updated_at) VALUES 
(1, 'Lustre Central', 'LED', false, 0, '#FFFFFF', 5.0, 5.0, NOW(), NOW()),
(1, 'Spot 1', 'Dicroica', false, 0, '#FFFFFF', 2.0, 2.0, NOW(), NOW()),
(2, 'Abajur', 'Incandescente', false, 0, '#FFEB9C', 3.0, 1.0, NOW(), NOW()),
(3, 'Luminária de Bancada', 'LED', false, 0, '#FFFFFF', 1.0, 3.0, NOW(), NOW());
*/
