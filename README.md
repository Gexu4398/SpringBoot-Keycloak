# 项目名称

## 简介

这个项目是基于 Spring Boot 3.3.* 和 Keycloak 26.0.* 构建的应用程序。该项目的目的是提供一个安全的用户身份验证和授权解决方案。

## 技术栈

- **后端**: Spring Boot 3.3.*
- **身份验证**: keycloak/keycloak 26.0.* 或 bitnami/keycloak 26.0.*
- **数据库**: PostgreSQL 17
- **构建工具**: Maven 3.9.5

## 快速开始

### 环境要求

- JDK 21 或更高版本
- Maven 3.9.5 或更高版本
- PostgreSQL 16 或更高版本
- Docker

## KEYCLOAK

**注意**

1. `ssl-required` 一定要设置为 `none`，包括系统中也要同步设置，否则对于 http 请求，会一直 401.
2. 新版keycloak请在控制台的realm-User profile中增加配置phoneNumber、picture和status，否则无法添加对应属性
3. `User profile`中First name默认Required for Only users

### 克隆项目

```bash
git clone https://github.com/Gexu4398/SpringBoot-Keycloak.git
cd SpringBoot-Keycloak
