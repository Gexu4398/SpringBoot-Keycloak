package com.gexu.keycloak.bizservice.integration.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.hutool.json.JSONUtil;
import com.gexu.keycloak.bizkeycloakmodel.model.Group;
import com.gexu.keycloak.bizkeycloakmodel.service.KeycloakGroupService;
import com.gexu.keycloak.testenvironments.KeycloakIntegrationTestEnvironment;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

@Slf4j
@WithMockUser(username = "admin", authorities = "department:crud")
public class GroupControllerTest extends KeycloakIntegrationTestEnvironment {

  @Autowired
  private KeycloakGroupService keycloakGroupService;

  @Test
  @SneakyThrows
  void testNewGroup() {

    final var group = new Group();
    group.setName(faker.name().bloodGroup());

    mockMvc.perform(post("/department")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONUtil.toJsonStr(group)))
        .andExpect(status().isOk());

    mockMvc.perform(get("/department"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", equalTo(1)))
        .andExpect(jsonPath("$.content[?(@.name==='" + group.getName() + "')]",
            hasSize(1)));
  }
}
