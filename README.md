All credits go to https://dev.to/issam1991/crafting-high-performance-full-stack-applications-quarkus-native-and-angular-2eif 


# Native Quarkus + Angular User Management System

A full-stack web application demonstrating modern Java development with Quarkus Native Image compilation and Angular frontend. This project showcases a complete user management system with REST API, database integration, and a responsive web interface.

## 🚀 Features

### Backend (Quarkus Native)
- **Native Image Compilation**: Ultra-fast startup times with GraalVM native image
- **RESTful API**: Complete CRUD operations for user management
- **Database Integration**: SQLite (file-based) with Hibernate ORM Panache
- **API Documentation**: OpenAPI 3.0 / Swagger UI integration
- **Docker Support**: Containerized deployment with Docker Compose
- **CORS Configuration**: Cross-origin resource sharing enabled

### Frontend (Angular)
- **Modern Angular**: Built with Angular 20+ and standalone components
- **Server-Side Rendering (SSR)**: Enhanced performance and SEO
- **Responsive Design**: Mobile-friendly user interface
- **TypeScript**: Type-safe development
- **HTTP Client**: RESTful API integration

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌──────────────────────────┐
│   Angular 20+   │    │    Quarkus 3    │    │    SQLite (file)         │
│   Frontend      │◄──►│   Native Image  │◄──►│    Database              │
│   (Port 4200)   │    │   (Port 8080)   │    │   (file: ./quarkus/data/users.db) │
└─────────────────┘    └─────────────────┘    └──────────────────────────┘
```

## 🛠️ Technology Stack

### Backend
- **Java 21** - Latest LTS version
- **Quarkus 3.15.1** - Supersonic Subatomic Java framework
- **Hibernate ORM Panache** - Simplified data access
- **SQLite** - Embedded file-based relational database
- **GraalVM Native Image** - Native compilation
- **SmallRye OpenAPI** - API documentation
- **Maven** - Build tool

### Frontend
- **Angular 20.3.0** - Modern web framework
- **TypeScript 5.9.2** - Type-safe JavaScript
- **Angular SSR** - Server-side rendering
- **RxJS** - Reactive programming
- **Express** - SSR server

### DevOps
- **Docker & Docker Compose** - Containerization
- **Maven** - Java build tool
- **npm** - Node.js package manager

## 📋 Prerequisites

- **Java 21** or higher
- **Node.js 18+** and npm
- **Docker** and Docker Compose
- **Maven 3.6+**

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/issam1991/quarkus-native-angular-sample
cd quarkus-native-angular-sample
```

### 2. Database (SQLite)

This project uses SQLite as an embedded, file-based database by default. No separate database container is required — the database file is created at `quarkus/data/users.db` when the application runs.

If you prefer to run a MariaDB container instead, you can start it with Docker Compose (optional):

```bash
docker-compose up mariadb -d
```

### 3. Build and Run Backend
```bash
cd quarkus

# Run in development mode
./mvnw quarkus:dev

# Build the application
./mvnw clean package

# Build native executable
./mvnw clean package -Pnative

# Build native Docker image
./mvnw clean package -Pnative -Dquarkus.native.container-build=true
```

### 4. Build and Run Frontend
```bash
cd front
npm install
npm start
```

### 5. Access the Application
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/q/swagger-ui

## 🐳 Docker Deployment

### Full Stack with Docker Compose

The Docker Compose setup uses a pre-built native Docker image. You need to build the native Docker image first:

```bash
# Build the native Docker image
cd quarkus
./mvnw -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true -Dquarkus.container-image.name=quarkus-native-users -Dquarkus.container-image.tag=latest clean package

# Then start all services (from project root)
cd ..
docker-compose up -d
```

Alternatively, you can build the native executable first, then build the Docker image manually:
```bash
cd quarkus
# Build native executable
./mvnw -Pnative -Dquarkus.native.container-build=true clean package
# Build Docker image
docker build -f src/main/docker/Dockerfile.native -t quarkus-native-users:latest .
cd ..
docker-compose up -d
```

This will start:
- Quarkus native application on port 8080
- Frontend on port 4200

Note: The application uses SQLite by default; no DB container is needed.

**Note:** The `docker-compose.yml` expects the Docker image `quarkus-native-users:latest` to be available. Make sure you build it first using one of the methods above.

## 📚 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/count` | Get user count |
| POST | `/api/users` | Create new user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Example API Usage

```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'

# Get all users
curl http://localhost:8080/api/users

# Update a user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Doe", "email": "jane@example.com"}'
```

## 🗄️ Database Schema (SQLite)

```sql
CREATE TABLE users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE
);
```

## 🔧 Configuration

### Application Properties
- Database connection: `quarkus/src/main/resources/application.properties`
- Docker environment variables in `docker-compose.yml`

### Environment Variables
- `QUARKUS_DATASOURCE_JDBC_URL`: Database connection URL
- `QUARKUS_DATASOURCE_USERNAME`: Database username
- `QUARKUS_DATASOURCE_PASSWORD`: Database password

## 🧪 Testing

### Backend Tests
```bash
cd quarkus
./mvnw test
```

### Frontend Tests
```bash
cd front
npm test
```

## 📦 Build Commands

### Backend
```bash
cd quarkus

# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package JAR
./mvnw package

# Build native executable
./mvnw clean package -Pnative

# Build native Docker image
./mvnw clean package -Pnative -Dquarkus.native.container-build=true
```

### Frontend
```bash
cd front

# Install dependencies
npm install

# Development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Build with SSR
npm run build
npm run serve:ssr:user-management
```

## 🚀 Performance Benefits

### Native Image Advantages
- **Ultra-fast startup**: ~50ms vs ~3-5 seconds for JVM
- **Lower memory footprint**: ~50MB vs ~200MB+ for JVM
- **Better resource utilization**: Optimized for cloud deployments
- **Instant scaling**: Perfect for serverless and microservices

## 🔍 Monitoring and Observability

- **Health Checks**: Built-in Quarkus health endpoints
- **API Documentation**: Interactive Swagger UI at `/q/swagger-ui`
- **Database Monitoring**: Connection pool metrics
- **Logging**: Structured logging with configurable levels

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Quarkus team for the excellent framework
- Angular team for the modern frontend framework
- GraalVM team for native image compilation
- SQLite project for the lightweight file-based database

## 📞 Support

If you have any questions or need help, please:
- Open an issue on GitHub
- Check the documentation
- Review the API documentation at `/q/swagger-ui`

---

**Happy Coding! 🎉**
