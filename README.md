# 📚 Liter Alura - Catálogo de Livros

## 🎯 Sobre o Projeto

**Liter Alura** é uma aplicação Java desenvolvida como parte do **Challenge da Alura** que implementa um catálogo interativo de livros utilizando a API Gutendex. A aplicação permite buscar, visualizar e gerenciar informações sobre os mais de 70.000 livros disponíveis no Project Gutenberg.

### 🌟 Características Principais

- **Interface via Terminal**: Interação completa através de linha de comando
- **Integração com API Gutendex**: Acesso ao catálogo do Project Gutenberg
- **Persistência de Dados**: Armazenamento em banco de dados MySQL
- **Busca Inteligente**: Pesquisa por título e autor
- **Arquitetura Spring Boot**: Framework robusto e escalável

## 🛠️ Tecnologias Utilizadas

- **Java 21**: Linguagem de programação principal
- **Spring Boot 3.5.4**: Framework principal da aplicação
- **Spring Data JPA**: Para persistência e manipulação de dados
- **MySQL**: Banco de dados relacional
- **Lombok**: Redução de código boilerplate
- **Jackson**: Processamento de JSON
- **Maven**: Gerenciamento de dependências

## 📋 Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- ☕ **Java 21** ou superior
- 🗄️ **MySQL 8.0** ou superior
- 📦 **Maven 3.6** ou superior

## ⚙️ Configuração do Ambiente

### 1. Clone o Repositório
```bash
git clone https://github.com/seu-usuario/liter-alura.git
cd liter-alura
```

### 2. Configuração do Banco de Dados

Crie um banco de dados MySQL:
```sql
CREATE DATABASE literalura;
```

Configure as credenciais no arquivo `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/literalura
spring.datasource.username=root
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 3. Instalação das Dependências
```bash
mvn clean install
```

## 🚀 Como Executar

Execute a aplicação através do Maven:
```bash
mvn spring-boot:run
```

Ou compile e execute o JAR:
```bash
mvn clean package
java -jar target/Prrojeto-Alura-Backend-0.0.1-SNAPSHOT.jar
```

## 📖 Como Usar

Após iniciar a aplicação, você verá um menu interativo no terminal com as seguintes opções:

```
========== LITER ALURA ==========
1 - Buscar livro pelo título
2 - Listar livros registrados
3 - Listar autores registrados
4 - Listar autores vivos em determinado ano
5 - Listar livros em determinado idioma
0 - Sair
=================================
```

### Funcionalidades Disponíveis:

1. **🔍 Buscar livro pelo título**: Pesquisa livros na API Gutendex pelo título
2. **📚 Listar livros registrados**: Exibe todos os livros salvos no banco de dados
3. **👤 Listar autores registrados**: Mostra todos os autores cadastrados
4. **📅 Listar autores vivos em determinado ano**: Filtra autores por período
5. **🌐 Listar livros por idioma**: Filtra livros pelo idioma
6. **🚪 Sair**: Encerra a aplicação

## 🏗️ Arquitetura do Projeto

```
src/main/java/br/com/literalura/
├── 📄 LiterAluraApplication.java    # Classe principal
├── 📁 dto/                          # Data Transfer Objects
│   ├── AutorDTO.java
│   ├── LivroDTO.java
│   └── ResponseAPI.java
├── 📁 exceptions/                   # Exceções customizadas
│   └── EntradaInvalidaException.java
├── 📁 mapper/                       # Mapeadores DTO ↔ Entity
│   ├── AutorMapper.java
│   └── LivroMapper.java
├── 📁 model/                        # Entidades JPA
│   ├── Autor.java
│   └── Livro.java
├── 📁 principal/                    # Interface do usuário
│   └── Principal.java
├── 📁 repository/                   # Repositórios JPA
│   ├── AutorRepository.java
│   └── LivroRepository.java
└── 📁 service/                      # Camada de serviços
    ├── ConsumoApi.java
    ├── ConverteDados.java
    ├── IConverteDados.java
    └── LivroService.java
```

## 🌐 API Gutendex

A aplicação utiliza a **API Gutendex** para acessar o catálogo do Project Gutenberg:

- **URL Base**: `https://gutendex.com/`
- **Documentação**: [gutendex.com](https://gutendx.com)
- **Repositório**: [GitHub - garethbjohnson/gutendx](https://github.com/garethbjohnson/gutendx)

### Endpoints Utilizados:
- `GET /books/` - Lista todos os livros
- `GET /books/?search={termo}` - Busca livros por termo

## 📊 Modelo de Dados

### Entidade Livro
- ID (Chave primária)
- Título
- Idiomas
- Número de downloads
- Autor (Relacionamento)

### Entidade Autor
- ID (Chave primária)
- Nome
- Ano de nascimento
- Ano de falecimento
- Livros (Relacionamento)

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto foi desenvolvido como parte do Challenge da Alura e está disponível para fins educacionais.

## 👨‍💻 Autor

Desenvolvido como parte do **Challenge Backend Java da Alura ONE**.

---

⭐ **Gostou do projeto? Deixe uma estrela!**

## 🔧 Solução de Problemas

### Erro de Conexão com MySQL
- Verifique se o MySQL está executando
- Confirme as credenciais em `application.properties`
- Certifique-se de que o banco `literalura` foi criado

### Erro de Dependências
```bash
mvn clean install -U
```

### Problemas com Java 21
- Verifique a versão do Java: `java -version`
- Configure a variável JAVA_HOME corretamente

---

**Challenge Alura** | **Java Backend** | **Spring Boot** | **API Integration**
