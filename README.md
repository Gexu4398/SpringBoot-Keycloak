# SpringBoot-Keycloak

## 简介

本项目是基于 Spring Boot 3.5.7 和 Keycloak 26.4.5（keycloak/keycloak）
构建的应用程序。该项目的目的是提供一个安全的用户身份验证和授权解决方案。
持续升级spring boot和keycloak版本

## 技术栈

- **后端**: Spring Boot 3.5.7
- **身份验证**: Keycloak 26.4.5（keycloak/keycloak）
- **数据库**: PostgreSQL 17
- **构建工具**: Maven 3.9.9

## 快速开始

### 环境要求

- JDK 21
- Maven 3.9.9
- PostgreSQL 18
- Docker
- Docker Compose

## KEYCLOAK

**注意**

1. `ssl-required` 一定要设置为 `none`，包括系统中也要同步设置，否则对于 http 请求，会一直 401.
2. 新版keycloak请在控制台的realm-User profile中增加配置phoneNumber、picture和status，否则无法添加对应属性
3. `User profile`中First name默认Required for Only users

### 克隆项目

```bash
git clone https://github.com/Gexu4398/SpringBoot-Keycloak.git
cd SpringBoot-Keycloak
```

## 模块

- `biz-service`：API 服务，暴露用户、角色、组接口，集成 Swagger。
- `biz-keycloak-model`：封装 Keycloak Admin Client 与 JPA 仓储。
- `test-environments`：Testcontainers 集成测试环境。

## 启动依赖服务（Keycloak + Postgres）

```bash
docker-compose up -d
```

- Keycloak 管理地址：`http://localhost:8080/auth`
- 管理员账号：`admin` / `admin`
- 默认 Realm：`console-app`

## 启动后端服务

```bash
./mvnw.cmd -Pdev -pl biz-service spring-boot:run
```

或：

```bash
./mvnw.cmd -Pdev clean package
java -jar ./biz-service/target/biz-service.jar
```

## 访问接口与文档

- API 前缀：`http://localhost:8081/api/v1/`
- Swagger UI：`http://localhost:8081/api/v1/swagger-ui.html`

## 默认配置要点

- Realm：`console-app`
- Client：`console-cli`
- Auth Server：`http://localhost:8080/auth`
- JWK Set：`http://localhost:8080/auth/realms/console-app/protocol/openid-connect/certs`
- 数据源：`jdbc:postgresql://localhost:5432/keycloak`（`root`/`example`）

## Keycloak 注意事项

1. 开发环境请启用 `KC_HTTP_ENABLED=true` 并保持应用配置一致，否则可能出现 HTTP 401。
2. 可在 `Realm -> User profile` 添加 `phoneNumber`、`picture`、`status` 属性扩展用户信息。
3. `User profile` 中 `First name` 默认 Required for Only users。

## 代码参考

- 应用入口：`biz-service/src/main/java/com/gregory/keycloak/bizservice/BizServiceApplication.java:1`
- 安全配置：
  `biz-service/src/main/java/com/gregory/keycloak/bizservice/config/WebSecurityConfig.java:1`
- OpenAPI 配置：
  `biz-service/src/main/java/com/gregory/keycloak/bizservice/config/SwaggerConfig.java:1`
- 资源服务器配置：`biz-service/src/main/resources/application.yml:1`
- Keycloak Admin Client：
  `biz-keycloak-model/src/main/java/com/gregory/keycloak/bizkeycloakmodel/config/KeycloakConfig.java:1`
- 集成测试环境：
  `test-environments/src/main/java/com/gregory/keycloak/testenvironments/KeycloakIntegrationTestEnvironment.java:1`
