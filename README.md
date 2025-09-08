# Scenario Automation API

API back-end para automação e gerenciamento de cenários de iluminação inteligente, desenvolvida como parte da Imersão Team Cloud.

## 🚀 Sobre o Projeto

O Scenario Automation API é uma solução robusta para controle e automação de sistemas de iluminação, permitindo o gerenciamento completo de ambientes e luminárias através de uma interface RESTful moderna e segura.

## 🛠️ Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **Docker** (opcional)

## ⚡ Funcionalidades

### 🔐 Autenticação e Segurança

- Sistema de autenticação login/senha
- Geração e validação de tokens JWT
- Middleware de autorização para rotas protegidas

### 🏠 Gerenciamento de Ambientes

- ✅ Criar novo ambiente
- ✅ Listar todos os ambientes
- ✅ Editar ambiente existente
- ✅ Excluir ambiente
- ✅ Upload de imagens de ambiente
- ✅ Visualizar detalhes do ambiente

### 💡 Gerenciamento de Luminárias

- ✅ Listar luminárias por ambiente
- ✅ Inserir nova luminária
- ✅ Editar configurações de luminária
- ✅ Excluir luminária
- ✅ Controle de estado (ligado/desligado)
- ✅ Ajuste de intensidade e cor

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- PostgreSQL 12+
- Git

## 🔧 Instalação e Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/v1cferr/scenario-automation-api.git
cd scenario-automation-api
```

### 2. Configure o banco de dados

```sql
-- Criar database PostgreSQL
CREATE DATABASE scenario_automation;
CREATE USER scenario_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE scenario_automation TO scenario_user;
```

### 3. Configure as variáveis de ambiente

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/scenario_automation
spring.datasource.username=scenario_user
spring.datasource.password=your_password

# JWT Configuration
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000
```

### 4. Execute a aplicação

```bash
mvn clean install
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 🔗 Endpoints da API

### Autenticação

```http
POST /api/auth/login
POST /api/auth/register
```

### Ambientes

```http
GET    /api/environments          # Listar ambientes
POST   /api/environments          # Criar ambiente
GET    /api/environments/{id}     # Buscar por ID
PUT    /api/environments/{id}     # Atualizar ambiente
DELETE /api/environments/{id}     # Deletar ambiente
POST   /api/environments/{id}/image # Upload imagem
```

### Luminárias

```http
GET    /api/environments/{id}/luminaires     # Listar luminárias
POST   /api/environments/{id}/luminaires     # Criar luminária
PUT    /api/luminaires/{id}                 # Atualizar luminária
DELETE /api/luminaires/{id}                 # Deletar luminária
```

## 📊 Estrutura do Banco de Dados

### Tabela: environments

- id (Primary Key)
- name
- description
- image_url
- created_at
- updated_at

### Tabela: luminaires

- id (Primary Key)
- environment_id (Foreign Key)
- name
- type
- status (on/off)
- brightness (0-100)
- color
- position_x
- position_y
- created_at
- updated_at

## 🧪 Testes

```bash
# Executar todos os testes
mvn test

# Executar testes com cobertura
mvn clean test jacoco:report
```

## 📦 Deploy

### Usando Docker

```bash
# Build da imagem
docker build -t scenario-automation-api .

# Executar container
docker run -p 8080:8080 scenario-automation-api
```

### Usando Docker Compose

```bash
docker-compose up -d
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👥 Equipe

Desenvolvido durante a **Imersão Team Cloud** por:

- **v1cferr** - Desenvolvedor Principal

## 📞 Contato

- GitHub: [@v1cferr](https://github.com/v1cferr)
- Email: [dev.victorferreira@gmail.com]

---

**Scenario Automation API** - Transformando a automação residencial com tecnologia cloud ☁️✨
