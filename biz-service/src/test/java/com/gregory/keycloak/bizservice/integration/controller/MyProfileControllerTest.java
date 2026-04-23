package com.gregory.keycloak.bizservice.integration.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.hutool.json.JSONUtil;
import com.gregory.keycloak.bizkeycloakmodel.model.User;
import com.gregory.keycloak.testenvironments.KeycloakIntegrationTestEnvironment;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Slf4j
public class MyProfileControllerTest extends KeycloakIntegrationTestEnvironment {

  @Test
  @SneakyThrows
  void testUpdateProfile() {

    final var username = faker.name().firstName().toLowerCase();
    dataHelper.newUser(username, faker.internet().password());

    final var user = new User();
    user.setName(faker.name().fullName());
    user.setPhoneNumber("01234567891");
    user.setPicture(faker.internet().image());

    mockMvc.perform(put("/profile")
            .with(jwt()
                .authorities(new SimpleGrantedAuthority("user:crud"))
                .jwt(token -> token.claim("preferred_username", username)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONUtil.toJsonStr(user)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.phoneNumber", equalTo(user.getPhoneNumber())))
        .andExpect(jsonPath("$.picture", equalTo(user.getPicture())));
  }
}
