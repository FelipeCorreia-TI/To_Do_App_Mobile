# 📋 To Do App Mobile

Aplicativo mobile para gerenciamento de tarefas desenvolvido com **Kotlin**, **Jetpack Compose**, **Flask** e **MySQL**.

O sistema permite que usuários realizem cadastro, autenticação e gerenciamento completo de tarefas através de uma aplicação Android integrada a uma API REST.

---

# 📖 Sobre o Projeto

O **To Do App Mobile** foi desenvolvido como projeto acadêmico com o objetivo de aplicar conceitos de:

* Desenvolvimento Mobile
* APIs REST
* Banco de Dados Relacional
* Arquitetura Cliente-Servidor
* Integração Android com Backend

A aplicação possui autenticação de usuários e gerenciamento individual de tarefas, garantindo que cada usuário visualize apenas seus próprios registros.

---

# 🚀 Funcionalidades

## 👤 Autenticação

* Cadastro de novos usuários
* Login de usuários cadastrados
* Armazenamento seguro de senhas utilizando hash criptográfico

## ✅ Gerenciamento de Tarefas

* Criar tarefas
* Listar tarefas
* Editar tarefas
* Excluir tarefas
* Marcar tarefas como concluídas
* Persistência dos dados em banco MySQL

---

# 🛠️ Tecnologias Utilizadas

## Front-End (Android)

* Kotlin
* Jetpack Compose
* Material Design 3
* Navigation Compose
* Retrofit
* Gson Converter

## Back-End

* Python
* Flask
* Flask-SQLAlchemy
* Werkzeug Security

## Banco de Dados

* MySQL
* SQL

## Ferramentas

* Android Studio
* Visual Studio Code
* Git
* GitHub

---

# 🏗️ Arquitetura do Sistema

```text
┌────────────────────┐
│   Android App      │
│ Kotlin + Compose   │
└──────────┬─────────┘
           │ HTTP/JSON
           ▼
┌────────────────────┐
│     API Flask      │
│      Python        │
└──────────┬─────────┘
           │ SQLAlchemy
           ▼
┌────────────────────┐
│      MySQL         │
│     Database       │
└────────────────────┘
```

---

# 📂 Estrutura do Projeto

```text
To_Do_App_Mobile
│
├── Front_End
│   ├── app
│   ├── gradle
│   ├── MainActivity.kt
│   ├── TeladeLogin.kt
│   └── TelaAppToDoList.kt
│
├── Back_End
│   └── Main.py
│
└── Data_Base
    ├── LoginSQL.sql
    └── scriptdb.sql
```

---

# 🗄️ Banco de Dados

## Tabela de Usuários

```sql
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL
);
```

### Campos

| Campo | Tipo         | Descrição                   |
| ----- | ------------ | --------------------------- |
| id    | INT          | Identificador do usuário    |
| email | VARCHAR(100) | E-mail utilizado para login |
| senha | VARCHAR(255) | Senha criptografada         |

---

## Tabela de Tarefas

```sql
CREATE TABLE tarefas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT,
    concluido BOOLEAN DEFAULT FALSE,
    usuario_id INT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
```

### Campos

| Campo      | Tipo         | Descrição               |
| ---------- | ------------ | ----------------------- |
| id         | INT          | Identificador da tarefa |
| titulo     | VARCHAR(100) | Título da tarefa        |
| descricao  | TEXT         | Descrição da tarefa     |
| concluido  | BOOLEAN      | Status da tarefa        |
| usuario_id | INT          | Usuário proprietário    |

---

# 🔗 API REST

## Cadastro de Usuário

### Endpoint

```http
POST /cadastro
```

### Exemplo de Requisição

```json
{
  "email": "usuario@email.com",
  "senha": "123456"
}
```

---

## Login

### Endpoint

```http
POST /login
```

### Exemplo de Requisição

```json
{
  "email": "usuario@email.com",
  "senha": "123456"
}
```

### Exemplo de Resposta

```json
{
  "status": "Sucesso",
  "usuario_id": 1,
  "email": "usuario@email.com"
}
```

---

## Buscar Tarefas

### Endpoint

```http
GET /tarefas
```

---

## Criar Tarefa

### Endpoint

```http
POST /tarefas
```

### Exemplo

```json
{
  "titulo": "Estudar Kotlin",
  "descricao": "Praticar Jetpack Compose",
  "usuario_id": 1
}
```

---

## Atualizar Tarefa

### Endpoint

```http
PUT /tarefas/{id}
```

---

## Excluir Tarefa

### Endpoint

```http
DELETE /tarefas/{id}
```

---

# 🔒 Segurança

As senhas dos usuários não são armazenadas em texto puro.

O sistema utiliza os métodos:

```python
generate_password_hash()
check_password_hash()
```

fornecidos pela biblioteca Werkzeug para armazenamento seguro das credenciais.

---

# 📱 Executando o Projeto

## 1. Clonar o Repositório

```bash
git clone https://github.com/FelipeCorreia-TI/To_Do_App_Mobile.git
```

---

## 2. Configurar o Banco de Dados

Execute os scripts localizados em:

```text
Data_Base/LoginSQL.sql
Data_Base/scriptdb.sql
```

---

## 3. Instalar Dependências do Backend

```bash
pip install flask
pip install flask_sqlalchemy
pip install pymysql
pip install werkzeug
```

---

## 4. Configurar o Banco no Flask

Ajuste as configurações de conexão no arquivo:

```text
Back_End/Main.py
```

Exemplo:

```python
app.config['SQLALCHEMY_DATABASE_URI'] = \
'mysql+pymysql://usuario:senha@localhost/to_do_list'
```

---

## 5. Executar o Backend

```bash
python Main.py
```

Servidor iniciado em:

```text
http://localhost:5000
```

---

## 6. Configurar o Aplicativo Android

No Retrofit, configure o endereço do servidor:

```kotlin
private const val BASE_URL = "http://10.0.2.2:5000/"
```

### Observação

Para emuladores Android:

```text
10.0.2.2
```

representa o localhost da máquina hospedeira.

---

## 7. Executar o Aplicativo

Abra a pasta:

```text
Front_End
```

no Android Studio.

Execute em:

* Emulador Android
* Dispositivo físico

---

# 📸 Telas do Sistema

* Tela de Login
* Tela de Cadastro
* Tela Principal de Tarefas
* Tela de Edição de Tarefas

---

# 🔄 Fluxo da Aplicação

```text
Usuário
   │
   ▼
Tela de Login
   │
   ▼
API Flask
   │
   ▼
Autenticação
   │
   ▼
Tela Principal
   │
   ├── Criar Tarefa
   ├── Editar Tarefa
   ├── Excluir Tarefa
   └── Concluir Tarefa
   │
   ▼
Banco de Dados MySQL
```

---

# 📚 Conceitos Aplicados

* Programação Mobile
* Jetpack Compose
* Navegação entre telas
* Consumo de APIs REST
* Retrofit
* CRUD
* Banco de Dados Relacional
* SQLAlchemy
* Hash de Senhas
* Arquitetura Cliente-Servidor

---

# 👨‍💻 Autor

**Felipe** e **Lucas**

Projeto desenvolvido para fins acadêmicos na disciplina de Desenvolvimento Mobile.
