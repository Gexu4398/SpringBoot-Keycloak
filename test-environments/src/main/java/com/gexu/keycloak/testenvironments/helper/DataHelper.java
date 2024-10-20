package com.gexu.keycloak.testenvironments.helper;

import com.gexu.keycloak.bizkeycloakmodel.model.Group;
import com.gexu.keycloak.bizkeycloakmodel.model.User;
import com.gexu.keycloak.bizkeycloakmodel.model.request.NewUserRequest;
import com.gexu.keycloak.bizkeycloakmodel.service.KeycloakGroupService;
import com.gexu.keycloak.bizkeycloakmodel.service.KeycloakUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@Slf4j
public class DataHelper {

  private final KeycloakUserService keycloakUserService;

  private final KeycloakGroupService keycloakGroupService;

  @Autowired
  public DataHelper(KeycloakUserService keycloakUserService,
      KeycloakGroupService keycloakGroupService) {

    this.keycloakUserService = keycloakUserService;
    this.keycloakGroupService = keycloakGroupService;
  }

  public User newUser(String username, String password) {

    final var request = new NewUserRequest();
    request.setUsername(username);
    request.setPassword(password);
    return keycloakUserService.newUser(request);
  }

  public String newGroup(String name, String parentId) {

    final var group = new Group();
    group.setName(name);
    group.setParentId(parentId);
    return keycloakGroupService.newGroup(group).getId();
  }
}