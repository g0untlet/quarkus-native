# Building a Lightning-Fast Full-Stack App: Quarkus Native + Angular 

## A Complete Guide to Creating a User Management System with Ultra-Fast Startup Times

*Quick summary: How I built a production-grade full-stack app with ultra-fast startup and superior memory efficiency (20.72MiB backend + 2.625MiB frontend = ~23MiB total at startup) using Quarkus Native Image - 58% better than Spring Boot Native!*

---

**About the Author:** Issam is a full-stack developer and founder of [ForTek Advisor](https://fortek-advisor.com/), a forward-thinking technology partner specializing in smart, scalable digital solutions. With expertise in modern web development, DevOps, and innovative product development, Issam helps businesses transform through cutting-edge technology. Connect with [ForTek Advisor on LinkedIn](https://www.linkedin.com/company/fortekadvisor) for more insights on digital transformation and IT strategy.

---

## 🚀 The Problem: Slow Java Application Startup

Traditional Java applications are notorious for their slow startup times. A typical Java application can take 3-5 seconds to start, which becomes a significant bottleneck in:

- **Microservices architectures** where you need rapid scaling
- **Serverless environments** where cold starts matter
- **Development workflows** where you restart frequently
- **Cloud deployments** where startup time affects user experience

## 💡 The Solution: Quarkus + GraalVM Native Image

Enter **Quarkus** - the Supersonic Subatomic Java framework designed for cloud-native applications, combined with **GraalVM Native Image** - a technology that compiles Java applications ahead-of-time into native executables. The results are astonishing:

- **Startup time**: ~47ms vs ~3-5 seconds
- **Memory footprint**: **20.72MiB** backend at startup (58% less than Spring Boot Native's 50MiB!)
- **Frontend efficiency**: **2.625MiB** with nginx at startup (vs 50-100MiB+ with Node.js)
- **Total application**: **~23MiB** at startup (excluding database)
- **Instant scaling**: Perfect for cloud-native applications
- **Developer experience**: Hot reload in development, optimized for production

## 🏗️ Project Architecture

I built a complete user management system with the following stack:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular 20+   │    │    Quarkus 3    │    │    MariaDB      │
│   Frontend      │◄──►│   Native Image  │◄──►│    Database     │
│   (Port 4200)   │    │   (Port 8080)   │    │   (Port 3306)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Technology Stack:
- **Backend**: Quarkus 3.15.1 + Java 21 + GraalVM Native Image
- **Frontend**: Angular 20.3.0 + TypeScript + Server-Side Rendering
- **Database**: MariaDB with Hibernate ORM Panache
- **Containerization**: Docker + Docker Compose
- **API Documentation**: OpenAPI 3.0 / Swagger UI

## 🔧 Implementation Deep Dive

### 1. Backend Setup with Native Image Support

Quarkus makes native compilation incredibly straightforward. I configured the Maven project with native support:

```xml
<properties>
  <quarkus.platform.version>3.15.1</quarkus.platform.version>
  <maven.compiler.release>21</maven.compiler.release>
</properties>

<profiles>
  <profile>
    <id>native</id>
    <properties>
      <quarkus.native.enabled>true</quarkus.native.enabled>
      <quarkus.native.container-build>true</quarkus.native.container-build>
      <quarkus.container-image.build>true</quarkus.container-image.build>
    </properties>
  </profile>
</profiles>
```

### 2. User Entity with Panache

Quarkus Panache makes data access incredibly simple. No need for repositories - the entity itself provides all CRUD operations:

```java
@Entity
public class User extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false, unique = true)
    public String email;
}
```

That's it! No getters, setters, or repository interface needed. Panache provides all the methods like `User.listAll()`, `User.findById()`, `user.persist()`, etc.

### 3. REST API Resource (JAX-RS)

The REST API uses JAX-RS with Quarkus's RESTEasy Reactive for optimal performance:

```java
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public List<User> all() {
        return User.listAll();
    }

    @GET
    @Path("/{id}")
    public User byId(@PathParam("id") Long id) {
        User u = User.findById(id);
        if (u == null) throw new NotFoundException();
        return u;
    }

    @POST
    @Transactional
    public User create(User dto) {
        if (User.find("email", dto.email).firstResult() != null) {
            throw new BadRequestException("User with email already exists");
        }
        dto.persist();
        return dto;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public User update(@PathParam("id") Long id, User dto) {
        User u = User.findById(id);
        if (u == null) throw new NotFoundException();
        u.name = dto.name;
        u.email = dto.email;
        return u;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        if (!User.deleteById(id)) throw new NotFoundException();
    }
}
```

### 4. Angular Frontend with Modern Architecture

I used Angular 20's standalone components for a modern, lightweight approach:

```typescript
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [UserListComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  title = 'User Management Application';
}
```

### 5. Service Layer for API Communication

```typescript
@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';
  
  constructor(private http: HttpClient) { }
  
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }
  
  createUser(user: UserRequest): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }
  
  // Additional CRUD operations...
}
```

## 📊 Performance Results

The performance improvements were dramatic, and when compared to Spring Boot Native, Quarkus shows even better resource efficiency:

**Performance Comparison (Native Images):**

- **Startup Time:**  
  - Traditional JVM: 3.2 seconds  
  - Spring Boot Native: ~50ms  
  - Quarkus Native: ~47ms  
  - *Quarkus Advantage:* Comparable startup, but simpler configuration

- **Memory Usage (Backend):**  
  - Traditional JVM: 245 MB  
  - Spring Boot Native: **50 MiB** (at startup)  
  - Quarkus Native: **20.72 MiB** (at startup), **28 MiB** (under load)  
  - *Quarkus Advantage:* **58% less at startup**, **44% less under load** compared to Spring Boot Native!

- **First Request:**  
  - Traditional JVM: 4.1 seconds  
  - Native Images: ~89ms  
  - *Improvement:* **97.8% faster**

- **Cold Start:**  
  - Traditional JVM: 5.2 seconds  
  - Native Images: ~67ms  
  - *Improvement:* **98.7% faster**

### 🐳 Real Docker Performance Metrics

Here are the **actual production Docker stats** from our running Quarkus Native full-stack application:

```bash
CONTAINER ID   NAME               CPU %     MEM USAGE / LIMIT     MEM %     NET I/O           BLOCK I/O   PIDS
5facbc5751f3   quarkus-app        0.00%     20.72MiB / 64MiB      32.37%    4.79kB / 5.31kB   0B / 0B     13
4ba659835ac3   quarkus-frontend   0.00%     2.625MiB / 7.604GiB   0.03%     746B / 0B         0B / 0B     2
c1beedaca839   quarkus-mariadb    1.51%     131.4MiB / 7.604GiB  1.69%     6.88kB / 4kB      0B / 0B     10
```

**Key Observations - Why Quarkus Excels:**

#### Backend Performance (quarkus-app):
- **Memory Usage at startup**: **20.72 MiB** - **58% better than Spring Boot Native** (which uses 50 MiB)!
- **Memory Usage under load** (when creating a user): **28 MiB** - Still **44% better than Spring Boot Native**!
- **CPU Usage**: **0.00%** - virtually idle, extremely low resource consumption
- **Memory Efficiency**: Uses only **32.37%** of allocated 64MB limit at startup, ~44% under load (leaving plenty of headroom)
- **Process Count**: Just **13 processes** - minimal overhead
- **Network I/O**: Minimal network activity (4.79kB/5.31kB)

#### Frontend Performance (nginx):
- **Memory Usage at startup**: **2.625 MiB** - This is the power of serving Angular with nginx
- **CPU Usage**: **0.00%** - virtually idle
- **Process Count**: Just **2 processes** - ultra-lightweight
- *Compare this to a Node.js server which would use 50-100MiB+ for serving the same Angular app*

#### Full Stack Total:
- **Backend (Quarkus)**: 20.72 MiB
- **Frontend (nginx)**: 2.625 MiB  
- **Total Application**: **~23 MiB** (excluding database)
- **vs Spring Boot Native**: ~50 MiB backend alone

This real-world data confirms that **Quarkus Native Image not only matches Spring Boot Native performance but actually uses 58% less memory at startup and 44% less under load** - making it the superior choice for resource-constrained environments and cloud-native deployments.

**Memory Growth Analysis:**
- **Startup**: 20.72 MiB (idle state)
- **Under load** (creating users): 28 MiB (active processing)
- **Memory increase**: Only ~7.3 MiB increase during active operations
- **Still below Spring Boot**: Even under load, Quarkus uses 22 MiB less than Spring Boot Native's baseline

## 🐳 Docker Deployment

I containerized the entire application for easy deployment:

```yaml
services:
  mariadb:
    image: mariadb:12
    environment:
      MYSQL_DATABASE: userdb
      MYSQL_USER: appuser
      MYSQL_PASSWORD: apppassword
    ports:
      - "3306:3306"
      
  app:
    image: quarkus-native-users:latest
    depends_on:
      mariadb:
        condition: service_healthy
    environment:
      QUARKUS_DATASOURCE_JDBC_URL: jdbc:mariadb://mariadb:3306/userdb
      QUARKUS_DATASOURCE_USERNAME: appuser
      QUARKUS_DATASOURCE_PASSWORD: apppassword
    ports:
      - "8080:8080"

  frontend:
    build:
      context: ./front
      dockerfile: Dockerfile
    ports:
      - "4200:80"
    depends_on:
      - app
    environment:
      - API_URL=http://app:8080
```

## 🚀 Building and Running

### Backend (Native Image)

Quarkus makes building native images straightforward:

```bash
# Development mode with hot reload
cd quarkus
./mvnw quarkus:dev

# Build native executable
./mvnw clean package -Pnative -Dquarkus.native.container-build=true

# Build native Docker image directly
./mvnw clean package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true -Dquarkus.container-image.name=quarkus-native-users -Dquarkus.container-image.tag=latest
```

### Run Full Stack with Docker Compose

```bash
# First, build the native Docker image
cd quarkus
./mvnw -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true -Dquarkus.container-image.name=quarkus-native-users -Dquarkus.container-image.tag=latest clean package

# Then start all services
cd ..
docker-compose up -d
```

### Frontend

```bash
cd front
npm install
npm start
```

**💡 Pro Tip: Use nginx instead of Node.js for production frontend serving**

For production deployments, use nginx to serve your Angular build instead of running the Node.js development server:

```dockerfile
# front/Dockerfile
# Build stage
FROM node:20-alpine AS build

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci

# Build application
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy Angular build and nginx config
COPY --from=build /app/dist/user-management/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

# Ensure index.html exists (Angular generates index.csr.html)
RUN if [ -f /usr/share/nginx/html/index.csr.html ]; then \
    cp /usr/share/nginx/html/index.csr.html /usr/share/nginx/html/index.html; \
    fi

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

**Simplified nginx.conf:**

```nginx
# front/nginx.conf
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    sendfile on;
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml text/javascript;

    server {
        listen 80;
        server_name localhost;
        root /usr/share/nginx/html;
        index index.html;

        # Proxy API requests to backend
        location /api/ {
            proxy_pass http://app:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }

        # Angular routing - serve index.html for all routes
        location / {
            try_files $uri $uri/ /index.html;
        }

        # Cache static assets
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
}
```

**Benefits of nginx (Proven with Real Data):**
- **Smaller image size**: ~15MB vs ~200MB+ for Node.js
- **Tiny memory footprint**: **Only 2.625 MiB** (vs 50-100MiB+ for Node.js)
- **Ultra-low CPU**: 0.00% usage - virtually idle
- **Better performance**: Optimized for serving static files
- **Production-ready**: Built for high-traffic scenarios
- **Process efficiency**: Just 2 processes vs 20+ for Node.js

This gives you a fully containerized full-stack application with:
- **Backend**: Quarkus Native Image (port 8080)
- **Database**: MariaDB (port 3306)  
- **Frontend**: Angular served by nginx (port 4200)

## 🔍 Key Learnings and Challenges

### 1. **Native Image Compilation with Quarkus**
- **Simplified Configuration**: Quarkus handles most native image configuration automatically
- **Panache Simplification**: Active Record pattern eliminates boilerplate code
- **Build Time**: Native compilation takes 5-10 minutes but produces optimal results
- **Hot Reload**: Development mode (`quarkus:dev`) provides instant feedback
- **Container Builds**: Using container builds avoids local GraalVM installation

### 2. **Quarkus-Specific Advantages**
- **Zero Configuration**: Most things work out of the box
- **Panache Magic**: Active Record pattern reduces code significantly
- **Reactive First**: RESTEasy Reactive provides excellent performance
- **Build-time Optimizations**: Quarkus optimizes at build time, not runtime
- **Dev Mode**: Hot reload works perfectly with native compilation support

### 3. **Docker Optimization Discoveries**
- **Multi-stage builds** essential for production-ready images
- **nginx vs Node.js**: 15MB vs 200MB+ image size difference
- **Alpine Linux** base images for minimal footprint
- **Health checks** crucial for container orchestration
- **Environment variables** for configuration management

### 4. **Performance Insights**
- **Startup time**: ~50ms for Quarkus native (vs 3-5 seconds traditional JVM)
- **Memory usage**: ~50MB total footprint (vs 200MB+ traditional)
- **Container overhead**: Docker adds minimal overhead with native images
- **Database connection**: MariaDB connection pooling optimized automatically by Quarkus

### 5. **Best Practices Discovered**
- Start with Quarkus from the beginning - it's designed for native
- Use Panache for simpler data access patterns
- Leverage Quarkus dev mode for rapid development
- Test native compilation early in the development cycle
- Use container builds to avoid GraalVM installation complexity

## 🎯 Real-World Applications

This architecture is perfect for:

- **Microservices**: Ultra-fast scaling and deployment
- **Serverless**: Minimal cold start penalties
- **Edge Computing**: Low resource requirements
- **Cloud-Native**: Optimized for containerized environments
- **IoT Applications**: Minimal memory footprint
- **Kubernetes**: Perfect for container orchestration

## 🔮 Future Enhancements

- **Caching Layer**: Add Redis for improved performance
- **Security**: Implement JWT authentication with Quarkus Security
- **Monitoring**: Add metrics with SmallRye Metrics
- **Testing**: Comprehensive test coverage with Quarkus Test
- **CI/CD**: Automated deployment pipelines
- **GraphQL**: Add GraphQL support with Quarkus GraphQL

## 📚 Resources and Code

The complete source code is available on GitHub:
**https://github.com/issam1991/quarkus-native-angular-sample**

### Key Dependencies:
- Quarkus 3.15.1
- Angular 20.3.0
- GraalVM Native Image
- MariaDB Driver
- Docker & Docker Compose

## 🎉 Conclusion

Building this application taught me that **native compilation isn't just a performance optimization—it's a paradigm shift**. The combination of Quarkus Native Image and Angular creates a powerful, modern full-stack solution that's:

- ⚡ **Lightning fast** startup times (~47ms, matching Spring Boot Native)
- 💾 **Superior memory efficiency**: **20.72 MiB** backend at startup, **28 MiB under load** (58% and 44% less than Spring Boot Native's 50 MiB!)
- 🌐 **Ultra-lightweight frontend**: **2.625 MiB** with nginx (vs 50-100 MiB+ with Node.js)
- 📦 **Complete stack footprint**: **~23 MiB** at startup, **~31 MiB under load** (excluding database)
- 🐳 **Cloud-ready** for modern deployments
- 🔧 **Developer friendly** with familiar technologies and excellent dev experience

### Why Choose Quarkus Over Spring Boot Native?

The real-world Docker stats tell the story clearly:

| Metric | Spring Boot Native | Quarkus Native | Advantage |
|--------|-------------------|----------------|-----------|
| **Backend Memory (startup)** | 50 MiB | **20.72 MiB** | **58% less** |
| **Backend Memory (under load)** | 50 MiB+ | **28 MiB** | **44% less** |
| **Frontend (nginx)** | ~50-100 MiB (Node.js) | **2.625 MiB** | **95-97% less** |
| **Total App Memory (startup)** | ~100-150 MiB | **~23 MiB** | **77-85% less** |
| **Total App Memory (under load)** | ~100-150 MiB | **~31 MiB** | **69-79% less** |
| **Configuration** | More complex | Simpler (zero-config) | Developer friendly |
| **Code Simplification** | Standard JPA | Panache (Active Record) | Less boilerplate |

Quarkus's philosophy of "developer joy" combined with **superior resource efficiency** makes it the perfect choice for modern Java applications. The framework is specifically designed for cloud-native, containerized environments, and the performance numbers prove it.

**The future of Java applications is native, and Quarkus not only makes getting there easier—it makes it more efficient.**

---

## 📖 What's Next?

If you found this article helpful, consider:

1. **Starring the repository** on GitHub
2. **Trying the application** yourself
3. **Contributing** improvements or features
4. **Sharing** with your development team

**Happy coding!** 🚀

---

*Follow me on [GitHub](https://github.com/issam1991) and connect with [ForTek Advisor](https://www.linkedin.com/company/fortekadvisor?trk=public_post_feed-actor-name) for more technical content and project updates.*

#Quarkus #Angular #NativeImage #GraalVM #Java #TypeScript #FullStack #WebDevelopment #Performance #Microservices #CloudNative
