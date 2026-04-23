package com.gregory.keycloak.bizservice.integration.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.hutool.json.JSONUtil;
import com.gregory.keycloak.bizkeycloakmodel.model.request.NewRoleRequest;
import com.gregory.keycloak.bizkeycloakmodel.model.request.RenameRoleRequest;
import com.gregory.keycloak.bizkeycloakmodel.repository.KeycloakRoleRepository;
import com.gregory.keycloak.testenvironments.KeycloakIntegrationTestEnvironment;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Slf4j
class RoleControllerTest extends KeycloakIntegrationTestEnvironment {

  @Autowired
  private KeycloakRoleRepository keycloakRoleRepository;

  @Test
  @SneakyThrows
  void testNewRole() {

    final var request = new NewRoleRequest();
    request.setName(faker.name().title());
    request.setScopes(List.of("role:crud"));

    mockMvc.perform(post("/role")
            .with(jwt().authorities(new SimpleGrantedAuthority("role:crud")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONUtil.toJsonStr(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", equalTo(request.getName())));
  }

  @Test
  @SneakyThrows
  void testUpdateRole() {

    final var role = dataHelper.newRole(faker.name().title());

    mockMvc.perform(put("/role/" + role.getName())
            .with(jwt().authorities(new SimpleGrantedAuthority("role:crud")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONUtil.toJsonStr(Set.of("role:crud", "department:crud"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.scopes.length()", equalTo(2)))
        .andExpect(jsonPath("$.scopes", hasItem("role:crud")))
        .andExpect(jsonPath("$.scopes", hasItem("department:crud")));
  }

  @Test
  @SneakyThrows
  void testGetRoles() {

    dataHelper.newRole(faker.name().title());
    dataHelper.newRole(faker.name().title());
    dataHelper.newRole(faker.name().title());

    mockMvc.perform(get("/role")
            .with(jwt().authorities(new SimpleGrantedAuthority("role:crud"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", equalTo(4)))
        .andExpect(jsonPath("$[0].name", equalTo("超级管理员")));
  }

  @Test
  @SneakyThrows
  void testGetRole() {

    final var role = dataHelper.newRole(faker.name().title());

    mockMvc.perform(get("/role/" + role.getName())
            .with(jwt().authorities(new SimpleGrantedAuthority("role:crud"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", equalTo(role.getId())))
        .andExpect(jsonPath("$.name", equalTo(role.getName())))
        .andExpect(jsonPath("$.scopes.length()", equalTo(role.getScopes().size())));
  }

  @Test
  @SneakyThrows
  void testDeleteRole() {

    final var role = dataHelper.newRole(faker.name().title());

    mockMvc.perform(delete("/role/" + role.getName())
            .with(jwt().authorities(new SimpleGrantedAuthority("role:crud"))))
        .andExpect(status().isOk());

    mockMvc.perform(head("/role")
            .with(jwt().authorities(new SimpleGrantedAuthority("role:crud")))
            .param("roleName", role.getName()))
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void testRenameRole() {

    final var role = dataHelper.newRole(faker.name().title());

    final var request = new RenameRoleRequest();
    request.setNewRoleName(faker.name().title());

    mockMvc.perform(post("/role/" + role.getName() + ":rename")
            .with(jwt().authorities(new SimpleGrantedAuthority("role:crud")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONUtil.toJsonStr(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", equalTo(request.getNewRoleName())));
  }
}
