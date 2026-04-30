# Prueba Técnica — Arquitectura Microservicios (Perfil SemiSenior)

Solución completa al reto técnico de arquitectura de microservicios con Java Spring Boot, RabbitMQ, PostgreSQL y Docker.

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.2.5 | Framework base ambos microservicios |
| Spring Data JPA | - | Persistencia con herencia JOINED |
| Spring Security | 6 | Hashing de contraseñas con BCrypt |
| Spring AMQP | - | Comunicación asíncrona con RabbitMQ |
| PostgreSQL | 15 | Base de datos relacional |
| RabbitMQ | 3.12 | Broker de mensajes |
| Docker / Docker Compose | - | Despliegue de todos los servicios |
| JUnit 5 + Mockito | - | Pruebas unitarias e integración |
| Lombok | - | Reducción de boilerplate |

---

## Funcionalidades implementadas

| ID | Descripción | Estado |
|---|---|---|
| F1 | CRUD completo: Clientes, Cuentas y Movimientos | ✅ |
| F2 | Registro de movimientos y actualización de saldo disponible | ✅ |
| F3 | Validación de saldo: retorna `"Saldo no disponible"` (HTTP 400) | ✅ |
| F4 | Reporte de estado de cuenta por cliente y rango de fechas | ✅ |
| F5 | Pruebas unitarias (Cliente y Movimiento) | ✅ |
| F6 | Pruebas de integración con MockMvc | ✅ |

---

### ms-clientes (puerto 8080)
- Gestiona las entidades `Persona` y `Cliente` (herencia JPA tipo JOINED)
- Aplica BCrypt al almacenar contraseñas
- Publica eventos a RabbitMQ cuando se crea o elimina un cliente

### ms-cuentas (puerto 8081)
- Gestiona las entidades `Cuenta` y `Movimiento`
- Consume eventos de RabbitMQ y mantiene un caché en memoria con los nombres de clientes
- Genera el reporte de estado de cuenta (F4)

---

## Estructura del proyecto

```
ejercicio-tecnico-backend2/
├── ms-clientes/                  # Microservicio de clientes
│   ├── src/main/java/.../
│   │   ├── domain/               # Entidades y repositorios
│   │   ├── application/          # DTOs, servicios
│   │   └── infrastructure/       # Controladores, config, mensajería, excepciones
│   └── src/test/java/...         # Pruebas unitarias e integración
├── ms-cuentas/                   # Microservicio de cuentas
│   ├── src/main/java/.../
│   │   ├── domain/
│   │   ├── application/
│   │   └── infrastructure/
│   └── src/test/java/...
├── BaseDatos.sql                 # Script DDL de inicialización
├── docker-compose.yml            # Orquestación de los 4 servicios
├── BancoMicroservicios.postman_collection.json
└── README.md
```

---

## Cómo ejecutar

### Requisitos previos
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y en ejecución

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/CDavilad/ejercicio-tecnico-backend2.git
cd ejercicio-tecnico-backend2

# 2. Levantar todos los servicios
docker-compose up --build -d

# 3. Verificar que los 4 contenedores estén corriendo
docker ps
```

Los servicios tardan unos segundos en iniciar. El orden de arranque es:
`postgres` → `rabbitmq` → `ms-clientes` → `ms-cuentas`

### Servicios disponibles

| Servicio | URL |
|---|---|
| ms-clientes API | http://localhost:8080/api |
| ms-cuentas API | http://localhost:8081/api |
| RabbitMQ Management | http://localhost:15672 (admin / admin123) |

### Detener los servicios

```bash
docker-compose down
```

Para eliminar también los volúmenes (datos de la BD):

```bash
docker-compose down -v
```

---

## Endpoints disponibles

### ms-clientes — `http://localhost:8080/api`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/clientes` | Listar todos los clientes |
| GET | `/clientes/{id}` | Obtener cliente por ID |
| POST | `/clientes` | Crear cliente |
| PUT | `/clientes/{id}` | Actualizar cliente completo |
| PATCH | `/clientes/{id}` | Actualizar cliente parcial |
| DELETE | `/clientes/{id}` | Eliminar cliente |

### ms-cuentas — `http://localhost:8081/api`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/cuentas` | Listar todas las cuentas |
| GET | `/cuentas/{id}` | Obtener cuenta por ID |
| POST | `/cuentas` | Crear cuenta |
| PUT | `/cuentas/{id}` | Actualizar cuenta completa |
| PATCH | `/cuentas/{id}` | Actualizar cuenta parcial |
| DELETE | `/cuentas/{id}` | Eliminar cuenta |
| GET | `/movimientos` | Listar movimientos |
| GET | `/movimientos/{id}` | Obtener movimiento por ID |
| POST | `/movimientos` | Registrar movimiento (F2/F3) |
| DELETE | `/movimientos/{id}` | Eliminar movimiento |
| GET | `/reportes?clienteId=&fechaInicio=&fechaFin=` | Reporte estado de cuenta (F4) |

#### Ejemplo reporte (F4):
```
GET /api/reportes?clienteId=marianela.montalvo&fechaInicio=2026-04-01&fechaFin=2026-04-30
```

---

## Validación con Postman

1. Abrir Postman
2. Importar el archivo `BancoMicroservicios.postman_collection.json`
3. Ejecutar la carpeta **"00 - Flujo Completo PDF"** en orden (crea todos los datos base)
4. Las demás carpetas (`01`, `02`, `03`, `04`) usan variables automáticas generadas en el paso anterior

---

## Pruebas unitarias

```bash
# ms-clientes (10 pruebas)
cd ms-clientes
mvn test

# ms-cuentas (4 pruebas)
cd ms-cuentas
mvn test
```

Las pruebas usan H2 en memoria, no requieren Docker.

---

## Comunicación asíncrona

Cuando se crea o elimina un cliente en `ms-clientes`, se publica un evento a RabbitMQ:

- **Exchange:** `banco.exchange` (TopicExchange)
- **Routing keys:** `cliente.creado` / `cliente.eliminado`
- **Queue:** `clientes.queue`

`ms-cuentas` consume estos eventos y mantiene un caché `clienteId → nombre` que usa al generar reportes (F4), sin necesidad de llamadas HTTP síncronas entre microservicios.
