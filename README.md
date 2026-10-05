# Race Walking System (Marcha Atlética Platform)

Plataforma Web completa para gestão técnica e arbitragem de provas de Marcha Atlética (World Athletics / R.F.E.A.).

Este projeto substitui a versão protótipo móvel (baseada em Google Firebase) por uma aplicação Web moderna, desacoplada, escalável e independente, executada sobre **Spring Boot 3 (Java 21)**, **Vue.js 3** e orquestrada com **Docker**.

---

## Dependencies

- **Require download**
  - Java 21
  - Maven
  - Node 14+ (Node Version Manager recommended)
  - Docker
- **No download required**
  - Spring-boot
  - Vue.js

---

## 🏗️ Arquitetura do Sistema

```
Personal_Project-SpeedWalkingPlatform/
├── backend/                  # API REST em Spring Boot 3 (Java 21)
│   ├── src/main/java/        # Controladores, Serviços, Modelos JPA e Segurança
│   ├── src/main/resources/   # Configuração de base de dados (H2 local / PostgreSQL Docker)
│   ├── Dockerfile            # Multi-stage build com Maven & Temurin Java 21
│   ├── mvnw & mvnw.cmd       # Maven Wrapper integrado
│   └── pom.xml               # Dependências do ecossistema Spring Boot
├── frontend/                 # Aplicação SPA em Vue.js 3 + Vite
│   ├── src/                  # Vistas, Componentes táteis, Pinia stores e Rotas
│   ├── Dockerfile            # Multi-stage build com Node & Nginx
│   ├── nginx.conf            # Reverse Proxy para o backend e History Mode
│   └── package.json          # Dependências Vue.js
└── docker-compose.yml        # Orquestração do PostgreSQL, Backend e Frontend
```

---

## 🏃 Regras da Marcha Atlética Implementadas

De acordo com os regulamentos técnicos oficiais da **World Athletics** e da **R.F.E.A.**:

1. **Infrações Avaliadas**:
   - **`>` Flexão de Joelho (*Bent Knee*)**: A perna dianteira deve estar estendida (sem flexão no joelho) desde o primeiro contacto até à posição vertical.
   - **`~` Perda de Contacto (*Loss of Contact*)**: Contacto ininterrupto com o solo, sem fase de voo percetível ao olho humano.
2. **Tipos de Notificações**:
   - **Yellow Paddle (YP / Aviso)**: Aviso de advertência quando o atleta está em risco de violar a regra. Cada juiz só pode exibir 1 aviso de cada tipo ao mesmo atleta.
   - **Red Card (RC / Proposta de Desqualificação)**: Notificação de violação enviada ao Secretariado / Juiz Chefe.
3. **Quadro de Desqualificação (Posting Board)**:
   - **3 Cartões Vermelhos** emitidos por **3 juízes distintos** resultam na **Desqualificação imediata (DQ)** do atleta.
   - O sistema suporta ainda o modo *Penalty Zone / Pit Lane* (3 cartões = paragem obrigatória, 4º cartão = DQ).

---

## 🚀 Como Executar

### Opção 1: Com Docker (Recomendado - Tudo incluído)

Não necessita de instalar ferramentas adicionais além do Docker:

```bash
docker compose up --build
```

A aplicação estará disponível em:
- **Frontend Web**: [http://localhost:3000](http://localhost:3000)
- **API REST Backend**: [http://localhost:8080/api](http://localhost:8080/api)
- **Base de Dados PostgreSQL**: Porta `5432`

Para encerrar os contentores:
```bash
docker compose down
```

---

### Opção 2: Execução Local (Maven & Node)

#### 1. Backend (Java 21 & Maven)

O backend inclui configuração automática com base de dados H2 em memória caso o PostgreSQL não esteja a correr:

```bash
cd backend

# No Windows:
.\mvnw.cmd spring-boot:run

# No Linux / macOS:
./mvnw spring-boot:run
```

O servidor iniciará em `http://localhost:8080`.
Console da base de dados H2 disponível em: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:speedwalking`, utilizador: `sa`, sem password).

#### 2. Frontend (Node & Vue.js)

```bash
cd frontend
npm install
npm run dev
```

Aceda à interface em [http://localhost:3000](http://localhost:3000).

---

## 🔑 Contas de Demonstração

O sistema inicializa automaticamente dados de teste com competições, juízes e atletas:

| Utilizador | Password | Perfil | Código Juiz |
| :--- | :--- | :--- | :--- |
| `juiz1` | `pass123` | Juiz de Marcha | `J01` (António Silva) |
| `juiz2` | `pass123` | Juiz de Marcha | `J02` (Beatriz Costa) |
| `juiz3` | `pass123` | Juiz de Marcha | `J03` (Carlos Ferreira) |
| `admin` | `admin123` | Secretariado Chefe / Árbitro | `JC` |

*(A página de login dispõe de atalhos rápidos para preenchimento com 1 clique).*

---

## 📡 Endpoints da API REST

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Autenticação e emissão de JWT Token |
| `GET` | `/api/auth/judges` | Lista de juízes registados |
| `GET` | `/api/competitions` | Lista de competições |
| `GET` | `/api/athletes?competitionId=1` | Lista de atletas inscritos na prova |
| `GET` | `/api/athletes/bib/{bib}?competitionId=1` | Pesquisa de atleta pelo dorsal (BIB) |
| `POST` | `/api/athletes` | Registo de novo atleta |
| `POST` | `/api/infractions` | Submissão de infração (YP ou RC) |
| `GET` | `/api/infractions?competitionId=1` | Histórico cronológico de infrações |
| `GET` | `/api/board/summary?competitionId=1` | Quadro de Desqualificações (Posting Board) |
