package com.gexu.keycloak.bizservice.integration.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gexu.keycloak.bizkeycloakmodel.model.request.NewUserRequest;
import com.gexu.keycloak.bizkeycloakmodel.model.request.UpdateUserRequest;
import com.gexu.keycloak.bizkeycloakmodel.service.KeycloakService;
import com.gexu.keycloak.bizkeycloakmodel.service.KeycloakUserService;
import com.gexu.keycloak.testenvironments.KeycloakIntegrationTestEnvironment;
import com.gexu.keycloak.testenvironments.service.KeycloakAccessTokenService;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@WithMockUser(username = "admin", authorities = "user:crud")
class UserControllerTest extends KeycloakIntegrationTestEnvironment {

  @Autowired
  private KeycloakUserService keycloakUserService;

  @Autowired
  private KeycloakAccessTokenService keycloakAccessTokenService;

  @Autowired
  private KeycloakService keycloakService;

  @Test
  @SneakyThrows
  void testNewUser() {

    final var role = dataHelper.newRole(faker.name().title());
    final var group = dataHelper.newGroup(faker.team().name(), null);

    final var request = new NewUserRequest();
    request.setUsername(faker.name().firstName());
    request.setPassword(faker.internet().password());
    // 接口上使用了@Valid注解对参数进行校验，所以必须全部参数都需要填值
    request.setName(faker.name().firstName());
    request.setRoleId(Set.of(role.getId()));
    request.setGroupId(group);
    request.setPicture(faker.internet().image());
    request.setPhoneNumber(faker.phoneNumber().cellPhone());

    mockMvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONUtil.toJsonStr(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username", equalTo(request.getUsername().toLowerCase())))
        .andExpect(jsonPath("$.phoneNumber", equalTo(request.getPhoneNumber())))
        .andExpect(jsonPath("$.picture", equalTo(request.getPicture())))
        .andExpect(jsonPath("$.role[0].id", equalTo(CollUtil.getFirst(request.getRoleId()))))
        .andExpect(jsonPath("$.group.id", equalTo(request.getGroupId())));
  }

  @Test
  @SneakyThrows
  void testUpdateUser() {

    final var user = dataHelper.newUser(faker.name().firstName(), faker.internet().password());

    final var request = new UpdateUserRequest();
    request.setName(faker.name().firstName());
    request.setPhoneNumber(faker.phoneNumber().cellPhone());
    request.setPicture(faker.internet().image());

    mockMvc.perform(put("/user/" + user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONUtil.toJsonStr(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.phoneNumber", equalTo(request.getPhoneNumber())))
        .andExpect(jsonPath("$.picture", equalTo(request.getPicture())));
  }

  @Test
  @SneakyThrows
  void testUpdateUser_group() {

    final var group = dataHelper.newGroup(faker.team().name(), null);
    final var user = dataHelper.newUser(faker.name().firstName(), faker.internet().password(),
        group, null);

    Assertions.assertEquals(user.getGroup().getId(), group);

    final var group_2 = dataHelper.newGroup(faker.team().name(), null);

    final var request = new UpdateUserRequest();
    request.setName(faker.name().firstName());
    request.setGroupId(group_2);

    mockMvc.perform(put("/user/" + user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONUtil.toJsonStr(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.group.id", equalTo(group_2)));
  }

  @Test
  @SneakyThrows
  void testUpdateUser_role() {

    final var role = dataHelper.newRole(faker.name().title());
    final var user = dataHelper.newUser(faker.name().firstName(), faker.internet().password(),
        null, role.getId());

    final var role_2 = dataHelper.newRole(faker.name().title());

    final var request = new UpdateUserRequest();
    request.setName(faker.name().firstName());
    request.setRoleId(Set.of(role_2.getId()));

    mockMvc.perform(put("/user/" + user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONUtil.toJsonStr(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role.size()", equalTo(1)))
        .andExpect(jsonPath("$.role[0].id", equalTo(role_2.getId())));
  }

  @Test
  @SneakyThrows
  void testGetUser() {

    final var role = dataHelper.newRole(faker.name().title());
    final var group = dataHelper.newGroup(faker.team().name(), null);

    final var request = new NewUserRequest();
    request.setUsername(faker.name().firstName());
    request.setPassword(faker.internet().password());
    // 接口上使用了@Valid注解对参数进行校验，所以必须全部参数都需要填值
    request.setName(faker.name().firstName());
    request.setRoleId(Set.of(role.getId()));
    request.setGroupId(group);
    request.setPicture(faker.internet().image());
    request.setPhoneNumber(faker.phoneNumber().cellPhone());

    mockMvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONUtil.toJsonStr(request)))
        .andExpect(status().isOk());

    final var user = keycloakUserService.getUsers(null, null, request.getUsername().toLowerCase(),
        null).getFirst();

    mockMvc.perform(get("/user/" + user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username", equalTo(request.getUsername().toLowerCase())))
        .andExpect(jsonPath("$.phoneNumber", equalTo(request.getPhoneNumber())))
        .andExpect(jsonPath("$.picture", equalTo(request.getPicture())))
        .andExpect(jsonPath("$.role[0].id", equalTo(CollUtil.getFirst(request.getRoleId()))))
        .andExpect(jsonPath("$.group.id", equalTo(request.getGroupId())));
  }

  @Test
  @SneakyThrows
  void testGetUsers() {

    final var group = dataHelper.newGroup(faker.team().name(), null);
    final var group_2 = dataHelper.newGroup(faker.team().name(), null);
    final var role = dataHelper.newRole(faker.name().title());

    dataHelper.newUser(faker.name().firstName(), faker.internet().password());
    dataHelper.newUser(faker.name().firstName(), faker.internet().password(), group, null);
    dataHelper.newUser(faker.name().firstName(), faker.internet().password(), group, role.getId());
    dataHelper.newUser(faker.name().firstName(), faker.internet().password(), group_2,
        role.getId());

    mockMvc.perform(get("/user"))
        .andExpect(status().isOk())
        // 默认会有admin用户，此处需要为5
        .andExpect(jsonPath("$.content.length()", equalTo(5)));

    mockMvc.perform(get("/user")
            .param("status", "normal"))
        .andExpect(status().isOk())
        // admin用户没有状态，且只有角色不为空的才是normal状态
        .andExpect(jsonPath("$.content.length()", equalTo(2)));

    mockMvc.perform(get("/user")
            .param("groupId", group_2))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()", equalTo(1)));

    mockMvc.perform(get("/user")
            .param("roleId", role.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()", equalTo(2)));

    mockMvc.perform(get("/user")
            .param("groupId", group)
            .param("roleId", role.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()", equalTo(1)));
  }

  @Test
  @SneakyThrows
  void testDisable_and_enable_user() {

    final var user = dataHelper.newUser(faker.name().firstName(), faker.internet().password());

    mockMvc.perform(post("/user/" + StrUtil.join(",", List.of(user.getId())) + ":disable"))
        .andExpect(status().isOk());

    Assertions.assertFalse(keycloakUserService.getUser(user.getId()).getEnabled());

    mockMvc.perform(post("/user/" + user.getId() + ":enable"))
        .andExpect(status().isOk());

    Assertions.assertTrue(keycloakUserService.getUser(user.getId()).getEnabled());
  }

  @Test
  @SneakyThrows
  void testResetUserPassword() {

    final var user = dataHelper.newUser(faker.name().firstName(), faker.internet().password());

    mockMvc.perform(post("/user/" + StrUtil.join(",", List.of(user.getId())) + ":reset-password"))
        .andExpect(status().isOk());

    final var tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
        keycloakService.getAuthServerUrl(),
        keycloakService.getRealm());

    final var jsonObject = WebClient.create(tokenUrl)
        .post()
        .body(BodyInserters.fromFormData("client_id", "model-cli")
            .with("username", user.getUsername())
            .with("password", "123123")
            .with("client_secret", "22ISi1NmKgkpUm3xJjdqvURIafg2ZLpx")
            .with("grant_type", "password"))
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono(clientResponse -> clientResponse.statusCode().equals(HttpStatus.OK)
            ? clientResponse.bodyToMono(JSONObject.class) : Mono.empty())
        .block();

    assert null != jsonObject;
  }

  @Test
  @SneakyThrows
  void testOnlineUserNum() {

    dataHelper.newUser("user_1", "user_1");
    dataHelper.newUser("user_2", "user_2");
    dataHelper.newUser("user_3", "user_3");
    dataHelper.newUser("user_4", "user_4");
    dataHelper.newUser("user_5", "user_5");

    keycloakAccessTokenService.getBearer(keycloak, "user_1", "user_1");
    keycloakAccessTokenService.getBearer(keycloak, "user_2", "user_2");
    keycloakAccessTokenService.getBearer(keycloak, "user_3", "user_3");
    keycloakAccessTokenService.getBearer(keycloak, "user_4", "user_4");

    mockMvc.perform(get("/user/onlineNum"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", equalTo(4)));
  }

  @Test
  @SneakyThrows
  void testOfflineUserNum() {

    dataHelper.newUser("user_1", "user_1");
    dataHelper.newUser("user_2", "user_2");
    dataHelper.newUser("user_3", "user_3");
    dataHelper.newUser("user_4", "user_4");
    dataHelper.newUser("user_5", "user_5");

    keycloakAccessTokenService.getBearer(keycloak, "user_3", "user_3");
    keycloakAccessTokenService.getBearer(keycloak, "user_4", "user_4");

    mockMvc.perform(get("/user/offlineNum"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", equalTo(4)));
  }
}
