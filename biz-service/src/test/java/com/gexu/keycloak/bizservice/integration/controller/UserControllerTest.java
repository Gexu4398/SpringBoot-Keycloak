package com.gexu.keycloak.bizservice.integration.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.gexu.keycloak.bizkeycloakmodel.model.request.NewUserRequest;
import com.gexu.keycloak.testenvironments.KeycloakIntegrationTestEnvironment;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

@Slf4j
@WithMockUser(username = "admin", authorities = "user:crud")
class UserControllerTest extends KeycloakIntegrationTestEnvironment {

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
}
