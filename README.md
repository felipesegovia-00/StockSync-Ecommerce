<div align="center">
  <img src="https://github.com/user-attachments/assets/40a97c72-f0f4-403a-9af9-d5bb08a6139c" alt="StockSync Logo" width="300"/>

  # StockSync
  *Inventario sin errores, negocio sin límites.*
</div>

## Sobre el Proyecto
**StockSync** es un sistema de gestión de inventario y E-commerce desarrollado como proyecto integrador. Diseñado para optimizar el control de stock, gestionar pedidos y facilitar la administración de productos en tiempo real, garantizando trazabilidad y eficiencia.

## Arquitectura

```
┌──────────────────────────────────────────────────────────┐
│                    StockSync                             │
│                                                          │
│  ┌─────────────────────┐     ┌────────────────────────┐  │
│  │   Frontend (Vue 3)  │     │   Backend (Spring Boot)│  │
│  │   Vuetify + Pinia   │───▶│   REST API + JPA + JWT │  │
│  │   Vue Router (hash) │     │   PostgreSQL + Flyway  │  │
│  └─────────────────────┘     └────────────────────────┘  │
│         localhost:5173              localhost:8080       │
│         (dev) / /api/ (prod)        /api/v1/*            │
└──────────────────────────────────────────────────────────┘
```

## Tech Stack

### Backend
- **Java 21** con Spring Boot 4.0.6
- **Spring Security** + JWT (jjwt 0.13.0)
- **Spring Data JPA** + Hibernate
- **PostgreSQL** + Flyway Migrations
- **MapStruct** para mapeo DTO/Entity
- **Springdoc OpenAPI** (Swagger UI)

### Frontend
- **Vue 3** (Composition API + `<script setup>`)
- **Vite** como bundler
- **Vuetify 4** (Material Design components)
- **Pinia** para estado global
- **Vue Router 4** (hash mode)
- **Axios** con interceptors JWT

## Requisitos

- Java 21+
- Node.js 18+
- PostgreSQL 15+

## Configuración

### Backend

```bash
cd backend
cp .env.example .env   # editar credenciales de DB
./mvnw spring-boot:run
```

### Frontend (desarrollo)

```bash
cd frontend
npm install
npm run dev
# Abrir http://localhost:5173/api/
```

### Build producción

```bash
cd frontend
npm run deploy         # build + copia a backend/static/
cd ../backend
./mvnw spring-boot:run
# Servido en http://localhost:8080/api/
```

## Estructura del proyecto

```
StockSync-Ecommerce/
├── backend/                          # Spring Boot REST API
│   ├── src/main/java/
│   │   ├── auth/                     # Auth (login, register, JWT)
│   │   ├── security/                 # JWT filter, SecurityConfig
│   │   ├── stock/                    # Productos, Bodegas, Stock, Categorías
│   │   └── user/                     # Usuarios
│   └── src/main/resources/
│       ├── db/migration/             # Flyway migrations
│       └── static/                   # Frontend build (generado)
├── frontend/                         # Vue 3 SPA
│   ├── src/
│   │   ├── api/                      # Axios client + endpoints
│   │   ├── stores/                   # Pinia stores
│   │   ├── components/               # Layouts reutilizables
│   │   ├── views/                    # Páginas
│   │   │   ├── public/               # Landing, Productos, Bodegas
│   │   │   ├── auth/                 # Login, Register
│   │   │   └── admin/                # Dashboard, CRUDs
│   │   ├── router/                   # Vue Router
│   │   └── plugins/                  # Vuetify config
│   └── vite.config.js
└── README.md
```

## API REST Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/login` | Iniciar sesión |
| POST | `/api/register` | Registrarse |
| GET/POST/PUT/DELETE | `/api/v1/products` | CRUD productos |
| GET/POST/PUT/DELETE | `/api/v1/warehouses` | CRUD bodegas |
| GET/POST/PUT/DELETE | `/api/v1/categories` | CRUD categorías |
| GET/POST/PUT/DELETE | `/api/v1/stocks` | CRUD stock |
| POST | `/api/v1/stocks/transfer` | Transferir entre bodegas |
| GET/DELETE | `/api/users` | Listar/eliminar usuarios |
| GET/DELETE | `/api/admin/*` | Admin data endpoints |

Documentación Swagger: `http://localhost:8080/api/swagger-ui.html`

## Equipo

- **Allan Nuñez**
- **Dafne Mandujano**
- **Dante Escalona**
- **Felipe Segovia**
- **Renato Campos**

```bash
git clone https://github.com/felipesegovia-00/StockSync.git
```
