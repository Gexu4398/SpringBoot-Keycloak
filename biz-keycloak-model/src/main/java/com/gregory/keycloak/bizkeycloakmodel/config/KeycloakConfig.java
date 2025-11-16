package com.gregory.keycloak.bizkeycloakmodel.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Setter
@Getter
@Slf4j
public class KeycloakConfig {

  private AdminCredential admin;

  private String authServerUrl;

  private String realm;

  private String clientId;

  @Bean
  public Keycloak keycloakAdminClient() {

    // Admin 只能在 master 的 admin-cli 上操作
    return Keycloak.getInstance(authServerUrl, "master", admin.username, admin.password,
        "admin-cli");
  }

  @Getter
  @Setter
  public static class AdminCredential {

    private String username;

    private String password;
  }
}
