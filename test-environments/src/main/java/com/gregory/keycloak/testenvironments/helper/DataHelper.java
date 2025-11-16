package com.gregory.keycloak.testenvironments.helper;

import cn.hutool.core.util.StrUtil;
import com.gregory.keycloak.bizkeycloakmodel.model.Group;
import com.gregory.keycloak.bizkeycloakmodel.model.Role;
import com.gregory.keycloak.bizkeycloakmodel.model.User;
import com.gregory.keycloak.bizkeycloakmodel.model.UserEntity;
import com.gregory.keycloak.bizkeycloakmodel.model.request.NewRoleRequest;
import com.gregory.keycloak.bizkeycloakmodel.model.request.NewUserRequest;
import com.gregory.keycloak.bizkeycloakmodel.repository.UserEntityRepository;
import com.gregory.keycloak.bizkeycloakmodel.service.KeycloakGroupService;
import com.gregory.keycloak.bizkeycloakmodel.service.KeycloakRoleService;
import com.gregory.keycloak.bizkeycloakmodel.service.KeycloakUserService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  private final KeycloakRoleService keycloakRoleService;

  private final UserEntityRepository userEntityRepository;

  @Autowired
  public DataHelper(KeycloakUserService keycloakUserService,
      KeycloakGroupService keycloakGroupService, KeycloakRoleService keycloakRoleService,
      UserEntityRepository userEntityRepository) {

    this.keycloakUserService = keycloakUserService;
    this.keycloakGroupService = keycloakGroupService;
    this.keycloakRoleService = keycloakRoleService;
    this.userEntityRepository = userEntityRepository;
  }

  public Optional<UserEntity> getUser(String username) {

    return userEntityRepository.findByUsername(username);
  }

  public User newUser(String username, String password) {

    final var request = new NewUserRequest();
    request.setUsername(username);
    request.setPassword(password);
    return keycloakUserService.newUser(request);
  }

  public User newUser(String username, String password, String groupId, String roleId) {

    final var request = new NewUserRequest();
    request.setUsername(username);
    request.setPassword(password);
    request.setGroupId(groupId);
    if (StrUtil.isNotBlank(roleId)) {
      request.setRoleId(Set.of(roleId));
    }
    return keycloakUserService.newUser(request);
  }

  public String newGroup(String name, String parentId) {

    final var group = new Group();
    group.setName(name);
    group.setParentId(parentId);
    return keycloakGroupService.newGroup(group).getId();
  }

  public Role newRole(String name) {

    final var request = new NewRoleRequest();
    request.setName(name);
    request.setScopes(List.of("user:crud"));
    return keycloakRoleService.newRole(request);
  }
}